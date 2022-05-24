package game.entity;

import game.Main;
import game.World;
import game.entity.enemy.IEnemy;

/**
 * This class represents an action that can be performed by the player. Currently
 * this just means actions that can be performed during a battle. All instances of
 * this class are predetermined, however, this class can be extended to create new
 * player actions.
 */
public non-sealed class PlayerAction extends Action {
    /**
     * This array stores every action the player can perform
     * during a battle.
     */
    public static final PlayerAction[] PLAYER_BATTLE_ACTIONS = new PlayerAction[] {
        // These are the basic hit (attack) options the player has
        new PlayerAction("Weak Hit",   Type.HIT, 300, 0, 0,   0),
        new PlayerAction("Medium Hit", Type.HIT, 500, 0, 150, 3),
        new PlayerAction("Strong Hit", Type.HIT, 900, 0, 400, 8),
        new PlayerAction("Very Strong Hit", Type.HIT, 1200, 0, 700, 15),
        
        // These are the poison (multi-turn damage) attack options the player has
        new PlayerAction("Weak Poison",   Type.POISON, 100, 3, 0,   0 ),
        new PlayerAction("Medium Poison", Type.POISON, 150, 4, 300, 5 ),
        new PlayerAction("Strong Poison", Type.POISON, 300, 5, 600, 10),
        new PlayerAction("Very Strong Poison", Type.POISON, 450, 6, 800, 18),
        
        // These are the options the player has to heal themself
        new PlayerAction("Weak Healing",      Type.HEALING, 250, 0, 200, 0),
        new PlayerAction("Strong Healing",    Type.HEALING, 500, 0, 500, 6),
        new PlayerAction("Sustained Healing", Type.HEALING, 500, 4, 700, 12),
        new PlayerAction("Miraculous Healing", Type.HEALING, 2000, 0, 1000, 16),
        
        // These are the options the player has to protect themself
        new PlayerAction("Weak Protection",   Type.PROTECTION, 0.5, 3, 200, 0),
        new PlayerAction("Strong Protection", Type.PROTECTION, 0.5, 5, 500, 10),
        new PlayerAction("Incredible Protection", Type.PROTECTION, 0.5, 7, 700, 16),
        
        // This is the player's special attack
        new PlayerAction("Special Attack", Type.SPECIAL, 0, 0, 1000, 0)
    };

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
    protected PlayerAction(
        String nameIn, Type typeIn, double effectIn,
        int numTurnsIn, int manaCostIn, int requiredAbilityPointsIn
    ) {
        super(nameIn, typeIn, effectIn, numTurnsIn, manaCostIn, requiredAbilityPointsIn);
    }

    /**
     * This method applys the effect that this action has on the
     * player to the player. Since this is a player action only
     * healing and protection actions will have any effect on the
     * player since all the other actions do damage.
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
        if (type == Action.Type.HIT || type == Action.Type.POISON || type == Action.Type.SPECIAL) {
            return;
        }

        // Create a randomizer on the effect (+/- 100)
        int randomEffect = Main.RANDOM.nextInt(201)-100;
        
        // Apply the action's effect based on its type and show a message	
        switch (type) {
            // Healing actions give the player health back
            case HEALING:
                // Show a different message if it was a sustaind heal
                if (numTurns > 0) {
                    world.showMessage(
                        String.format(
                            "%s will heal %d health for %d turns.",
                            player.getName(),
                            (int)effect+randomEffect, numTurns
                        ),
                        5
                    );
                } else {
                    world.showMessage(
                        String.format("%s healed %d health.", player.getName(), (int)effect+randomEffect),
                        5
                    );
                }

                // Apply the effect
                player.applyHealingEffect((int)effect +randomEffect, numTurns);
                return;
            
            // Protection actions reduce incoming damage by a multiplier
            case PROTECTION:
                // Show a message
                world.showMessage(
                    String.format(
                        "%s will take %.1f%% damage for %d turns",
                        player.getName(),
                        effect * 100,
                        numTurns
                    ),
                    6
                );

                // Apply the effect
                player.applyProtectionEffect(effect, numTurns);
                return;
            
            // Actions other than healing and protection have no effect
            // on the player
            default:
                return;
        }
    }

    /**
     * This method applys the effect that this action has on the
     * enemy to the enemy. Since this is a player action only hit,
     * poison, and special actions will have any effect on the
     * enemy since the other actions provide a positive benefit.
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
        if (type == Action.Type.HEALING || type == Action.Type.PROTECTION) {
            return;
        }
    	
    	  // If the enemy dodges this action then stop executing this method after showing a message
        if (Main.RANDOM.nextDouble() < enemy.getSecondaryAttributeValue(Attribute.DODGE_CHANCE)) {
            // Show a message and then return
            world.showMessage(String.format("Enemy dodged %s's attack.", player.getName()), 5);
            return;
        }

        // Create a randomizer on the effect (+/- 100)
        int randomEffect = Main.RANDOM.nextInt(201)-100;

        // Determine whether this action should be treated as critical or regular
        boolean critical = Main.RANDOM.nextDouble() < player.getSecondaryAttributeValue(Attribute.CRIT_CHANCE);

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
                            "%s dealt a critical hit on enemy for %d damage!",
                            player.getName(),
                            (int)(effect * damageMultiplier +randomEffect)
                        ),
                        6
                    );
                } else {
                    world.showMessage(
                        String.format(
                            "%s hit enemy for %d damage!",
                            player.getName(),
                            (int)effect +randomEffect
                        ),
                        5
                    );
                }

                // Inflict the damage
                enemy.inflictDamage((int)(effect * damageMultiplier +randomEffect));
                return;
            
            // The player's special attack does an amount of damage
            // based on their attributes
            case SPECIAL:
                // Show a different message based on if the hit was critical
                if (critical) {
                    world.showMessage(
                        String.format(
                            "The special attack dealt a critical hit on enemy for %d damage!",
                            (int)(player.getSecondaryAttributeValue(Attribute.SPECIAL_DAMAGE) * damageMultiplier +randomEffect)
                        ),
                        6
                    );
                } else {
                    world.showMessage(
                        String.format(
                            "The special attack dealt %d damage to enemy.",
                            (int)player.getSecondaryAttributeValue(Attribute.SPECIAL_DAMAGE) +randomEffect
                        ),
                        5
                    );
                }

                // Inflict the damage
                enemy.inflictDamage((int)(player.getSecondaryAttributeValue(Attribute.SPECIAL_DAMAGE) * damageMultiplier +randomEffect));
                return;
            
            // Poison actions do damage over multiple turns
            case POISON:
                // Show a message
                world.showMessage(
                    String.format(
                        "%s inflicted poison on enemy dealing %d damage for %d turns.",
                        player.getName(),
                        (int)effect +randomEffect,
                        numTurns
                    ),
                    6
                );

                // Inflict the effect
                enemy.inflictPoison((int)effect+randomEffect, numTurns);
                return;
            
            // Actions other than hit, special, and poison have no effect
            // on the enemy
            default:
                return;
        }
    }
}
