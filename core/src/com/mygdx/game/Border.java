package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

public class Border extends Entity{
    private final static short cBits = 8;
    private final static short mBits = (1 | 2 | 4);
    private final static int ID = 4;
    private final static float density = 1.0f;
    public Border(World world, float x, float y, float width, float height) {
        super(world, x, y, width, height, cBits, mBits, ID, true, false, density);
    }
}