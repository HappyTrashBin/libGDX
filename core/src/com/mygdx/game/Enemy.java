package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

public class Enemy {
    public Body body;
    public int health = 100;
    public boolean destroyed = false;
    private final float PPM = Const.PPM;
    private final int size = Const.enemySize;
    public Enemy(World world, float x, float y) {
        createBody(world, x, y);
    }
    public void getDamage(int damage) {
        this.health -= damage;
    }
    public void setDestroyed() {
        this.destroyed = true;
    }
    private void createBody(World world, float x, float y) {
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x/PPM,y/PPM);
        def.fixedRotation = true;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size/2/PPM, size/2/PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = 2;
        fixtureDef.filter.maskBits = (2 | 1 | 4);

        this.body = world.createBody(def);
        this.body.createFixture(fixtureDef).setUserData(2);
    }
}
