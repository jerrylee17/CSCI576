import java.awt.*;
import java.awt.image.*;
import java.util.TimerTask;
import javax.swing.*;

// Tales a buffered image and displays it
public class ImageDisplayHandler {
    JFrame jFrame;
    JLabel label;
    BufferedImage img;
    ImageDisplayHandler() {
        jFrame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        jFrame.getContentPane().setLayout(gLayout);
        label = new JLabel(new ImageIcon());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        jFrame.getContentPane().add(label, c);
        jFrame.setVisible(true);
    }

    public void SetImage(BufferedImage external_image) {
        img = external_image;
    }

    public void DisplayImage() {
        label.setIcon(new ImageIcon(img));

        jFrame.pack();
    }

    public void SetAndDisplayImage(BufferedImage external_image) {
        img = external_image;
        label.setIcon(new ImageIcon(img));

        jFrame.pack();
    }
}
