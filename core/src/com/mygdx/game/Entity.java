package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

public class Entity {
    public Body body;
    private final float width;
    private final float height;
    private final short cBits;
    private final short mBits;
    private final int ID;
    private final float density;
    public Entity(World world, float x, float y, float width, float height, short cBits, short mBits, int ID, boolean isStatic, boolean isRound, float density) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;
        this.mBits = mBits;
        this.ID = ID;
        this.density = density;
        createBody(world, x, y, isStatic, isRound);
    }
    private void createBody(World world, float x, float y, boolean isStatic, boolean isRound) {
        float PPM = Const.PPM;
        BodyDef def = new BodyDef();

        if (isStatic) def.type = BodyDef.BodyType.StaticBody;
        else def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x/PPM,y/PPM);
        def.fixedRotation = true;

        FixtureDef fixtureDef = new FixtureDef();

        if (isRound) {
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(width / 2 / PPM);
            fixtureDef.shape = circleShape;
        }
        else {
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2 / PPM, height / 2 / PPM);
            fixtureDef.shape = shape;
        }

        fixtureDef.density = density;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;


        this.body = world.createBody(def);
        this.body.createFixture(fixtureDef).setUserData(ID);
    }
}