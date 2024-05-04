package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

public class Enemy extends Entity{
    private final static int size = Const.enemySize;
    private final static short cBits = 2;
    private final static short mBits = (2 | 1 | 4);
    private final static int ID = 2;
    public int health = 100;
    public boolean destroyed = false;
    public Enemy(World world, float x, float y) {
        super(world, x, y, size, size, cBits, mBits, ID, false);
    }
    public void getDamage(int damage) {
        this.health -= damage;
    }
    public void setDestroyed() {
        this.destroyed = true;
    }
}
