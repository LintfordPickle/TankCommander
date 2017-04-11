package net.ld.oneroom.world;

import net.ld.library.cellworld.CellWorldEntity;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.controllers.PlayerController;
import net.ld.oneroom.world.TankCrew.STANCE;

public class TankEntity extends CellWorldEntity {

	public class TankComponent {

		public float timer;
		public TankCrew mMannedBy;

		public boolean isManable;

		public boolean isFixable;
		public float fixRate;

		/** modifier applied to all incoming damage (health -= damage * armour_mod) */
		public final float armour_mod;

		public final TankEntity parent;
		public final String name;
		public float height;
		public float x, y;

		// coords when manned (for the man)
		public float manx;
		public float many;
		public float manrot;

		public float ox, oy;
		public float r;
		public final float max_health;
		public float health;
		public float operational_health; // health above this? it works

		public void applyDamage(float pDamageAmt) {
			health -= pDamageAmt * armour_mod;

			if (health < 0) {
				health = 0;
			}
		}

		public TankComponent(TankEntity pParent, String pName, float pMaxHealth, float pArmourMod) {
			parent = pParent;
			name = pName;
			max_health = pMaxHealth;

			// modifier applied to all incoming damage (health -= damage * armour_mod)
			armour_mod = pArmourMod;

			reset();

		}

		public void reset() {
			health = max_health;
		}

		public boolean isOperational() {
			return health >= operational_health && (!isManable || isManable && mMannedBy != null);

		}

		public void updateManned(GameTime pGameTime) {
			//
			if (mMannedBy != null) {
				mMannedBy.x = ox;
				mMannedBy.y = oy;

			}
		}

	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int MAX_CREW = 7;
	public static final float MAX_HULL_HEALTH = 120;
	public static final float MAX_ENGINE_HEALTH = 40;
	public static final float MAX_ROCKETS = 8;
	public static final float MAX_FUEL_AMT = 100;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public float hullXOff = 0;
	public float hullYOff = 0;

	public float turretXOff = 0;
	public float turretYOff = 0;

	public final TankComponent mEngine;
	public final TankComponent mTurret;
	public final TankComponent mRocketStore;
	public final TankComponent mHull;

	// positions to sit and fix
	public final TankComponent mCommander;
	public final TankComponent mDriver;
	public final TankComponent mLoader;
	public final TankComponent mGunnerInt;
	public final TankComponent mGunnerFrontExt;
	public final TankComponent mGunnerBackExt;

	private Vector2f mGoingVector;
	private Vector2f mShootingVector;

	public boolean mDrawCupola;
	public boolean mLockTurret;
	public boolean roundChambered;
	public boolean mFireAtWill;

	private float mHeadingRot;
	private float mShootingRot;

	private float mCurrentTrottle = 0.0f; // [0,1]

	private float mTankTurnSpeed = 0.02f;
	private float mTurretTurnSpeed = 0.015f;
	private float mTankMaxSpeed = 11f;
	public float fuelAmt;
	public float hitRadius = 95f;
	public boolean targetTooClose;
	public boolean turretReloaded;
	public boolean turretReloadStarted; // because to start requires a rocket!
	public float turretReloadTimer;
	public boolean turretFireCommandRecieved;
	public final float turretReloadTime = 2500;

	// ---------------------------------------------
	// Properties

	public boolean drawCupola() {
		return mDrawCupola;
	}

	public void drawCupola(boolean pNewValue) {
		mDrawCupola = pNewValue;
	}

	public float trottle() {
		return mCurrentTrottle;
	}

	public void trottleSet(float pNewValue) {
		mCurrentTrottle = pNewValue;
	}

	public void trottleMod(float pModValue) {
		mCurrentTrottle += pModValue;
	}

	public float tankMaxSpeed() {
		return mTankMaxSpeed;
	}

	public float turrentTurnSpeed() {
		return mTurretTurnSpeed;
	}

	public float tankTurnSpeed() {
		return mTankTurnSpeed;
	}

	public float heading() {
		return mHeadingRot;
	}

	public void heading(float pNewValue) {
		mHeadingRot = pNewValue;
	}

	public void headingMod(float pModValue) {
		mHeadingRot += pModValue;
	}

	public float shootingRot() {
		return mShootingRot;
	}

	public void shootingRot(float pNewValue) {
		mShootingRot = pNewValue;
	}

	public void shootingMod(float pModValue) {
		mShootingRot += pModValue;

		mShootingRot = PlayerController.wrapAngle(mShootingRot);
	}

	public void setShootingVector(float pX, float pY) {
		mShootingVector.x = pX;
		mShootingVector.y = pY;

	}

	public Vector2f goingVector() {
		return mGoingVector;
	}

	public void setGoingVector(float pX, float pY) {
		mGoingVector.x = pX;
		mGoingVector.y = pY;

	}

	public Vector2f shootingVector() {
		return mShootingVector;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TankEntity() {
		super();

		mGoingVector = new Vector2f();
		mShootingVector = new Vector2f();

		// Need four of these ..
		radius = 112f;
		coll_repel_precedence = 2;
		fuelAmt = MAX_FUEL_AMT;

		init();

		mEngine = new TankComponent(this, "Engine", 30f, 0.2f);
		mTurret = new TankComponent(this, "Turrent", 30f, 0.3f);
		mRocketStore = new TankComponent(this, "Engine", MAX_ROCKETS, 0.08f); // 24 rockets ^^
		mHull = new TankComponent(this, "Hull", 30f, 0.05f);

		mCommander = new TankComponent(this, "Commander", 60f, 2f);
		mDriver = new TankComponent(this, "Driver", 60f, 2f);
		mLoader = new TankComponent(this, "Loader", 60f, 2f);
		mGunnerInt = new TankComponent(this, "GunnerInt", 60f, 2f);
		mGunnerFrontExt = new TankComponent(this, "GunnerExt1", 60f, 2f);
		mGunnerBackExt = new TankComponent(this, "GunnerExt2", 60f, 2f);

		setupComponents();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(GameTime pGameTime) {
		super.update(pGameTime);

		// Update the relative positions of the components
		float cos_t = (float) (Math.cos(mHeadingRot));
		float sin_t = (float) (Math.sin(mHeadingRot));

		mEngine.ox = -80f;
		mEngine.oy = -60f;

		// keep the components aligned with the body (regarding rotations)
		mRocketStore.x = x + mRocketStore.ox * cos_t - mRocketStore.oy * sin_t;
		mRocketStore.y = y + mRocketStore.ox * sin_t + mRocketStore.oy * cos_t;

		mEngine.x = x + mEngine.ox * cos_t - mEngine.oy * sin_t;
		mEngine.y = y + mEngine.ox * sin_t + mEngine.oy * cos_t;

		float cos_s = (float) (Math.cos(mShootingRot));
		float sin_s = (float) (Math.sin(mShootingRot));

		mTurret.x = ((x + mTurret.ox * cos_s - mTurret.oy * sin_s));
		mTurret.y = ((y + mTurret.ox * sin_s + mTurret.oy * cos_s));
		
		mGunnerInt.ox = 30;
		mGunnerInt.oy = -53;
	
		if (mEngine.mMannedBy != null) {
			mEngine.mMannedBy.mSTANCE = STANCE.standing;
			mEngine.mMannedBy.x = -45f;
			mEngine.mMannedBy.y = -50;
		}

		if (mCommander.mMannedBy != null) {
			mCommander.mMannedBy.mSTANCE = STANCE.sitting;
			mCommander.updateManned(pGameTime);
		}

		if (mDriver.mMannedBy != null) {
			mDriver.mMannedBy.mSTANCE = STANCE.sitting;
			mDriver.updateManned(pGameTime);
		}

		if (mLoader.mMannedBy != null) {
			mLoader.mMannedBy.mSTANCE = STANCE.sitting;
			mLoader.updateManned(pGameTime);
		}

		if (mGunnerInt.mMannedBy != null) {
			mGunnerInt.mMannedBy.mSTANCE = STANCE.sitting;
			mGunnerInt.updateManned(pGameTime);
		}

		if (mGunnerFrontExt.mMannedBy != null) {
			mGunnerFrontExt.mMannedBy.mSTANCE = STANCE.prone;
			mGunnerFrontExt.updateManned(pGameTime);
		}

		if (mGunnerBackExt.mMannedBy != null) {
			mGunnerBackExt.mMannedBy.mSTANCE = STANCE.prone;
			mGunnerBackExt.updateManned(pGameTime);
		}
		
		if (mTurret.mMannedBy != null) {
			mTurret.mMannedBy.mSTANCE = STANCE.sitting;
			mTurret.mMannedBy.x = 5;
			mTurret.mMannedBy.y = -10;
		}
		
		if (mHull.mMannedBy != null) {
			mHull.mMannedBy.mSTANCE = STANCE.sitting;
			mHull.mMannedBy.x = 30;
			mHull.mMannedBy.y = -10;
		}

		turretXOff *= 0.95f;
		turretYOff *= 0.95f;

		hullXOff *= 0.4f;
		hullYOff *= 0.4f;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/** FUTURE: Load from data file */
	private void setupComponents() {
		mEngine.ox = -56f;
		mEngine.oy = -16f;
		mEngine.r = 40;
		mEngine.height = 35;
		mEngine.reset();
		mEngine.operational_health = mEngine.max_health * 0.30f;
		mEngine.isManable = false; // to be fixed
		mEngine.isFixable = true;

		mRocketStore.ox = 76f;
		mRocketStore.oy = 48f;
		mRocketStore.r = 40f;
		mRocketStore.height = 0;
		mRocketStore.reset();
		mRocketStore.isManable = false;
		mRocketStore.isFixable = true;

		mTurret.ox = 210;
		mTurret.oy = 0;
		mTurret.r = 32;
		mTurret.height = 10; // tiny
		mTurret.reset();
		mTurret.operational_health = 10;
		mTurret.isManable = false;
		mTurret.isFixable = true;

		// Takes the space of the main tank
		mHull.ox = 0;
		mHull.oy = 0;
		mHull.r = hitRadius;
		mHull.height = 80;
		mHull.reset();
		mHull.operational_health = 5;
		mHull.isManable = false;
		mHull.isFixable = true;

		// Assign places to sit and their associated variables
		mCommander.ox = -35;
		mCommander.oy = 15;
		mCommander.manrot = 0;
		mCommander.reset();
		mCommander.isManable = true;
		mCommander.isFixable = false;

		mDriver.ox = 66;
		mDriver.oy = -6;
		mDriver.manrot = 90;
		mDriver.isManable = true;
		mDriver.isFixable = false;

		mLoader.ox = 10;
		mLoader.oy = 25;
		mLoader.reset();
		mLoader.isManable = true;
		mLoader.isFixable = false;

		mGunnerInt.ox = 30;
		mGunnerInt.oy = -53;
		mGunnerInt.reset();
		mGunnerInt.isManable = true;
		mGunnerInt.isFixable = false;

		mGunnerFrontExt.ox = 80;
		mGunnerFrontExt.oy = 40;
		mGunnerFrontExt.r = 16;
		mGunnerFrontExt.isManable = true;
		mGunnerFrontExt.isFixable = false;

		mGunnerBackExt.ox = -80;
		mGunnerBackExt.oy = -50;
		mGunnerBackExt.r = 16;
		mGunnerBackExt.isManable = true;
		mGunnerBackExt.isFixable = false;

	}

}
