import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImageRGBReader {
    int width = 512;
    int height = 512;

    int[] processedRGB;
    ImageRGBReader(int width, int height){
        this.width = width;
        this.height = height;
        processedRGB = new int[width*height];
    }

    // Return frame from image
    ImageFrame readToFrame(String imgPath) {
        ImageFrame frame = new ImageFrame(width, height);

        try {
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                int row_loc = y * width;
                for(int x = 0; x < width; x++)
                {
                    int index = row_loc + x;
                    int r = Byte.toUnsignedInt(bytes[ind]);
                    int g = Byte.toUnsignedInt(bytes[ind+height*width]);
                    int b = Byte.toUnsignedInt(bytes[ind+height*width*2]);
                    frame.rgb_set(index, r, g, b);

                    ind++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return frame;
    }

    public void frameToImage(ImageFrame frame) {
        for(int y = 0; y < height; y++)
        {
            int row_loc = y * width;
            for(int x = 0; x < width; x++)
            {
                int index = row_loc + x;
                int r_int = frame.get_r(index);
                int g_int = frame.get_g(index);
                int b_int = frame.get_b(index);

                // Clamp
                r_int = Math.max(Math.min(255, r_int), 0);
                g_int = Math.max(Math.min(255, g_int), 0);
                b_int = Math.max(Math.min(255, b_int), 0);

                byte r = (byte)(r_int);
                byte g = (byte)(g_int);
                byte b = (byte)(b_int);
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                processedRGB[index] = pix;
            }
        }
    }

    // Set BufferedImg rgb
    public void DisplayImage(BufferedImage img) {
        for(int y = 0; y < height; y++)
        {
            int row_loc = y * width;
            for(int x = 0; x < width; x++)
            {
                int index = row_loc + x;
                img.setRGB(x, y, processedRGB[index]);
            }
        }
    }

}
