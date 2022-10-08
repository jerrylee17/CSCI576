import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

// Tales a folder of rgb files and displays it
public class VideoDisplayHandler {
    ArrayList<String> fileNames;
    // Buffered image = processed frames ready to display
    ArrayList<BufferedImage> images;
    // Frames = raw rgb values
    ArrayList<ImageFrame> frames;
    int width = 640; // default image width and height
    int height = 480;

    VideoDisplayHandler(String videoPath) {
        fileNames = new ArrayList<String>();
        images = new ArrayList<BufferedImage>();
        frames = new ArrayList<ImageFrame>();
        if (!videoPath.isEmpty()){
            RetrieveFileNames(videoPath);
            RetrieveFileRGB(fileNames);
        }
    }

    private void RetrieveFileNames(String videoPath) {
        fileNames.clear();

        File[] files = new File(videoPath).listFiles();
        if (files == null) {
            System.out.println("Path is incorrect");
            return;
        }
        for (File file: files) {
            if (file.isFile()) {
                fileNames.add(videoPath + "/" + file.getName());
            }
        }
        Collections.sort(fileNames);
    }

    public void RetrieveFileRGB(ArrayList<String> fileNames) {
        frames.clear();
        ImageRGBReader imageRGBReader = new ImageRGBReader();
        for (String fileName: fileNames) {
            ImageFrame frame = imageRGBReader.readToFrame(fileName);
            frames.add(frame);
        }
    }

    public void PrepareForRendering(){
        images.clear();
        ImageRGBReader imageRGBReader = new ImageRGBReader();
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
        timer.scheduleAtFixedRate(task, 0, 42);
    }
}
