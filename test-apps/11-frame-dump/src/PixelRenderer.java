import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.Stack;
import com.ohos.shim.bridge.OHBridge;

/**
 * Renders an OHBridge draw log to a real PNG image using Java2D.
 *
 * Reads the sequence of DrawRecord entries (save, restore, translate, scale,
 * rotate, clipRect, drawColor, drawRect, drawRoundRect, drawCircle, drawOval,
 * drawArc, drawLine, drawText) and paints them onto a BufferedImage via
 * Graphics2D, producing pixel-accurate screenshots of Android Activities.
 */
public class PixelRenderer {

    /**
     * Render a draw log to a BufferedImage.
     *
     * @param drawLog the list of draw operations recorded by OHBridge
     * @param width   canvas width in pixels
     * @param height  canvas height in pixels
     * @return a BufferedImage containing the rendered frame
     */
    public static BufferedImage render(List<OHBridge.DrawRecord> drawLog, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Transform save/restore stack
        Stack<AffineTransform> saveStack = new Stack<AffineTransform>();
        // Also save/restore clip along with transforms
        Stack<Shape> clipStack = new Stack<Shape>();

        for (int i = 0; i < drawLog.size(); i++) {
            OHBridge.DrawRecord r = drawLog.get(i);
            String op = r.op;

            if ("save".equals(op)) {
                saveStack.push(new AffineTransform(g.getTransform()));
                clipStack.push(g.getClip());

            } else if ("restore".equals(op)) {
                if (!saveStack.isEmpty()) {
                    g.setTransform(saveStack.pop());
                    Shape clip = clipStack.pop();
                    g.setClip(clip);
                }

            } else if ("translate".equals(op)) {
                g.translate(r.args[0], r.args[1]);

            } else if ("scale".equals(op)) {
                g.scale(r.args[0], r.args[1]);

            } else if ("rotate".equals(op)) {
                float degrees = r.args[0];
                float px = r.args.length > 1 ? r.args[1] : 0;
                float py = r.args.length > 2 ? r.args[2] : 0;
                g.rotate(Math.toRadians(degrees), px, py);

            } else if ("clipRect".equals(op)) {
                int cl = (int) r.args[0];
                int ct = (int) r.args[1];
                int cr = (int) r.args[2];
                int cb = (int) r.args[3];
                g.clipRect(cl, ct, cr - cl, cb - ct);

            } else if ("drawColor".equals(op)) {
                Color c = argbToColor(r.color);
                g.setColor(c);
                // Fill the entire canvas (respects current clip)
                g.fillRect(0, 0, width, height);

            } else if ("drawRect".equals(op)) {
                float l = r.args[0];
                float t = r.args[1];
                float rr = r.args[2];
                float b = r.args[3];
                g.setColor(argbToColor(r.color));
                g.fillRect((int) l, (int) t, (int) (rr - l), (int) (b - t));

            } else if ("drawRoundRect".equals(op)) {
                float l = r.args[0];
                float t = r.args[1];
                float rr = r.args[2];
                float b = r.args[3];
                float rx = r.args.length > 4 ? r.args[4] : 8;
                float ry = r.args.length > 5 ? r.args[5] : 8;
                g.setColor(argbToColor(r.color));
                g.fill(new RoundRectangle2D.Float(l, t, rr - l, b - t, rx * 2, ry * 2));

            } else if ("drawCircle".equals(op)) {
                float cx = r.args[0];
                float cy = r.args[1];
                float cr = r.args[2];
                g.setColor(argbToColor(r.color));
                g.fill(new Ellipse2D.Float(cx - cr, cy - cr, cr * 2, cr * 2));

            } else if ("drawOval".equals(op)) {
                float l = r.args[0];
                float t = r.args[1];
                float rr = r.args[2];
                float b = r.args[3];
                g.setColor(argbToColor(r.color));
                g.fill(new Ellipse2D.Float(l, t, rr - l, b - t));

            } else if ("drawArc".equals(op)) {
                float l = r.args[0];
                float t = r.args[1];
                float rr = r.args[2];
                float b = r.args[3];
                float startAngle = r.args[4];
                float sweepAngle = r.args[5];
                boolean useCenter = r.args.length > 6 && r.args[6] != 0;
                g.setColor(argbToColor(r.color));
                int arcType = useCenter ? Arc2D.PIE : Arc2D.OPEN;
                g.fill(new Arc2D.Float(l, t, rr - l, b - t, startAngle, sweepAngle, arcType));

            } else if ("drawLine".equals(op)) {
                float x1 = r.args[0];
                float y1 = r.args[1];
                float x2 = r.args[2];
                float y2 = r.args[3];
                float strokeWidth = r.args.length > 4 ? r.args[4] : 1.0f;
                g.setColor(argbToColor(r.color));
                g.setStroke(new BasicStroke(strokeWidth));
                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

            } else if ("drawText".equals(op)) {
                if (r.text != null) {
                    int textColor = r.color != 0 ? r.color : 0xFF000000;
                    g.setColor(argbToColor(textColor));
                    // Font size is stored in args[2] if available
                    float fontSize = r.args.length > 2 ? r.args[2] : 14.0f;
                    int intSize = (int) fontSize;
                    if (intSize < 1) intSize = 14;
                    g.setFont(new Font("SansSerif", Font.PLAIN, intSize));
                    g.drawString(r.text, r.args[0], r.args[1]);
                }

            } else if ("drawBitmap".equals(op)) {
                // Bitmap rendering not supported in mock — draw placeholder
                float bx = r.args[0];
                float by = r.args[1];
                g.setColor(new Color(200, 200, 200));
                g.fillRect((int) bx, (int) by, 48, 48);
                g.setColor(Color.GRAY);
                g.drawRect((int) bx, (int) by, 48, 48);
            }
            // Ignore: drawPath, clipPath, concat — not easily mapped without path data
        }

        g.dispose();
        return img;
    }

    /**
     * Convert an ARGB int to a java.awt.Color.
     */
    private static Color argbToColor(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return new Color(r, green, b, a);
    }

    /**
     * Save a BufferedImage as a PNG file.
     *
     * @param img  the image to save
     * @param path the output file path
     */
    public static void savePNG(BufferedImage img, String path) throws Exception {
        File f = new File(path);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        ImageIO.write(img, "PNG", f);
    }
}
