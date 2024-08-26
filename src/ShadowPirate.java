import bagel.*;
import bagel.Font;
import bagel.Image;
import bagel.Window;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 1, 2022
 *
 * Please fill your name below
 * @author  Edwin Zhu
 */
public class ShadowPirate extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "ShadowPirate";
    private final static String INSTRUCT_MESSAGE_1 = "PRESS SPACE";
    private final static String INSTRUCT_MESSAGE_2 = "PRESS S TO ATTACK";
    private final static String INSTRUCT_MESSAGE_3 = "USE ARROW KEYS TO FIND LADDER";
    private final static String INSTRUCT_MESSAGE_4 = "FIND THE TREASURE";
    private final static String LEVEL_COMPLETE_MESSAGE = "LEVEL COMPLETE!";
    private final static String WON_MESSAGE = "CONGRATULATIONS";
    private final static String LOST_MESSAGE = "GAME OVER";
    private final static String WORLD_FILE_LEVEL_0 = "res/level0.csv";
    private final static String WORLD_FILE_LEVEL_1 = "res/level1.csv";

    private final Image BACKGROUND_LEVEL_0 = new Image("res/background0.png");
    private final Image BACKGROUND_LEVEL_1 = new Image("res/background1.png");
    private Image backgroundImage = BACKGROUND_LEVEL_0;

    private final static int FONT_Y_POS = 402;
    private final static int FONT_SIZE = 55;
    private final static int MESSAGE_Y_OFFSET = 70;
    private final Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    // whether the current level has started
    private boolean hasStarted = false;

    // whether it's currently level 0 or level 1
    private boolean isLevel0 = true;

    // whether the current level is ending, with counters for how long it has been ending
    private boolean levelEnd = false;
    private int levelEndCounter = 0;
    private final static int MAX_LEVEL_END_COUNTER = 180;

    // whether game has been lost or won
    private boolean gameLost = false;
    private boolean gameWon = false;


    private Sailor sailor;
    private Treasure treasure;

    // all entities
    private final ArrayList<Entity> entities = new ArrayList<>();

    // all entities that update timers and other states
    private final ArrayList<Updatable> updatables = new ArrayList<>();

    // all entities that move
    private final ArrayList<Movable> movers = new ArrayList<>();

    // all characters
    private final ArrayList<Character> characters = new ArrayList<>();

    // all enemies that can target players
    private final ArrayList<Targeter> enemyTargeters = new ArrayList<>();

    // all obstacles (as well as those that damage like bomb)
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();

    // all players that can damage enemies (just the one sailor)
    private final ArrayList<Damager> sailorDamager = new ArrayList<>();

    // all enemies that can damage players (minus obstacles that damage like bomb, so only projectiles)
    private final ArrayList<Damager> enemyDamagers = new ArrayList<>();

    // all items on level 1
    private final ArrayList<Item> items = new ArrayList<>();

    // keeps track of indexes to remove from current list traversed
    private final ArrayList<Integer> toRemove = new ArrayList<>();


    /**
     *
     */
    public ShadowPirate() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowPirate game = new ShadowPirate();
        game.readCSV(WORLD_FILE_LEVEL_0);
        game.run();
    }

    /**
     * Method used to read file and create objects
     */
    private void readCSV(String fileName){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String text;

            // first entry refers to sailor
            if ((text = br.readLine()) != null) {
                String cells[] = text.split(",");

                double leftX = Double.parseDouble(cells[1]);
                double topY = Double.parseDouble(cells[2]);

                addSailor(leftX, topY);
            }

            // other entries refer to other game entities, or specifies game edges(boundary)
            while ((text = br.readLine()) != null) {
                String cells[] = text.split(",");

                String entityName = cells[0];
                double leftX = Double.parseDouble(cells[1]);
                double topY = Double.parseDouble(cells[2]);

                // specifies game edges
                if (entityName.equals("TopLeft") || entityName.equals("BottomRight")) {
                    setEdges(entityName, (int)leftX, (int)topY);
                }

                // specifies game entity
                else {
                    addToArrays(entityName, fileName, leftX, topY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // ------------------------ CHECK ALL GAME STATES BEFORE UPDATING ENTITIES/ARRAYS ------------------------

        // game is Lost
        if (gameLost) {
            drawMessage(LOST_MESSAGE);
            return;
        }

        // game is Won
        if (gameWon) {
            drawMessage(WON_MESSAGE);
            return;
        }

        // level is currently ending
        if (levelEnd) {
            drawMessage(LEVEL_COMPLETE_MESSAGE);
            levelEndCounter++;
            if (levelEndCounter >= MAX_LEVEL_END_COUNTER) {
                levelEnd = false;
                levelEndCounter = 0;
            }
            return;
        }

        // level has not started
        if (!hasStarted) {
            drawLevelStartMessage(input);
            return;
        }

        // ------------------------ GAME STATES CHECKED, NOW UPDATE THE GAME ENTITIES/ARRAYS ------------------------
        backgroundImage.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // update sailor according to the input, the only object that responds to the input
        sailor.inputUpdate(input);

        // update timers on some classes
        updateUpdatables();

        // all objects that move will move
        updateMovers();

        // check if characters collide with obstacles and damaging entities, and draw characters' health
        updateCharacters();

        // remove damagers(projectiles) that have collided with boundary/sailor
        removeDamagers();

        // remove bombs on level 1 that have expired
        // also check collisions with treasure, items and draw sailor's inventory on level 1
        if (!isLevel0) {
            removeObstacles();
            gameWon = sailor.checkTreasureCollision(treasure);
            sailor.checkItemCollisions(items);
            sailor.drawInventory();
        }

        // all entities that target will launch an attack
        updateTargeters();

        // draws non-deleted entities to the screen
        updateEntities();

        // check if game is lost
        gameLost = sailor.isDead();

        // sets up details for level 1 if sailor completes level 0
        if (isLevel0 && sailor.levelComplete()) {
            setupLevel1();
        }

    }

    /** Draws level start message based on current level
     *
     * @param input The keyboard input. Starts the level if 'SPACE' is pressed
     */
    private void drawLevelStartMessage(Input input) {
        FONT.drawString(INSTRUCT_MESSAGE_1, (Window.getWidth()/2.0 - (FONT.getWidth(INSTRUCT_MESSAGE_1)/2.0)),
                FONT_Y_POS);
        FONT.drawString(INSTRUCT_MESSAGE_2, (Window.getWidth()/2.0 - (FONT.getWidth(INSTRUCT_MESSAGE_2)/2.0)),
                (FONT_Y_POS + MESSAGE_Y_OFFSET));
        if (isLevel0) {
            FONT.drawString(INSTRUCT_MESSAGE_3, (Window.getWidth()/2.0 - (FONT.getWidth(INSTRUCT_MESSAGE_3)/2.0)),
                    (FONT_Y_POS + 2 * MESSAGE_Y_OFFSET));
        } else {
            FONT.drawString(INSTRUCT_MESSAGE_4, (Window.getWidth()/2.0 - (FONT.getWidth(INSTRUCT_MESSAGE_4)/2.0)),
                    (FONT_Y_POS + 2 * MESSAGE_Y_OFFSET));
        }

        if (input.wasPressed(Keys.SPACE)){
            hasStarted = true;
        }
    }

    /** Draws a one line message to the game window
     *
     * @param message The message to be drawn
     */
    public void drawMessage(String message) {
        FONT.drawString(message, (Window.getWidth()/2.0 - (FONT.getWidth(message)/2.0)), FONT_Y_POS);
    }

    /** Adds sailor to all its relevant arrays
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public void addSailor(double leftX, double topY) {
        Sailor sailor = new Sailor(leftX, topY);

        this.sailor = sailor;
        updatables.add(sailor);
        sailorDamager.add(sailor);
        movers.add(sailor);
        characters.add(sailor);
        entities.add(sailor);
    }

    /** Adds an entity to the game based on its name, fileName and position coordinates
     *
     * @param entityName The name of the entity
     * @param fileName The filename that determines what level it is
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public void addToArrays(String entityName, String fileName, double leftX, double topY) {
        if (entityName.equals("Pirate") || entityName.equals("Blackbeard")) {
            addEnemy(entityName, leftX, topY);
        }

        else if (entityName.equals("Block")) {
            addObstacle(fileName, leftX, topY);
        }

        else if (entityName.equals("Potion") || entityName.equals("Sword") || entityName.equals("Elixir")) {
            addItem(entityName, leftX, topY);
        }

        else if (entityName.equals("Treasure")) {
            treasure = new Treasure(leftX, topY);
            entities.add(treasure);
        }
    }


    /** adds an enemy to the game
     *
     * @param enemyName The name of the enemy
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public void addEnemy(String enemyName, double leftX, double topY) {
        Enemy enemy;

        if (enemyName.equals("Pirate")) {
            enemy = new Pirate(leftX, topY);
        } else {
            enemy = new Blackbeard(leftX, topY);
        }

        updatables.add(enemy);
        movers.add(enemy);
        enemyTargeters.add(enemy);
        characters.add(enemy);
        entities.add(enemy);
    }

    /** Adds an obstacle to the game
     *
     * @param fileName The filename that determines what level it is
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public void addObstacle(String fileName, double leftX, double topY) {
        Obstacle obstacle;

        // block is added on level 0
        if (fileName.equals(WORLD_FILE_LEVEL_0)) {
            obstacle = new Block(leftX, topY);

        }

        // bomb is added on level 1
        else {
            obstacle = new Bomb(leftX, topY);
            Bomb bomb = (Bomb) obstacle;
            updatables.add(bomb);
        }

        obstacles.add(obstacle);
        entities.add(obstacle);
    }

    /** Adds an item to the game
     *
     * @param entityName The name of the item
     * @param leftX X-coordinate of top-left corner
     * @param topY y-coordinate of top-left corner
     */
    public void addItem(String entityName, double leftX, double topY) {
        Item item;

        if (entityName.equals("Potion")) {
            item = new Potion(leftX, topY);
        } else if (entityName.equals("Sword")) {
            item = new Sword(leftX, topY);
        } else {
            item = new Elixir(leftX, topY);
        }

        items.add(item);
        entities.add(item);
    }

    /** Sets the game boundary for relevant classes
     *  projectiles and characters can respond if they touch/cross game boundaries
     *
     * @param corner Refers to either the top-left or bottom-right corner
     * @param x X-coordinate of corner
     * @param y Y-coordinate of corner
     */
    public void setEdges(String corner, int x, int y) {
        if (corner.equals("TopLeft")) {
            Character.setTopLeftEdges(x, y);
            Projectile.setTopLeftEdges(x, y);
        } else {
            Character.setBottomRightEdges(x, y);
            Projectile.setBottomRightEdges(x, y);
        }
    }

    /** Clear arrays to set up for a new level
     *
     */
    public void clearArrays() {
        movers.clear();
        enemyTargeters.clear();
        obstacles.clear();
        sailorDamager.clear();
        enemyDamagers.clear();
        characters.clear();
        entities.clear();
    }

    /** Sets up for level 1 when sailor completes level 0
     * Changes background image and reads the level 1 file into fresh arrays
     */
    public void setupLevel1() {
        isLevel0 = false;
        levelEnd = true;
        hasStarted = false;
        backgroundImage = BACKGROUND_LEVEL_1;
        clearArrays();
        readCSV(WORLD_FILE_LEVEL_1);
    }

    /** Removes deleted entities from an array so that they don't clog up the array
     *
     * @param array The array to remove deleted entities from
     */
     public void removeDeletedEntities(ArrayList<?> array) {
        for (int i = toRemove.size() - 1; i >= 0; i--) {
            array.remove((int)toRemove.get(i));
        }

        toRemove.clear();
    }

    /** Updates entities that have timers and other states.
     * Simultaneously removes deleted entities from this array
     */
    public void updateUpdatables() {
        int index = -1;
        for (Updatable updatable : updatables) {
            index++;
            Entity entity = (Entity) updatable;
            if (entity.isDeleted()) {
                toRemove.add(index);
                continue;
            }
            updatable.update();
        }
        removeDeletedEntities(updatables);
    }

    /** Makes every moving entity move.
     * Checks moving entities for boundary collisions.
     * Simultaneously removes deleted entities from this array
     */
    public void updateMovers() {
        int index = -1;
        for (Movable mover : movers) {
            index++;
            Entity entity = (Entity) mover;
            if (entity.isDeleted()) {
                toRemove.add(index);
                continue;
            }
            mover.move();
            mover.checkBoundaryCollision();
        }
        removeDeletedEntities(movers);
    }

    /** Checks characters for collisions with obstacles.
     * Characters can be damaged, so also checks whether it has collided with any damagers.
     * Draws the characters' health to the screen
     * Simultaneously removes deleted entities from this array
     */
    public void updateCharacters() {
        int index = -1;
        for (Character character : characters) {
            index++;
            if (character.isDeleted()) {
                toRemove.add(index);
                continue;
            }
            character.checkObstacleCollisions(obstacles);

            if (character instanceof Enemy) {
                character.checkDamagerCollisions(sailorDamager);
            } else if (character instanceof Sailor) {
                character.checkDamagerCollisions(enemyDamagers);
            }

            character.drawHealth();
        }
        removeDeletedEntities(characters);
    }

    /** Removes deleted damagers(projectiles) from the enemyDamagers array
     *
     */
    public void removeDamagers() {
        int index = -1;
        for (Damager enemyDamager : enemyDamagers) {
            index++;
            Entity entity = (Entity) enemyDamager;
            if (entity.isDeleted()) {
                toRemove.add(index);
            }
        }
        removeDeletedEntities(enemyDamagers);
    }

    /** Removes bombs that have expired
     *
     */
    public void removeObstacles() {
        int index = -1;
        for (Obstacle obstacle : obstacles) {
            index++;
            if (obstacle.isDeleted()) {
                toRemove.add(index);
            }
        }
        removeDeletedEntities(obstacles);
    }

    /** Checks whether targeters send out attacks, and adds the released attacks (projectiles) to arrays.
     * Simultaneously removes deleted entities from this array
     */
    public void updateTargeters() {
        int index = -1;
        for (Targeter targeter : enemyTargeters) {
            index++;
            Entity entity = (Entity) targeter;
            if (entity.isDeleted()) {
                toRemove.add(index);
                continue;
            }

            Enemy enemy = (Enemy) targeter;
            Damager projectileDamager = enemy.targetAttack(sailor);

            if (projectileDamager != null) {
                Projectile projectile = (Projectile) projectileDamager;
                movers.add(projectile);
                entities.add(projectile);
                enemyDamagers.add(projectile);
            }
        }
        removeDeletedEntities(enemyTargeters);
    }

    /** Draws all non-deleted entities to the game window
     * Simultaneously removes deleted entities from this array
     */
    public void updateEntities() {
        int index = -1;
        for (Entity entity : entities) {
            index++;
            if (entity.isDeleted()) {
                toRemove.add(index);
                continue;
            }
            entity.draw();
        }
        removeDeletedEntities(entities);
    }

}
