package invaders.factory;

import invaders.engine.GameEngine;
import invaders.gameobject.GameObject;
import invaders.physics.Collider;
import invaders.physics.Vector2D;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;

public class EnemyProjectile extends Projectile{
    private ProjectileStrategy strategy;

    public EnemyProjectile(Vector2D position, ProjectileStrategy strategy, Image image) {
        super(position,image);
        this.strategy = strategy;
    }

    @Override
    public void update(GameEngine model) {
        strategy.update(this);

        if(this.getPosition().getY()>= model.getGameHeight() - this.getImage().getHeight()){
            this.takeDamage(1);
        }

    }

    @Override
    public String getRenderableObjectName() {
        return "EnemyProjectile";
    }

    public ProjectileStrategy getStrategy() { return strategy; }

    @Override
    public Projectile clone() {
        Projectile clonedProjectile = new EnemyProjectile(getPosition().clone(), strategy, getImage());
        return clonedProjectile;
    }
}
