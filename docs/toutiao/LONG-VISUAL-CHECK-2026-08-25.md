# Toutiao long visual check - 2026-08-25

## Deliverable

- Video: `toutiao-long-visual-check-20260825.mp4`
- Duration: 386.2 seconds (6:26.2)
- Format: H.264, 720x1248, 10 fps, yuv420p
- MD5: `7cc77c9efa0108b195bd28b0f22f115c`

This is a cut, sampled screenshot-capture evidence reel. The board does not expose Android's
native `screenrecord`, so it must not be described as an uninterrupted native recording. Each
run is labeled in the top black band. Long cold-start waits are time-compressed by presenting the
captured JPEG samples at a fixed frame rate.

## What the reel proves

| Requested check | Result | Evidence |
| --- | --- | --- |
| Start and live feed | PASS, with a media caveat | The app starts and the feed fills with live Chinese headlines and metadata. Feed media slots are often gray placeholders. |
| Search text entry | PASS | Fresh run B visibly commits `AI` to the focused search field using the generic focused-text bridge. |
| Search results | PARTIAL | Fresh run B visibly renders the result HTML and links, but styling is fallback-like and `<img>` elements are broken. |
| Open searched item | FAIL in the fresh path | The selected AI result creates its detail UI but remains on `正努力加载中，请耐心等待` for several minutes. |
| Article/WebView body | PASS only in the earlier same-stack control | The labeled PID 29046 segment visibly renders a long article body. Its view-tree oracle reports `dataLen=45304`, `started=1`, `ready=1`, and `dataReady=1`. The article image still says `图片加载失败，点击重试`. |
| Scroll down and up | NOT freshly proven | No reliable current-run up/down visual pair was obtained after the AI detail stalled and the input callback stopped draining. |
| Video playback | PASS | Moving video pixels are present in runs A and the PID 29046 control. The structural oracle reports `TTReusePlayer`, `renderStarted=1`, `paused=0`, `playing=1`. |
| Navigate all five bottom menus | FAIL | Home exposes Home, Video, Task, Shop, and Mine structurally. Video was selected once. Subsequent menu input routes failed their own oracles and did not produce verified loaded pages for all destinations. |
| Clean JIT execution | FAIL | The JIT process survives and continues rendering, but current launches still emit recoverable `CHILDSEGV` diagnostics at interpreter `InstructionHandler::GOTO`, address `0x8`. |

## Why images are failing

The evidence excludes a blanket networking failure: repeated TCP/TLS port-443 connections
complete with `SO_ERROR=0`. Both bundled HEIF decoders resolve and load:

- `libttheif_dec.so`
- `libbdheif.so`

Nevertheless, fresh search HTML shows broken image elements, feed media is often a gray
placeholder, and the loaded article shows the explicit image-load-failure message. This narrows
the defect to the WebView/Fresco resource request, response, or decode/integration path. The
current evidence does not isolate which of those stages is causal, so HEIF decode is a candidate,
not a concluded root cause.

The stalled AI detail also logs a caught `NumberFormatException` from
`UgcAggrListPresenter` for an empty string. That is correlated with the stalled content path but
is not yet proved to cause it.

## Input-route controls

The menu retries were kept in the reel because they refute several misleading success signals:

1. `ViewRootImpl.enqueueInputEvent` reported the tap delivered, but the selected page did not
   change after the first Video navigation.
2. The main-message-queue route reported `posted=1`, but its callback never ran.
3. Synchronous hit-testing reported `DOWN handled=1`, while its click fallback returned
   `callOnClick=0`.
4. The framework force-click helper reported `invokeCount=2`, `runCount=0` after 30 seconds.

Thus, “event submitted” is not counted as navigation success.

## Deployed artifacts

- `/data/local/tmp/asx/libart.so`: `9ad65d8de366498e9db30b8d08693753`
- `/data/local/tmp/asx/liboh_adapter_bridge.so`: `3afe8f5766e3e30ada80ddcac2e07ac0`
- `/data/local/tmp/asx/focused-text-helper.jar`: `89866d46f8b16f8326a9420718dbd968`
- `/data/local/tmp/asx/fw/oh-adapter-framework.jar`: unchanged baseline `0a40d8344680f25ed8b94062e425416e`

The bridge adds a generic focused-`TextView` commit path via a standalone helper jar. The APK is
unchanged. The boot framework jar was not replaced because its boot-image rebuild did not
complete.

## Source evidence

- `start-feed-video-frames/`: startup, feed, and visibly moving video
- `clean-final-frames/`: fresh search, results, stalled AI detail, and restart/menu attempts
- `fd-main-frames/`: three controlled menu-input route retries
- `adapter_child_8504.full.stderr`: fresh search/network/decoder diagnostics
- `../run-29046-evidence/`: earlier same-stack article-body and video control
- `verify-reel/`: frames extracted back from the final MP4 for visual verification
