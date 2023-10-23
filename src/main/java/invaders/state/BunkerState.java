package invaders.state;

import invaders.gameobject.Bunker;

public interface BunkerState {
    public void takeDamage();
    public BunkerState clone();
    public void setBunker(Bunker bunker);
}
