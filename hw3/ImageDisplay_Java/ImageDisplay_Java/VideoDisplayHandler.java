import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

// Tales a folder of rgb files and displays it
public class VideoDisplayHandler {
    // Buffered image = processed frames ready to display
    ArrayList<BufferedImage> images;
    // Frames = raw rgb values
    ArrayList<ImageFrame> frames;
    int width = 512; // default image width and height
    int height = 512;

    VideoDisplayHandler(ArrayList<ImageFrame> frames) {
        this.images = new ArrayList<BufferedImage>();
        this.frames = frames;
        PrepareForRendering();
    }

    public void PrepareForRendering(){
        images.clear();
        ImageRGBReader imageRGBReader = new ImageRGBReader(width, height);
        for (ImageFrame frame: frames) {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageRGBReader.frameToImage(frame);
            imageRGBReader.DisplayImage(img);
            images.add(img);
        }
    }

    public void DisplayVideo() {
        Timer timer = new Timer();
        System.out.println("Starting video render");
        TimerTask task = new ImageDisplayTask(images);
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
}
