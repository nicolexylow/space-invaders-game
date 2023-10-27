package invaders.memento;

import invaders.engine.GameState;
import invaders.entities.Player;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.List;

public class StateMemento {
    private int score;
    private long elapsedMillis;
    private List<Enemy> enemies;
    private List<Bunker> bunkers;
    private Player player;
    private List<Projectile> playerProjectiles;
    private GameState gameState;

    public StateMemento(int score, long elapsedMillis, List<Enemy> enemies, List<Bunker> bunkers, Player player, List<Projectile> playerProjectiles, GameState gameState) {
        this.score = score;
        this.elapsedMillis = elapsedMillis;
        this.enemies = enemies;
        this.bunkers = bunkers;
        this.player = player;
        this.playerProjectiles = playerProjectiles;
        this.gameState = gameState;
    }

    public int getScore() { return score; }

    public long getElapsedMillis() { return elapsedMillis; }

    public List<Enemy> getEnemies() { return enemies; }

    public List<Bunker> getBunkers() { return bunkers; }
    public Player getPlayer() { return player; }
    public List<Projectile> getPlayerProjectiles() { return playerProjectiles; }
    public GameState getGameState() { return gameState; }
}
