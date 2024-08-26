import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** Represent all game entities
 *
 */
public abstract class Entity {
    private static final int FPS = 60;

    private double leftX;
    private double topY;
    private Image currentImage;
    private boolean deleted = false;

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Entity(double leftX, double topY) {
        this.leftX = leftX;
        this.topY = topY;
    }

    /** Method that draws the entity to the game window
     *
     */
    public void draw() {
        currentImage.drawFromTopLeft(leftX, topY);
    }

    /**
     *
     * @return Returns the boundary of the entity's image
     */
    public Rectangle computeBoundary() {
        return currentImage.getBoundingBoxAt(new Point(leftX + currentImage.getWidth()/2.0,
                                                        topY + currentImage.getHeight()/2.0));
    }

    /**
     *
     * @return Returns the X-coordinate of the top-left corner
     */
    public double getLeftX() {
        return leftX;
    }

    /**
     *
     * @return Returns the Y-coordinate of the top-left corner
     */
    public double getTopY() {
        return topY;
    }

    /** Method that sets the X-coordinate of the top-left corner
     *
     * @param leftX The number to set as the X-coordinate of the top-left corner
     */
    public void setLeftX(double leftX) {
        this.leftX = leftX;
    }

    /** Method that sets the Y-coordinate of the top-left corner
     *
     * @param topY The number to set as the Y-coordinate of the top-left corner
     */
    public void setTopY(double topY) {
        this.topY = topY;
    }

    /**
     *
     * @return Returns whether the entity is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /** Method that flags the entity as deleted
     *
     * @param deleted The boolean value to set whether entity is deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     *
     * @return Returns the frames per second
     */
    public static int getFPS() {
        return FPS;
    }

    /**
     *
     * @return Returns the current image of the entity
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /** Method that sets the current image of the entity
     *
     * @param currentImage The image to set as the current image of the entity
     */
    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

    /** Method that checks for overlapping between two entities
     *
     * @param other The other entity to check overlapping with
     * @return Returns whether the two entities overlap
     */
    public boolean overlaps(Entity other) {
        return computeBoundary().intersects(other.computeBoundary());
    }

}
