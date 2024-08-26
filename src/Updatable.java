/** Represents all entities that can update timers with each frame, and change state accordingly
 *
 */
public interface Updatable {
    /** Method that updates the entity's timers, and changes its state accordingly if needed
     *
     */
    void update();
}
