package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private World world;
	private Body player, rightBoarder, leftBoarder,upBoarder,downBoarder, bullet;
	private final ArrayList<Body> enemies = new ArrayList<>();
	private final ArrayList<BulletAndPoint> bullets = new ArrayList<>();
	private Box2DDebugRenderer b2dr;
	private final float PPM = Const.PPM;
	private int deleteCount = 0;
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w/2, h/2);

		world = new World(new Vector2(0,0), false);
		world.setContactListener(new CollisionProcessing(world));
		b2dr = new Box2DDebugRenderer();


		player = createBox(300,300,32, 32, false, (short) 1, (short) 2,1);
		rightBoarder = createBox(0,0,1,900, true, (short) 2, (short) (2 | 1 | 4),2);
		leftBoarder = createBox(800,0,1,900, true, (short) 2, (short) (2 | 1 | 4),2);
		upBoarder = createBox(0,450,1600,1, true, (short) 2, (short) (2 | 1 | 4),2);
		downBoarder = createBox(0,0,1600,1, true, (short) 2, (short) (2 | 1 | 4),2);

		List<Body> newEnemies = IntStream.range(0, 3)
				.mapToObj(i -> {
					int x = MathUtils.random(Gdx.graphics.getWidth()/2);
					int y = MathUtils.random(Gdx.graphics.getHeight()/2);
					Body enemy = createBox(x, y, 32,32,false, (short) 2, (short) (2 | 1 | 4),1);
					return enemy;
				})
				.collect(Collectors.toList());
		enemies.addAll(newEnemies);
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
		enemies.forEach(enemy -> enemyUpdate(enemy));
		bullets.forEach(bullet -> bulletUpdate(bullet.getPoint().x, bullet.getPoint().y));
	}
	public void inputUpdate() {
		int horizontalForce = 0;
		int verticalForce = 0;

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			horizontalForce -= 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			horizontalForce += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			verticalForce += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			verticalForce -= 1;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			BulletAndPoint bulletAndPoint = new BulletAndPoint();
			bullet = createBox((int) (player.getPosition().x * PPM),
					(int) (player.getPosition().y * PPM),
					16,
					16,
					false,
					(short) 4,
					(short) 2,
					3);
			bulletAndPoint.setBullet(bullet, Gdx.input.getX()/2, (Gdx.graphics.getHeight() - Gdx.input.getY())/2);
			bullets.add(bulletAndPoint);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			if (deleteCount < enemies.size()) {
				world.destroyBody(enemies.get(deleteCount));
				deleteCount += 1;
			}
		}
		player.setLinearVelocity(horizontalForce * 5, verticalForce * 5);
	}
	public void cameraUpdate() {
		camera.update();
	}
	public void enemyUpdate(Body enemy) {
		float Dx = (player.getPosition().x * PPM - enemy.getPosition().x * PPM);
		float Dy = (player.getPosition().y * PPM - enemy.getPosition().y * PPM);
		float vector = (float) Math.sqrt(Dx*Dx + Dy*Dy);
		enemy.setLinearVelocity(Dx/vector, Dy/vector);
	}
	public void bulletUpdate(float x, float y) {
		float Dx = (x - bullet.getPosition().x * PPM);
		float Dy = (y - bullet.getPosition().y * PPM);
		float vector = (float) Math.sqrt(Dx*Dx + Dy*Dy);
		bullet.setLinearVelocity(Dx/vector * 10, Dy/vector * 10);
	}

	public Body createBox(int x, int y, int width, int height, boolean isStatic, short cBits,short mBits, int ID) {
		Body pBody;
		BodyDef def = new BodyDef();

		if (isStatic) def.type = BodyDef.BodyType.StaticBody;
		else def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(x/PPM,y/PPM);
		def.fixedRotation = true;

		pBody = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2 / PPM, height/2 / PPM);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.filter.categoryBits = cBits;
		fixtureDef.filter.maskBits = mBits;

		pBody.createFixture(fixtureDef).setUserData(ID);
		shape.dispose();
		return pBody;
	}

}
