package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class BulletAndPoint {
    private final Vector2 point = new Vector2();
    private Body bullet;
    private final float PPM = Const.PPM;
    private final short cBits = 4;
    private final short mBits = 2;
    private final int ID = 3;
    private final short size = 16;
    public void setBullet(Body bullet, int x, int y) {
        this.bullet = bullet;
        float X = bullet.getPosition().x * Const.PPM;
        float Y = bullet.getPosition().y * Const.PPM;

        float newX;
        if (x - X > 0) { newX = 850;}
        else {newX = -50;}
        float newY = (y - Y)/(x - X)*newX + (Y*x - y*X)/(x - X);
        point.set(newX, newY);
    }
    public Vector2 getPoint() {
        return point;
    }
    public Body createBullet(World world, float x, float y) {
        Body pBody;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x/PPM,y/PPM);
        def.fixedRotation = true;

        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size/2/PPM, size/2/PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;

        pBody.createFixture(fixtureDef).setUserData(ID);
        shape.dispose();
        return pBody;
    }
}
