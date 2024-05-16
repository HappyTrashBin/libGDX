package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity{
    private final static int size = Const.playerSize;
    private final static short cBits = 1;
    private final static short mBits = (2 | 8);
    private final static int ID = 1;
    private final static float density = 20.0f;
    public int health = 100;
    public boolean gameOver = false;

    public Player(World world, float x, float y) {
        super(world, x, y, size, size, cBits, mBits, ID, false, true, density);
    }
    public void getDamage(int damage) {
        this.health -= damage;
    }
    public void youLose() {
        this.gameOver = true;
    }
    public void moreHealth() {
        this.health = 100;
        this.health += 50;
    }
}