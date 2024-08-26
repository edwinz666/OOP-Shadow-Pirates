import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

/** Represents attacks launched by enemies
 *
 */
public class Projectile extends Entity implements Movable, Damager{
    private final double DIRECTION_X;
    private final double DIRECTION_Y;
    private final double SPEED;
    private final int DAMAGE_POINTS;
    private final DrawOptions option = new DrawOptions();
    private final String enemyName;

    private static int edgeLeft;
    private static int edgeRight;
    private static int edgeTop;
    private static int edgeBottom;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     * @param DIRECTION_X Horizontal direction vector, not standardised
     * @param DIRECTION_Y Vertical direction vector, not standardised
     * @param SPEED Speed of the projectile
     * @param DAMAGE_POINTS Damage point value of the projectile
     * @param IMAGE Image of the projectile
     * @param enemyName Name of enemy who launched the projectile
     */
    public Projectile(double leftX, double topY, double DIRECTION_X, double DIRECTION_Y,
                      double SPEED, int DAMAGE_POINTS, Image IMAGE, String enemyName) {
        super(leftX, topY);

        // normalise direction vector
        Vector2 d = new Vector2(DIRECTION_X, DIRECTION_Y);
        this.DIRECTION_X = d.normalised().x;
        this.DIRECTION_Y = d.normalised().y;

        // sets rotation of the projectile
        double rotation = Math.atan(DIRECTION_Y / DIRECTION_X);
        option.setRotation(rotation);

        this.SPEED = SPEED;
        this.DAMAGE_POINTS = DAMAGE_POINTS;
        setCurrentImage(IMAGE);

        this.enemyName = enemyName;
    }

    /**
     *
     * @param edgeLeft X-coordinate of Top-Left corner of game boundary
     * @param edgeTop Y-coordinate of Top-Left corner of game boundary
     */
    public static void setTopLeftEdges(int edgeLeft, int edgeTop) {
        Projectile.edgeLeft = edgeLeft;
        Projectile.edgeTop = edgeTop;
    }

    /**
     *
     * @param edgeRight X-coordinate of Bottom-Right corner of game boundary
     * @param edgeBottom Y-coordinate of Bottom-Right corner of game boundary
     */
    public static void setBottomRightEdges(int edgeRight, int edgeBottom) {
        Projectile.edgeRight = edgeRight;
        Projectile.edgeBottom = edgeBottom;
    }

    public int getDamagePoints() {
        return DAMAGE_POINTS;
    }

    /**
     *
     * @return Returns the name of the Enemy subclass who launched the projectile
     */
    public String getEnemyName() {
        return enemyName;
    }

    public void move(){
        setLeftX(getLeftX() + SPEED * DIRECTION_X);
        setTopY(getTopY() + SPEED * DIRECTION_Y);
    }

    /**
     *
     * @param other The other entity to check overlapping with
     * @return Returns whether the projectile overlaps with an entity, based on the projectile's centre
     */
    @Override
    public boolean overlaps(Entity other) {
        // find centre of projectile
        double centreX = getLeftX() + getCurrentImage().getWidth()/2.0;
        double centreY = getTopY() + getCurrentImage().getHeight()/2.0;
        Point centre = new Point(centreX, centreY);

        return other.computeBoundary().intersects(centre);
    }

    /** Method that draws the projectile according to its rotation
     *
     */
    @Override
    public void draw() {
        getCurrentImage().drawFromTopLeft(getLeftX(), getTopY(), option);
    }

    /** Method that checks whether projectile has reached the game boundaries
     *
     */
    public void checkBoundaryCollision() {
        // deletes projectile if it reaches the game boundaries
        if (getLeftX() < edgeLeft || getLeftX() > edgeRight || getTopY() < edgeTop || getTopY() > edgeBottom) {
            setDeleted(true);
        }
    }

}
