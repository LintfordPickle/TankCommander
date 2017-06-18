package net.ld.oneroom.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.ld.library.cellworld.CellGridLevel;
import net.ld.library.cellworld.EntityPool;
import net.ld.library.cellworld.entities.CellEntity;
import net.ld.library.cellworld.entities.CircleCollider;
import net.ld.library.core.time.GameTime;

public class GameWorld extends CellGridLevel {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public enum PICKUP_TYPE {
		ammo, fuel, scrap,
	}

	private static final int NUM_FUEL = 10;
	private static final int NUM_SCRAP = 10;
	private static final int NUM_AMMO = 10;

	// ---------------------------------------------
	// Inner-Class
	// ---------------------------------------------

	public class SpawnPoint extends CellEntity implements CircleCollider {
		public float timer;
		public float spawnTime;
		public boolean isDestroyed;

		public void init(float pWorldX, float pWorldY, float pSpawnTime) {
			isDestroyed = false;
			spawnTime = pSpawnTime;

			setPosition(pWorldX, pWorldY, cellSize);

		}

		@Override
		public void update(GameTime pGameTime) {
			if (isDestroyed)
				return;

			timer += pGameTime.elapseGameTime();

			if (timer > spawnTime) {
				mEnemyManager.addEnemyInstance(0, xx, yy);
				timer = 0;
			}

		}

		@Override
		public void init() {
			// TODO Auto-generated method stub

		}

		@Override
		public void kill() {
			// TODO Auto-generated method stub

		}

		@Override
		public float getRadius() {
			return radius;

		}

		@Override
		public void setRadius(float pNewValue) {
			radius = pNewValue;

		}

	}

	public class Pickup extends CellEntity {

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		public PICKUP_TYPE pickupType;
		public float srcX, srcY; // tex source coord
		public float srcW, srcH;
		public float dstW, dstH;
		public boolean consumed;
		public boolean clearOnConsume;

		// ---------------------------------------------
		// Constructors
		// ---------------------------------------------

		public Pickup() {

		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		public void init(PICKUP_TYPE pType, float pWorldX, float pWorldY) {
			init();

			consumed = false;
			pickupType = pType;

			setPosition(pWorldX, pWorldY, cellSize);

			switch (pType) {
			case ammo:
				srcX = 160;
				srcY = 288;
				srcW = 32;
				srcH = 32;
				dstW = 32;
				dstH = 32;
				clearOnConsume = true;

				break;

			case fuel:
				srcX = 192;
				srcY = 288;
				srcW = 32;
				srcH = 32;
				dstW = 32;
				dstH = 32;
				clearOnConsume = true;

				break;

			default: // Scrap
				srcX = 160;
				srcY = 160;
				srcW = 96;
				srcH = 64;
				dstW = 96;
				dstH = 64;
				clearOnConsume = true;
				break;
			}

		}

		@Override
		public void update(GameTime pGameTime) {

			// Collision detection

		}

		@Override
		public void init() {
			// TODO Auto-generated method stub

		}

		@Override
		public void kill() {
			// TODO Auto-generated method stub

		}

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Player mPlayer;
	private Random mRandom = new Random();

	private List<Pickup> mPickups;
	private List<SpawnPoint> mSpawnPoint;
	private EnemyManager mEnemyManager;

	private EntityPool<CellEntity> mWorldEntities;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public EntityPool<CellEntity> worldEntities() {
		return mWorldEntities;
	}

	public List<Pickup> pickups() {
		return mPickups;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWorld(int pCellSize, int pCellsWide, int pCellsHigh) {
		super(pCellSize, pCellsWide, pCellsHigh);

		mWorldEntities = new EntityPool<>();

		mPickups = new ArrayList<>();
		mSpawnPoint = new ArrayList<>();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(Player pPlayer, EnemyManager pEnemyManager) {
		mPlayer = pPlayer;
		mEnemyManager = pEnemyManager;

		for (int i = 0; i < NUM_AMMO; i++) {

			float lNewX = mRandom.nextFloat() * cellSize * cellsWide;
			float lNewY = mRandom.nextFloat() * cellSize * cellsHigh;

			addNewPickup(PICKUP_TYPE.ammo, lNewX, lNewY);
		}

		for (int i = 0; i < NUM_FUEL; i++) {

			float lNewX = mRandom.nextFloat() * cellSize * cellsWide;
			float lNewY = mRandom.nextFloat() * cellSize * cellsHigh;

			addNewPickup(PICKUP_TYPE.fuel, lNewX, lNewY);
		}

		for (int i = 0; i < NUM_SCRAP; i++) {

			float lNewX = mRandom.nextFloat() * cellSize * cellsWide;
			float lNewY = mRandom.nextFloat() * cellSize * cellsHigh;

			addNewPickup(PICKUP_TYPE.scrap, lNewX, lNewY);
		}

		// Add some random enemies
		for (int i = 0; i < 1; i++) {
			float lNewX = mRandom.nextFloat() * cellSize * cellsWide;
			float lNewY = mRandom.nextFloat() * cellSize * cellsHigh;

			mEnemyManager.addEnemyInstance(0, lNewX, lNewY);

		}

		// Add some spawn points
		addNewSpawnPoint((cellsWide - 4) * cellSize * 10, (cellsHigh - 4) * cellSize * 10);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void addNewPickup(PICKUP_TYPE pType, float pWorldX, float pWorldY) {
		Pickup lNewInstance = new Pickup();

		lNewInstance.init(pType, pWorldX, pWorldY);

		// Don't add pickup to the entities list, we only need col checks with
		// the player (and no 'physics')
		// mEntities.add(lNewInstance);
		mPickups.add(lNewInstance);

	}

	public void pickupCollision(Pickup pPickup) {
		switch (pPickup.pickupType) {
		case ammo:
			if (mPlayer.tank().mRocketStore.health < mPlayer.tank().mRocketStore.max_health) {
				mPlayer.tank().mRocketStore.health++;
				System.out.println("Picked up ammo");
				pPickup.kill();

			}
			break;

		case fuel:
			if (mPlayer.tank().fuelAmt < TankEntity.MAX_FUEL_AMT) {
				mPlayer.tank().fuelAmt += 20;
				System.out.println("Picked up fuel");
				pPickup.kill();
			}
			break;

		case scrap:
			if (!pPickup.consumed && mPlayer.scrapAmount() < Player.SCRAP_MAX) {
				mPlayer.scrapAmountMod(1);
				System.out.println("Picked up scrap metal");
				pPickup.consumed = true;
				pPickup.srcY = 224;

			}
			break;

		}

	}

	public void addNewSpawnPoint(float pWorldX, float pWorldY) {
		SpawnPoint lSpawnPoint = new SpawnPoint();

		lSpawnPoint.init(pWorldX, pWorldY, 4000);

		// Don't add pickup to the entities list, we only need col checks with
		// the player (and no 'physics')
		// mEntities.add(lNewInstance);
		mSpawnPoint.add(lSpawnPoint);

	}

	public void addEntity(CellEntity pEntity) {
		mWorldEntities.addEntity(pEntity);

	}

	public void removeEntity(CellEntity pEntity) {
		mWorldEntities.removeEntity(pEntity);

	}

}
