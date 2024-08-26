import bagel.Image;

/** Bomb is a type of obstacle found on level 1.
 *
 */
public class Bomb extends Obstacle implements Damager, Updatable{
    private final static int DAMAGE_POINTS = 10;
    private final static int MAX_EXPLODED_COUNT = 500 * Entity.getFPS() / 1000;
    private final static Image NORMAL_IMAGE = new Image("res/bomb.png");
    private final static Image EXPLOSION_IMAGE = new Image("res/explosion.png");

    private boolean hasExploded = false;
    private int explodedCount = 0;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY X-coordinate of top-left corner
     */
    public Bomb(double leftX, double topY) {
        super(leftX, topY, NORMAL_IMAGE);
    }

    public int getDamagePoints() {
        return DAMAGE_POINTS;
    }

    /**
     *
     * @return Returns whether the bomb has exploded
     */
    public boolean isHasExploded() {
        return hasExploded;
    }

    /** Sets the bomb to an exploded state
     *
     */
    public void setExplodedState() {
        this.hasExploded = true;
        setCurrentImage(EXPLOSION_IMAGE);
    }

    public void update() {
        // increment explodedCount if bomb has exploded
        if (!isDeleted() && hasExploded) {
            explodedCount++;
            // bomb is deleted once expired
            if (explodedCount >= MAX_EXPLODED_COUNT) {
                setDeleted(true);
            }
        }
    }

}
