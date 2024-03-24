package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	private Const constant = new Const();
	private final float halfSize = constant.size/2;
	private final float width = constant.width;
	private final float height = constant.height;
	SpriteBatch batch;
	private mainChar me;
	private keyboardAdapter inputProcessor = new keyboardAdapter();
	@Override
	public void create () {
		Gdx.input.setInputProcessor(inputProcessor);
		batch = new SpriteBatch();
		me = new mainChar(width/2 - halfSize, height/2 - halfSize);
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
