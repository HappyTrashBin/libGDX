package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyGdxGame extends ApplicationAdapter {
	private Const constant = new Const();
	private final float halfSize = constant.size/2;
	private final float width = constant.width;
	private final float height = constant.height;
	SpriteBatch batch;
	private mainChar me;
	private final List<mainChar> enemies = new ArrayList<>();
	private keyboardAdapter inputProcessor = new keyboardAdapter();
	private final Vector2 goTo = new Vector2(1,1);
	private final int speedConst = 2;
	@Override
	public void create () {
		Gdx.input.setInputProcessor(inputProcessor);
		batch = new SpriteBatch();
		me = new mainChar(width/2 - halfSize, height/2 - halfSize);
		List<mainChar> newEnemies = IntStream.range(0,15)
				.mapToObj(i -> {
					int x = MathUtils.random(Gdx.graphics.getWidth());
					int y = MathUtils.random(Gdx.graphics.getHeight());

					return new mainChar(x, y, "badlogic.jpg");
				})
				.collect(Collectors.toList());
		enemies.addAll(newEnemies);
	}
	@Override
	public void render () {
		me.moveTo(inputProcessor.getDirection());
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		me.render(batch);
		enemies.forEach(enemy -> {
			enemy.render(batch);
			float xD = me.getPosition().x-enemy.getPosition().x;
			float yD = me.getPosition().y-enemy.getPosition().y;
			float vector = (float) Math.sqrt(Math.pow(xD,2) + Math.pow(yD,2));
			float normalX = xD / vector;
			float normalY = yD / vector;
			goTo.set(speedConst * normalX,speedConst * normalY);
			enemy.moveTo(goTo);
		});
		batch.end();
	}
	@Override
	public void dispose () {
		batch.dispose();
		me.dispose();
	}
}
