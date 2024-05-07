package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;

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
	private final int enemyCount = 5;
	private long time = 0;
	private Screen currentScreen = Screen.TITLE;
	@Override
	public void create () {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		resize(width/2, height/2);
		world = new World(new Vector2(0,0), false);
		world.setContactListener(new CollisionProcessing());
		b2dr = new Box2DDebugRenderer();

		createNewPlayer();
		createBoarders(width,height);
		createNewEnemies(enemyCount);

	}
	@Override
	public void render () {
		if (currentScreen == Screen.TITLE) {
			Gdx.gl.glClearColor(0, 1, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				currentScreen = Screen.MAIN_GAME;
			}
		}
		else if (currentScreen == Screen.MAIN_GAME) {
			update(player.gameOver);
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			b2dr.render(world, camera.combined.scl(PPM));
		}
		else if (currentScreen == Screen.GAME_OVER) {
			Gdx.gl.glClearColor(1, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				currentScreen = Screen.MAIN_GAME;
				createNewPlayer();
				createNewEnemies(enemyCount);
			}
		}
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
		camera.update();

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
			currentScreen = Screen.GAME_OVER;
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
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			addNewBullet();
		}
		player.body.setLinearVelocity(horizontalForce * Const.playerSpeed, verticalForce * Const.playerSpeed);
	}
	public void enemyUpdate(Enemy enemy) {
		if (enemy.health > 0) {
			float Dx = (player.body.getPosition().x * PPM - enemy.body.getPosition().x * PPM);
			float Dy = (player.body.getPosition().y * PPM - enemy.body.getPosition().y * PPM);
			float vector = (float) Math.sqrt(Dx * Dx + Dy * Dy);
			enemy.body.setLinearVelocity(Dx / vector * Const.enemiesSpeed, Dy / vector * Const.enemiesSpeed);
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
			if (enemy.body == obj) enemy.getDamage(Const.playerDamage);
		}));
		CollisionProcessing.clearDamageList();
	}
	public void playerHealthUpdate() {
		if (player.health <= 0) {
			player.youLose();
		}

		boolean damage = CollisionProcessing.playerDamage;
		if (damage) {
			player.getDamage(Const.enemiesDamage);
			CollisionProcessing.playerGotDamage();
		}
	}
 	public void addNewBullet() {
		if ((TimeUtils.timeSinceMillis(time) >= Const.playerAttackSpeed) || (time == 0)) {
			float X0 = player.body.getPosition().x * PPM;
			float Y0 = player.body.getPosition().y * PPM;
			float X1 = Gdx.input.getX() / 2;
			float Y1 = (Gdx.graphics.getHeight() - Gdx.input.getY()) / 2;
			Bullet bullet = new Bullet(world, X0, Y0, X1, Y1);
			bullets.add(bullet);
			time = TimeUtils.millis();
		}
	}
	public void createNewPlayer() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		if (player != null) {world.destroyBody(player.body);}
		player = new Player(world, width/4,height/4);
	}
	public void createBoarders(int width, int height) {
		Border rightBoarder = new Border(world,0,0,1,height);
		Border leftBoarder = new Border(world,width/2,0,1,height);
		Border upBoarder = new Border(world,0,height/2,width,1);
		Border downBoarder = new Border(world,0,0,width,1);
	}
	public void createNewEnemies(int count) {
		enemies.forEach(enemy -> world.destroyBody(enemy.body));
		enemies.clear();
		List<Enemy> newEnemies = IntStream.range(0, count)
				.mapToObj(i -> {
					int x = getSpawnPosition(true);
					int y = getSpawnPosition(false);
					return new Enemy(world, x, y);
				})
				.collect(Collectors.toList());
		enemies.addAll(newEnemies);
	}
	public void addNewEnemy() {
		int x = getSpawnPosition(true);
		int y = getSpawnPosition(false);
		Enemy enemy = new Enemy(world, x, y);
		enemies.add(enemy);
	}
	public int getSpawnPosition(boolean isX) {
		int pos;
		float playerPos;
		if (isX) {
			pos = MathUtils.random(Gdx.graphics.getWidth()/2);
			playerPos = player.body.getPosition().x * PPM;
		}
		else {
			pos = MathUtils.random(Gdx.graphics.getHeight()/2);
			playerPos = player.body.getPosition().y * PPM;
		}

		if ((playerPos + Const.safeZone < pos) || (playerPos - Const.safeZone > pos)) {
			return pos;
		}
		else {
			return getSpawnPosition(isX);
		}
	}
}
