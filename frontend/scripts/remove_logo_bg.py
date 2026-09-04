from PIL import Image
from pathlib import Path

src = Path(r"c:\Users\Gabriel Natan\Desktop\gustavo\rede-social\frontend\src\assets\logo-raw.jpg")
out = Path(r"c:\Users\Gabriel Natan\Desktop\gustavo\rede-social\frontend\src\assets\logo-branca.png")

img = Image.open(src).convert("RGBA")
pixels = img.load()
w, h = img.size

corners = [pixels[0, 0], pixels[w - 1, 0], pixels[0, h - 1], pixels[w - 1, h - 1]]
avg = tuple(sum(c[i] for c in corners) // 4 for i in range(3))
print("bg approx", avg)

for y in range(h):
    for x in range(w):
        r, g, b, a = pixels[x, y]
        near_avg = (
            abs(r - avg[0]) < 30
            and abs(g - avg[1]) < 30
            and abs(b - avg[2]) < 30
            and min(r, g, b) > 140
        )
        near_gray = (
            r > 170
            and g > 170
            and b > 170
            and abs(r - g) < 20
            and abs(g - b) < 20
            and abs(r - b) < 20
        )
        if near_avg or near_gray:
            pixels[x, y] = (r, g, b, 0)

img.save(out, "PNG")
print("saved", out, out.stat().st_size)
