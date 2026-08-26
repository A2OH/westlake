# Toutiao forced-JIT visual acceptance — 2026-08-25

## Result

The unchanged Toutiao APK now passes the requested visual acceptance paths on the
OpenHarmony bridge with forced ART JIT enabled:

| Path | Result | Direct evidence |
| --- | --- | --- |
| Recommended feed | PASS | `run-1819-final/tt1819-final-feed4.jpeg` and `run-29046-evidence/tt29046-feed.jpeg` contain readable populated cards and headlines |
| Article WebView | PASS | `run-29046-evidence/tt29046-back.jpeg` contains a readable title and body paragraphs; the DOM callback reports `ready=complete`, 6 body children, and 20,122 bytes of body HTML |
| Video | PASS | `run-29046-evidence/tt29046-video-play-c.jpeg` and `tt29046-video-play-d.jpeg` are visibly different decoded frames; `video-visible.vt` reports `renderStarted=1 playing=1 preparing=0` |
| Forced JIT | PASS | PID 29046 log contains `[JIT-528] ... (JIT IS LIVE)` |
| Noice unchanged control | PASS | `widgets=95 jitlive=1 fatal=0 segv=0 alive=1` |

The board was finally left on PID 1819 with the populated Toutiao feed visible.

## Generic fixes

1. Restored the Android child working-directory contract in
   `/home/dspfac/bridge-build-arm64/deploy-jit796/run_tt.sh`: the launcher changes
   to `/` and exports `user.dir=/` before forking apps. This prevents an unrelated
   staging directory named `null` from making Toutiao emit `file://null/...`
   WebView asset URLs.
2. Added zero-mask `ArrayElementVarHandle` access-mode support in ART's generic
   VarHandle fallback. This fixes Toutiao's RxJava article pipeline, which had
   unwound from `setRelease()`/`poll()` with `UnsupportedOperationException`.
   Byte-array and byte-buffer view VarHandles remain deliberately excluded.

After the ART fix, PID 29046 produced zero
`PFCUT-VARHANDLE.*ArrayElementVarHandle` events and zero
`pending UOE.*X.Ihp.poll` events.

## Proof reel

`toutiao-pid29046-jit-webview-video-feed-proof.mp4` is a 74.67-second H.264
sampled-frame proof reel (1200x1920, 1.5 fps). It begins with the readable article
WebView, continues through real changing video frames and subtitles, and ends on
the accepted populated feed frame. The board has no native `screenrecord`, so the
reel was built from repeated real `snapshot_display` framebuffer captures.

MD5: `bbb7fc62e79a40f2aaa083073bba4fc5`

## Artifact hashes and rollback

- New `libart.so`: `2b8f47fd73b78ead6fb088d420d32d41`
- Previous `libart.so`: `8acae5eba91e9fb067d7cdd89eb5a711`
- Deployed `run_tt.sh`: `a333325a78337a073e5b9dd94906e900`
- Device rollback files:
  - `/data/local/tmp/asx/libart.so.pre-array-varhandle-20260825`
  - `/data/local/tmp/asx/run_tt.sh.pre-cwd-20260825`

The old and new ART libraries both expose 32,342 defined dynamic symbols and
462 unresolved imports, with no symbol-name differences.

## Honest caveats

- The accepted article's title and body render, but one inline remote image shows
  Toutiao's `image load failed` placeholder. Image-cache traffic continues and
  this was not required to establish that the WebView and article data path work.
- Material Catalog still fails before its side channel with a null application
  context/resources and `topLocale is null`. This is the already-flagged control
  issue and occurs before the new ArrayElementVarHandle path; it is not counted as
  a Toutiao acceptance pass.
- A synthetic Back event did not unwind the full-screen player cleanly, so the
  final feed segment in the proof reel uses the earlier accepted same-PID feed
  framebuffer rather than pretending that navigation succeeded.
