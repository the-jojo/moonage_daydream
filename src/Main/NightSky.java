package Main;

import Physics.Planet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.*;
import java.util.List;

public class NightSky {
    private class Star{
        public float x;
        public float y;
        public Color col;
        public float size;
        public void display(){
            p.stroke(col.getRGB());
            p.strokeWeight(size);
            p.point(x, y);

        }
    }

    private class SkyRegion {
        public PVector topLeft;
        public List<Star> stars;
        public static final int SIZE = 500;
        private PImage getImg;
        public boolean hasGetted = false;

        public void generateStars(){
            stars = new ArrayList<>();
            for (int i = 0; i < Math.round(STAR_RATIO*(SIZE*SIZE)); i++) {
                Star s = new Star();
                s.x = p.random(topLeft.x, topLeft.x + SIZE);
                s.y = p.random(topLeft.y, topLeft.y + SIZE);
                s.col = new Color(255, 255, Math.round(p.random(80, 255)));
                s.size = p.random(0.5f, 3.2f);
                stars.add(s);
            }
        }

        public void display(PVector realTopLeft){
            for (Star star:stars) {
                displayStarAsPixel(star, realTopLeft);

                //star.display();
            }

        }

        private void displayStarAsPixel(Star star, PVector realTopLeft) {
            // make relative position
            PVector relPos = new PVector(star.x - realTopLeft.x, star.y - realTopLeft.y);
            // check if we stay within bounds of screen
            if (relPos.x >= 0 && relPos.y >= 0
                    && relPos.x + star.size < MainGame.WINDOW_WIDTH
                    && relPos.y+ star.size < MainGame.WINDOW_HEIGHT) {

                // write to all pixels the star's colour
                for (int x = Math.round(relPos.x); x < relPos.x + star.size; x++) {
                    for (int y = Math.round(relPos.y); y < relPos.y + star.size; y++) {
                        int i = getPixelIndex(Math.round(x), Math.round(y));
                        if (i >= p.pixels.length) {
                            System.out.println(p.pixelWidth + " " + p.pixelHeight);
                        } else {
                            p.pixels[i] = star.col.getRGB();
                        }

                    }
                }

            }
        }
    }

    // Game elements
    private MainGame p;
    private HashMap<PVector, SkyRegion> generatedRegions = new HashMap<>();

    private static final float STAR_RATIO = 4f/(100f*100f);

    public NightSky(MainGame pa){
        p = pa;
    }

    public void display(PVector topLeft, float width, float height){
        Set<SkyRegion> toDisplay = generateRegionsAndGetToDisplay(topLeft, Math.round(width), Math.round(height));

        p.imageMode(p.CORNER);
        p.loadPixels();
        for(SkyRegion reg : toDisplay){
            reg.display(topLeft);
        }
        p.updatePixels();
        p.imageMode(p.CENTER);
    }

    public Set<SkyRegion> generateRegionsAndGetToDisplay(PVector topLeft, int dWidth, int dHeight){
        // snap top left of display to nearest smaller region
        int tl_remainder_x = mod(Math.round(topLeft.x), SkyRegion.SIZE);
        int tl_remainder_y = mod(Math.round(topLeft.y), SkyRegion.SIZE);
        int tl_next_x = Math.round(topLeft.x) - tl_remainder_x;
        int tl_next_y = Math.round(topLeft.y) - tl_remainder_y;

        // snap bottom right of display to nearest smaller region
        int br_remainder_x = mod(Math.round(topLeft.x + dWidth) , SkyRegion.SIZE);
        int br_remainder_y = mod(Math.round(topLeft.y + dHeight), SkyRegion.SIZE);
        int br_next_x = Math.round(topLeft.x + dWidth)  - br_remainder_x;
        int br_next_y = Math.round(topLeft.y + dHeight) - br_remainder_y;

        // find min and max pVectors of regions which need generation
        PVector needed_min = new PVector(tl_next_x - SkyRegion.SIZE, tl_next_y - SkyRegion.SIZE);
        PVector needed_max = new PVector(br_next_x + SkyRegion.SIZE, br_next_y + SkyRegion.SIZE);

        // build set of generatedRegions which need to be generated
        HashSet<PVector> neededRegions = new HashSet<>();
        for (int x = Math.round(needed_min.x); x <= needed_max.x; x = x + SkyRegion.SIZE) {
            for (int y = Math.round(needed_min.y); y <= needed_max.y; y = y + SkyRegion.SIZE) {
                neededRegions.add(new PVector(x, y));
            }
        }

        // build set of generatedRegions which will be displayed
        HashSet<PVector> displayingRegions = new HashSet<>();
        for (int x = tl_next_x; x <= br_next_x; x = x + SkyRegion.SIZE) {
            for (int y = (int) tl_next_y; y <= br_next_y; y = y + SkyRegion.SIZE) {
                displayingRegions.add(new PVector(x, y));
            }
        }

        // delete regions which are too far away
        PVector middle = new PVector(topLeft.x + dWidth/2, topLeft.y + dHeight/2);
        generatedRegions.entrySet().removeIf(e-> e.getKey().dist(middle) > Math.max(dHeight, dWidth) * 4);

        // filter out generatedRegions which were already generated
        HashSet<PVector> neededRegions_2 = (HashSet<PVector>) neededRegions.clone();
        for(PVector neededRegion: neededRegions){
            if(generatedRegions.get(neededRegion) != null){
                neededRegions_2.remove(neededRegion);
            }
        }

        // generate regions which need generation
        for(PVector neededRegion: neededRegions_2){
            SkyRegion reg = new SkyRegion();
            reg.topLeft = neededRegion;
            reg.generateStars();
            generatedRegions.put(neededRegion, reg);
        }

        Set<SkyRegion> toDisplay = new HashSet<>();
        for(PVector displayingRegion: displayingRegions){
            SkyRegion reg = generatedRegions.get(displayingRegion);
            toDisplay.add(reg);
        }

        return toDisplay;
    }

    private boolean isWithinScreen(SkyRegion region, PVector topLeft, float width, float height){
        return region.topLeft.x >= topLeft.x &&
                region.topLeft.y >= topLeft.y &&
                region.topLeft.x + region.SIZE <= topLeft.x + width &&
                region.topLeft.y + region.SIZE <= topLeft.y + height;
    }

    private static int mod(int x, int y) {
        int result = x % y;
        return result < 0? result + y : result;
    }

    /**
     * Returns pixel index in p.pixels[] array given a x and y and assumes window width is always same
     * @param x
     * @param y
     * @return
     */
    private static int getPixelIndex(int x, int y){
        return x + ( MainGame.WINDOW_WIDTH * y );
    }
}
