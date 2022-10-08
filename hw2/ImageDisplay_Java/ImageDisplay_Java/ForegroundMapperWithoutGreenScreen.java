import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class ForegroundMapperWithoutGreenScreen {
    VideoDisplayHandler video;
    int width = 640; // default image width and height
    int height = 480;
    int num_frames = 480;
    // 0 = background, 1 = foreground, 2 = blur
    byte[][] bitmap;

    double[][] averagePixelHSL;

    ForegroundMapperWithoutGreenScreen(VideoDisplayHandler external_video) {
        bitmap = new byte[num_frames][width*height];
        averagePixelHSL = new double[width*height][3];
        video = external_video;
    }

    private static boolean isThresholdExceedConjunction(double[] diff, double[] threshold) {
        if (
            diff[0] > threshold[0] &&
            diff[1] > threshold[1] &&
            diff[2] > threshold[2]) {
            return true;
        }
        return false;
    }

    private static boolean isThresholdExceedDisjunction(double[] diff, double[] threshold) {
        if (
                diff[0] > threshold[0] ||
                        diff[1] > threshold[1] ||
                        diff[2] > threshold[2]) {
            return true;
        }
        return false;
    }

    private void generateAveragePixelValueHSL() {
        ColorConverter colorConverter = new ColorConverter();
        for (int i = 0; i < num_frames; i++) {
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for (int x = 0; x < width; x++) {
                    int index = row_loc + x;
                    ImageFrame current_frame = video.frames.get(i);
                    int r = current_frame.get_r(index);
                    int g = current_frame.get_g(index);
                    int b = current_frame.get_b(index);
                    double[] hsl = colorConverter.rgbToHSL(r, g, b);

                    // Calculate average HSL
                    averagePixelHSL[index][0] += hsl[0] / num_frames;
                    averagePixelHSL[index][1] += hsl[1] / num_frames;
                    averagePixelHSL[index][2] += hsl[2] / num_frames;
                }
            }
        }
    }

    boolean didPixelMoveAverageHSL(int frame_index, int index) {
        ColorConverter colorConverter = new ColorConverter();
        ImageFrame current_frame = video.frames.get(frame_index);
        int r = current_frame.get_r(index);
        int g = current_frame.get_g(index);
        int b = current_frame.get_b(index);
        double[] hsl = colorConverter.rgbToHSL(r, g, b);

        double hsl_diff[] = {
                Math.abs(averagePixelHSL[index][0] - hsl[0]),
                Math.abs(averagePixelHSL[index][1] - hsl[1]),
                Math.abs(averagePixelHSL[index][2] - hsl[2])
        };

        double hsl_threshold[] = {10,0.05,0.05};

        if (isThresholdExceedConjunction(hsl_diff, hsl_threshold)) {
            return true;
        }

        return false;
    }

    boolean didPixelMoveHSL(int frame_index, int index) {
        // Beginning of scanning index
        int start_index = Math.max(0, frame_index - 5);
        int end_index = Math.min(frame_index + 3, num_frames - 1);
        int scan_length = end_index - start_index + 1;
        double average_pixel_hsl[] = {0,0,0};
        ColorConverter colorConverter = new ColorConverter();
        // Collect average color
        for (int i = start_index; i <= end_index; i++) {
            ImageFrame current_frame = video.frames.get(i);
            int r = current_frame.get_r(index);
            int g = current_frame.get_g(index);
            int b = current_frame.get_b(index);

            double[] hsl = colorConverter.rgbToHSL(r, g, b);

            // Calculate average HSL
            average_pixel_hsl[0] += hsl[0] / scan_length;
            average_pixel_hsl[1] += hsl[1] / scan_length;
            average_pixel_hsl[2] += hsl[2] / scan_length;
        }
        ImageFrame current_frame = video.frames.get(frame_index);
        int r = current_frame.get_r(index);
        int g = current_frame.get_g(index);
        int b = current_frame.get_b(index);
        double[] hsl = colorConverter.rgbToHSL(r, g, b);

        double hsl_diff[] = {
                Math.abs(average_pixel_hsl[0] - hsl[0]),
                Math.abs(average_pixel_hsl[1] - hsl[1]),
                Math.abs(average_pixel_hsl[2] - hsl[2])
        };

        double hsl_threshold[] = {2,0.01,0.01};

        if (isThresholdExceedDisjunction(hsl_diff, hsl_threshold)) {
            return true;
        }

        return false;
    }

    boolean didPixelMoveRGB(int frame_index, int index) {
        // Beginning of scanning index
        int start_index = Math.max(0, frame_index - 5);
        int end_index = Math.min(frame_index + 2, num_frames - 1);
        int scan_length = end_index - start_index + 1;
        double average_pixel_rgb[] = {0,0,0};
        ColorConverter colorConverter = new ColorConverter();
        // Collect average color
        for (int i = start_index; i <= end_index; i++) {
            // Calculate average RGB
            ImageFrame current_frame = video.frames.get(i);
            int r = current_frame.get_r(index);
            int g = current_frame.get_g(index);
            int b = current_frame.get_b(index);

            average_pixel_rgb[0] += r / scan_length;
            average_pixel_rgb[1] += g / scan_length;
            average_pixel_rgb[2] += b / scan_length;
        }
        ImageFrame current_frame = video.frames.get(frame_index);
        int r = current_frame.get_r(index);
        int g = current_frame.get_g(index);
        int b = current_frame.get_b(index);
        // Actual filter
        double rgb_diff[] = {
                Math.abs(average_pixel_rgb[0] - r),
                Math.abs(average_pixel_rgb[1] - g),
                Math.abs(average_pixel_rgb[2] - b)
        };

        int rgb_value = 8;
        double rgb_threshold[] = {rgb_value,rgb_value,rgb_value};
        if (isThresholdExceedDisjunction(rgb_diff, rgb_threshold)) {
            return true;
        }

        return false;
    }
    void maskFrames() {
        int frame_index = 0;
        for (ImageFrame frame : video.frames) {
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for(int x = 0; x < width; x++) {
                    int index = row_loc + x;

                    if (
                            didPixelMoveAverageHSL(frame_index, index) &&
                            didPixelMoveHSL(frame_index, index)
                    ) {
                        // Keep moved pixels
                        bitmap[frame_index][index] = 1;
                    } else {
                        bitmap[frame_index][index] = 0;
                    }
                }
            }
            frame_index++;
        }
    }

    int[] directionGenerator(int index, int radius) {
        int[] directions = new int[radius*4];
        for (int i = 0; i < radius*4; i = i + 4) {
            int distance = (i / 4) + 1;
            directions[i] = index + distance * width;
            directions[i+1] = index - distance * width;
            directions[i+2] = index + distance;
            directions[i+3] = index - distance;
        }
        return directions;
    }

    void removeNoise(int radius) {
        for (int z = 0; z < num_frames; z++) {
            byte[] bitmap_cache = new byte[width*height];
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for (int x = 0; x < width; x++) {
                    int index = row_loc + x;
                    bitmap_cache[index] = bitmap[z][index];
                    // Setting background
//                    if (bitmap[z][index] == 0){
//                        continue;
//                    }
//                    int[] directions = {
//                            index - width, // up
//                            index + width, // down
//                            index - 1, // left
//                            index + 1, // right
//                            index - 2 * width, // up 2
//                            index + 2 * width, // down 2
//                            index - 2, // left 2
//                            index + 2, // right 2
//                    };
                    int[] directions = directionGenerator(index, radius);
                    int counter = 0;
                    if (bitmap[z][index] == 0) {
                        bitmap_cache[index] = 0;
                        for (int direction : generateAdjacentPixels(index, directions)) {
                            if (bitmap[z][direction] == 1) {
                                counter++;
                            }
                            // Change pixel to foreground
                            if (counter > radius * 2 - 2) {
                                bitmap_cache[index] = 1;
                                break;
                            }
                        }
                    } else {
                        bitmap_cache[index] = 1;
                        for (int direction : generateAdjacentPixels(index, directions)) {
                            if (bitmap[z][direction] == 0) {
                                counter++;
                            }
                            // Change pixel to background
                            if (counter > radius * 2 + 1) {
                                bitmap_cache[index] = 0;
                                break;
                            }
                        }
                    }
                }
            }
            bitmap[z] = bitmap_cache;
        }
    }

    int[] generateAdjacentPixels(int index, int[] directions) {

        directions = Arrays.stream(directions).filter(x -> (x >= 0) && (x < height*width)).toArray();
        return directions;
    }

    public void prepareForMask() {
        generateAveragePixelValueHSL();
    }

    // For applying blur
    void detectEdges() {
        for (int z = 0; z < num_frames; z++) {
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for (int x = 0; x < width; x++) {
                    int index = row_loc + x;
                    // Only want to blur foreground, ignore background pixels
                    if (bitmap[z][index] == 0){
                        continue;
                    }
                    int[] directions = {
                            index - width, // up
                            index + width, // down
                            index - 1, // left
                            index + 1, // right
                            index - 2 * width, // up 2
                            index + 2 * width, // down 2
                            index - 2, // left 2
                            index + 2, // right 2
                    };
                    for (int direction : generateAdjacentPixels(index, directions)) {
                        if (bitmap[z][direction] == 0) {
                            bitmap[z][index] = 2;
                            break;
                        }
                    }
                }
            }
        }
    }

    void overlayVideo(VideoDisplayHandler background) {
        int frame_index = 0;
        for (ImageFrame frame : video.frames) {
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for(int x = 0; x < width; x++) {
                    int index = row_loc + x;
                    int r = background.frames.get(frame_index).get_r(index);
                    int g = background.frames.get(frame_index).get_g(index);
                    int b = background.frames.get(frame_index).get_b(index);
                    if (bitmap[frame_index][index] == 0) {
                        // CHANGE THIS BACK LATER
                        video.frames.get(frame_index).rgb_set(index, r,g,b);
                    }
                }
            }
            frame_index++;
        }
    }
}
