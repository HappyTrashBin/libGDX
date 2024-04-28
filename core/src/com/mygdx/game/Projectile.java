package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private final Const constant = new Const();
    private final float size = constant.projectileSize;
    private final Vector2 position = new Vector2();
    private final Vector2 point = new Vector2();
    private final Texture texture;
    public Projectile(float x, float y, float X, float Y, String textureName) {
        texture = new Texture(textureName);
        position.set(x, y);
        float newX = (X - x)*50 + x;
        float newY = (Y - y)*50 + y;
        point.set(newX, newY);
    }
    public void render (Batch batch) {
        batch.draw(texture, position.x, position.y, size, size);
    }
    public void dispose () {
        texture.dispose();
    }
    public void moveTo(Vector2 direction) {
        position.add(direction);
    }
    public Vector2 getPosition() {return position;}
    public Vector2 getPoint() {return point;}
}
