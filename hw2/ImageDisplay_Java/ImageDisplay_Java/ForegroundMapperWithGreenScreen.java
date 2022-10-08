import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ForegroundMapperWithGreenScreen {
    VideoDisplayHandler video;
    int width = 640; // default image width and height
    int height = 480;
    int frames = 480;
    // 0 = background, 1 = foreground, 2 = blur
    byte[][] bitmap;

    ForegroundMapperWithGreenScreen(VideoDisplayHandler external_video) {
        bitmap = new byte[frames][width*height];
        video = external_video;
    }

    private boolean isPixelGreenEnough(int r, int g, int b){
        ColorConverter colorConverter = new ColorConverter();
        double[] hsl = colorConverter.rgbToHSL(r, g, b);
        return hsl[0] >= 60 && hsl[0] <= 170 && hsl[1] > 0.27 && hsl[2] >= 0.10 && hsl[2] <= 0.8;
    }

    void maskFrames() {
        int frame_index = 0;
        for (ImageFrame frame : video.frames) {
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for(int x = 0; x < width; x++) {
                    int index = row_loc + x;
                    int r_int = frame.get_r(index);
                    int g_int = frame.get_g(index);
                    int b_int = frame.get_b(index);

                    if (isPixelGreenEnough(r_int, g_int, b_int)) {
                        // Mask out frame
                        bitmap[frame_index][index] = 0;
                    } else {
                        bitmap[frame_index][index] = 1;
                    }
                }
            }
            frame_index++;
        }
    }

    int[] generateAdjacentPixels(int index) {
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
        directions = Arrays.stream(directions).filter(x -> (x >= 0) && (x < height*width)).toArray();
        return directions;
    }

    void detectEdges() {
        for (int z = 0; z < frames; z++) {
            for(int y = 0; y < height; y++) {
                int row_loc = y * width;
                for (int x = 0; x < width; x++) {
                    int index = row_loc + x;
                    // Only want to blur foreground, ignore background pixels
                    if (bitmap[z][index] == 0){
                        continue;
                    }
                    for (int direction : generateAdjacentPixels(index)) {
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
                        video.frames.get(frame_index).rgb_set(index, r, g, b);
                    } else if (bitmap[frame_index][index] == 2) {
                        r = (r + video.frames.get(frame_index).get_r(index)) / 2;
                        g = (g + video.frames.get(frame_index).get_g(index)) / 2;
                        b = (b + video.frames.get(frame_index).get_b(index)) / 2;
//                        r /= 2;
//                        g /= 2;
//                        b /= 2;
                        video.frames.get(frame_index).rgb_set(index, r, g, b);
                    }
                }
            }
            frame_index++;
        }
    }
}
