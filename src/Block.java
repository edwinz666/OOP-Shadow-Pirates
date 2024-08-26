import bagel.Image;

/** Block is a type of obstacle found on level 0.
 *
 */
public class Block extends Obstacle{
    private final static Image NORMAL_IMAGE = new Image("res/block.png");

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Block(double leftX, double topY) {
        super(leftX, topY, NORMAL_IMAGE);
    }

}
