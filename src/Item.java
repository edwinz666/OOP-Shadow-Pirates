import bagel.Image;

/** Represents all items in the game
 *
 */
public abstract class Item extends Entity{
    private final Image ICON_IMAGE;

    /**
     *
     * @param leftX X-coordinate of the top-left corner
     * @param topY Y-coordinate of the top-left corner
     * @param IMAGE The item's image when displayed normally in the game
     * @param ICON_IMAGE The item's image as an icon when collected by sailor
     */
    public Item(double leftX, double topY, Image IMAGE, Image ICON_IMAGE) {
        super(leftX, topY);
        this.ICON_IMAGE = ICON_IMAGE;
        setCurrentImage(IMAGE);
    }

    /**
     *
     * @return Returns the item's image as an icon, after being collected by sailor
     */
    public Image getICON_IMAGE() {
        return ICON_IMAGE;
    }

    /**
     *
     * @return Returns the item's name as a String for printing messages to the command line
     */
    public abstract String toString();
}
