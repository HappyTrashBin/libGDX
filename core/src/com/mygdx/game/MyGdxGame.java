package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private World world;
	private Body player, rightBoarder, leftBoarder,upBoarder,downBoarder, enemy;
	private Box2DDebugRenderer b2dr;
	private float PPM = 32;
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w/2, h/2);

		world = new World(new Vector2(0,0), false);
		b2dr = new Box2DDebugRenderer();

		player = createBox(300,300,32, 32, false);
		rightBoarder = createBox(0,0,1,900, true);
		leftBoarder = createBox(800,0,1,900, true);
		upBoarder = createBox(0,450,1600,1, true);
		downBoarder = createBox(0,0,1600,1, true);
		enemy = createBox(500, 300, 32,32,false);
	}
	@Override
	public void render () {
		update();
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		b2dr.render(world, camera.combined.scl(PPM));
	}
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width/2, height/2);
	}
	@Override
	public void dispose () {
		world.dispose();
		b2dr.dispose();
	}
	public void update() {
		world.step(1/60f, 6, 2);

		inputUpdate();
		cameraUpdate();
		enemyUpdate();
	}
	public void inputUpdate() {
		int horizontalForce = 0;
		int verticalForce = 0;

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			horizontalForce -= 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			horizontalForce += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			verticalForce += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			verticalForce -= 1;
		}
		player.setLinearVelocity(horizontalForce * 5, verticalForce * 5);
	}
	public void cameraUpdate() {
		camera.update();
	}
	public void enemyUpdate() {
		float Dx = (player.getPosition().x * PPM - enemy.getPosition().x * PPM);
		float Dy = (player.getPosition().y * PPM - enemy.getPosition().y * PPM);
		float vector = (float) Math.sqrt(Dx*Dx + Dy*Dy);
		enemy.setLinearVelocity(Dx/vector, Dy/vector);
	}
	public Body createBox(int x, int y, int width, int height, boolean isStatic) {
		Body pBody;
		BodyDef def = new BodyDef();

		if (isStatic) def.type = BodyDef.BodyType.StaticBody;
		else def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(x/PPM,y/PPM);
		def.fixedRotation = true;

		pBody = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2 / PPM, height/2 / PPM);

		pBody.createFixture(shape, 1.0f);
		shape.dispose();
		return pBody;
	}

}
