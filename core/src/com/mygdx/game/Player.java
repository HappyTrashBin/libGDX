package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

public class Player {
    public Body body;
    private final float PPM = Const.PPM;
    private final short cBits = 1;
    private final short mBits = 2;
    private final int ID = 1;
    private final int size = 32;
    public Player(World world, float x, float y) {
        createBody(world, x, y);
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
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;

        this.body = world.createBody(def);
        this.body.createFixture(fixtureDef).setUserData(ID);
    }
}
