import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;

public class Hw2Programming {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 640; // default image width and height
	int height = 480;

	public void showIms(String[] args){
		VideoDisplayHandler foreground = new VideoDisplayHandler(args[0]);
		VideoDisplayHandler background = new VideoDisplayHandler(args[1]);
		int mode = Integer.parseInt(args[2]);
		if (mode == 0) {
			ForegroundMapperWithoutGreenScreen
					foregroundMapperWithoutGreenScreen = new ForegroundMapperWithoutGreenScreen(foreground);
			foregroundMapperWithoutGreenScreen.prepareForMask();
			foregroundMapperWithoutGreenScreen.maskFrames();
			foregroundMapperWithoutGreenScreen.removeNoise(3);
			foregroundMapperWithoutGreenScreen.removeNoise(2);
			foregroundMapperWithoutGreenScreen.detectEdges();
			foregroundMapperWithoutGreenScreen.overlayVideo(background);
		} else {
			ForegroundMapperWithGreenScreen
					foregroundMapperWithGreenScreen = new ForegroundMapperWithGreenScreen(foreground);
			foregroundMapperWithGreenScreen.maskFrames();
			foregroundMapperWithGreenScreen.detectEdges();
			foregroundMapperWithGreenScreen.overlayVideo(background);
		}

		foreground.PrepareForRendering();
		foreground.DisplayVideo();
	}

	public static void main(String[] args) {
		Hw2Programming ren = new Hw2Programming();
		ren.showIms(args);
	}

}
