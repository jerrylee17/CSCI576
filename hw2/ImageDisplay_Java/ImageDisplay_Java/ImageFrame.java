import java.util.ArrayList;

public class ImageFrame {
    int width = 640;
    int height = 480;
    short[] r_values;
    short[] g_values;
    short[] b_values;
    ImageFrame() {
        r_values = new short[width*height];
        g_values = new short[width*height];
        b_values = new short[width*height];
    }
    int get_r(int index) {
        return r_values[index];
    }
    int get_g(int index) {
        return g_values[index];
    }
    int get_b(int index) {
        return b_values[index];
    }

    void rgb_set(int index, int r, int g, int b) {
        r_values[index] = (short)r;
        g_values[index] = (short)g;
        b_values[index] = (short)b;
    }
}
