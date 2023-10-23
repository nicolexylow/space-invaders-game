package invaders.memento;

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



    public StateMemento(int score, long elapsedMillis, List<Enemy> enemies) {
        this.score = score;
        this.elapsedMillis = elapsedMillis;
        this.enemies = enemies;
        this.bunkers = bunkers;
    }

    public int getScore() { return score; }

    public long getElapsedMillis() { return elapsedMillis; }

    public List<Enemy> getEnemies() { return enemies; }

    public List<Bunker> getBunkers() { return bunkers; }
}
