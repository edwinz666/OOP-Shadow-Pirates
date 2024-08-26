import java.util.ArrayList;

/** Represents all game entities that are targetable
 *
 */
public interface Targetable {
    /** Method that inflicts damage on the targetable entity, from a damaging entity
     *
     * @param damager The entity that inflicts damage
     */
    void receiveDamage(Damager damager);

    /** Method that checks for collisions with damagers, which will inflict damage on the targetable entity
     *
     * @param damagers The damagers to check for collisions
     */
    void checkDamagerCollisions(ArrayList<Damager> damagers);
}
