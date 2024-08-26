import bagel.Image;

/** Represent all obstacles in the game
 *
 */
public abstract class Obstacle extends Entity{

    /**
     *
     * @param leftX X-coordinate of the top-left corner
     * @param topY Y-coordinate of the top-left corner
     * @param NORMAL_IMAGE The normal initial image of the obstacle
     */
    public Obstacle(double leftX, double topY, Image NORMAL_IMAGE) {
        super(leftX, topY);
        setCurrentImage(NORMAL_IMAGE);
    }
}
