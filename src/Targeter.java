/** Represents all entities that can target another entity
 *
 */
public interface Targeter {
    /** Launches an attack on the target if the target is in range
     *
     * @param target The target that the entity is targeting
     * @return Returns a projectile directed at the target if the target is in range
     */
    Damager targetAttack(Targetable target);
}
