import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Encoder {
    // Takes in a frame and current width and returns an encoded frame size width/2
    ImageFrame encode(ImageFrame frame) {
        int currentWidth = frame.width;
        int newWidth = currentWidth / 2;
        ImageFrame rowEncoded = new ImageFrame(newWidth, currentWidth);
        ImageFrame encodedFrame = new ImageFrame(newWidth, newWidth);
        // Get row encoded
        for(int y = 0; y < currentWidth; y++)
        {
            int new_row_loc = y * newWidth;
            int old_row_loc = y * currentWidth;
            for(int x = 0; x < newWidth; x++) {
                int enc_ind = new_row_loc + x;
                int frame_ind1 = old_row_loc + 2 * x;
                int frame_ind2 = frame_ind1 + 1;
                int r = (frame.get_r(frame_ind1) + frame.get_r(frame_ind2)) / 2;
                int g = (frame.get_g(frame_ind1) + frame.get_g(frame_ind2)) / 2;
                int b = (frame.get_b(frame_ind1) + frame.get_b(frame_ind2)) / 2;
                rowEncoded.rgb_set(enc_ind, r, g, b);
            }
        }
        // Get column encoded from row encoded
        for(int y = 0; y < newWidth; y++)
        {
            int row_loc = y * newWidth;
            for(int x = 0; x < newWidth; x++) {
                int enc_ind = row_loc + x;
                int frame_ind1 = 2 * row_loc + x;
                int frame_ind2 = frame_ind1 + newWidth;
                int r = (rowEncoded.get_r(frame_ind1) + rowEncoded.get_r(frame_ind2)) / 2;
                int g = (rowEncoded.get_g(frame_ind1) + rowEncoded.get_g(frame_ind2)) / 2;
                int b = (rowEncoded.get_b(frame_ind1) + rowEncoded.get_b(frame_ind2)) / 2;
                encodedFrame.rgb_set(enc_ind, r, g, b);
            }
        }
        return encodedFrame;
    }

    ImageFrame decode(ImageFrame frame) {
        int currentWidth = frame.width;
        int newWidth = currentWidth * 2;
        ImageFrame decodedFrame = new ImageFrame(newWidth, newWidth);
        for(int y = 0; y < currentWidth; y++) {
            int row_loc = y * currentWidth;
            int new_row_loc = y * newWidth;
            for (int x = 0; x < currentWidth; x++) {
                int dec_ind = row_loc + x;
                int mapped_pix1 = 2 * new_row_loc + 2 * x;
                int mapped_pix2 = mapped_pix1 + 1;
                int mapped_pix3 = mapped_pix1 + newWidth;
                int mapped_pix4 = mapped_pix3 + 1;
                int r = frame.get_r(dec_ind);
                int g = frame.get_g(dec_ind);
                int b = frame.get_b(dec_ind);
                decodedFrame.rgb_set(mapped_pix1, r, g, b);
                decodedFrame.rgb_set(mapped_pix2, r, g, b);
                decodedFrame.rgb_set(mapped_pix3, r, g, b);
                decodedFrame.rgb_set(mapped_pix4, r, g, b);
            }
        }
        return decodedFrame;
    }

    ArrayList<ImageFrame> encodeLayers(ImageFrame frame){
        // Arraylist of image frames starting from no encoding to fully encoded
        ArrayList<ImageFrame> images = new ArrayList<ImageFrame>();
        images.add(frame);
        ImageFrame processed_frame = new ImageFrame(frame.width, frame.height);
        processed_frame.copy(frame);
        for (int i = 0; i < 9; i++) {
            processed_frame = encode(processed_frame);
            ImageFrame decoded_frame = decode(processed_frame);
            for (int j = 0; j < i; j++) {
                decoded_frame = decode(decoded_frame);
            }
            images.add(decoded_frame);
        }
        return images;
    }

}
