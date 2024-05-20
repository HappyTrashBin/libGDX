package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	private int enemyCount = 2;
	private Screen currentScreen = Screen.TITLE;
	private boolean canShoot = true;
	private boolean newEnemy = true;
	private boolean gameWin = false;
	private boolean pause = false;
	private boolean canGetUpgrade = true;
	private int damageUpdateCount = 0;
	private int speedUpdateCount = 0;
	private int healthUpdateCount = 0;
	private SpriteBatch batch;
	private TextureRegion backgroundTextureTitle;
	private TextureRegion backgroundTextureMain;
	private TextureRegion backgroundTextureOver;
	private TextureRegion backgroundTextureWin;
	private TextureRegion WhiteRect;
	private TextureRegion RedRect;
	private TextureRegion pauseTexture;
	private BitmapFont font;
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
		backgroundTextureWin = new TextureRegion(new Texture("game_win.png"), 1600, 900);
		pauseTexture = new TextureRegion(new Texture("pause.png"), 320, 180);
		WhiteRect = new TextureRegion(new Texture("WhiteRect.png"), 400, 100);
		RedRect = new TextureRegion(new Texture("RedRect.png"), 400, 100);
		font = new BitmapFont();
		font.setColor(0,0,0,1);
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
			bullets.forEach(bullet -> {
				if (bullet.health > 0) {
					batch.draw(new Texture("BlueC.png"),
							bullet.body.getPosition().x * PPM - 8,
							bullet.body.getPosition().y * PPM - 8,
							16,
							16);
				}
			});
			drawHealth(batch);
			if (pause) {
				batch.draw(pauseTexture, 640/2, 360/2, 160, 80);
			}
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
			batch.draw(backgroundTextureWin, 0, 0, 800, 450);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			drawUpgrades(batch);
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				currentScreen = Screen.MAIN_GAME;
				bullets.forEach(bullet -> {
					if (!bullet.destroyed) {
						world.destroyBody(bullet.body);
					}
				});
				clearBullets();
				createNewPlayer();
				enemies.clear();
				createNewEnemies(enemyCount);
				gameWin = false;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && canGetUpgrade && (damageUpdateCount < 3)) {
				System.out.println("Damage +");
				Const.playerDamage += 10;
				damageUpdateCount += 1;
				canGetUpgrade = false;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) && canGetUpgrade && (speedUpdateCount < 3)) {
				System.out.println("Speed +");
				Const.playerSpeed += 1;
				speedUpdateCount += 1;
				canGetUpgrade = false;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) && canGetUpgrade && (healthUpdateCount < 3)) {
				System.out.println("Health +");
				Const.playerHealth += 20;
				healthUpdateCount += 1;
				canGetUpgrade = false;
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
			if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
				pause = !pause;
			}
			if (!pause) {
				inputUpdate();

				bullets.forEach(bullet -> bulletUpdate(bullet));
				enemies.forEach(enemy -> enemyUpdate(enemy));
				gameWin = allDeadEnemiesCheck();

				playerHealthUpdate();
			}
			else {
				enemies.forEach(enemy -> enemy.body.setLinearVelocity(0,0));
				bullets.forEach(bullet -> bullet.body.setLinearVelocity(0,0));
				player.body.setLinearVelocity(0,0);
			}
		}
		else if (gameOver && !gameWin){
			enemies.forEach(enemy -> enemy.body.setLinearVelocity(0,0));
			bullets.forEach(bullet -> bullet.body.setLinearVelocity(0,0));
			player.body.setLinearVelocity(0,0);
			damageUpdateCount = 0;
			speedUpdateCount = 0;
			healthUpdateCount = 0;

			currentScreen = Screen.GAME_OVER;
		}
		else if (!gameOver && gameWin) {
			enemies.forEach(enemy -> enemy.body.setLinearVelocity(0,0));
			bullets.forEach(bullet -> bullet.body.setLinearVelocity(0,0));
			player.body.setLinearVelocity(0,0);
			enemyCount += 1;
			canGetUpgrade = true;

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
		if (bullet.health > 0) {
			float Dx = (bullet.getPoint().x - bullet.body.getPosition().x * PPM);
			float Dy = (bullet.getPoint().y - bullet.body.getPosition().y * PPM);
			float vector = (float) Math.sqrt(Dx * Dx + Dy * Dy);
			bullet.body.setLinearVelocity(Dx / vector * 10, Dy / vector * 10);
		}
		else {
			if (!bullet.destroyed) {
				bullet.body.setActive(false);
				world.destroyBody(bullet.body);
				bullet.setDestroyed();
			}
		}
	}
	public void damageListUpdate() {
		damageList = CollisionProcessing.damageList;
		damageList.forEach(obj -> {
			enemies.forEach(enemy -> {
				if (enemy.body == obj) enemy.getDamage(Const.playerDamage);
			});
			bullets.forEach(bullet -> {
				if (bullet.body == obj) bullet.getDamage(20);
			});
		});
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
	public void drawHealth(Batch batch){
		font.draw(batch, "Health", 5, 445);
		font.draw(batch, String.valueOf(player.health), 5, 428);
		batch.draw(WhiteRect, 35, 415, 75, 15);
		batch.draw(RedRect, 37, 417, 71*player.health/100, 11);
	}
	public void drawUpgrades(Batch batch){
		font.draw(batch, "Damage upgrades", 140, 170);
		font.draw(batch, "Speed upgrades", 343, 170);
		font.draw(batch, "Health upgrades", 547, 170);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				batch.draw(WhiteRect, 160 + i*200, 50 + j*30, 80, 20);
			}
		}
		for (int i = 0; i < damageUpdateCount; i++) {
			batch.draw(RedRect, 162, 52 + i*30, 76, 16);
		}
		for (int i = 0; i < speedUpdateCount; i++) {
			batch.draw(RedRect, 362, 52 + i*30, 76, 16);
		}
		for (int i = 0; i < healthUpdateCount; i++) {
			batch.draw(RedRect, 562, 52 + i*30, 76, 16);
		}
	}
}