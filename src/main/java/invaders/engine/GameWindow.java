package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.ConfigReader;
import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import invaders.gameobject.Enemy;
import invaders.memento.StateCaretaker;
import invaders.observer.GamePanel;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import org.json.simple.JSONObject;

public class GameWindow {
	private final int width;
    private final int height;
	private Scene scene;
    private Pane pane;
    private GameEngine model;
    private GamePanel gamePanel;
    private StateCaretaker stateCaretaker = new StateCaretaker();
    private List<EntityView> entityViews =  new ArrayList<EntityView>();
    private Renderable background;

    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    // private static final double VIEWPORT_MARGIN = 280.0;

	public GameWindow(GameEngine model, GamePanel gamePanel){
        this.model = model;
        this.gamePanel = gamePanel;
		this.width =  model.getGameWidth();
        this.height = model.getGameHeight();

        pane = new Pane();
        scene = new Scene(pane, width, height);

        VBox panelBox = gamePanel.getPanelBox();
        panelBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.getChildren().add(panelBox);

        // SAVE AND UNDO BUTTON
        Button saveButton = new Button("Save");
        saveButton.setFocusTraversable(false);
        saveButton.setOnAction(e -> {
            stateCaretaker.saveState(model);
        });

        Button undoButton = new Button("Undo");
        undoButton.setFocusTraversable(false);
        undoButton.setOnAction(e -> {
            for (EntityView view : entityViews) {
                pane.getChildren().remove(view.getNode());
            }
            entityViews.clear();

            stateCaretaker.revertState(model);
        });

        // CHEAT BUTTONS
        // enemies
        Button removeFastEnemyButton = new Button("Remove Fast Enemies");
        removeFastEnemyButton.setFont(new Font(10));
        removeFastEnemyButton.setFocusTraversable(false);
        removeFastEnemyButton.setOnAction(e -> model.removeEnemy("fast"));

        Button removeSlowEnemyButton = new Button("Remove Slow Enemies");
        removeSlowEnemyButton.setFont(new Font(10));
        removeSlowEnemyButton.setFocusTraversable(false);
        removeSlowEnemyButton.setOnAction(e -> model.removeEnemy("slow"));

        // projectiles
        Button removeFastProjectile = new Button("Remove Fast Projectiles");
        removeFastProjectile.setFont(new Font(10));
        removeFastProjectile.setFocusTraversable(false);
        removeFastProjectile.setOnAction(e -> model.removeProjectile("fast"));

        Button removeSlowProjectile = new Button("Remove Slow Projectiles");
        removeSlowProjectile.setFont(new Font(10));
        removeSlowProjectile.setFocusTraversable(false);
        removeSlowProjectile.setOnAction(e -> model.removeProjectile("slow"));

        HBox firstLine = new HBox(10);
        firstLine.getChildren().addAll(removeFastEnemyButton, removeSlowEnemyButton);

        HBox secondLine = new HBox(10);
        secondLine.getChildren().addAll(removeFastProjectile, removeSlowProjectile);

        VBox groupedButtons = new VBox(10);  // 10 is the spacing between lines
        groupedButtons.getChildren().addAll(firstLine, secondLine);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, undoButton, groupedButtons);
        buttonBox.layoutXProperty().bind(pane.widthProperty().subtract(buttonBox.widthProperty().add(10)));
        buttonBox.setLayoutY(10);

        pane.getChildren().add(buttonBox);

        this.background = new SpaceBackground(model, pane);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(this.model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

    }

	public void run() {
         Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

         timeline.setCycleCount(Timeline.INDEFINITE);
         timeline.play();
    }


    private void draw(){
        model.update();

        List<Renderable> renderables = model.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }


        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);

    }

	public Scene getScene() {
        return scene;
    }
}
