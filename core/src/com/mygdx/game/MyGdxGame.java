package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	private float size = 64;
	private final float halfSize = size/2;
	SpriteBatch batch;
	private mainChar me;
	private keyboardAdapter inputProcessor = new keyboardAdapter();
	
	@Override
	public void create () {
		Gdx.input.setInputProcessor(inputProcessor);
		batch = new SpriteBatch();
		me = new mainChar(800 - halfSize, 450 - halfSize, size);
	}

	@Override
	public void render () {
		me.moveTo(inputProcessor.getDirection());
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		me.render(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		me.dispose();
	}
}
