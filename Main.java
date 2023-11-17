import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class Main {
    static BufferedImage img = null;
    static final float[] SOBEL_X = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
    static final float[] SOBEL_Y = {-1, -2, -1, 0, 0, 0, 1, 2, 1};

    public static void main(String[] args) throws Exception {
        try {
            img = ImageIO.read(new File("image.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        img = convertToGrayscale(img);
        img = applySobel(img);
        applyHoughTransform(img);
        guiRun();
    }

    private static BufferedImage applySobel(BufferedImage img) throws IOException {
        BufferedImageOp sobel_x_op = new ConvolveOp(new Kernel(3, 3, SOBEL_X));
        BufferedImageOp sobel_y_op = new ConvolveOp(new Kernel(3, 3, SOBEL_Y));
        BufferedImage sobel_x_img = sobel_x_op.filter(img, null);
        BufferedImage sobel_y_img = sobel_y_op.filter(img, null);
        File outputfile = new File("outputX.jpg");
        ImageIO.write(sobel_x_img, "jpg", outputfile);
        File outputfile2 = new File("outputY.jpg");
        ImageIO.write(sobel_y_img, "jpg", outputfile2);
        File outputfile3 = new File("output.jpg");
        BufferedImage finalSobel = combineSobel(sobel_x_img, sobel_y_img);
        ImageIO.write(finalSobel, "jpg", outputfile3);
        return finalSobel;
    }

    private static BufferedImage combineSobel(BufferedImage sobel_x_img, BufferedImage sobel_y_img) {
        int width = sobel_x_img.getWidth();
        int height = sobel_x_img.getHeight();
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int threshold = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int sobel_x = sobel_x_img.getRGB(x, y) & 0xff;
                int sobel_y = sobel_y_img.getRGB(x, y) & 0xff;
                int sobel = (int) Math.sqrt(sobel_x * sobel_x + sobel_y * sobel_y);
                if (sobel > threshold) {
                    combined.setRGB(x, y, sobel << 32 | sobel << 16 | sobel << 8 | sobel);
                } else {
                    combined.setRGB(x, y, 0);
                }
            }
        }
        return combined;
    }

    private static BufferedImage applyHoughTransform(BufferedImage image) {
        int[][] accumulator = houghTransform(image);

        int threshold = 9000;

        return drawCircles(image, accumulator, threshold);
    }

    private static int[][] houghTransform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxRadius = Math.min(width, height) / 2;
    
        int[][] accumulator = new int[width][height];
    
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int edgePixel = (image.getRGB(x, y) & 0xff) > 0 ? 1 : 0;
                System.out.println(edgePixel);
                if (edgePixel == 1) {
                    for (int r = 1; r <= maxRadius; r++) {
                        for (int theta = 0; theta < 360; theta++) {
                            int a = x - (int) (r * Math.cos(Math.toRadians(theta)));
                            int b = y - (int) (r * Math.sin(Math.toRadians(theta)));
    
                            if (a >= 0 && a < width && b >= 0 && b < height) {
                                accumulator[a][b]++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("where am i");
        return accumulator;
    }

    private static BufferedImage drawCircles(BufferedImage image, int[][] accumulator, int threshold) {
        int width = image.getWidth();
        int height = image.getHeight();
    
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
        int maxAccumulatorValue = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (accumulator[x][y] > maxAccumulatorValue) {
                    maxAccumulatorValue = accumulator[x][y];
                }
            }
        }
    
        Graphics g = resultImage.getGraphics();
        g.drawImage(image, 0, 0, null);
    
        g.setColor(Color.RED);
    
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (accumulator[x][y] > threshold) {
                    int colorValue = (int) (255.0 * accumulator[x][y] / maxAccumulatorValue);
    
                    int circleRadius = 5;
                    g.drawOval(x - circleRadius, y - circleRadius, 2 * circleRadius, 2 * circleRadius);
                }
            }
        }
        img = resultImage;
        return resultImage;
    }
    

    private static void guiRun() {
        JFrame f = new JFrame("Detector 3031 VER 1.2");
        f.addWindowListener(new WindowAdapter() {
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