import bagel.Image;

/** Represents the Elixir item.
 *
 */
public class Elixir extends Item{
    private final static Image IMAGE = new Image("res/items/elixir.png");
    private final static Image IMAGE_ICON = new Image("res/items/elixirIcon.png");

    /**
     *
     * @param leftX X-coordinate of the top-left corner
     * @param topY Y-coordinate of the top-left corner
     */
    public Elixir(double leftX, double topY) {
        super(leftX, topY, IMAGE, IMAGE_ICON);
    }

    @Override
    public String toString() {
        return "Elixir";
    }

}
