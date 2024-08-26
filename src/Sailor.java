import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;

/** Represents the Sailor controlled by the player
 *
 */
public class Sailor extends Character implements Damager{
    private static final Image HIT_LEFT = new Image("res/sailor/sailorHitLeft.png");
    private static final Image HIT_RIGHT = new Image("res/sailor/sailorHitRight.png");
    private static final Image LEFT = new Image("res/sailor/sailorLeft.png");
    private static final Image RIGHT = new Image("res/sailor/sailorRight.png");

    private final static double SPEED = 1;
    private final static int INITIAL_MAX_HEALTH = 100;
    private final static int INITIAL_DAMAGE_POINTS = 15;
    private final static int HEALTH_X = 10;
    private final static int HEALTH_Y = 25;
    private final static int WIN_X = 990;
    private final static int WIN_Y = 630;
    private final static int FONT_SIZE = 30;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);
    private final static int INVENTORY_Y_OFFSET = 50;
    private final static int MAX_ATTACKING_COUNT = 1000 * Entity.getFPS() / 1000;
    private final static int MAX_COOLDOWN_COUNT = 2000 * Entity.getFPS() / 1000;
    private final static int SWORD_BONUS = 15;
    private final static int POTION_BONUS = 25;
    private final static int ELIXIR_BONUS = 35;

    private boolean isAttacking = false;
    private int attackingCount = 0;
    private boolean isRecharging = false;
    private final ArrayList<Item> items = new ArrayList<>();

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Sailor(double leftX, double topY) {
        super(leftX, topY, SPEED, HIT_LEFT, HIT_RIGHT, LEFT, RIGHT, INITIAL_MAX_HEALTH,
                INITIAL_DAMAGE_POINTS, MAX_COOLDOWN_COUNT);
        setCurrentImage(RIGHT);
    }

    /**
     *
     * @return Returns whether the sailor has completed level 0
     */
    public boolean levelComplete() {
        return getLeftX() > WIN_X && getTopY() > WIN_Y;
    }

    /**
     *
     * @return Returns whether the sailor is currently in attack mode
     */
    public boolean getIsAttacking() {
        return isAttacking;
    }

    /** Method that draws the sailor's inventory to the screen
     *
     */
    public void drawInventory() {
        int numItems = 0;
        for (Item item : items) {
            numItems++;
            item.getCurrentImage().drawFromTopLeft(HEALTH_X, HEALTH_Y + numItems * INVENTORY_Y_OFFSET);

        }
    }

    /** Method that checks for collisions between the sailor and game items
     *
     * @param items The items to check for collisions
     */
    public void checkItemCollisions(ArrayList<Item> items) {
        for (Item item : items) {
            // item exists in the game and sailor collides with it
            // sailor gets the item effect and the item is added to its inventory
            if (!item.isDeleted() && overlaps(item)) {
                getItemEffect(item);
                item.setCurrentImage(item.getICON_IMAGE());
                this.items.add(item);
                item.setDeleted(true);
            }
        }
    }

    /** Method that changes sailor attributes according to item collected
     *
     * @param item The item that has been collected
     */
    public void getItemEffect(Item item) {
        System.out.print("Sailor finds " + item.toString() + ".  " + "Sailor's ");

        if (item instanceof Sword) {
            setDamagePoints(getDamagePoints() + SWORD_BONUS);
            System.out.println("damage points increased to " + (getDamagePoints()));
        }
        else if (item instanceof Potion) {
            int newHealth = getCurrentHealth() + POTION_BONUS;
            if (newHealth > getMaxHealth()) {
                newHealth = getMaxHealth();
            }
            setCurrentHealth(newHealth);
            System.out.println(printHealth());
        }
        else if (item instanceof Elixir) {
            setMaxHealth(getMaxHealth() + ELIXIR_BONUS);
            setCurrentHealth(getMaxHealth());
            System.out.println(printHealth());
        }
    }

    /** Method that checks for collision between the sailor and the treasure
     *
     * @param treasure The treasure to be checked
     * @return Returns whether the treasure has been found by the sailor
     */
    public boolean checkTreasureCollision(Treasure treasure) {
        return overlaps(treasure);
    }

    @Override
    public void drawHealth() {
        double percentageHP = ((double) getCurrentHealth()/getMaxHealth()) * 100;
        setHealthColour(percentageHP);
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, getCOLOUR());
    }

    @Override
    public void checkDamagerCollisions(ArrayList<Damager> damagers){
        for (Damager damager : damagers) {
            Projectile projectile = (Projectile) damager;
            // if projectile overlaps with the sailor, sailor takes damage and the projectile is deleted
            if (!projectile.isDeleted() && projectile.overlaps(this)) {
                projectile.setDeleted(true);
                receiveDamage(projectile);
                System.out.println(projectile.getEnemyName() + " inflicts " + projectile.getDamagePoints() +
                        " damage points on Sailor. " + printHealth());
            }
        }
    }

    @Override
    public void checkObstacleCollisions(ArrayList<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            // if sailor collides with obstacle, then it moves back to its old position
            if (!obstacle.isDeleted() && this.overlaps(obstacle)) {
                moveBack();
                // if the obstacle is a bomb, sailor takes damage from the bomb, and bomb changes to exploded state
                if (obstacle instanceof Bomb) {
                    Bomb bomb = (Bomb) obstacle;
                    if (!bomb.isHasExploded()) {
                        receiveDamage(bomb);
                        bomb.setExplodedState();
                        System.out.println("Bomb inflicts " + bomb.getDamagePoints() + " damage points on Sailor. "
                        + "Sailor's " + printHealth());
                    }
                }
                return;
            }
        }
    }

    @Override
    public void checkBoundaryCollision() {
        if (getLeftX() < getEdgeLeft() || getLeftX() > getEdgeRight() ||
                getTopY() < getEdgeTop() || getTopY() > getEdgeBottom()) {
            moveBack();
        }
    }

    /** Sailor movement is already fully captured already by the inputUpdate method
     *
     */
    public void move() {
    }

    /** Method that updates the sailor's movement and attack mode, according to the input
     *
     * @param input The keyboard input
     */
    public void inputUpdate(Input input) {
        if (input.isDown(Keys.LEFT)) {
            setOldPoints();
            setLeftX(getLeftX() - SPEED);
            setLeftImage();
        } else if (input.isDown(Keys.RIGHT)) {
            setOldPoints();
            setLeftX(getLeftX() + SPEED);
            setRightImage();
        } else if (input.isDown(Keys.UP)) {
            setOldPoints();
            setTopY(getTopY() - SPEED);
        } else if (input.isDown(Keys.DOWN)) {
            setOldPoints();
            setTopY(getTopY() + SPEED);
        }

        if (input.wasPressed(Keys.S) && isCanAttack()) {
            setAttackMode();
        }

    }


    public void update() {
        // increment attackingCount if sailor is currently in attack mode
        if (isAttacking) {
            attackingCount++;
            // revert back to idle mode after attacking mode subsides
            if (attackingCount >= MAX_ATTACKING_COUNT) {
                setIdleMode();
            }
        }

        // increment cooldownCount if sailor is currently recharging
        if (isRecharging) {
            setCooldownCount(getCooldownCount() + 1);
            // sailor can attack again once off cooldown
            if (getCooldownCount() >= MAX_COOLDOWN_COUNT) {
                setCanAttackMode();
            }
        }
    }

    /** Method that puts the sailor in attack mode
     *
     */
    public void setAttackMode() {
        isAttacking = true;
        setCanAttack(false);
        if (getCurrentImage() == getRIGHT()) {
            setCurrentImage(getHIT_RIGHT());
        } else if (getCurrentImage() == getLEFT()) {
            setCurrentImage(getHIT_LEFT());
        }
    }

    /** Method that puts the sailor in idle mode
     *
     */
    public void setIdleMode() {
        isAttacking = false;
        attackingCount = 0;
        isRecharging = true;
        if (getCurrentImage() == getHIT_RIGHT()) {
            setCurrentImage(getRIGHT());
        } else if (getCurrentImage() == getHIT_LEFT()) {
            setCurrentImage(getLEFT());
        }
    }

    /** Method that puts the sailor in 'can attack' mode
     *
     */
    public void setCanAttackMode() {
        setCanAttack(true);
        setCooldownCount(0);
        isRecharging = false;
    }

    /** Method that changes the sailor's image to left facing
     *
     */
    public void setLeftImage() {
        if (getCurrentImage() == RIGHT) {
            setCurrentImage(LEFT);
        } else if (getCurrentImage() == HIT_RIGHT) {
            setCurrentImage(HIT_LEFT);
        }
    }

    /** Method that changes the sailor's image to right facing
     *
     */
    public void setRightImage() {
        if (getCurrentImage() == LEFT) {
            setCurrentImage(RIGHT);
        } else if (getCurrentImage() == HIT_LEFT) {
            setCurrentImage(HIT_RIGHT);
        }
    }

    @Override
    public String toString() {
        return "Sailor";
    }

}
