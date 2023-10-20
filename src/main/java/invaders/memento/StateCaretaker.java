package invaders.memento;

import invaders.engine.GameEngine;

public class StateCaretaker {
    private StateMemento stateHistory;

    public void saveState(GameEngine engine) {
        stateHistory = engine.save();
    }

    public void revertState(GameEngine engine) {
        if (stateHistory == null) {
            System.out.println("No previous state to revert to.");
            return;
        }
        engine.revert(stateHistory);
    }

}
