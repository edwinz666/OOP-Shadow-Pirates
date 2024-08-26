import bagel.Image;

/** Represents the Potion item
 *
 */
public class Potion extends Item{
    private final static Image IMAGE = new Image("res/items/potion.png");
    private final static Image IMAGE_ICON = new Image("res/items/potionIcon.png");

    /**
     *
     * @param leftX X-coordinate of top-left corner
     * @param topY Y-coordinate of top-left corner
     */
    public Potion(double leftX, double topY) {
        super(leftX, topY, IMAGE, IMAGE_ICON);
    }

    @Override
    public String toString() {
        return "Potion";
    }

}
