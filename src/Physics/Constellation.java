package Physics;

import Elements.Monster;
import Elements.PlayableGObject;
import Elements.SatelliteDish;
import Main.MainGame;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constellation {
    protected static final PVector[][] CONSTELLATIONS = new PVector[][]{
        {
            // big dipper
            new PVector(0, 0),
            new PVector(4.1f, 1.2f),
            new PVector(5.5f, 3),
            new PVector(7.1f, 4.8f),
            new PVector(12, 6.6f),
            new PVector(10.2f, 8.8f),
            new PVector(6.8f, 7)
        },
        {
            // cassiopeia
            new PVector(0, 0),
            new PVector(2.5f, 3.4f-0.5f),
            new PVector(5.8f, 3.1f-0.5f),
            new PVector(6.6f, 5.75f-0.5f),
            new PVector(7.9f, 6.9f-0.5f),
            new PVector(11.4f, 4.1f-0.5f),
        },
        {
            // Cepheus
            new PVector(0, 0),
            new PVector(4, 2f),
            new PVector(2, 4.5f),
            new PVector(6, 4.6f),
            new PVector(4.6f, 7.2f),
        },
        {
            // Cancer
            new PVector(0, 0),
            new PVector(2.2f, 1.8f),
            new PVector(3.2f, 3f),
            new PVector(4, 5.8f),
            new PVector(7.9f, 3.5f),
        }
    };
    protected static final String[] CONST_NAMES = new String[]{
            "big dipper",
            "cassiopeia",
            "cepheus",
            "cancer"
    };

    /**
     * See report - section "Procedural Generation" > "Planet Systems"
     * for details
     */
    private class GenerationRegion{
        public PVector topLeft;
        public float width, height;
        public List<GenerationRegion> subRegions = new ArrayList<>();

        public void divideVer(){
            float x = p.random(topLeft.x + width/4, topLeft.x + width * 3f/4f);
            GenerationRegion sub1 = new GenerationRegion();
            sub1.topLeft = new PVector(x, topLeft.y);
            sub1.width = width - (x - topLeft.x);
            sub1.height = height;
            GenerationRegion sub2 = new GenerationRegion();
            sub2.topLeft = topLeft.copy();
            sub2.width = x - topLeft.x;
            sub2.height = height;
            subRegions.add(sub1);
            subRegions.add(sub2);
        }

        public void divideHor(){
            float y = p.random(topLeft.y + height/4, topLeft.y + height * 3f/4f);
            GenerationRegion sub1 = new GenerationRegion();
            sub1.topLeft = new PVector(topLeft.x, y);
            sub1.width = width;
            sub1.height = height - (y - topLeft.y);
            GenerationRegion sub2 = new GenerationRegion();
            sub2.topLeft = topLeft.copy();
            sub2.width = width;
            sub2.height = y - topLeft.y;
            subRegions.add(sub1);
            subRegions.add(sub2);
        }

        public List<Planet> generatePlanets(PVector translateVect){
            List<Planet> resL = new ArrayList<>();
            if(!subRegions.isEmpty()){
                // ask subregions to generate
                for(GenerationRegion region: subRegions){
                    resL.addAll(region.generatePlanets(translateVect));
                }
            }else{
                // not divided, so make planet
                float x = p.random(topLeft.x + 1f/3f*width, topLeft.x + 2f/3f*width);
                float y = p.random(topLeft.y + 1f/3f*height, topLeft.y + 2f/3f*height);
                PVector planetPos = new PVector(translateVect.x + x, translateVect.y + y);
                float maxRadius = Math.min((x - topLeft.x),
                        Math.min((y - topLeft.y),
                                Math.min(width - (x-topLeft.x), height - (y - topLeft.y))));
                float r = 0;
                // prefer larger radii
                if(p.random(0, 1) < 0.2f){
                    r = p.random(1f/4f*maxRadius, 1f/2f*maxRadius);
                }else{
                    r = p.random(1f/2f*maxRadius, maxRadius);
                }

                Planet planet = new Planet(p, r, planetPos);
                resL.add(planet);
            }
            return resL;
        }
    }

    protected MainGame p;

    protected List<PlanetSystem> systems = new ArrayList<>();
    protected static int constIndex = 2;

    public void display(){
        for (PlanetSystem ps: systems){
            ps.display();
        }
    }

    public Constellation(MainGame p) {
        this.p = p;

        // make constellation
        systems.add(generateFirstSystem());
        for(int i = 1; i < CONSTELLATIONS[constIndex].length; i++){
            systems.add(generatePlanetSystem(CONSTELLATIONS[constIndex][i].copy().mult(MainGame.WINDOW_WIDTH*2)));
        }
        addSatelliteDishToSystem(systems.get(systems.size()-1));
    }

    /**
     * Generates the first/start PlanetSystem. This system is always the same.
     * @return
     */
    private PlanetSystem generateFirstSystem(){
        List<Planet> planets = new ArrayList<>();
        List<Monster> enemies = new ArrayList<>();
        Planet planet = new Planet(p, 160, new PVector(MainGame.WINDOW_WIDTH/2, MainGame.WINDOW_HEIGHT/2));
        Planet planet2 = new Planet(p, 300, new PVector(MainGame.WINDOW_WIDTH, MainGame.WINDOW_HEIGHT));
        Planet planet3 = new Planet(p, 200, new PVector(100, 100));
        planets.add(planet);
        planets.add(planet2);
        planets.add(planet3);
        // add enemies to the second 2
        enemies.addAll(planet2.addEnemies((int) Math.floor(planet2.radius/60)));
        enemies.addAll(planet3.addEnemies((int) Math.floor(planet3.radius/60)));

        // add stars to 3 random enemies
        List<Monster> enemiesWithStars = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Monster m = enemies.get((int) Math.floor(p.random(0, enemies.size())));
            enemies.remove(m);
            m.setSpawnsStar();
            enemiesWithStars.add(m);
        }
        for(Monster m: enemiesWithStars){
            enemies.add(m);
        }
        return new PlanetSystem(p, planets, enemies);
    }

    /**
     * Generate random planet system:
     *  1. choose random number of planets
     *  2. divide the generation regions until enough regions exist
     *  3. generate planets into those regions
     *  4. place a random number of enemies onto planets according to their size
     *  5. add stars to 3 random enemies
     * @param relCenter
     * @return
     */
    private PlanetSystem generatePlanetSystem(PVector relCenter){
        int nuPlanets = (int) Math.floor(p.random(1, 7));
        if(nuPlanets == 0) nuPlanets = 1;

        GenerationRegion level1 = new GenerationRegion();
        level1.topLeft = new PVector(0, 0);
        level1.width = MainGame.WINDOW_WIDTH*1f/0.8f;
        level1.height = MainGame.WINDOW_HEIGHT*1f/0.8f;

        int level = 0;
        boolean vertical = true;
        for (int i = 0; i < nuPlanets; i++) {
            GenerationRegion nextRegToDivide = null;
            while (nextRegToDivide == null){
                List<GenerationRegion> regs = getRegionsAtLevel(level1, level);
                List<GenerationRegion> empty = new ArrayList<>();
                regs.forEach(gr -> {
                    if(gr.subRegions.isEmpty())
                        empty.add(gr);
                });
                if(empty.isEmpty()){
                    level++;
                    vertical = !vertical;
                }else{
                    nextRegToDivide = empty.get((int) Math.floor(p.random(0, empty.size())));
                }
            }

            if(vertical){
                nextRegToDivide.divideVer();
            }else{
                nextRegToDivide.divideHor();
            }
        }

        List<Planet> planets = level1.generatePlanets(new PVector(
                relCenter.x - MainGame.WINDOW_WIDTH/2,
                relCenter.y + MainGame.WINDOW_HEIGHT/2));
        List<Monster> enemies = new ArrayList<>();
        for(Planet pln: planets){
            enemies.addAll(pln.addEnemies((int) Math.floor(pln.radius/60)));
        }

        // add stars to 3 random enemies
        List<Monster> enemiesWithStars = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Monster m = enemies.get((int) Math.floor(p.random(0, enemies.size())));
            enemies.remove(m);
            m.setSpawnsStar();
            enemiesWithStars.add(m);
        }
        for(Monster m: enemiesWithStars){
            enemies.add(m);
        }

        return new PlanetSystem(p, planets, enemies);

    }

    private void addSatelliteDishToSystem(PlanetSystem system){
        Planet planet = system.getPlanets().get((int) Math.floor(p.random(0, system.getPlanets().size())));
        SatelliteDish dish = planet.addSatelliteDish();
        system.addSatelliteDish(dish);
    }

    /**
     * Used for generation of systems
     * @param root
     * @param level
     * @return
     */
    private List<GenerationRegion> getRegionsAtLevel(GenerationRegion root, int level){
        if(level == 0){
            return new ArrayList<>(Arrays.asList(new GenerationRegion[]{root}));
        }else if(level == 1){
            List<GenerationRegion> resL = new ArrayList<>();
            for(GenerationRegion r: root.subRegions){
                resL.add(r);
            }
            return resL;
        }else{
            List<GenerationRegion> resL = new ArrayList<>();
            for(GenerationRegion r: root.subRegions){
                resL.addAll(getRegionsAtLevel(r, level-1));
            }
            return resL;
        }
    }

    public PVector[] getSystemsSmall(){
        return CONSTELLATIONS[constIndex];
    }

    public PVector getPlayerPosSmall(){
        PVector playerPos = p.getUniverse().getPlayerPos();
        float realDist = systems.get(0).getCentre().dist(systems.get(1).getCentre());
        float relaDist = CONSTELLATIONS[constIndex][0].dist(CONSTELLATIONS[constIndex][1]);
        float scaleFact = relaDist/realDist;
        PVector playerRelaPos = new PVector(playerPos.x - systems.get(0).getCentre().x, playerPos.y - systems.get(0).getCentre().y);
        playerRelaPos.mult(scaleFact);
        return playerRelaPos;
    }

    public PlanetSystem getStart(){
        return systems.get(0);
    }

    public PlanetSystem findNearestSystem(PlayableGObject player){
        float nearestDist = Float.MAX_VALUE;
        PlanetSystem nearestSystem = systems.get(0);
        for(PlanetSystem ps : systems){
            if(ps.getCentre().dist(player.getCentrePos()) < nearestDist){
                nearestSystem = ps;
                nearestDist = ps.getCentre().dist(player.getCentrePos());
            }
        }
        return nearestSystem;
    }

    public String getConstellationName(){
        return CONST_NAMES[constIndex];
    }

    public boolean planetSystemComplete(int indx){
        return systems.get(indx).getStarsCollected() == 3;
    }

    public static void randomConstIndex(MainGame p){
        constIndex = (int) Math.floor(p.random(0, CONSTELLATIONS.length));
    }

    public static void nextConstIndex(MainGame p){
        constIndex++;
        if(constIndex >= CONSTELLATIONS.length){
            constIndex = 0;
        }
    }

    /**
     * Gets the percentage of completed systems,
     * eg 1/2 if 1 out of 2 possible systems were completed (all stars collected)
     * @return
     */
    public float getPercCompletedSystems(){
        float r = 0;
        float c = 0;
        for(PlanetSystem s: systems){
            if(s.getStarsCollected() == 3){
                r++;
            }
            c++;
        }
        return r / c;
    }
}
