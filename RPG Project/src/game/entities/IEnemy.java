package game.entities;

import game.EnemyAction;

/**
 * This interface extends ILivingEntity to inherit methods common
 * to both players and enemies, and this interface declares methods
 * that are unique to enemies.
 */
public interface IEnemy extends ILivingEntity {
    /**
     * This method is called by the battle screen when it is the enemy's turn
     * and the enemy needs to do an action. This method will then generate the
     * enemy's action and return it for the battle screen to carry out.
     * 
     * @param player the player
     * 
     * @return the enemy's action for their turn in battle
     */
    EnemyAction generateBattleAction(Player player);

    /**
     * This method resets the state of the enemy so that it can be fought
     * again.
     */
    void reset();
}
