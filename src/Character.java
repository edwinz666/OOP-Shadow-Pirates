import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Colour;

import java.util.ArrayList;

/** Represents all game entities that are characters.
 *
 */
public abstract class Character extends Entity implements Targetable, Movable, Updatable{
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private static int edgeLeft;
    private static int edgeRight;
    private static int edgeTop;
    private static int edgeBottom;

    private final double SPEED;
    private final int MAX_COOLDOWN_COUNT;
    private final Image HIT_LEFT;
    private final Image HIT_RIGHT;
    private final Image LEFT;
    private final Image RIGHT;

    private int maxHealth;
    private int currentHealth;
    private int damagePoints;
    private boolean canAttack = true;
    private int cooldownCount = 0;

    private double oldX;
    private double oldY;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     * @param SPEED Speed of the character
     * @param HIT_LEFT Image of the character when it is moving left, and is:
     *                 Invincible (if instanceof Enemy), Attacking (if instanceof Sailor)
     * @param HIT_RIGHT Image of the character when it is moving right, and is:
     *                  Invincible (if instanceof Enemy), Attacking (if instanceof Sailor)
     * @param LEFT Image of the character when it is moving left, and is:
     *                  not Invincible (if instanceof Enemy), not Attacking (if instanceof Sailor)
     * @param RIGHT Image of the character when it is moving right, and is:
     *                  not Invincible (if instanceof Enemy), not Attacking (if instanceof Sailor)
     * @param maxHealth Initial maximum health of the character
     * @param damagePoints Initial damage point value of the character
     * @param MAX_COOLDOWN_COUNT If cooldownCount reaches this number, the character can attack again
     */
    public Character(double leftX, double topY, double SPEED, Image HIT_LEFT,
                     Image HIT_RIGHT, Image LEFT, Image RIGHT, int maxHealth, int damagePoints, int MAX_COOLDOWN_COUNT){
        super(leftX, topY);
        this.SPEED = SPEED;
        this.HIT_LEFT = HIT_LEFT;
        this.HIT_RIGHT = HIT_RIGHT;
        this.LEFT = LEFT;
        this.RIGHT = RIGHT;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.damagePoints = damagePoints;
        this.MAX_COOLDOWN_COUNT = MAX_COOLDOWN_COUNT;
    }

    /**
     *
     * @param edgeLeft X-coordinate of Top-Left corner of game boundary
     * @param edgeTop Y-coordinate of Top-Left corner of game boundary
     */
    public static void setTopLeftEdges(int edgeLeft, int edgeTop) {
        Character.edgeLeft = edgeLeft;
        Character.edgeTop = edgeTop;
    }

    /**
     *
     * @param edgeRight X-coordinate of Bottom-Right corner of game boundary
     * @param edgeBottom Y-coordinate of Bottom-Right corner of game boundary
     */
    public static void setBottomRightEdges(int edgeRight, int edgeBottom) {
        Character.edgeRight = edgeRight;
        Character.edgeBottom = edgeBottom;
    }

    /**
     *
     * @return Returns the X-coordinate of left game boundary
     */
    public static int getEdgeLeft() {
        return edgeLeft;
    }

    /**
     *
     * @return Returns the X-coordinate of right game boundary
     */
    public static int getEdgeRight() {
        return edgeRight;
    }

    /**
     *
     * @return Returns the Y-coordinate of top game boundary
     */
    public static int getEdgeTop() {
        return edgeTop;
    }

    /**
     *
     * @return Returns the Y-coordinate of bottom game boundary
     */
    public static int getEdgeBottom() {
        return edgeBottom;
    }

    /**
     *
     * @return Returns the speed of the character in pixels per frame
     */
    public double getSPEED() {
        return SPEED;
    }

    /**
     *
     * @return Returns the Image of the character when it is moving left, and is:
     *         Invincible (if instanceof Enemy), Attacking (if instanceof Sailor)
     */
    public Image getHIT_LEFT() {
        return HIT_LEFT;
    }

    /**
     *
     * @return Returns the Image of the character when it is moving right, and is:
     *         Invincible (if instanceof Enemy), Attacking (if instanceof Sailor)
     */
    public Image getHIT_RIGHT() {
        return HIT_RIGHT;
    }

    /**
     *
     * @return Returns the Image of the character when it is moving left, and is:
     *         not Invincible (if instanceof Enemy), not Attacking (if instanceof Sailor)
     */
    public Image getLEFT() {
        return LEFT;
    }

    /**
     *
     * @return Returns the Image of the character when it is moving right, and is:
     *         not Invincible (if instanceof Enemy), not Attacking (if instanceof Sailor)
     */
    public Image getRIGHT() {
        return RIGHT;
    }

    /**
     *
     * @return Returns the maximum health of the character
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     *
     * @return Returns the current health of the character
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /** Sets the maximum health of the character
     *
     * @param maxHealth The maximum health to set
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /** Sets the current health of the character
     *
     * @param currentHealth The current health to set
     */
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    /** Sets the damage points of the character
     *
     * @param damagePoints The damage point value to set
     */
    public void setDamagePoints(int damagePoints) {
        this.damagePoints = damagePoints;
    }

    /**
     *
     * @return Returns the DrawOptions to help with drawing health
     */
    public DrawOptions getCOLOUR() {
        return COLOUR;
    }

    /** Sets the colour of the health to draw based on the health percentage of the character
     *
     * @param percentageHP The health percentage of the character
     */
    public void setHealthColour(double percentageHP) {
        if (percentageHP < RED_BOUNDARY) {
            COLOUR.setBlendColour(RED);
        } else if (percentageHP < ORANGE_BOUNDARY) {
            COLOUR.setBlendColour(ORANGE);
        } else {
            COLOUR.setBlendColour(GREEN);
        }
    }

    /**
     *
     * @return Returns whether the character is dead
     */
    public boolean isDead() {
        return currentHealth == 0;
    }

    /** Recalculates health after being damaged by a damager
     *
     * @param damager The damager that the character receives damage from
     */
    public void receiveDamage(Damager damager) {
        currentHealth -= damager.getDamagePoints();
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    public int getDamagePoints() {
        return damagePoints;
    }


    /**
     *
     * @return Returns the number that when cooldownCount reaches this number, the character can attack again
     */
    public int getMAX_COOLDOWN_COUNT() {
        return MAX_COOLDOWN_COUNT;
    }


    /**
     *
     * @return Returns whether the character can attack
     */
    public boolean isCanAttack() {
        return canAttack;
    }


    /** Method that changes whether the character can currently attack or not
     *
     * @param canAttack The boolean value to set
     */
    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }


    /**
     *
     * @return Returns how long it has been since character started cooldown
     */
    public int getCooldownCount() {
        return cooldownCount;
    }


    /** Method that sets how long it has been since character started cooldown
     *
     * @param cooldownCount The value to set
     */
    public void setCooldownCount(int cooldownCount) {
        this.cooldownCount = cooldownCount;
    }


    /** Method that keeps track of character's old points when it moves.
     * Used with moveBack() when colliding with an obstacle.
     *
     */
    public void setOldPoints() {
        oldX = getLeftX();
        oldY = getTopY();
    }


    /** Method that moves character back to its old position when it has collided with an obstacle
     *
     */
    public void moveBack() {
        setLeftX(oldX);
        setTopY(oldY);
    }


    /** Method that draws the character's health to the screen
     *
     */
    public abstract void drawHealth();


    /** Method that checks for overlap with valid damagers, receiving damage from those that are valid
     *
     * @param damagers Array of entities that can inflict damage
     */
    public abstract void checkDamagerCollisions(ArrayList<Damager> damagers);


    /** Method that checks for overlap with obstacles, moving back if it has overlapped with an obstacle
     * The Bomb obstacle especially interacts with Enemy and Sailor differently.
     *
     * @param obstacles Array of obstacles to check for overlap
     */
    public abstract void checkObstacleCollisions(ArrayList<Obstacle> obstacles);


    /** Method that checks whether character is trying to move past game boundary
     *
     */
    public abstract void checkBoundaryCollision();


    /** Method that converts class name to a String, to facilitate printing messages to the command line
     *
     * @return Returns String corresponding to class name of the character
     */
    public abstract String toString();


    /** Method that prints a line to the command line representing the current and max health of the character,
     * which is repeated frequently.
     *
     * @return Returns the corresponding String
     */
    public String printHealth() {
        return this + "'s current health: " + getCurrentHealth() + "/" + getMaxHealth();
    }
}
