package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class newChar {
    private final Const constant = new Const();
    private final float size = constant.charSize;
    private final Vector2 position = new Vector2();
    private final Texture texture;
    public newChar(float x, float y) {
        this(x, y, "BlueC.png");
    }
    public newChar(float x, float y, String textureName) {
        texture = new Texture(textureName);
        position.set(x, y);
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
}
