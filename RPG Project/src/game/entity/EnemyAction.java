package game.entity;

import game.Main;

/**
 * This class represents an action that can be preformed by an enemy. Currently
 * this just means actions that can be performed during a battle. While this class
 * is very simple it can be extended to create more complex enemy actions.
 */
public non-sealed class EnemyAction extends Action {
    /**
	 * This creates a PlayerAction using all of the given parameters as the
	 * values for its properties.
	 * 
	 * @param nameIn                  the name of this action
	 * @param typeIn                  the type of action
	 * @param effectIn                a number representing the action's effect (its meaning
	 *                                is determined by the action's type)
	 * @param numTurnsIn              the number of turns this action lasts
	 * @param manaCostIn              the amount of mana required to use this action
	 * @param requiredAbilityPointsIn the number of points in the ability attribute the
	 *                                player must have to use this action
     * 
     * @see Action#Action(String, Type, double, int, int, int)
	 */
    public EnemyAction(String nameIn, Type typeIn, double effectIn, int numTurnsIn) {
        super(nameIn, typeIn, effectIn, numTurnsIn, 0, 0);
    }

    /**
     * This method applys the effect that this action has on the
     * player to the player. Since this is an enemy action only hit
     * and poison actions will have any effect on the player since
     * all the other actions have a positive benefit.
     * 
     * @param player the player
     * @param enemy  the enemy
     * 
     * @see Action#applyPlayerEffect(Player, IEnemy)
     */
    @Override
    public void applyPlayerEffect(Player player, IEnemy enemy) {
        // If the player dodges this action then stop executing this method
        if (Main.RANDOM.nextDouble() < player.getSecondaryAttributeValue(Attribute.DODGE_CHANCE)) {
            return;
        }

        // Determine whether this action should be treated as critical or regular
        boolean critical = Main.RANDOM.nextDouble() < enemy.getSecondaryAttributeValue(Attribute.CRIT_CHANCE);

        // Determine the damage multiplier based on whether or not this action is
        // regular or critical
        double damageMultiplier = critical ? Action.CRITICAL_DAMAGE_MULTIPLIER : 1.0;

        // Apply the action's effect based on its type
        switch (type) {
            // Hit actions do a predetermined amount of action
            case HIT:
                player.inflictDamage((int)(effect * damageMultiplier));
                return;
            
            // Poison actions do damage over multiple turns
            case POISON:
            player.inflictPoison((int)effect, numTurns);
            
            // Actions other than hit and poison have no effect on the player
            default:
                return;
        }
    }

    /**
     * This method applys the effect that this action has on the
     * enemy to the enemy. Since this is an enemy action only
     * healing actions will have any effect on the enemy since
     * all other actions either do damage or are unsupported for
     * enemies.
     * 
     * @param enemy  the enemy
     * @param player the player
     * 
     * @see Action#applyEnemyEffect(IEnemy, Player)
     */
    @Override
    public void applyEnemyEffect(IEnemy enemy, Player player) {
        // Apply the action's effect based on its type
        switch (type) {
            // Healing actions give the enemy health back
            case HEALING:
                enemy.addHealth((int)effect);
                return;
            
            // Actions other than healing have no effect on the enemy
            default:
                return;
        }
    }
}
