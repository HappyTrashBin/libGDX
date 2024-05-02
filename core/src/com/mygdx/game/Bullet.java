package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Bullet {
    private final Vector2 point = new Vector2();
    public Body body;
    private final float PPM = Const.PPM;
    private final int size = Const.bulletSize;
    public Bullet(World world, float X0, float Y0, float X1, float Y1) {
        createBody(world, X0, Y0);

        float X2;
        if (X1 - X0 > 0) { X2 = 850;}
        else {X2 = -50;}
        float Y2 = ((Y1 - Y0)*X2 + (Y0*X1 - Y1*X0)) / (X1 - X0);
        point.set(X2, Y2);
    }
    public Vector2 getPoint() {
        return point;
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
        fixtureDef.filter.categoryBits = 4;
        fixtureDef.filter.maskBits = 2;

        this.body = world.createBody(def);
        this.body.createFixture(fixtureDef).setUserData(3);
    }
}
