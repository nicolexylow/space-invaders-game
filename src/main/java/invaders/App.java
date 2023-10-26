package invaders;

import invaders.observer.GamePanel;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import invaders.engine.GameEngine;
import invaders.engine.GameWindow;
import org.json.simple.JSONObject;

import java.util.Map;

public class App extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showDifficultySelectionScreen();
    }

    private void showDifficultySelectionScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Text heading = new Text("Select Difficulty");
        heading.setFont(new Font(20));

        Button easyButton = new Button("Easy");
        easyButton.setFocusTraversable(false);
        easyButton.setOnAction(e -> startGame("src/main/resources/config_easy.json"));

        Button mediumButton = new Button("Medium");
        mediumButton.setFocusTraversable(false);
        mediumButton.setOnAction(e -> startGame("src/main/resources/config_medium.json"));

        Button hardButton = new Button("Hard");
        hardButton.setFocusTraversable(false);
        hardButton.setOnAction(e -> startGame("src/main/resources/config_hard.json"));

        root.getChildren().addAll(heading, easyButton, mediumButton, hardButton);

        Scene scene = new Scene(root, 600, 800);
        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame(String configPath) {
        GameEngine model = new GameEngine(configPath);
        GameWindow window = new GameWindow(model, new GamePanel(model));
        window.run();

        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(window.getScene());
        primaryStage.show();

        window.run();
    }
}
