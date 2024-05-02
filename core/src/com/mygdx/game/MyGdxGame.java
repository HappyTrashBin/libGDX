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
	private Player player;
	private final ArrayList<Enemy> enemies = new ArrayList<>();
	private final ArrayList<Bullet> bullets = new ArrayList<>();
	private ArrayList<Body> deleteList = new ArrayList<>();
	private ArrayList<Body> damageList = new ArrayList<>();
	private Box2DDebugRenderer b2dr;
	private final float PPM = Const.PPM;
	private int enemyCount = 1;
	private boolean gameOver = false;
	@Override
	public void create () {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		resize(w/2, h/2);
		world = new World(new Vector2(0,0), false);
		world.setContactListener(new CollisionProcessing());
		b2dr = new Box2DDebugRenderer();

		player = new Player(world, w/4,h/4);
		createBoarders(w,h);
		createNewEnemies(enemyCount);
	}
	@Override
	public void render () {
		update(player.gameOver);
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
	public void update(boolean gameOver) {
		world.step(1/60f, 6, 2);
		cameraUpdate();

		if (!gameOver) {
			deleteListUpdate();
			inputUpdate();
			enemies.forEach(enemy -> enemyUpdate(enemy));
			bullets.forEach(bullet -> bulletUpdate(bullet));
			playerHealthUpdate();
		}
		else {
			enemies.forEach(enemy -> enemy.body.setLinearVelocity(0,0));
			bullets.forEach(bullet -> bullet.body.setLinearVelocity(0,0));
			player.body.setLinearVelocity(0,0);
		}

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
			addNewBullet();
		}
		player.body.setLinearVelocity(horizontalForce * 5, verticalForce * 5);
	}
	public void cameraUpdate() {
		camera.update();
	}
	public void enemyUpdate(Enemy enemy) {
		if (enemy.health > 0) {
			float Dx = (player.body.getPosition().x * PPM - enemy.body.getPosition().x * PPM);
			float Dy = (player.body.getPosition().y * PPM - enemy.body.getPosition().y * PPM);
			float vector = (float) Math.sqrt(Dx * Dx + Dy * Dy);
			enemy.body.setLinearVelocity(Dx / vector, Dy / vector);
		}
		else {
			if (!enemy.destroyed) {
				enemy.body.setActive(false);
				world.destroyBody(enemy.body);
				enemy.setDestroyed();
			}
		}
		damageListUpdate();
	}
	public void bulletUpdate(Bullet bullet) {
		float Dx = (bullet.getPoint().x - bullet.body.getPosition().x * PPM);
		float Dy = (bullet.getPoint().y - bullet.body.getPosition().y * PPM);
		float vector = (float) Math.sqrt(Dx*Dx + Dy*Dy);
		bullet.body.setLinearVelocity(Dx/vector * 10, Dy/vector * 10);
	}
	public void deleteListUpdate() {
		deleteList = CollisionProcessing.deleteList;
		deleteList.forEach(obj -> {
			obj.setActive(false);
			world.destroyBody(obj);
		});
		CollisionProcessing.clearDeleteList();
	}
	public void damageListUpdate() {
		damageList = CollisionProcessing.damageList;
		damageList.forEach(obj -> enemies.forEach(enemy -> {
			if (enemy.body == obj) {
				//System.out.println("Same body");
				enemy.getDamage(25);
			}
		}));
		CollisionProcessing.clearDamageList();
	}
	public void playerHealthUpdate() {
		if (player.health <= 0) {
			player.youLose();
		}
		playerDamageUpdate();
	}
	public void playerDamageUpdate() {
		boolean damage = CollisionProcessing.playerDamage;
		if (damage) {
			player.getDamage(20);
			CollisionProcessing.playerGotDamage();
		}
	}
 	public void addNewBullet() {
		float X0 = player.body.getPosition().x * PPM;
		float Y0 = player.body.getPosition().y * PPM;
		float X1 = Gdx.input.getX()/2;
		float Y1 = (Gdx.graphics.getHeight() - Gdx.input.getY())/2;
		Bullet bullet = new Bullet(world, X0, Y0, X1, Y1);
		bullets.add(bullet);
	}
	public void createBoarders(int w, int h) {
		Body rightBoarder = createBox(0,0,1,h, true, (short) 2, (short) (2 | 1 | 4),4);
		Body leftBoarder = createBox(w/2,0,1,h, true, (short) 2, (short) (2 | 1 | 4),4);
		Body upBoarder = createBox(0,h/2,w,1, true, (short) 2, (short) (2 | 1 | 4),4);
		Body downBoarder = createBox(0,0,w,1, true, (short) 2, (short) (2 | 1 | 4),4);
	}
	public void createNewEnemies(int count) {
		List<Enemy> newEnemies = IntStream.range(0, count)
				.mapToObj(i -> {
					int x = MathUtils.random(Gdx.graphics.getWidth()/2);
					int y = MathUtils.random(Gdx.graphics.getHeight()/2);
					Enemy enemy = new Enemy(world, x, y);
					return enemy;
				})
				.collect(Collectors.toList());
		enemies.addAll(newEnemies);
	}
	public Body createBox(float x, float y, int width, int height, boolean isStatic, short cBits,short mBits, int ID) {
		Body pBody;
		BodyDef def = new BodyDef();

		if (isStatic) def.type = BodyDef.BodyType.StaticBody;
		else def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(x/PPM,y/PPM);
		def.fixedRotation = true;

		pBody = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2/PPM, height/2/PPM);

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
