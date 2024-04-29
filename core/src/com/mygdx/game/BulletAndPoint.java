package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletAndPoint {
    private final Vector2 point = new Vector2();
    private Body bullet;
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
}
