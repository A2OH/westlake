#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
STAGE_DIR=$SCRIPT_DIR/native/out/asx
REMOTE_ROOT=/data/local/tmp/asx
HDC=${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}
HDC_TARGET=${HDC_TARGET:-}
DRY_RUN=0
VERIFY=1

usage() {
    cat <<'EOF'
Usage: deploy_asx.sh [options] [stage-directory]

Push a prepared directory tree into /data/local/tmp/asx without deleting any
unlisted device files.

Options:
  --remote PATH    remote root (default: /data/local/tmp/asx)
  --target SERIAL  hdc target serial (or set HDC_TARGET)
  --hdc PATH       hdc executable (or set HDC)
  --no-verify      skip post-transfer MD5 comparison
  --dry-run        print operations without changing the device
  -h, --help       show this help
EOF
}

while (($# > 0)); do
    case $1 in
        --remote)
            (($# >= 2)) || { echo "--remote needs a path" >&2; exit 2; }
            REMOTE_ROOT=$2
            shift 2
            ;;
        --target)
            (($# >= 2)) || { echo "--target needs a serial" >&2; exit 2; }
            HDC_TARGET=$2
            shift 2
            ;;
        --hdc)
            (($# >= 2)) || { echo "--hdc needs a path" >&2; exit 2; }
            HDC=$2
            shift 2
            ;;
        --dry-run)
            DRY_RUN=1
            shift
            ;;
        --no-verify)
            VERIFY=0
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        --*)
            echo "unknown option: $1" >&2
            usage >&2
            exit 2
            ;;
        *)
            STAGE_DIR=$1
            shift
            (($# == 0)) || { echo "only one stage directory is allowed" >&2; exit 2; }
            ;;
    esac
done

if [[ ! -d $STAGE_DIR ]]; then
    echo "stage directory does not exist: $STAGE_DIR" >&2
    exit 1
fi
STAGE_DIR=$(CDPATH= cd -- "$STAGE_DIR" && pwd)

if [[ ! $REMOTE_ROOT =~ ^/[A-Za-z0-9._/-]+$ ]] || \
        [[ $REMOTE_ROOT == / || $REMOTE_ROOT == /data || \
           $REMOTE_ROOT == /data/local || $REMOTE_ROOT == /data/local/tmp ]]; then
    echo "refusing unsafe remote root: $REMOTE_ROOT" >&2
    exit 1
fi

hdc_cmd=("$HDC")
if [[ -n $HDC_TARGET ]]; then
    hdc_cmd+=(-t "$HDC_TARGET")
fi
if ((DRY_RUN == 0)) && [[ ! -x $HDC ]]; then
    echo "hdc executable is not available: $HDC" >&2
    exit 1
fi

print_command() {
    printf '+'
    printf ' %q' "$@"
    printf '\n'
}

run_hdc() {
    if ((DRY_RUN != 0)); then
        print_command "${hdc_cmd[@]}" "$@"
    else
        "${hdc_cmd[@]}" "$@"
    fi
}

verify_remote_file() {
    local local_file=$1
    local remote_file=$2
    if ((VERIFY == 0)); then
        return
    fi
    if ((DRY_RUN != 0)); then
        print_command md5sum "$local_file"
        print_command "${hdc_cmd[@]}" shell "md5sum '$remote_file'"
        return
    fi
    local local_md5 remote_output remote_md5
    local_md5=$(md5sum "$local_file")
    local_md5=${local_md5%% *}
    remote_output=$("${hdc_cmd[@]}" shell "md5sum '$remote_file'")
    remote_md5=$(printf '%s\n' "$remote_output" | \
        grep -Eo '[0-9a-fA-F]{32}' | head -1 || true)
    if [[ -z $remote_md5 || ${remote_md5,,} != ${local_md5,,} ]]; then
        echo "MD5 mismatch after push: $remote_file" >&2
        echo "  local:  $local_md5" >&2
        echo "  remote: ${remote_md5:-unavailable}" >&2
        exit 1
    fi
}

remote_mkdir() {
    run_hdc shell "mkdir -p '$1'"
}

remote_mkdir "$REMOTE_ROOT"
while IFS= read -r -d '' directory; do
    relative=${directory#"$STAGE_DIR"/}
    [[ $directory == "$STAGE_DIR" ]] && continue
    if [[ ! $relative =~ ^[A-Za-z0-9._/-]+$ ]]; then
        echo "unsupported directory name: $relative" >&2
        exit 1
    fi
    remote_mkdir "$REMOTE_ROOT/$relative"
done < <(find "$STAGE_DIR" -mindepth 1 -type d -print0 | sort -z)

count=0
while IFS= read -r -d '' local_file; do
    relative=${local_file#"$STAGE_DIR"/}
    if [[ ! $relative =~ ^[A-Za-z0-9._/-]+$ ]]; then
        echo "unsupported file name: $relative" >&2
        exit 1
    fi
    remote_file=$REMOTE_ROOT/$relative
    run_hdc file send "$local_file" "$remote_file"
    mode=0644
    if [[ $relative == *.so || -x $local_file ]]; then
        mode=0755
    fi
    run_hdc shell "chmod $mode '$remote_file'"
    verify_remote_file "$local_file" "$remote_file"
    ((count += 1))
done < <(find "$STAGE_DIR" -type f -print0 | sort -z)

echo "Pushed $count files from $STAGE_DIR to $REMOTE_ROOT"
