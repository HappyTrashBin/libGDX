package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class keyboardAdapter extends InputAdapter {
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;
    private boolean spacePressed;
    private final Const constant = new Const();
    private final float size = constant.charSize;
    private final float halfSize = size/2;
    private final float maxWidth = constant.width - size;
    private final float minWidth = 0;
    private final float maxHeight = constant.height - size;
    private final float minHeight = 0;
    private final Vector2 direction = new Vector2();
    private Vector2 currentPosition = new Vector2(constant.width/2 - halfSize,constant.height/2 - halfSize);

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) leftPressed = true;
        if (keycode == Input.Keys.D) rightPressed = true;
        if (keycode == Input.Keys.W) upPressed = true;
        if (keycode == Input.Keys.S) downPressed = true;
        if (keycode == Input.Keys.SPACE) spacePressed = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) leftPressed = false;
        if (keycode == Input.Keys.D) rightPressed = false;
        if (keycode == Input.Keys.W) upPressed = false;
        if (keycode == Input.Keys.S) downPressed = false;
        if (keycode == Input.Keys.SPACE) spacePressed = false;
        return false;
    }
    public Vector2 getDirection() {
        direction.set(0,0);
        if ((leftPressed) && (currentPosition.x > minWidth)) direction.add(-5,0);
        if ((rightPressed) && (currentPosition.x < maxWidth)) direction.add(5,0);
        if ((upPressed) && (currentPosition.y < maxHeight)) direction.add(0,5);
        if ((downPressed) && (currentPosition.y > minHeight)) direction.add(0,-5);
        currentPosition.add(direction.x,direction.y);

        return direction;
    }
    public boolean space(){
        return spacePressed;
    }
}
