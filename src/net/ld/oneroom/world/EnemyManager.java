package net.ld.oneroom.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.ld.library.cellworld.entities.CellEntity;
import net.ld.library.core.camera.Camera;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.screens.GameScreen;

/** Just a collection of enemy instances */
public class EnemyManager {

	Random mRandom = new Random();

	public enum ENEMY_STATE {
		idle, chasing, attacking, wandering, defending, dead_Shot, dead_squashed;

	}

	public class EnemyEntity extends CellEntity {

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		public ENEMY_STATE mState = ENEMY_STATE.idle;
		public float runSpeedMod;
		public float shootSpeedMod;
		public float rotation;
		public float mTimeBetweenShots = 500;
		public float mShootTimer;
		public int mCurrentShotNum;
		public int mShotsPerBurst = 2;
		public float health;
		public float killTimer = 0;

		// ---------------------------------------------
		// Properties
		// ---------------------------------------------

		public boolean isAlive() {
			return health > 0;
		}
		
		public ENEMY_STATE state() {
			return mState;
		}

		public void state(ENEMY_STATE pNewValue) {
			mState = pNewValue;
		}

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		public EnemyEntity() {
			super();

			runSpeedMod = mRandom.nextFloat() * 2f;
			shootSpeedMod = mRandom.nextFloat() * 2f;
			mShotsPerBurst = 1 + (mRandom.nextInt(7));

			radius = 32;
			coll_repel_precedence = 1;
		}

		// ---------------------------------------------
		// Core-Methods
		// ---------------------------------------------

		@Override
		public void update(GameTime pGameTime) {
			mShootTimer += pGameTime.elapseGameTime();

		}

		@Override
		public void init() {
			health = 3;
			mShootTimer = 0;
			mState = ENEMY_STATE.idle;
			rotation = 0;

		}

		@Override
		public void kill() {
			health = 0;
			mState = ENEMY_STATE.dead_Shot;
			killTimer = 10000;

		}

	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int INITIAL_POOL_SIZE = 128; // Max enemies

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameWorld mGameWorld;

	private List<EnemyEntity> mEnemyInstancePool;
	private List<EnemyEntity> mEnemies;
	private List<EnemyEntity> mEnemiesToUpdate;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<EnemyEntity> enemies() {
		return mEnemies;
	}

	public int getNumAliveEnemies() {
		int lReturn = 0;
		final int lNumEnemies = mEnemies.size();
		for (int i = 0; i < lNumEnemies; i++) {
			if (mEnemies.get(i).health > 0) {
				lReturn++;

			}

		}

		return lReturn;

	}

	public int getNumAliveBuildings() {
		return 0;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EnemyManager() {
		mEnemyInstancePool = new ArrayList<>(INITIAL_POOL_SIZE);
		mEnemies = new ArrayList<>();
		mEnemiesToUpdate = new ArrayList<>();

		allocateInstances(INITIAL_POOL_SIZE);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameWorld pGameWorld, TankEntity pPlayer, Camera pGameCamera) {
		mGameWorld = pGameWorld;

	}

	public void update(GameTime pGameTime) {
		mEnemiesToUpdate.clear();
		final int lNumEnemies = mEnemies.size();
		for (int i = 0; i < lNumEnemies; i++) {
			EnemyEntity lEnemy = mEnemies.get(i);

			mEnemiesToUpdate.add(lEnemy);

		}

		final int lUpdateCount = mEnemiesToUpdate.size();
		for (int i = 0; i < lUpdateCount; i++) {
			EnemyEntity lEnemy = mEnemiesToUpdate.get(i);

			if (!lEnemy.isAlive()) {
				lEnemy.killTimer -= pGameTime.elapseGameTime();

				if (lEnemy.killTimer <= 0) {

					// Remove from next update loop
					if (mEnemies.contains(lEnemy)) {
						mEnemies.remove(lEnemy);

					}

					// unregister from game world (no need to check for further colls
					mGameWorld.removeEntity(lEnemy);

					// Add it back to the pool
					if (!mEnemyInstancePool.contains(lEnemy)) {
						mEnemyInstancePool.add(lEnemy);

					}

				}

				continue;

			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private EnemyEntity allocateInstances(int pAmt) {
		if (pAmt <= 0)
			pAmt = 4;

		EnemyEntity lReturn = new EnemyEntity();

		for (int i = 0; i < pAmt; i++) {
			EnemyEntity lnewInstance = new EnemyEntity();
			mEnemyInstancePool.add(lnewInstance);
		}

		// Return this without adding it to the pool
		return lReturn;
	}

	public EnemyEntity getFreeInstance() {
		if(mEnemyInstancePool.size() > 0){
			EnemyEntity lEntity = mEnemyInstancePool.remove(0);
			return lEntity;
		}

		return allocateInstances(8);
	}

	public void addEnemyInstance(int pType, float pX, float pY) {
		EnemyEntity lNewEnemy = getFreeInstance();

		if (mEnemyInstancePool.contains(lNewEnemy)) {
			mEnemyInstancePool.remove(lNewEnemy);
		}

		mGameWorld.addEntity(lNewEnemy);
		
		lNewEnemy.setPosition(pX, pY, GameScreen.CELL_SIZE);
		lNewEnemy.init();

		if (!mEnemies.contains(lNewEnemy)) {
			mEnemies.add(lNewEnemy);
		}

	}

	public CellEntity getClosestEnemy(float pX, float pY) {
		CellEntity lResult = null;
		float lShortestDist = Float.MAX_VALUE;

		final int lEnemies = mEnemies.size();
		for (int i = 0; i < lEnemies; i++) {
			EnemyEntity lEnemy = mEnemies.get(i);
			if (!lEnemy.isAlive())
				continue;

			float xx = pX - lEnemy.xx;
			float yy = pY - lEnemy.yy;

			float dist = (xx * xx) + (yy * yy);
			if (dist < lShortestDist * lShortestDist) {
				lResult = lEnemy;
				lShortestDist = dist;
			}
		}

		return lResult;
	}

	public CellEntity getClosestEnemy(float pX, float pY, float pMaxRange) {
		CellEntity lResult = null;
		float lShortestDist = pMaxRange;

		final int lEnemies = mEnemies.size();
		for (int i = 0; i < lEnemies; i++) {
			EnemyEntity lEnemy = mEnemies.get(i);
			if (!lEnemy.isAlive())
				continue;

			float xx = lEnemy.xx - pX;
			float yy = lEnemy.yy - pY;

			float dist = (float)Math.sqrt((xx * xx) + (yy * yy));
			if (dist < lShortestDist ) {
				lResult = lEnemy;
				lShortestDist = dist;
			}
		}
			
		return lResult;
	}

}
