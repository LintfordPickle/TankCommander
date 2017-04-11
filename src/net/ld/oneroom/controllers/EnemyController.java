package net.ld.oneroom.controllers;

import java.util.Random;

import net.ld.library.core.time.GameTime;
import net.ld.oneroom.screens.GameScreen;
import net.ld.oneroom.world.EnemyManager;
import net.ld.oneroom.world.EnemyManager.ENEMY_STATE;
import net.ld.oneroom.world.EnemyManager.EnemyEntity;
import net.ld.oneroom.world.TankEntity;

public class EnemyController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final float SIGHT_RANGE = 1500;
	private static final float MINIMUM_SHOOTING_RANGE = 1000;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameScreen mGameScreen;
	private EnemyManager mEnemyManager;
	private TankEntity mPlayer;

	private Random mRandom;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EnemyController() {
		mRandom = new Random();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameScreen pGameScreen, EnemyManager pEnemyManager, TankEntity pPlayerEntity) {
		mGameScreen = pGameScreen;
		mEnemyManager = pEnemyManager;
		mPlayer = pPlayerEntity;

	}

	public void update(GameTime pGameTime) {
		// Check for collisions with the player
		// if the player is moving, then squash
		final int lNumEnemies = mEnemyManager.enemies().size();
		for (int i = 0; i < lNumEnemies; i++) {
			EnemyEntity lEnemy = mEnemyManager.enemies().get(i);

			// Waiting to be cleared
			if (!lEnemy.isAlive) {
				continue;
			}

			if (lEnemy.health <= 0) {
				lEnemy.state(ENEMY_STATE.dead_Shot);
				lEnemy.kill();
				continue;
			}

			float exx = mPlayer.x - lEnemy.x;
			float eyy = mPlayer.y - lEnemy.y;

			float dist = (float) Math.sqrt(exx * exx + eyy * eyy);

			if (dist <= (16f + mPlayer.hitRadius)) {
				lEnemy.state(ENEMY_STATE.dead_squashed);
				lEnemy.isAlive = false;
				continue;

			}

			switch (lEnemy.state()) {
			case idle:
				idle(lEnemy);
				break;

			case defending:
				defend(lEnemy);
				break;

			case attacking:
				attack(lEnemy);
				break;

			case chasing:
				chase(lEnemy);
				break;

			case wandering:
				wander(lEnemy);
				break;

			default:
				break;

			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void spawnEnemy(int pType, float pX, float pY) {
		final float lRAND_RANGE = 128f;
		float pRandX = mRandom.nextFloat() * lRAND_RANGE * 2 - lRAND_RANGE;
		float pRandY = mRandom.nextFloat() * lRAND_RANGE * 2 - lRAND_RANGE;

		mEnemyManager.addEnemyInstance(pType, pX + pRandX, pY + pRandY);

	}

	private void idle(EnemyEntity pEnemy) {
		float lHeadingVectorX = mPlayer.x - pEnemy.x;
		float lHeadingVectorY = mPlayer.y - pEnemy.y;

		float lDistToPlayer = (float) Math
				.sqrt((lHeadingVectorX * lHeadingVectorX) + (lHeadingVectorY * lHeadingVectorY));
		lHeadingVectorX /= lDistToPlayer;
		lHeadingVectorY /= lDistToPlayer;

		// Slow amble towards the player
		final float RUNNING_SPEED = 0.006f;

		// Add random vector to wander path

		// I feel like the enemy should be running around now :s
		pEnemy.dx += lHeadingVectorX * RUNNING_SPEED * pEnemy.runSpeedMod;
		pEnemy.dy += lHeadingVectorY * RUNNING_SPEED * pEnemy.runSpeedMod;

		// Check transition qualifiers
		if (lDistToPlayer < 2300f) {
			pEnemy.state(ENEMY_STATE.chasing);
		}

	}

	private void wander(EnemyEntity pEnemy) {

	}

	private void defend(EnemyEntity pEnemy) {

	}

	private void attack(EnemyEntity pEnemy) {
		float lHeadingVectorX = mPlayer.x - pEnemy.x;
		float lHeadingVectorY = mPlayer.y - pEnemy.y;

		float lDistToPlayer = (float) Math
				.sqrt((lHeadingVectorX * lHeadingVectorX) + (lHeadingVectorY * lHeadingVectorY));

		// Check transition condition
		if (lDistToPlayer > MINIMUM_SHOOTING_RANGE) {
			pEnemy.state(ENEMY_STATE.chasing);
			return;
		}

		pEnemy.rotation = (float) Math.atan2(lHeadingVectorY, lHeadingVectorX) + (float) Math.toRadians(-90);

		// Should the enemy move towards the player
		if (lDistToPlayer > 350f) {

			// normalise heading
			lHeadingVectorX /= lDistToPlayer;
			lHeadingVectorY /= lDistToPlayer;

			// Move forward while attacking
			// TODO: Randomise chance of this
			final float RUNNING_SPEED = 0.02f;

			// I feel like the enemy should be running around now :s
			pEnemy.dx += lHeadingVectorX * RUNNING_SPEED * pEnemy.runSpeedMod;
			pEnemy.dy += lHeadingVectorY * RUNNING_SPEED * pEnemy.runSpeedMod;

		}

		float lNextShotTime = (pEnemy.mCurrentShotNum < pEnemy.mShotsPerBurst) ? 50 : pEnemy.mTimeBetweenShots;

		if (pEnemy.mShootTimer > lNextShotTime * pEnemy.shootSpeedMod) {

			float lShotAng = pEnemy.rotation;
			final float RAND_AMT = 30.0f;
			lShotAng += (float) Math.toRadians((mRandom.nextFloat() * RAND_AMT - RAND_AMT / 2)) + Math.toRadians(90);

			final float BULLET_SPEED = 1000.0f;
			float lShootVecX = (float) Math.cos(lShotAng) * BULLET_SPEED;
			float lShootVecY = (float) Math.sin(lShotAng) * BULLET_SPEED;

			mGameScreen.enemyBullets().addParticle(pEnemy.x, pEnemy.y, lShootVecX, lShootVecY, 1500f);

			pEnemy.mCurrentShotNum++;
			if (pEnemy.mCurrentShotNum >= pEnemy.mShotsPerBurst + 1) {
				pEnemy.mCurrentShotNum = 0;
			}

			pEnemy.mShootTimer = 0;

		}

	}

	private void chase(EnemyEntity pEnemy) {
		float lHeadingVectorX = mPlayer.x - pEnemy.x;
		float lHeadingVectorY = mPlayer.y - pEnemy.y;

		pEnemy.rotation = (float) Math.atan2(lHeadingVectorY, lHeadingVectorX) + (float) Math.toRadians(-90);

		final float l = 0.45f;

		float lDistToPlayer = (float) Math
				.sqrt((lHeadingVectorX * lHeadingVectorX) + (lHeadingVectorY * lHeadingVectorY));
		lHeadingVectorX /= lDistToPlayer;
		lHeadingVectorY /= lDistToPlayer;

		lHeadingVectorX += lerp(mRandom.nextFloat() * 4f - 2f, -l, l);
		lHeadingVectorY += lerp(mRandom.nextFloat() * 4f - 2f, -l, l);

		final float RUNNING_SPEED = 0.25f;

		// I feel like the enemy should be running around now :s
		pEnemy.dx += (lHeadingVectorX) * RUNNING_SPEED * pEnemy.runSpeedMod;
		pEnemy.dy += (lHeadingVectorY) * RUNNING_SPEED * pEnemy.runSpeedMod;

		// Check transition qualifiers
		if (lDistToPlayer < MINIMUM_SHOOTING_RANGE) {
			pEnemy.state(ENEMY_STATE.attacking);

		} else if (lDistToPlayer > SIGHT_RANGE) {
			pEnemy.state(ENEMY_STATE.idle);

		}

	}

	public static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

}
