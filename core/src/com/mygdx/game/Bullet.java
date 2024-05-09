package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Bullet extends Entity{
    private final static int size = Const.bulletSize;
    private final static short cBits = 4;
    private final static short mBits = 2;
    private final static int ID = 3;
    private final static float density = 0.01f;
    private final Vector2 point = new Vector2();
    public Bullet(World world, float X0, float Y0, float X1, float Y1) {
        super(world, X0, Y0, size, size, cBits, mBits, ID, false, true, density);

        float X2;
        if (X1 - X0 > 0) { X2 = 850;}
        else {X2 = -50;}
        float Y2 = ((Y1 - Y0)*X2 + (Y0*X1 - Y1*X0)) / (X1 - X0);
        point.set(X2, Y2);
    }
    public Vector2 getPoint() {
        return point;
    }
}