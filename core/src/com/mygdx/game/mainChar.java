package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class mainChar {
    private float size;
    private final Vector2 position = new Vector2();
    private final Texture texture;
    public mainChar(float x, float y, float s) {
        texture = new Texture("badlogic.jpg");
        position.set(x, y);
        size = s;
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
}
