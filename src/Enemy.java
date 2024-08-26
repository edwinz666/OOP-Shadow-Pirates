import bagel.Font;
import bagel.Image;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Random;

/** Represent all game entities that are enemies
 *
 */
public abstract class Enemy extends Character implements Targeter{
    private final static double MIN_ENEMY_SPEED = 0.2;
    private final static double MAX_ENEMY_SPEED = 0.7;
    private final static Random random = new Random();

    private final static int HEALTH_Y_OFFSET = 6;
    private final static int MAX_INVISIBLE_COUNT = 1500 * Entity.getFPS() / 1000;
    private final int ATTACK_DISTANCE;
    private final String PROJECTILE_FILE;
    private final double PROJECTILE_SPEED;
    private final static int FONT_SIZE = 15;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private boolean isInvincible = false;
    private int invincibleCount = 0;
    private int directionX;
    private int directionY;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     * @param HIT_LEFT Image of the character when it is moving left, and is:
     *                 Invincible (if instanceof Enemy), Attacking (if instanceof Sailor)
     * @param HIT_RIGHT Image of the character when it is moving right, and is:
     *                 Invincible (if instanceof Enemy), Attacking (if instanceof Sailor)
     * @param LEFT Image of the character when it is moving left, and is:
     *                 not Invincible (if instanceof Enemy), not Attacking (if instanceof Sailor)
     * @param RIGHT Image of the character when it is moving right, and is:
     *                 not Invincible (if instanceof Enemy), not Attacking (if instanceof Sailor)
     * @param maxHealth Initial maximum health of the character
     * @param damagePoints Initial damage point value of the character
     * @param MAX_COOLDOWN_COUNT If cooldownCount reaches this number, the character can attack again
     * @param attackDistance Attack distance of the enemy, measured from its centre
     * @param projectileFile File for the image of the projectile launched by the enemy
     * @param PROJECTILE_SPEED Speed of the projectile launched by the enemy
     */
    public Enemy(double leftX, double topY, Image HIT_LEFT, Image HIT_RIGHT, Image LEFT, Image RIGHT, int maxHealth,
                 int damagePoints, int MAX_COOLDOWN_COUNT, int attackDistance, String projectileFile,
                 double PROJECTILE_SPEED) {
        // speed parameter includes randomly selecting a speed between MAX_ENEMY_SPEED and MIN_ENEMY_SPEED
        super(leftX, topY, random.nextDouble() * (MAX_ENEMY_SPEED - MIN_ENEMY_SPEED) + MIN_ENEMY_SPEED,
                HIT_LEFT, HIT_RIGHT, LEFT, RIGHT, maxHealth, damagePoints, MAX_COOLDOWN_COUNT);
        this.ATTACK_DISTANCE = attackDistance;
        this.PROJECTILE_FILE = projectileFile;
        this.PROJECTILE_SPEED = PROJECTILE_SPEED;
        setInitialDirection();
    }

    /** Randomly selects an initial direction for the enemy
     *
     */
    public void setInitialDirection() {
        // randomly selecting a direction
        int directionRand = random.nextInt(4);

        if (directionRand == 0) {
            directionX = 1;
            directionY = 0;
        } else if (directionRand == 1) {
            directionX = -1;
            directionY = 0;
        } else if (directionRand == 2) {
            directionX = 0;
            directionY = 1;
        } else {
            directionX = 0;
            directionY = -1;
        }

        // setting the correct image based on the direction
        if (directionX == -1) {
            setCurrentImage(getLEFT());
        } else {
            setCurrentImage(getRIGHT());
        }
    }

    public Damager targetAttack(Targetable target){
        Entity entity = (Entity) target;

        // returns a projectile directed at the target if target is in range
        if (isCanAttack() && computeAttackRange().intersects(entity.computeBoundary())) {
            // enemy goes on cooldown after an attack
            setCanAttack(false);

            // top left coordinates of the projectile
            double centreX = getLeftX() + getCurrentImage().getWidth()/2.0;
            double centreY = getTopY() + getCurrentImage().getHeight()/2.0;

            // find direction of the projectile based on enemy and target's position
            double directionX = entity.getLeftX() - this.getLeftX();
            double directionY = entity.getTopY() - this.getTopY();

            // image of the projectile based on the enemy who launched it
            Image projectileImage = new Image(PROJECTILE_FILE);

            return new Projectile(centreX - projectileImage.getWidth()/2.0,
                                centreY - projectileImage.getHeight()/2.0,
                    directionX, directionY, PROJECTILE_SPEED, getDamagePoints(), projectileImage, toString());
        }

        // target is not in range, so return a null
        else {
            return null;
        }
    }

    /**
     *
     * @return Returns a rectangle representing the attack range of the enemy
     */
    public Rectangle computeAttackRange(){
        double centreX = getLeftX() + getCurrentImage().getWidth()/2.0;
        double centreY = getTopY() + getCurrentImage().getHeight()/2.0;
        return new Rectangle(centreX - ATTACK_DISTANCE/2.0, centreY - ATTACK_DISTANCE/2.0,
                ATTACK_DISTANCE, ATTACK_DISTANCE);
    }

    @Override
    public void drawHealth() {
        double percentageHP = ((double) getCurrentHealth()/getMaxHealth()) * 100;
        setHealthColour(percentageHP);
        FONT.drawString(Math.round(percentageHP) + "%", getLeftX(), getTopY() - HEALTH_Y_OFFSET, getCOLOUR());
    }


    @Override
    public void checkDamagerCollisions(ArrayList<Damager> damagers){
        for (Damager damager : damagers) {
            Sailor sailor = (Sailor) damager;

            // only attacked if the enemy is not invincible, sailor is attacking, and they overlap
            if (!isInvincible && sailor.getIsAttacking() && overlaps(sailor)) {
                receiveDamage(sailor);
                System.out.println("Sailor inflicts " + sailor.getDamagePoints() + " damage points on " + this +
                        ".  " + printHealth());
                // enemy is deleted if reaches 0 health after an attack
                if (isDead()) {
                    setDeleted(true);
                }

                // enemy becomes invincible if it's not dead after an attack
                else {
                    setInvincibleState();
                }
            }
        }
    }

    @Override
    public void checkObstacleCollisions(ArrayList<Obstacle> obstacles){
        for (Obstacle obstacle : obstacles) {
            // enemy moves back to old position and changes direction upon collisions with an obstacle
            if (!obstacle.isDeleted() && this.overlaps(obstacle)) {
                moveBack();
                changeDirection();
                return;
            }
        }
    }


    @Override
    public void checkBoundaryCollision() {
        // enemy moves back to old position and changes direction upon collisions with game boundary
        if (getLeftX() < getEdgeLeft() || getLeftX() > getEdgeRight() ||
                getTopY() < getEdgeTop() || getTopY() > getEdgeBottom()) {
            moveBack();
            changeDirection();
        }
    }

    /** Method that changes the enemy's direction on collision with game boundary or obstacle
     *
     */
    public void changeDirection() {
        // was moving right, change to left facing image
        if (directionX == 1) {
            if (isInvincible) {
                setCurrentImage(getHIT_LEFT());
            } else {
                setCurrentImage(getLEFT());
            }
        }

        // was moving left, change to right facing image
        if (directionX == -1) {
            if (isInvincible) {
                setCurrentImage(getHIT_RIGHT());
            } else {
                setCurrentImage(getRIGHT());
            }
        }

        // change direction vector
        directionX *= -1;
        directionY *= -1;
    }

    public void move() {
        setOldPoints();
        setLeftX(getLeftX() + getSPEED() * directionX);
        setTopY(getTopY() + getSPEED() * directionY);
    }

    public void update() {
        // increment invincibleCount if the enemy is invincible
        if (isInvincible) {
            invincibleCount++;
            // revert to a not invincible state if invincibility has subsided
            if (invincibleCount >= MAX_INVISIBLE_COUNT) {
                setNotInvincibleState();
            }
        }

        // increment cooldownCount if enemy can't attack
        if (!isCanAttack()) {
            setCooldownCount(getCooldownCount() + 1);
            // enemy can attack again once off cooldown
            if (getCooldownCount() >= getMAX_COOLDOWN_COUNT()) {
                setCanAttack(true);
                setCooldownCount(0);
            }
        }
    }

    /** Method that sets the enemy into a not invincible state
     *
     */
    public void setNotInvincibleState() {
        isInvincible = false;
        invincibleCount = 0;

        // change enemy image to not invincible
        if (getCurrentImage() == getHIT_LEFT()) {
            setCurrentImage(getLEFT());
        } else {
            setCurrentImage(getRIGHT());
        }
    }

    /** Method that sets the enemy into an invincible state
     *
     */
    public void setInvincibleState() {
        isInvincible = true;

        // change enemy image to invincible
        if (getCurrentImage() == getLEFT()) {
            setCurrentImage(getHIT_LEFT());
        } else {
            setCurrentImage(getHIT_RIGHT());
        }
    }

}
