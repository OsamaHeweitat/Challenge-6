import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class Main{
    static BufferedImage img = null;
    static final float[] SOBEL_X = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
    static final float[] SOBEL_Y = {-1, -2, -1, 0, 0, 0, 1, 2, 1};

    public static void main(String[] args) throws Exception{
        try {
            img = ImageIO.read(new File("image.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        img = convertToGrayscale(img);
        applySobel(img);
        guiRun();
    }

    private static void applySobel(BufferedImage img) throws IOException{
        BufferedImageOp sobel_x_op = new ConvolveOp(new Kernel(3, 3, SOBEL_X));
        BufferedImageOp sobel_y_op = new ConvolveOp(new Kernel(3, 3, SOBEL_Y));
        BufferedImage sobel_x_img = sobel_x_op.filter(img, null);
        BufferedImage sobel_y_img = sobel_y_op.filter(img, null);
        File outputfile = new File("outputX.jpg");
        ImageIO.write(sobel_x_img, "jpg", outputfile);
        File outputfile2 = new File("outputY.jpg");
        ImageIO.write(sobel_y_img, "jpg", outputfile2);
    }

    private static void guiRun() {
        JFrame f = new JFrame("Detector 3031 VER 1.2");
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JLabel label = new JLabel(new ImageIcon(img));
        f.add(label);
        f.pack();
        f.setVisible(true);
    }

    public static BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage result = new BufferedImage(
                  image.getWidth(),
                  image.getHeight(),
                  BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }
}