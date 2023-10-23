package invaders.memento;

import invaders.entities.Player;
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


    public StateMemento(int score, long elapsedMillis, List<Enemy> enemies, List<Bunker> bunkers, Player player) {
        this.score = score;
        this.elapsedMillis = elapsedMillis;
        this.enemies = enemies;
        this.bunkers = bunkers;
        this.player = player;
    }

    public int getScore() { return score; }

    public long getElapsedMillis() { return elapsedMillis; }

    public List<Enemy> getEnemies() { return enemies; }

    public List<Bunker> getBunkers() { return bunkers; }

    public Player getPlayer() { return player; }
}
