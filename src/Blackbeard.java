import bagel.Image;

/** Represents a Blackbeard, a type of enemy that is stronger than a normal pirate.
 * Has different stats compared to a normal pirate, as well as its image and projectile image
 */
public class Blackbeard extends Enemy{
    private static final Image HIT_LEFT = new Image("res/blackbeard/blackbeardHitLeft.png");
    private static final Image HIT_RIGHT = new Image("res/blackbeard/blackbeardHitRight.png");
    private static final Image LEFT = new Image("res/blackbeard/blackbeardLeft.png");
    private static final Image RIGHT = new Image("res/blackbeard/blackbeardRight.png");
    private static final String PROJECTILE_FILE = "res/blackbeard/blackbeardProjectile.png";

    private static final double PROJECTILE_SPEED = 0.8;
    private static final int ATTACK_DISTANCE = 200;
    private static final int DAMAGE_POINTS = 20;
    private static final int MAX_COOLDOWN_COUNT = 1500 * Entity.getFPS() / 1000;
    private static final int MAX_HEALTH = 90;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Blackbeard(double leftX, double topY) {
        super(leftX, topY, HIT_LEFT, HIT_RIGHT, LEFT, RIGHT, MAX_HEALTH, DAMAGE_POINTS,
                MAX_COOLDOWN_COUNT, ATTACK_DISTANCE, PROJECTILE_FILE, PROJECTILE_SPEED);
    }

    @Override
    public String toString() {
        return "Blackbeard";
    }

}
