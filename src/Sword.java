import bagel.Image;

/** Represent the Sword item
 *
 */
public class Sword extends Item{
    private final static Image IMAGE = new Image("res/items/sword.png");
    private final static Image IMAGE_ICON = new Image("res/items/swordIcon.png");

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Sword(double leftX, double topY) {
        super(leftX, topY, IMAGE, IMAGE_ICON);
    }

    @Override
    public String toString() {
        return "Sword";
    }

}
