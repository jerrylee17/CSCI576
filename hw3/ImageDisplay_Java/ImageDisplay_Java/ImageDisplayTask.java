import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

public class ImageDisplayTask extends TimerTask {
    ImageDisplayHandler imageDisplayHandler;
    ArrayList<BufferedImage> images;
    int index;
    ImageDisplayTask(ArrayList<BufferedImage> external_images) {
        imageDisplayHandler = new ImageDisplayHandler();
        images = external_images;
        index = 0;
    }
    public void run() {
        try {
            if (index < 10) {
                imageDisplayHandler.SetImage(images.get(9 - index));
                imageDisplayHandler.DisplayImage();
                index++;
            } else if (index == 10) {
                System.out.println("Video is done playing");
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
