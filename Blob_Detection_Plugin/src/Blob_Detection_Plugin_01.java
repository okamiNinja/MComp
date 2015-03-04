import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

import ij.process.ImageProcessor;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.IJ;
import ij.gui.ImageWindow;

/**
 *
 * @author Ben Cook and Laura Goold
 */


public class Blob_Detection_Plugin_01 implements PlugInFilter{
    ImagePlus imp;
    
    public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}
    
    /**
     * Main work done in the plugin
     * @param ip image to work on.
     */
    public void run(ImageProcessor ip) {
        BufferedImage img = ip.getBufferedImage();
        
        try {
            File outputfile = new File("C:/Users/Laura/Documents/Year4/MComp/newImg.png");
            if(!ImageIO.write(img, "png", outputfile)){
                System.out.println("ha");
            } else { 
                System.out.println("Save Success");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        
        /*int redMin = 0; int redMax = 10;
        int greenMin = 15; int greenMax = 161;
        int blueMin = 255; int blueMax = 255;*/
        
        int redMin = 50; int redMax = 53;
        int greenMin = 65; int greenMax = 70;
        int blueMin = 240; int blueMax = 255;
        
        Color[][] colours = new Color[img.getHeight()][img.getWidth()];
        int[][] blobVals = new int[img.getHeight()][img.getWidth()];
        int curBlobCount = 0;
        ArrayList<Integer> blobConnectionList = new ArrayList<Integer>();
        
        
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                colours[i][j] = new Color(img.getRGB(j, i));
            }
        }
        
        System.out.print("Searching between: " + redMin + ", " + greenMin + ", " + blueMin);
        System.out.println(" and " + redMax + ", " + greenMax + ", " + blueMax);
        IJ.showMessage("Searching between: " + redMin + ", " + greenMin + ", " + blueMin
                        + " and " + redMax + ", " + greenMax + ", " + blueMax);
        
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                
                if(colours[i][j].getRed() >= redMin && colours[i][j].getRed() <= redMax
                        && colours[i][j].getGreen() >= greenMin && colours[i][j].getGreen() <= greenMax
                        && colours[i][j].getBlue() >= blueMin && colours[i][j].getBlue() <= blueMax){
                    int[] surrounding = new int[4];
                    int x = img.getWidth();
                    surrounding[0] = j > 0 ? blobVals[i][j-1]: 0;      //Left
                    surrounding[1] = (i > 0 && j > 0) ? blobVals[i-1][j-1] : 0;    //Top-Left
                    surrounding[2] = i > 0 ? blobVals[i-1][j] : 0;      //Top
                    surrounding[3] = (i > 0 && j < img.getWidth()-1) ? blobVals[i-1][j+1] : 0;    //Top-Right
                    
                    
                    for(int k = 0; k < surrounding.length; k++){
                        if(surrounding[k] != 0){
                            surrounding[k] = findBottom(blobConnectionList, surrounding[k]);
                        }
                    }
                    
                    
                    
                    if(i == 333 && j >= 437){
                            int k = 4;
                        }
                    int foundID = findMinNZ(surrounding);
                    if(foundID == 0){
                        blobVals[i][j] = ++curBlobCount;
                        if(curBlobCount == 129){
                            int k = 4;
                        }
                        blobConnectionList.add(curBlobCount);
                    } else {
                        blobVals[i][j] = foundID;
                        
                        for(int pixel : surrounding){
                            if(pixel > 0 && pixel != foundID){
                                blobConnectionList.set(pixel-1, foundID);
                            }
                        }
                    }
                    
                } else {
                    blobVals[i][j] = 0;
                }
            }
        }
        
        ArrayList<Integer> bottoms = new ArrayList<Integer>();
        
        for(int i = 0; i < blobConnectionList.size(); i++){
            int bottom = findBottom(blobConnectionList, blobConnectionList.get(i));
            bottoms.add(bottom);
        }
        
        
        List<Integer> list2 = new ArrayList<Integer>();
        HashSet<Integer> lookup = new HashSet<Integer>();
        for (int item : bottoms) {
            if (lookup.add(item)) {
                // Set.add returns false if item is already in the set
                list2.add(item);
            }
        }
        List<Integer> list = list2;
        System.out.println(list2);
        
        int blobsFound = 0;
        ArrayList<Tuple<Integer, Integer>> associations = new ArrayList<Tuple<Integer, Integer>>();
        ArrayList<ArrayList<Integer>> includeList = new ArrayList<ArrayList<Integer>>();
        
        
        for(int i = 0; i < list.size(); i++){
            associations.add(new Tuple(++blobsFound, list.get(i)));
            includeList.add(new ArrayList<Integer>());
        }
        
        for(int i = 0; i < bottoms.size(); i++){
            boolean exists = false;
            for(int j = 0; j < associations.size(); j++){
                
                int a = associations.get(j).y;
                int b = bottoms.get(i);
                
                
                if(a == b){
                    includeList.get(j).add(i+1);
                    exists = true;
                    break;
                }
            }      
            if(!exists){
                int k = 4;
            }
        }
        
        
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                if(blobVals[i][j] != 0){
                    blobVals[i][j] = blobNumLookup(blobVals[i][j], includeList);
                } 
            }
        }
        
        Random randomGenerator = new Random();
        ArrayList<Color> colorList = new ArrayList<Color>();
        for(int i = 0; i < blobsFound; i++){
            int redRand = randomGenerator.nextInt(256);
            int greenRand = randomGenerator.nextInt(256);
            int blueRand = randomGenerator.nextInt(256);
            
            colorList.add(new Color(redRand, greenRand, blueRand));
        }
        
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                if(blobVals[i][j] != 0){
                    img.setRGB(j, i, colorList.get(blobVals[i][j]-1).getRGB());
                } else {
                    img.setRGB(j, i, new Color(255,255,255).getRGB());
                }
            }
        }
                
        try {
            File outputfile = new File("C:/Users/Laura/Documents/Year4/MComp/newImg.png");
            if(!ImageIO.write(img, "png", outputfile)){
                System.out.println("ha");
            } else { 
                System.out.println("Save Success");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        
        System.out.println(blobConnectionList.size());
        System.out.println(blobConnectionList);
        System.out.println(bottoms);
        /*for(int i = 0; i < includeList.size(); i++){
            System.out.println(includeList.get(i));
        }*/
        System.out.println("Found: " + blobsFound);
        IJ.showMessage("Found: " + blobsFound);
        
        ImagePlus res = new ImagePlus("Blob detection results", img);
        //res.draw();
        ImageWindow resultsWindow = new ImageWindow(res);
        //resultsWindow
    }
    
    public static int findMinNZ(int[] toCheck){
        int curCheck = 100000;
        boolean nonZeroFound = false;
        
        for(int i = 0; i < toCheck.length; i++){
            if(toCheck[i] > 0 && toCheck[i] < curCheck){
                curCheck = toCheck[i];
                nonZeroFound = true;
            }
        }
        
        if(nonZeroFound){
            return curCheck;
        } else {
            return 0;
        }
    }
    
    public static int findBottom(ArrayList<Integer> list, int toCheck){        
        if(list.get(toCheck-1) == toCheck){
            return toCheck;
        }
        
        return findBottom(list, list.get(toCheck-1));
    }
    
    public static int getBlobCount(ArrayList<Integer> blobList){
        ArrayList al = new ArrayList();
        HashSet hs = new HashSet();
        hs.addAll(blobList);
        al.addAll(hs);
        return al.size();
    }
    
    public static int blobNumLookup(int num, ArrayList<ArrayList<Integer>> toSearch){
        for(int i = 0; i < toSearch.size(); i++){
            for(int a : toSearch.get(i)){
                if(a == num){
                    return i+1;
                }
            }
        }
        return 0;
    }
    
/**
 *
 * @author Ben Cook
 */
public class Tuple<X, Y> { 
    public final X x; 
    public final Y y; 
    public Tuple(X x, Y y) { 
        this.x = x; 
        this.y = y; 
    } 
}
}
