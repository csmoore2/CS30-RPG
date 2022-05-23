package game.entity;

import game.Main;
import game.World;
import game.entity.enemy.IEnemy;

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
     * @param world  the world
     * @param player the player
     * @param enemy  the enemy
     * 
     * @see Action#applyPlayerEffect(World, Player, IEnemy)
     */
    @Override
    public void applyPlayerEffect(World world, Player player, IEnemy enemy) {
        // If this action has no effect on the player then do not continue
        if (type == Action.Type.HEALING || type == Action.Type.PROTECTION) {
            return;
        }

        // If the player dodges this action then stop executing this method after showing a message
        if (Main.RANDOM.nextDouble() < player.getSecondaryAttributeValue(Attribute.DODGE_CHANCE)) {
            // Show a message and then return
            world.showMessage("Player dodged enemy's attack.", 2);
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
                // Show a different message based on if the hit was critical
                if (critical) {
                    world.showMessage(
                        String.format(
                            "Enemy dealt a critical hit to %s for %d damage!",
                            player.getName(),
                            (int)(effect * damageMultiplier)
                        ),
                        4
                    );
                } else {
                    world.showMessage(
                        String.format(
                            "Enemy hit %s for %d damage!",
                            player.getName(),
                            (int)effect
                        ),
                        3
                    );
                }

                // Inflict the damage
                player.inflictDamage((int)(effect * damageMultiplier));
                return;
            
            // Poison actions do damage over multiple turns
            case POISON:
                // Show a message
                world.showMessage(
                    String.format(
                        "Enemy inflicted poison on %s dealing %d damage for %d turns.",
                        player.getName(),
                        (int)effect,
                        numTurns
                    ),
                    4
                );

                // Inflict the effect
                player.inflictPoison((int)effect, numTurns);
                return;
            
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
     * @param world  the world
     * @param enemy  the enemy
     * @param player the player
     * 
     * @see Action#applyEnemyEffect(World, IEnemy, Player)
     */
    @Override
    public void applyEnemyEffect(World world, IEnemy enemy, Player player) {
        // If this action has no effect on the enemy then do not continue
        if (type == Action.Type.HIT || type == Action.Type.POISON || type == Action.Type.SPECIAL) {
            return;
        }

        // Apply the action's effect based on its type
        switch (type) {
            // Healing actions give the enemy health back
            case HEALING:
                // Show a message
                world.showMessage(
                    String.format(
                        "Enemy healed %d health.",
                        (int)effect
                    ),
                    3
                );

                // Give the enemy the health
                enemy.addHealth((int)effect);
                return;
            
            // Actions other than healing have no effect on the enemy
            default:
                return;
        }
    }
}
