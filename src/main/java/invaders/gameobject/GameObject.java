package invaders.gameobject;

import invaders.engine.GameEngine;

// contains basic methods that all GameObjects must implement
public interface GameObject {

    public void start();
    public void update(GameEngine model);

    // allow for deep copying used in the memento pattern so that it does not reference the same gameObjects list
    public GameObject clone();
}
