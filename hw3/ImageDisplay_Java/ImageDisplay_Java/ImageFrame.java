import java.util.ArrayList;

public class ImageFrame {
    int width;
    int height;
    short[] r_values;
    short[] g_values;
    short[] b_values;
    ImageFrame(int width, int height) {
        this.width = width;
        this.height = height;
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

    void copy(ImageFrame frame) {
        if (this.width != frame.width || this.height != frame.height) {
            System.out.println("Unable to copy frame");
            return;
        }
        r_values = frame.r_values.clone();
        g_values = frame.g_values.clone();
        b_values = frame.b_values.clone();
    }
}
