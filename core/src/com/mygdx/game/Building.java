package com.mygdx.game;

public class Building {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    public boolean upgraded = false;
    public final int ID;
    public final int points;
    public Building(float x, float y, float width, float height, int ID, int points) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ID = ID;
        this.points = points;
    }
    public float getWidth() {
        return this.width;
    }
    public float getHeight() {
        return height;
    }
    public float getY() {
        return y;
    }
    public float getX() {
        return x;
    }
}
