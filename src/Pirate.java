import bagel.Image;

/** Represents a Pirate, a type of Enemy
 *
 */
public class Pirate extends Enemy{
    private static final Image HIT_LEFT = new Image("res/pirate/pirateHitLeft.png");
    private static final Image HIT_RIGHT = new Image("res/pirate/pirateHitRight.png");
    private static final Image LEFT = new Image("res/pirate/pirateLeft.png");
    private static final Image RIGHT = new Image("res/pirate/pirateRight.png");
    private static final String PROJECTILE_FILE = "res/pirate/pirateProjectile.png";

    private static final double PROJECTILE_SPEED = 0.4;
    private static final int ATTACK_DISTANCE = 100;
    private static final int DAMAGE_POINTS = 10;
    private static final int MAX_COOLDOWN_COUNT = 3000 * Entity.getFPS() / 1000;
    private static final int MAX_HEALTH = 45;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Pirate(double leftX, double topY) {
        super(leftX, topY, HIT_LEFT, HIT_RIGHT, LEFT, RIGHT, MAX_HEALTH, DAMAGE_POINTS,
                MAX_COOLDOWN_COUNT, ATTACK_DISTANCE, PROJECTILE_FILE, PROJECTILE_SPEED);
    }

    @Override
    public String toString() {
        return "Pirate";
    }

}
