public class ColorConverter {
    public static double[] rgbToHSV(int r_int, int g_int, int b_int) {

        double[] hsv = {-1, -1, 0};

        double r = r_int / 255.0;
        double g = g_int / 255.0;
        double b = b_int / 255.0;

        // h, s, v = hue, saturation, value
        double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
        double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
        double diff = cmax - cmin; // diff of cmax and cmin.
        // if cmax and cmax are equal then h = 0
        if (cmax == cmin)
            hsv[0] = 0;

            // if cmax equal r then compute h
        else if (cmax == r)
            hsv[0] = (60 * ((g - b) / diff) + 360) % 360;

            // if cmax equal g then compute h
        else if (cmax == g)
            hsv[0] = (60 * ((b - r) / diff) + 120) % 360;

            // if cmax equal b then compute h
        else if (cmax == b)
            hsv[0] = (60 * ((r - g) / diff) + 240) % 360;

        // if cmax equal zero
        if (cmax == 0)
            hsv[1] = 0;
        else
            hsv[1] = (diff / cmax) * 100;

        // compute v
        hsv[2] = cmax * 100;
        return hsv;
    }

    public static double[] rgbToHSL(int r_int, int g_int, int b_int) {
        double[] hsl = {0, 0, 0};
        double r = r_int / 255.0;
        double g = g_int / 255.0;
        double b = b_int / 255.0;
        double cmax = Math.max(Math.max(r, g), b);
        double cmin = Math.min(Math.min(r, g), b);
        double diff = cmax - cmin;

        if (diff == 0) {
            hsl[0] = 0;
        } else if (cmax == r) {
            hsl[0] = (double)(g-b) / diff;
            if (hsl[0] < 0) hsl[0] += 6.f;
        } else if (cmax == g) {
            hsl[0] = (double)(b-r) / diff + 2.f;
        } else if (cmax == b) {
            hsl[0] = (double)(r-g) / diff + 4.f;
        }
        double h = 60.f * hsl[0];

        double l = (cmax + cmin) * 0.5f;

        double s;
        if (diff == 0) {
            s = 0.f;
        } else {
            s = diff / (1 - Math.abs(2.f * l - 1.f));
        }

        hsl[0] = Math.max(Math.min(h, 360), 0);
        hsl[1] = Math.max(Math.min(s, 1), 0);
        hsl[2] = Math.max(Math.min(l, 1), 0);
        return hsl;
    }

    public static int[] hslToRGB(double h, double s, double l) {
        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        double q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        double p = 2 * l - q;

        int r = (int) Math.round(Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)) * 256));
        int g = (int) Math.round(Math.max(0, HueToRGB(p, q, h) * 256));
        int b = (int) Math.round(Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)) * 256));

        int[] array = { r, g, b };
        return array;
    }

    private static double HueToRGB(double p, double q, double h) {
        if (h < 0)
            h += 1;

        if (h > 1)
            h -= 1;

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }
}
