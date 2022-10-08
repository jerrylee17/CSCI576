
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame1;
	JFrame frame2;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage imgOne;

	BufferedImage imgTwo;

	int width = 1920; // default image width and height
	int height = 1080;
	static int y_value;
	static int u_value;
	static int v_value;
	static double sw_value;
	static double sh_value;
	static int a_value;

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGBOriginal(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
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
				for(int x = 0; x < width; x++)
				{
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
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
	}

	private static double[] ConvertRGBtoYUV(int r, int g, int b) {
		// Run on a single pixel
		// [Y, U, V]
		double[] YUV_values = new double[3];
		YUV_values[0] = 0.299*r + 0.587*g + 0.114*b;
		YUV_values[1] = 0.596*r - 0.274*g - 0.322*b;
		YUV_values[2] = 0.211*r - 0.523*g + 0.312*b;
		return YUV_values;
	}

	private static int[] ConvertYUVtoRGB(double y, double u, double v) {
		// Run on a single pixel
		// [Y, U, V]
		int[] RGB_values = new int[3];
		RGB_values[0] = (int)Math.rint(1.000*y + 0.956*u + 0.621*v);
		RGB_values[1] = (int)Math.rint(1.000*y - 0.272*u - 0.647*v);
		RGB_values[2] = (int)Math.rint(1.000*y - 1.106*u + 1.703*v);
		return RGB_values;
	}

	private void readImageRGBProcessed(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			// each pixel = 3* pixel location in order of y,u,v
			double yuv_img[] = new double[3*height*width];
			// each pixel = 3*pixel location in order of r,g,b
			int rgb_processed[] = new int[3*height*width];

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];
			raf.read(bytes);

			// Part 2 - Convert to YUV space
			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					int pixel_loc = 3*(y*width + x);

					byte r_byte = bytes[ind];
					byte g_byte = bytes[ind+height*width];
					byte b_byte = bytes[ind+height*width*2];

					// Convert from byte to int
					int r = Byte.toUnsignedInt(r_byte);
					int g = Byte.toUnsignedInt(g_byte);
					int b = Byte.toUnsignedInt(b_byte);
					// Calculate changed yuv values
					double[] yuv_values = ConvertRGBtoYUV(r, g, b);
					yuv_img[pixel_loc] = yuv_values[0];
					yuv_img[pixel_loc + 1] = yuv_values[1];
					yuv_img[pixel_loc + 2] = yuv_values[2];

					ind++;
				}
			}

			// Part 3 + Part 4 - Process YUV subsampling, Adjust up sampling for display
			for (int y = 0; y < height; y++) {
				int row_start = y*width;
				// y value subsampled
				if (y_value > 1) {
					int rightmost_pixel = row_start + Math.floorDiv(width, y_value);
					// Subsample each yth pixel
					for (int x = 0; x < width - y_value; x += y_value) {
						// fill in pixels between x to x + y_value with average of x and x + y_value
						for (int z = x + 1; z < x + y_value; z++) {
							double left_value = yuv_img[3 * (row_start + x)];
							double right_value = yuv_img[3 * (row_start + x + y_value)];
							yuv_img[3 * (row_start + z)] = (left_value + right_value) / 2;
						}
					}
					// Edge pixels
					for (int x = width - y_value + 1; x < width; x++) {
						yuv_img[3 * (row_start + x)] = yuv_img[3*(rightmost_pixel)];
					}
				}
				// u value subsampled
				if (u_value > 1) {
					int rightmost_pixel = row_start + Math.floorDiv(width, u_value);
					// Subsample each yth pixel
					for (int x = 0; x < width - u_value; x+=u_value) {
						// fill in pixels between x to x + u_value with average of x and x+u_value
						for (int z=x+1; z < x+u_value; z++) {
							double left_value = yuv_img[3 * (row_start + x) + 1];
							double right_value = yuv_img[3 * (row_start + x + u_value) + 1];
							yuv_img[3 * (row_start + z) + 1] = (left_value + right_value) / 2;
						}
					}
					// Edge pixels
					for (int x = width - u_value + 1; x < width; x++) {
						yuv_img[3 * (row_start + x) + 1] = yuv_img[3*(rightmost_pixel) + 1];
					}
				}
				// v value subsampled
				if (v_value > 1) {
					int rightmost_pixel = row_start + Math.floorDiv(width, v_value);
					// Subsample each yth pixel
					for (int x = 0; x < width - v_value; x+=v_value) {
						// fill in pixels between x to x + v_value with average of x and x+v_value
						for (int z=x+1; z < x+v_value; z++) {
							double left_value = yuv_img[3 * (row_start + x) + 2];
							double right_value = yuv_img[3 * (row_start + x + v_value) + 2];
							yuv_img[3 * (row_start + z) + 2] = (left_value + right_value) / 2;
						}
					}
					// Edge pixels
					for (int x = width - v_value + 1; x < width; x++) {
						yuv_img[3 * (row_start + x) + 2] = yuv_img[3*(rightmost_pixel) + 2];
					}
				}
			}

			// Part 5 - Convert back to RGB space
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					int pixel_loc = 3*(y*width + x);

					// Calculate changed yuv values
					int[] rgb_values = ConvertYUVtoRGB(yuv_img[pixel_loc], yuv_img[pixel_loc + 1], yuv_img[pixel_loc + 2]);
					int r_int = rgb_values[0];
					int g_int = rgb_values[1];
					int b_int = rgb_values[2];

					// Clamp
					r_int = Math.max(Math.min(255, r_int), 0);
					g_int = Math.max(Math.min(255, g_int), 0);
					b_int = Math.max(Math.min(255, b_int), 0);

					rgb_processed[pixel_loc] = r_int;
					rgb_processed[pixel_loc + 1] = g_int;
					rgb_processed[pixel_loc + 2] = b_int;
				}
			}

			// Part 6 - Scaling with antialiasing
			int new_height = (int)(sh_value * height);
			int new_width = (int)(sw_value * width);
			int scaled[] = new int[3*new_height*new_width];

			for(int y = 0; y < new_height; y++)
			{
				// Current row on the original
				int current_row = width * (int)(y/sh_value);
				for(int x = 0; x < new_width; x++)
				{
					int new_pixel_loc = 3 * (y*new_width + x);
					int mapped_pixel_loc = 3 * (int)(current_row + (x/sw_value));

					// No antialiasing
					if (a_value == 0){
						scaled[new_pixel_loc] = rgb_processed[mapped_pixel_loc];
						scaled[new_pixel_loc + 1] = rgb_processed[mapped_pixel_loc + 1];
						scaled[new_pixel_loc + 2] = rgb_processed[mapped_pixel_loc + 2];
					}
					// Antialiasing
					else {
						int vertical_mov = 3*width;
						int horizontal_mov = 3;
						int mov_array[][] = {
								{-vertical_mov, -horizontal_mov},
								{-vertical_mov, 0},
								{-vertical_mov, horizontal_mov},
								{0, -horizontal_mov},
								{0, horizontal_mov},
								{vertical_mov, -horizontal_mov},
								{vertical_mov, 0},
								{vertical_mov, horizontal_mov}
						};

						int r_sub = rgb_processed[mapped_pixel_loc];
						int g_sub = rgb_processed[mapped_pixel_loc + 1];
						int b_sub = rgb_processed[mapped_pixel_loc + 2];
						int valid_pos = 1;
						for (int i = 0; i < 8; i++) {
							int x_mov = mov_array[i][0];
							int y_mov = mov_array[i][1];
							int pixel_loc = mapped_pixel_loc + x_mov + y_mov;
							if (pixel_loc < 0) continue;
							if (pixel_loc >= rgb_processed.length) {
								break;
							}
							r_sub += rgb_processed[pixel_loc];
							g_sub += rgb_processed[pixel_loc + 1];
							b_sub += rgb_processed[pixel_loc + 2];
							valid_pos++;
						}
						scaled[new_pixel_loc] = r_sub / valid_pos;
						scaled[new_pixel_loc + 1] = g_sub / valid_pos;
						scaled[new_pixel_loc + 2] = b_sub / valid_pos;
					}

				}
			}


			// Displaying the image
			for(int y = 0; y < new_height; y++)
			{
				for(int x = 0; x < new_width; x++)
				{
					int pixel_loc = 3*(y*new_width + x);

					byte r = (byte)(scaled[pixel_loc]);
					byte g = (byte)(scaled[pixel_loc + 1]);
					byte b = (byte)(scaled[pixel_loc + 2]);

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(x, y, pix);
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
	}
	public void showIms(String[] args){

		// Read parameters from command line
		y_value = Integer.parseInt(args[1]);
		u_value = Integer.parseInt(args[2]);
		v_value = Integer.parseInt(args[3]);
		sw_value = Double.parseDouble(args[4]);
		sh_value = Double.parseDouble(args[5]);
		a_value = Integer.parseInt(args[6]);

		// Check args
		if (sw_value < 0 || sw_value > 1){
			System.out.println("Enter valid SW value please!");
		}
		if (sh_value < 0 || sh_value > 1){
			System.out.println("Enter valid SH value please!");
		}

		// Read and set the original
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGBOriginal(width, height, args[0], imgOne);

		// Use label to display the image
		frame1 = new JFrame();
		GridBagLayout gLayout1 = new GridBagLayout();
		frame1.getContentPane().setLayout(gLayout1);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame1.getContentPane().add(lbIm1, c);

		frame1.pack();
		frame1.setVisible(true);

		// PROCESSED IMAGE

		// Read and set the processed image
		imgTwo = new BufferedImage((int)(width*sw_value), (int)(height*sh_value), BufferedImage.TYPE_INT_RGB);
		readImageRGBProcessed(width, height, args[0], imgTwo);

		frame2 = new JFrame();
		GridBagLayout gLayout2 = new GridBagLayout();
		frame2.getContentPane().setLayout(gLayout2);

		lbIm2 = new JLabel(new ImageIcon(imgTwo));

		GridBagConstraints c2 = new GridBagConstraints();
		c2.anchor = GridBagConstraints.CENTER;
		c2.weightx = 0.5;

		c2.fill = GridBagConstraints.HORIZONTAL;

		c2.gridx = 0;
		c2.gridy = 1;
		frame2.getContentPane().add(lbIm2, c);

		frame2.pack();
		frame2.setVisible(true);
	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}
}
