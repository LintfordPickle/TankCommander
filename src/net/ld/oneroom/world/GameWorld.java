package net.ld.oneroom.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.ld.library.cellworld.CellGridWorld;
import net.ld.library.cellworld.CellWorldEntity;
import net.ld.library.core.time.GameTime;

public class GameWorld extends CellGridWorld {

	// ---------------------------------------------
	// Enums
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

	public class SpawnPoint extends CellWorldEntity {
		public float timer;
		public float spawnTime;
		public boolean isDestroyed;

		public void init(float pWorldX, float pWorldY, float pSpawnTime) {
			isDestroyed = false;
			spawnTime = pSpawnTime;

			setCoordinate(pWorldX, pWorldY, cellSize);

		}

		@Override
		public void update(GameTime pGameTime) {
			super.update(pGameTime);

			if (isDestroyed)
				return;

			timer += pGameTime.elapseGameTime();

			if (timer > spawnTime) {
				mEnemyManager.addEnemyInstance(0, xx, yy);
				timer = 0;
			}

		}

	}

	public class Pickup extends CellWorldEntity {

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
			isAlive = true;
			pickupType = pType;

			setCoordinate(pWorldX, pWorldY, cellSize);

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
			if (!isAlive)
				return;

			// COLLISION

			rx += dx * pGameTime.elapseGameTime() / 1000.0f;
			ry += dy * pGameTime.elapseGameTime() / 1000.0f;

			dx *= 0.96f;
			dy *= 0.96f;

			if (isAlive) {

				// Check collisions to the right
				if (mParent.hasLevelCollisionAt(cx + 1, cy) && rx > 0.7f) {
					rx = 0.7f; // limit ratio
					dx = 0; // kill vel
				}

				// Check collision to the left
				if (mParent.hasLevelCollisionAt(cx - 1, cy) && rx <= 0.3f) {
					rx = 0.3f; // limit ratio
					dx = 0; // kill vel
				}

				if (mParent.hasLevelCollisionAt(cx, cy + 1) && ry > 0.7f) {
					ry = 0.7f; // limit ratio
					dy = 0; // kill vel
				}

				// Check collision to the left
				if (mParent.hasLevelCollisionAt(cx, cy - 1) && ry <= 0.3f) {
					ry = 0.3f; // limit ratio
					dy = 0; // kill vel
				}

				// Check collisions with other entities
				int lEntCount = mParent.entities().size();
				for (int i = 0; i < lEntCount; i++) {
					CellWorldEntity e = mParent.entities().get(i);
					if (e == this || !e.isInUse())
						continue;

					// Fast distance check
					if (e != this && Math.abs(cx - e.cx) <= 12 && Math.abs(cy - e.cy) <= 12) {
						float exx = e.xx - xx;
						float eyy = e.yy - yy;

						float dist = (float) Math.sqrt(exx * exx + eyy * eyy);
						if (dist == 0) {
							dx -= 0.1f;
							dy -= 0.1f;
							e.dx += 0.1f;
							e.dy += 0.1f;
						}

						else if (dist <= radius + e.radius) {

							float force = 0.1f;

							// figure out who to repel ..
							if (coll_repel_precedence < e.coll_repel_precedence) {
								// I go
								float repelPower = (radius + e.radius - dist) / (radius + e.radius);

								dx -= (exx / dist) * repelPower * force * 2;
								dy -= (eyy / dist) * repelPower * force * 2;
							} else if (coll_repel_precedence > e.coll_repel_precedence) {
								// They go
								float repelPower = (radius + e.radius - dist) / (radius + e.radius);

								e.dx += (exx / dist) * repelPower * force * 2;
								e.dy += (eyy / dist) * repelPower * force * 2;
							} else {
								// We go
								float repelPower = (radius + e.radius - dist) / (radius + e.radius);

								dx -= (exx / dist) * repelPower * force;
								dy -= (eyy / dist) * repelPower * force;
								e.dx += (exx / dist) * repelPower * force;
								e.dy += (eyy / dist) * repelPower * force;
							}

							if (e instanceof TankEntity)
								pickupCollision(this);

						}

					}

				}

			}

			final float cap = 3;
			if (dx < -cap)
				dx = -cap;
			if (dy < -cap)
				dy = -cap;

			if (dx > cap)
				dx = cap;
			if (dy > cap)
				dy = cap;

			while (rx < 0) {
				rx++;
				cx--;
			}

			while (rx > 1) {
				rx--;
				cx++;
			}

			while (ry < 0) {
				ry++;
				cy--;
			}

			while (ry > 1) {
				ry--;
				cy++;
			}

			xx = (cx + rx) * mParent.cellSize;
			yy = (cy + ry) * mParent.cellSize;

			// update the underlying world coordinates
			x = xx;
			y = yy;

			// kill the velocity if small enough
			if (Math.abs(dx) < 0.01f)
				dx = 0f;
			if (Math.abs(dy) < 0.01f)
				dy = 0f;

		}

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Player mPlayer;
	Random mRandom = new Random();

	List<Pickup> mPickups;
	List<SpawnPoint> mSpawnPoint;
	private EnemyManager mEnemyManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<Pickup> pickups() {
		return mPickups;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWorld(int pCellSize, int pCellsWide, int pCellsHigh) {
		super(pCellSize, pCellsWide, pCellsHigh);

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
		addNewSpawnPoint((cellsWide - 4) * cellSize, (cellsHigh - 4) * cellSize);

	}

	@Override
	public void update(GameTime pGameTime) {
		super.update(pGameTime);

		mEntitiesToUpdate.clear();

		final int lEntityCount = mPickups.size();
		for (int i = 0; i < lEntityCount; i++) {
			// Only update entities which are in use.
			if (!mPickups.get(i).isInUse())
				continue;

			mEntitiesToUpdate.add(mPickups.get(i));

		}

		final int lEntityUpdateCount = mEntitiesToUpdate.size();
		for (int i = 0; i < lEntityUpdateCount; i++) {
			Pickup lEntity = (Pickup) mEntitiesToUpdate.get(i);

			if (!lEntity.isAlive) {
				mPickups.remove(lEntity);
			}

			lEntity.update(pGameTime);

		}

		mEntitiesToUpdate.clear();

		final int lSpawnCount = mSpawnPoint.size();
		for (int i = 0; i < lSpawnCount; i++) {
			// Only update entities which are in use.
			if (!mSpawnPoint.get(i).isInUse())
				continue;

			mEntitiesToUpdate.add(mSpawnPoint.get(i));

		}

		final int lSpawnUpdateCount = mEntitiesToUpdate.size();
		for (int i = 0; i < lSpawnUpdateCount; i++) {
			SpawnPoint lEntity = (SpawnPoint) mEntitiesToUpdate.get(i);

			if (!lEntity.isAlive) {

			}

			lEntity.update(pGameTime);

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void addNewPickup(PICKUP_TYPE pType, float pWorldX, float pWorldY) {
		Pickup lNewInstance = new Pickup();

		lNewInstance.init(pType, pWorldX, pWorldY);
		lNewInstance.attachParent(this);

		// Don't add pickup to the entities list, we only need col checks with
		// the player (and no 'physics')
		// mEntities.add(lNewInstance);
		mPickups.add(lNewInstance);

	}

	private void pickupCollision(Pickup pPickup) {
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
		lSpawnPoint.attachParent(this);

		// Don't add pickup to the entities list, we only need col checks with
		// the player (and no 'physics')
		// mEntities.add(lNewInstance);
		mSpawnPoint.add(lSpawnPoint);

	}

}
