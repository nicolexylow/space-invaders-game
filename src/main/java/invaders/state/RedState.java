package invaders.state;
import invaders.gameobject.Bunker;
import javafx.scene.image.Image;
import java.io.File;

public class RedState implements BunkerState {
    private Bunker bunker;

    public RedState(Bunker bunker){
        this.bunker = bunker;
    }

    @Override
    public void takeDamage() {
        // bunker.

    }
    @Override
    public RedState clone() {
        return new RedState(null);
    }

    @Override
    public void setBunker(Bunker bunker) {
        this.bunker = bunker;
    }
}
