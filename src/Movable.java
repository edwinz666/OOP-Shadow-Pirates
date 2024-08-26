/** Represent all moving entities
 *
 */
public interface Movable {
    /** moves the entity accordingly
     *
     */
    void move();

    /** Method that checks whether the moving entity has collided with the game boundary
     *
     */
    void checkBoundaryCollision();
}
