import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ij.process.ImageProcessor;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.gui.ImageWindow;
/**
 *
 * @author Laura
 */
public class Histogram_Equalization_Plugin implements PlugInFilter{
    ImagePlus imp;

    @Override
    public int setup(String string, ImagePlus ip) {
        this.imp = ip;
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        BufferedImage img = ip.getBufferedImage();
        
        int red;
        int green;
        int blue;
        int alpha;
        int newPixel = 0;
 
        // Get the Lookup table for histogram equalization
        ArrayList<int[]> histLUT = histogramEqualizationLUT(img);
        BufferedImage histogramEQ = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
 
    for(int i=0; i<img.getWidth(); i++) {
        for(int j=0; j<img.getHeight(); j++) {
 
            // Get pixels by R, G, B
            alpha = new Color(img.getRGB (i, j)).getAlpha();
            red = new Color(img.getRGB (i, j)).getRed();
            green = new Color(img.getRGB (i, j)).getGreen();
            blue = new Color(img.getRGB (i, j)).getBlue();
 
            // Set new pixel values using the histogram lookup table
            red = histLUT.get(0)[red];
            green = histLUT.get(1)[green];
            blue = histLUT.get(2)[blue];
 
            // Return back to original format
            //newPixel = colorToRGB(alpha, red, green, blue);
            Color np = new Color(red, green, blue, alpha);
            newPixel = np.getRGB();
             
            // Write pixels into image
            histogramEQ.setRGB(i, j, newPixel);
        }
    }
    ImagePlus res = new ImagePlus("Histogram results", histogramEQ);
    ImageWindow resultsWindow = new ImageWindow(res);
    }
    // Return an ArrayList containing histogram values for separate R, G, B channels
public static ArrayList<int[]> imageHistogram(BufferedImage img) {
    //compute histogram of image
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];
 
        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
        for(int i=0; i<img.getWidth(); i++) {
            for(int j=0; j<img.getHeight(); j++) {
 
                int red = new Color(img.getRGB (i, j)).getRed();
                int green = new Color(img.getRGB (i, j)).getGreen();
                int blue = new Color(img.getRGB (i, j)).getBlue();
 
                // Increase the values of colors
                rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;
 
            }
        }
 
        ArrayList<int[]> hist = new ArrayList<int[]>();
        hist.add(rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);
        
        return hist;
    }

// Get the histogram equalization lookup table for separate R, G, B channels
private static ArrayList<int[]> histogramEqualizationLUT(BufferedImage input) {
 
    // Get an image histogram - calculated values by R, G, B channels
    ArrayList<int[]> imageHist = imageHistogram(input);
 
    // Create the lookup table
    ArrayList<int[]> imageLUT = new ArrayList<int[]>();
 
    // Fill the lookup table
    int[] rhistogram = new int[256];
    int[] ghistogram = new int[256];
    int[] bhistogram = new int[256];
 
    for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
    for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
    for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
    long sumr = 0;
    long sumg = 0;
    long sumb = 0;
 
    // Calculate the scale factor
    float scale_factor = (float) (255.0 / (input.getWidth() * input.getHeight()));
 
    for(int i=0; i<rhistogram.length; i++) {
        sumr += imageHist.get(0)[i];
        int valr = (int) (sumr * scale_factor);
        if(valr > 255) {
            rhistogram[i] = 255;
        }
        else rhistogram[i] = valr;
 
        sumg += imageHist.get(1)[i];
        int valg = (int) (sumg * scale_factor);
        if(valg > 255) {
            ghistogram[i] = 255;
        }
        else ghistogram[i] = valg;
 
        sumb += imageHist.get(2)[i];
        int valb = (int) (sumb * scale_factor);
        if(valb > 255) {
            bhistogram[i] = 255;
        }
        else bhistogram[i] = valb;
    }
 
    imageLUT.add(rhistogram);
    imageLUT.add(ghistogram);
    imageLUT.add(bhistogram);
 
    return imageLUT;
 
}
}
