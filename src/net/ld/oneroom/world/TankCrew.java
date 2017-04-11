package net.ld.oneroom.world;

import net.ld.library.cellworld.CellWorldEntity;
import net.ld.library.core.time.GameTime;

public class TankCrew extends CellWorldEntity {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public enum STANCE {
		sitting, prone, standing;
	}

	public static final float MAX_HEALTH = 10;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public STANCE mSTANCE = STANCE.sitting;
	public String name;
	public CellWorldEntity attacking;

	public float mTimeBetweenShots = 400;
	public float mShootTimer;
	public int mCurrentShotNum;
	public int mShotsPerBurst = 3;

	// Change magazine
	public final int MAX_NUM_BULLETS = 30;
	public int numBullets;
	public int mBulletTimer;
	public int mBulletRespawnIn = 3000;

	public float health;
	public float rot;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isAlive() {
		return health > 0;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TankCrew(String pName) {
		health = MAX_HEALTH;
		isAlive = true;
		name = pName;

		System.out.println("recruited " + name + " to the crew ");

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void update(GameTime pGameTime) {
		mShootTimer += pGameTime.elapseGameTime();

		if (numBullets < 2) {
			mBulletTimer += pGameTime.elapseGameTime();
			if (mBulletTimer > mBulletRespawnIn) {
				numBullets = MAX_NUM_BULLETS;
				mBulletTimer = 0;
			}
		}

	}

}
