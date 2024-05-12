package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

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
	private final int enemyCount = 2;
	private Screen currentScreen = Screen.TITLE;
	private boolean canShoot = true;
	private boolean newEnemy = true;
	private boolean gameWin = false;
	private SpriteBatch batch;
	private TextureRegion backgroundTextureTitle;
	private TextureRegion backgroundTextureMain;
	private TextureRegion backgroundTextureOver;
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

		batch = new SpriteBatch();
		backgroundTextureTitle = new TextureRegion(new Texture("title.png"), 1600, 900);
		backgroundTextureMain = new TextureRegion(new Texture("field.png"), 1600, 900);
		backgroundTextureOver = new TextureRegion(new Texture("game_over.png"), 1600, 900);

	}
	@Override
	public void render () {
		if (currentScreen == Screen.TITLE) {
			batch.begin();
			batch.draw(backgroundTextureTitle, 0, 0);
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				currentScreen = Screen.MAIN_GAME;
			}
			batch.end();
		}
		else if (currentScreen == Screen.MAIN_GAME) {
			batch.begin();
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.draw(backgroundTextureMain, 0, 0);
			batch.draw(new Texture("YellowC.png"),
					player.body.getPosition().x * PPM - 16,
					player.body.getPosition().y * PPM - 16,
					32,
					32);
			enemies.forEach(enemy -> {
				if (enemy.health > 0) {
					batch.draw(new Texture("RedC.png"),
							enemy.body.getPosition().x * PPM - 16,
							enemy.body.getPosition().y * PPM - 16,
							32,
							32);
				}
			});
			update(player.gameOver);
			b2dr.render(world, camera.combined.scl(PPM));
			batch.end();
		}
		else if (currentScreen == Screen.GAME_OVER) {
			batch.begin();
			batch.draw(backgroundTextureOver, 0, 0, 800, 450);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				currentScreen = Screen.MAIN_GAME;
				bullets.forEach(bullet -> world.destroyBody(bullet.body));
				clearBullets();
				createNewPlayer();
				enemies.forEach(enemy -> world.destroyBody(enemy.body));
				enemies.clear();
				createNewEnemies(enemyCount);
			}
			batch.end();
		}
		else if (currentScreen == Screen.WIN) {
			batch.begin();
			batch.draw(backgroundTextureTitle, 0, 0, 800, 450);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				currentScreen = Screen.MAIN_GAME;
				clearBullets();
				createNewPlayer();
				enemies.clear();
				createNewEnemies(enemyCount);
				gameWin = false;
			}
			batch.end();
		}
	}
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width/2, height/2);
	}
	@Override
	public void dispose () {
		batch.dispose();
		world.dispose();
		b2dr.dispose();
	}
	public void update(boolean gameOver) {
		world.step(1/60f, 6, 2);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		if (!gameOver && !gameWin) {
			deleteListUpdate();
			inputUpdate();

			bullets.forEach(bullet -> bulletUpdate(bullet));
			enemies.forEach(enemy -> enemyUpdate(enemy));
			gameWin = allDeadEnemiesCheck();

			playerHealthUpdate();
		}
		else if (gameOver && !gameWin){
			enemies.forEach(enemy -> enemy.body.setLinearVelocity(0,0));
			bullets.forEach(bullet -> bullet.body.setLinearVelocity(0,0));
			player.body.setLinearVelocity(0,0);
			currentScreen = Screen.GAME_OVER;
		}
		else if (!gameOver && gameWin) {
			enemies.forEach(enemy -> enemy.body.setLinearVelocity(0,0));
			bullets.forEach(bullet -> bullet.body.setLinearVelocity(0,0));
			player.body.setLinearVelocity(0,0);
			currentScreen = Screen.WIN;
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
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			addNewBullet();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			gameWin = true;
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
		if (canShoot) {
			canShoot = false;
			float X0 = player.body.getPosition().x * PPM;
			float Y0 = player.body.getPosition().y * PPM;
			float X1 = Gdx.input.getX() / 2;
			float Y1 = (Gdx.graphics.getHeight() - Gdx.input.getY()) / 2;
			Bullet bullet = new Bullet(world, X0, Y0, X1, Y1);
			bullets.add(bullet);
			Timer timer = new Timer();
			timer.scheduleTask(new Timer.Task() {
				@Override
				public void run() {
					canShoot = true;
				}
			}, Const.playerAttackSpeed/1000f);
		}
	}
	public void addNewEnemy() {
		if (newEnemy) {
			newEnemy = false;
			int x = getSpawnPosition(true);
			int y = getSpawnPosition(false);
			Enemy enemy = new Enemy(world, x, y);
			enemies.add(enemy);
			Timer timer = new Timer();
			timer.scheduleTask(new Timer.Task() {
				@Override
				public void run() {
					newEnemy = true;
				}
			}, Const.enemiesSpawnTime/1000f);
		}
	}
	public boolean allDeadEnemiesCheck() {
		boolean allDead = true;
		for (Enemy enemy : enemies) {
			if (!enemy.destroyed) {
				allDead = false;
			}
		}
		return allDead;
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
		List<Enemy> newEnemies = IntStream.range(0, count)
				.mapToObj(i -> {
					int x = getSpawnPosition(true);
					int y = getSpawnPosition(false);
					return new Enemy(world, x, y);
				})
				.collect(Collectors.toList());
		enemies.addAll(newEnemies);
	}
	public void clearBullets() {
		bullets.clear();
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