package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

public class Building {
    public Body body;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    public boolean destroyed = false;
    public Building(World world, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        createBody(world, x, y);
    }
    private void createBody(World world, float x, float y) {
        float PPM = Const.PPM;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x/PPM,y/PPM);
        def.fixedRotation = true;

        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / PPM, height / PPM);
        body.createFixture(shape, 1.0f);
        shape.dispose();

        this.body = world.createBody(def);
    }
    public float getWidth() {
        return this.width;
    }
    public float getHeight() {
        return height;
    }
    public float getY() {
        return y;
    }
    public float getX() {
        return x;
    }
}
