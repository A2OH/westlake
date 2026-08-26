#!/usr/bin/env python3
"""shotlit.py <img> [<img>...] — quantify how "lit" a screenshot is.

Eyeballing black-vs-not is what let a lock screen and a powered-off panel both get
recorded as "the app renders black". This prints numbers instead:
  lum   mean luminance 0-255
  lit%  fraction of pixels brighter than 16 (i.e. not near-black)
  cols  distinct quantised colours (a flat fill has very few)
  rows% fraction of image rows that contain any lit pixel  -> catches partial UI
verdict: BLACK (nothing), FLAT (uniform non-black fill), PARTIAL, LIT.
"""
import sys
from PIL import Image

def analyse(path):
    im = Image.open(path).convert("RGB")
    w, h = im.size
    small = im.resize((min(w, 300), min(h, 480)))
    px = list(small.getdata())
    sw, sh = small.size
    lum = [(3 * r + 6 * g + b) // 10 for (r, g, b) in px]
    n = len(lum)
    mean = sum(lum) / n
    lit = sum(1 for v in lum if v > 16) / n
    cols = len({(r >> 3, g >> 3, b >> 3) for (r, g, b) in px})
    rows_lit = sum(1 for y in range(sh)
                   if any(lum[y * sw + x] > 16 for x in range(sw))) / sh
    if lit < 0.01:
        verdict = "BLACK"
    elif cols <= 4:
        verdict = "FLAT"
    elif rows_lit < 0.5 or lit < 0.15:
        verdict = "PARTIAL"
    else:
        verdict = "LIT"
    return dict(path=path, size=f"{w}x{h}", lum=round(mean, 1),
                lit=round(lit * 100, 1), cols=cols,
                rows=round(rows_lit * 100, 1), verdict=verdict)

if __name__ == "__main__":
    print(f"{'shot':<34}{'lum':>7}{'lit%':>7}{'cols':>7}{'rows%':>7}  verdict")
    for p in sys.argv[1:]:
        try:
            r = analyse(p)
        except Exception as e:                     # unreadable/truncated capture
            print(f"{p.split('/')[-1]:<34}{'ERR':>7}  {e}")
            continue
        print(f"{r['path'].split('/')[-1]:<34}{r['lum']:>7}{r['lit']:>7}"
              f"{r['cols']:>7}{r['rows']:>7}  {r['verdict']}")
