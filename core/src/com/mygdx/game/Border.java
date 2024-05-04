package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

public class Border extends Entity{
    private final static short cBits = 2;
    private final static short mBits = (2 | 1 | 4);
    private final static int ID = 4;
    public Border(World world, float x, float y, float width, float height) {
        super(world, x, y, width, height, cBits, mBits, ID, true);
    }
}
