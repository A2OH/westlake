param(
    [string]$Serial = "cfb7c9e3",
    [string]$Adb = "C:\Users\dspfa\Dev\platform-tools\adb.exe",
    [string]$Repo = "\\wsl.localhost\Ubuntu-24.04\home\user\android-to-openharmony-migration",
    [string]$OutDir = "C:\Users\dspfa\TempWestlake\real_mcd"
)

$ErrorActionPreference = "Stop"

$shim = Join-Path $Repo "aosp-shim.dex"
$dalvikvm = Join-Path $Repo "ohos-deploy\arm64-a15\dalvikvm"
$apk = Join-Path $Repo "westlake-host-gradle\app\build\outputs\apk\debug\app-debug.apk"
$stamp = Get-Date -Format "yyyyMMdd_HHmmss"
$log = Join-Path $OutDir "real_mcd_${stamp}.log"
$png = Join-Path $OutDir "real_mcd_${stamp}.png"

New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

& $Adb -s $Serial devices
& $Adb -s $Serial push $dalvikvm /data/local/tmp/westlake/dalvikvm
& $Adb -s $Serial push $shim /data/local/tmp/westlake/aosp-shim.dex
& $Adb -s $Serial shell chmod 755 /data/local/tmp/westlake/dalvikvm
& $Adb -s $Serial install -r $apk
& $Adb -s $Serial shell sha256sum /data/local/tmp/westlake/dalvikvm /data/local/tmp/westlake/aosp-shim.dex
& $Adb -s $Serial logcat -c
& $Adb -s $Serial shell am start -S -W -n com.westlake.host/.WestlakeActivity --es launch WESTLAKE_ART_MCD
Start-Sleep -Seconds 25
& $Adb -s $Serial logcat -d | Out-File -Encoding ascii $log
$screenCmd = '"' + $Adb + '" -s "' + $Serial + '" exec-out screencap -p > "' + $png + '"'
& cmd.exe /c $screenCmd
if ($LASTEXITCODE -ne 0) {
    throw "screencap failed with exit code $LASTEXITCODE"
}

Write-Host "log=$log"
Write-Host "screenshot=$png"
