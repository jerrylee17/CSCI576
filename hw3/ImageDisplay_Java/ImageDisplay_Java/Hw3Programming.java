import java.util.ArrayList;
import java.util.Timer;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

public class Hw3Programming {
    static int width = 512; // default image width and height
    static int height = 512;

    public static void main(String[] args) {
        int low_pass_level = Integer.parseInt(args[1]);
        ImageRGBReader imageRGBReader = new ImageRGBReader(width, height);
        Encoder encoder = new Encoder();
        ImageFrame frame = imageRGBReader.readToFrame(args[0]);
        ArrayList<ImageFrame> frames = encoder.encodeLayers(frame);
        if (low_pass_level == -1) {
            VideoDisplayHandler videoDisplayHandler = new VideoDisplayHandler(frames);
            videoDisplayHandler.DisplayVideo();
        } else {
            int frame_index = 9 - low_pass_level;
            ImageDisplayHandler imageDisplayHandler = new ImageDisplayHandler();
            ImageFrame display_frame = frames.get(frame_index);
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageRGBReader.frameToImage(display_frame);
            imageRGBReader.DisplayImage(img);
            imageDisplayHandler.SetAndDisplayImage(img);
        }
    }
}
