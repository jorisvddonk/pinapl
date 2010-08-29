/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpinapl.core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Bitmap {
    public BufferedImage image;

    public Bitmap(File file) throws IOException {
        //System.err.println("Loading image: " + location);
        image = ImageIO.read(file);
        //ImageIO.write(image, "png", new File("C:\\loltest.png"));
    }

    public void setPixel(int x, int y, int rgb) {
        image.setRGB(x, y, rgb);
    }

    public Color getPixel(int x, int y) {
        //int[] bla = new int[4];
        
        //System.err.println("GETPIXELZ " + bla[0] + "," + bla[1] + "," + bla[2] + "," +  bla[3]);
        int clr = image.getRGB(x, y);
        Color tc = new Color(clr);
        //System.err.println("getPixel hook: " + clr + " --- " + tc);
        return tc;
    }

    public void setPixel(int x, int y, Color clr) {
        setPixel(x, y, clr.getRGB());
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int getWidth() {
        return image.getWidth();
    }


//    public void test() {
//        System.out.println("TESTING");
//        for (int y = 0; y < image.getHeight(); y++) {
//            System.out.println("row: " + y);
//            for (int x = 0; x < image.getWidth(); x++) {
//                int clr = image.getRGB(x, y);
//                System.out.println(clr + " -- " + new Color(clr));
//            }
//        }
//        System.out.println("END OF TEST");
//    }
 
}
