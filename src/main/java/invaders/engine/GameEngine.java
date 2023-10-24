package invaders.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import invaders.ConfigReader;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.EnemyProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.memento.StateMemento;
import invaders.observer.GamePanel;
import invaders.observer.Observer;
import invaders.observer.Subject;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import org.json.simple.JSONObject;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine implements Subject, Cloneable {
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();

	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();

	private List<Renderable> renderables =  new ArrayList<>();

	private List<Observer> observers = new ArrayList<>();

	private Player player;

	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;

	public GameEngine(String config){
		// Read the config here
		ConfigReader configReader = ConfigReader.getInstance(config);

		// Get game width and height
		gameWidth = ((Long)((JSONObject) configReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long)((JSONObject) configReader.getGameInfo().get("size")).get("y")).intValue();

		//Get player info
		this.player = new Player(configReader.getPlayerInfo());
		renderables.add(player);


		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		//Get Bunkers info
		for(Object eachBunkerInfo:configReader.getBunkersInfo()){
			Bunker bunker = director.constructBunker(bunkerBuilder, (JSONObject) eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
		}


		EnemyBuilder enemyBuilder = new EnemyBuilder();
		//Get Enemy info
		for(Object eachEnemyInfo:configReader.getEnemiesInfo()){
			Enemy enemy = director.constructEnemy(this,enemyBuilder,(JSONObject)eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
		}

	}


	/**
	 * Updates the game/simulation
	 */
	public void update(){
		timer+=1;

		movePlayer();

		for(GameObject go: gameObjects){
			go.update(this);

			if (go instanceof Enemy && !((Enemy) go).isAlive()) {
				pendingToRemoveGameObject.add(go);
			}
		}

		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i+1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				if((renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))
						||(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("Enemy"))||
						(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))){
				}else{
					if(renderableA.isColliding(renderableB) && (renderableA.getHealth()>0 && renderableB.getHealth()>0)) {
						renderableA.takeDamage(1);
						renderableB.takeDamage(1);


						// OBSERVER PATTERN
						// used to identify the relevant renderable objects and set the score based on the strategies
						for (Observer observer : observers) {
							if (observer instanceof GamePanel) {
								GamePanel gamePanel = (GamePanel) observer;

								// if renderableA is an Enemy and the collision was with a PlayerProjectile, update the score.
								if (renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("PlayerProjectile")) {
									if (((Enemy) renderableA).getProjectileStrategy() instanceof FastProjectileStrategy) {
										gamePanel.setScore("fastEnemy");
									} else if (((Enemy) renderableA).getProjectileStrategy() instanceof SlowProjectileStrategy) {
										gamePanel.setScore("slowEnemy");
									}
								} else if (renderableA.getRenderableObjectName().equals("PlayerProjectile") && renderableB.getRenderableObjectName().equals("Enemy")) {
									if (((Enemy) renderableB).getProjectileStrategy() instanceof FastProjectileStrategy) {
										gamePanel.setScore("fastEnemy");
									} else if (((Enemy) renderableB).getProjectileStrategy() instanceof SlowProjectileStrategy) {
										gamePanel.setScore("slowEnemy");
									}
								} else if (renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("PlayerProjectile")) {
									if (((EnemyProjectile) renderableA).getStrategy() instanceof FastProjectileStrategy) {
										gamePanel.setScore("fastEnemyProjectile");
									} else if (((EnemyProjectile) renderableA).getStrategy() instanceof SlowProjectileStrategy) {
										gamePanel.setScore("slowEnemyProjectile");
									}
								} else if (renderableA.getRenderableObjectName().equals("PlayerProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile")) {
									if (((EnemyProjectile) renderableB).getStrategy() instanceof FastProjectileStrategy) {
										gamePanel.setScore("fastEnemyProjectile");
									} else if (((EnemyProjectile) renderableB).getStrategy() instanceof SlowProjectileStrategy) {
										gamePanel.setScore("slowEnemyProjectile");
									}
								}
							}
						}
					}
				}
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
				ro.getPosition().setX((gameWidth - offset) -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(offset);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
				ro.getPosition().setY((gameHeight - offset) -ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(offset);
			}
		}

		if (allEnemiesDestroyed()) {
			for (Observer observer : observers) {
				if (observer instanceof GamePanel) {
					((GamePanel) observer).setCurrentState(GameState.WON);
				}
			}
		}

		notifyObservers();

	}

	public boolean allEnemiesDestroyed() {
		for (GameObject go : gameObjects) {
			if (go instanceof Enemy) {
				return false;
			}
		}
		return true;
	}

	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}


	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}
	public void rightPressed(){
		this.right = true;
	}

	public boolean shootPressed(){
		if(timer>45 && player.isAlive()){
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer=0;
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public void attach(Observer observer) { observers.add(observer); }

	@Override
	public void detach(Observer observer) { observers.add(observer); }

	@Override
	public void notifyObservers() {
		for (Observer observer : observers) {
			observer.update();
		}
	}

	public GamePanel getGamePanel() {
		for (Observer observer : observers) {
			if (observer instanceof GamePanel) {
				return (GamePanel) observer;
			}
		}
		return null;
	}


	//--------------------------------------------------------
	// MEMENTO PATTERN

	public StateMemento save() {

		List<Enemy> enemies = new ArrayList<>();
		List<Bunker> bunkers = new ArrayList<>();
		Player player = null;

		for (Renderable ro : renderables) {
			if (ro.getRenderableObjectName().equals("Enemy")) {
				enemies.add((Enemy) ro.clone());
			} else if (ro.getRenderableObjectName().equals("Bunker")) {
				bunkers.add((Bunker) ro.clone());
			} else if (ro.getRenderableObjectName().equals("Player")) {
				player = (Player) ro.clone();
			}
		}

		long currentElapsedMillis = System.currentTimeMillis() - getGamePanel().getStartTime();
		return new StateMemento(getGamePanel().getScore(), currentElapsedMillis, enemies, bunkers, player);
	}

	public void revert(StateMemento stateMemento) {

		renderables.clear();
		gameObjects.clear();

		// enemy revert
		for (Enemy enemy : stateMemento.getEnemies()) {
			Enemy newEnemy = enemy.clone();
			for (Projectile projectile : newEnemy.getProjectiles()) {
				Projectile newProjectile = projectile;
				renderables.add(newProjectile);
				gameObjects.add(newProjectile);
			}
			renderables.add(newEnemy);
			gameObjects.add(newEnemy);
		}

		// bunker revert
		for (Bunker bunker : stateMemento.getBunkers()) {
			Bunker newBunker = bunker.clone();
			renderables.add(newBunker);
			gameObjects.add(newBunker);
		}

		// player revert
		renderables.add(player);


		getGamePanel().manualSetScore(stateMemento.getScore());
		getGamePanel().manualSetStartTime(System.currentTimeMillis() - stateMemento.getElapsedMillis());
	}

	//--------------------------------------------------------
	// CHEAT FEATURE

	public void remove(String strategy) {
		for (GameObject go : gameObjects) {
			if (strategy.equals("fast")) {
				if (go instanceof Enemy && ((Enemy) go).getProjectileStrategy() instanceof FastProjectileStrategy ) {
					((Enemy) go).setLives(0);
					pendingToRemoveGameObject.add(go);
					getGamePanel().manualSetScore(getGamePanel().getScore() + 4);
				}

				if (go instanceof EnemyProjectile && ((EnemyProjectile) go).getStrategy() instanceof FastProjectileStrategy) {
					((EnemyProjectile) go).takeDamage(1);
					pendingToRemoveGameObject.add(go);
					getGamePanel().manualSetScore(getGamePanel().getScore() + 2);
				}
			} else if (strategy.equals("slow")) {
				if (go instanceof Enemy && ((Enemy) go).getProjectileStrategy() instanceof SlowProjectileStrategy ) {
					((Enemy) go).setLives(0);
					pendingToRemoveGameObject.add(go);
					getGamePanel().manualSetScore(getGamePanel().getScore() + 3);
				}

				if (go instanceof EnemyProjectile && ((EnemyProjectile) go).getStrategy() instanceof SlowProjectileStrategy) {
					((EnemyProjectile) go).takeDamage(1);
					pendingToRemoveGameObject.add(go);
					getGamePanel().manualSetScore(getGamePanel().getScore() + 1);
				}
			}
		}
	}
}
