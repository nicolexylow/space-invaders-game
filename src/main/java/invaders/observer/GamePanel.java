package invaders.observer;

import invaders.engine.GameState;
import invaders.gameobject.Enemy;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GamePanel implements Observer {
    private int score = 0;
    private String time = "0:00";
    private long startTime;
    private GameState currentState = GameState.ONGOING;

    // javafx display elements
    private VBox panelBox;
    private Text scoreText;
    private Text timerText;

    public GamePanel(Subject subject) {
        subject.attach(this);

        startTime = System.currentTimeMillis();

        scoreText = new Text("Player's Score: " + score);
        timerText = new Text("Time Elapsed: " + time);

        panelBox = new VBox();
        panelBox.setPadding(new Insets(10));
        panelBox.getChildren().addAll(timerText, scoreText);
    }

    public VBox getPanelBox() {
        return panelBox;
    }

    public void setScore(String target) {
        switch(target) {
            case "slowEnemyProjectile":
                score += 1;
                break;
            case "fastEnemyProjectile":
                score += 2;
                break;
            case "slowEnemy":
                score += 3;
                break;
            case "fastEnemy":
                score += 4;
                break;
        }
    }

    public void updateTime() {
        long elapsedMillis = System.currentTimeMillis() - this.startTime;
        long totalSeconds = elapsedMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        time = String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void update() {
        scoreText.setText("Player's Score: " + score);

        // timer does not update if game is won or lost
        if (currentState == GameState.ONGOING) {
            updateTime();
            timerText.setText("Time Elapsed: " + time);
        }
    }

    public void setCurrentState(GameState state) {
        currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}

