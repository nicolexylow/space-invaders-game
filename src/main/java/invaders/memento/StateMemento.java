package invaders.memento;

import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.List;

public class StateMemento {
    private int score;
    private long elapsedMillis;
    private List<Renderable> renderables;
    private List<GameObject> gameObjects;


    public StateMemento(int score, long elapsedMillis, List<Renderable> renderables, List<GameObject> gameObjects) {
        this.score = score;
        this.elapsedMillis = elapsedMillis;
        this.renderables = renderables;
        this.gameObjects = gameObjects;
    }

    public int getScore() { return score; }

    public long getElapsedMillis() { return elapsedMillis; }

    public List<Renderable> getRenderables() { return renderables; }

    public List<GameObject> getGameObjects() { return gameObjects; }
}
