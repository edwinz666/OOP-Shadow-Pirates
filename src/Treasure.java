import bagel.Image;

/** Represents the treasure to be found
 *
 */
public class Treasure extends Entity{
    private final static Image IMAGE = new Image("res/treasure.png");

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Treasure(double leftX, double topY) {
        super(leftX, topY);
        setCurrentImage(IMAGE);
    }
}
