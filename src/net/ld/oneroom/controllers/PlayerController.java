package net.ld.oneroom.controllers;

import java.util.Random;

import org.lwjgl.glfw.GLFW;

import net.ld.library.cellworld.entities.CellEntity;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.screens.GameScreen;
import net.ld.oneroom.world.Player;
import net.ld.oneroom.world.TankCrew;
import net.ld.oneroom.world.TankEntity.TankComponent;

/** Controls the PlayerEntity */
public class PlayerController {

	// Minimum range of rockets
	private static final float MAX_TURRET_BULLET_LIFE = 200;
	private static final float MAX_GUNNER_SIGHT_RANGE = 800;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Random mRandom = new Random();
	private GameScreen mGameScreen;
	private CameraController mCameraController; // coord screen -> world coords
	private Player mPlayerEntity;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameScreen pGameScreen, Player pPlayer, CameraController pCameraController) {
		mPlayerEntity = pPlayer;
		mCameraController = pCameraController;
		mGameScreen = pGameScreen;

		mPlayerEntity.tank().cx = 1;
		mPlayerEntity.tank().cy = 1;

		mPlayerEntity.tank().goingVector().x = pGameScreen.gameWorld().cellSize + 0.1f;
		mPlayerEntity.tank().goingVector().y = pGameScreen.gameWorld().cellSize;

		mPlayerEntity.tank().shootingVector().x = pGameScreen.gameWorld().cellSize * 2;
		mPlayerEntity.tank().shootingVector().y = pGameScreen.gameWorld().cellSize * 2;

	}

	public boolean handleInput(InputState pInputState) {
		if (mPlayerEntity == null)
			return false;

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_SPACE)) {
			if (mPlayerEntity.tank().turretReloaded)
				mPlayerEntity.tank().turretFireCommandRecieved = true;
		}

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_F)) {
			mPlayerEntity.tank().mFireAtWill = !mPlayerEntity.tank().mFireAtWill;
		}

		// Things below this point cannot be done without a commander
		if (!mPlayerEntity.isCommanderPresent())
			return false;

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_L)) {
			mPlayerEntity.tank().mLockTurret = !mPlayerEntity.tank().mLockTurret;
		}

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_C)) {
			mPlayerEntity.toggleChaseCam();
		}

		// INPUT BINDING T = Cupola toggle
		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_T)) {
			mPlayerEntity.tank().drawCupola(!mPlayerEntity.tank().drawCupola());
		}

		// Just listener for clicks - couldn't be easier !!?!
		if (pInputState.isMouseTimedLeftClickAvailable()) {
			if (pInputState.tryAquireLeftClickOwnership(hashCode())) {
				final float lX = mCameraController.gameCamera().getMouseCameraSpace().x;
				final float lY = mCameraController.gameCamera().getMouseCameraSpace().y;

				mPlayerEntity.tank().setGoingVector(lX, lY);
			}

		}

		if (pInputState.isMouseTimedRightClickAvailable()) {
			if (pInputState.tryAquireRightClickOwnership(hashCode())) {
				final float lX = mCameraController.gameCamera().getMouseCameraSpace().x;
				final float lY = mCameraController.gameCamera().getMouseCameraSpace().y;

				mPlayerEntity.tank().setShootingVector(lX, lY);
			}

		}

		return false;

	}

	public void update(GameTime pGameTime) {
		final int ENGINE_REPAIR_AMOUNT = 3; // for 1 scrap
		final int TURRET_REPAIR_AMOUNT = 3; // for 1 scrap
		final int HULL_REPAIR_AMOUNT = 5; // for 1 scrap

		// FIX the Engines
		if (mPlayerEntity.tank().mEngine.health < mPlayerEntity.tank().mEngine.max_health) {
			if (mPlayerEntity.tank().mEngine.isFixable && mPlayerEntity.tank().mEngine.mMannedBy != null) {
				if (mPlayerEntity.scrapAmountMod(-1)) {
					mPlayerEntity.tank().mEngine.timer += pGameTime.elapseGameTime();
					if (mPlayerEntity.tank().mEngine.timer > 3000) {
						System.out.println("fixing engine. Health : " + mPlayerEntity.tank().mEngine.health + "/"
								+ mPlayerEntity.tank().mEngine.max_health);

						mPlayerEntity.tank().mEngine.health += ENGINE_REPAIR_AMOUNT;
						if (mPlayerEntity.tank().mEngine.health > mPlayerEntity.tank().mEngine.max_health) {
							mPlayerEntity.tank().mEngine.health = mPlayerEntity.tank().mEngine.max_health;

						}

						mPlayerEntity.tank().mEngine.timer = 0;
					}

				}

			}

		}

		// FIX the Turret
		if (mPlayerEntity.tank().mTurret.health < mPlayerEntity.tank().mTurret.max_health) {
			if (mPlayerEntity.tank().mTurret.isFixable && mPlayerEntity.tank().mTurret.mMannedBy != null) {
				if (mPlayerEntity.scrapAmountMod(-1)) {
					mPlayerEntity.tank().mTurret.timer += pGameTime.elapseGameTime();
					if (mPlayerEntity.tank().mTurret.timer > 3000) {
						System.out.println("fixing turret. Health : " + mPlayerEntity.tank().mTurret.health + "/"
								+ mPlayerEntity.tank().mEngine.max_health);

						mPlayerEntity.tank().mTurret.health += TURRET_REPAIR_AMOUNT;
						if (mPlayerEntity.tank().mTurret.health > mPlayerEntity.tank().mTurret.max_health) {
							mPlayerEntity.tank().mTurret.health = mPlayerEntity.tank().mTurret.max_health;

						}

						mPlayerEntity.tank().mTurret.timer = 0;
					}

				}

			}

		}

		// FIX the HULL
		if (mPlayerEntity.tank().mHull.health < mPlayerEntity.tank().mHull.max_health) {
			if (mPlayerEntity.tank().mHull.isFixable && mPlayerEntity.tank().mHull.mMannedBy != null) {
				if (mPlayerEntity.scrapAmountMod(-1)) {
					mPlayerEntity.tank().mHull.timer += pGameTime.elapseGameTime();
					if (mPlayerEntity.tank().mHull.timer > 3000) {
						System.out.println("fixing turret. Health : " + mPlayerEntity.tank().mHull.health + "/"
								+ mPlayerEntity.tank().mEngine.max_health);

						mPlayerEntity.tank().mHull.health += HULL_REPAIR_AMOUNT;
						if (mPlayerEntity.tank().mHull.health > mPlayerEntity.tank().mHull.max_health) {
							mPlayerEntity.tank().mHull.health = mPlayerEntity.tank().mHull.max_health;

						}

						mPlayerEntity.tank().mHull.timer = 0;
					}

				}

			}

		}

		// Vehicle can only drive and turn if, there is both a driver and the
		// engines are working
		if (mPlayerEntity.tank().mDriver.mMannedBy != null && mPlayerEntity.tank().mEngine.isOperational()) {
			updateDrive(pGameTime);

		}

		updateTurrets(pGameTime);

		// This position can freely rotation (the front of the tank)
		if (mPlayerEntity.tank().mGunnerFrontExt.mMannedBy != null
				&& mPlayerEntity.tank().mGunnerFrontExt.mMannedBy.health > 0) {
			TankCrew lMember = mPlayerEntity.tank().mGunnerFrontExt.mMannedBy;

			if (lMember.numBullets > 0) {

				TankComponent lGunSlot = mPlayerEntity.tank().mGunnerFrontExt;

				// Work out the actual position of the crew member (relative to
				// the tank)
				float sin_t = (float) Math.sin(mPlayerEntity.tank().heading());
				float cos_t = (float) Math.cos(mPlayerEntity.tank().heading());

				float lTankPosX = mPlayerEntity.tank().xx;
				float lTankPosY = mPlayerEntity.tank().yy;

				// Check if already engaged ??
				float lMemberLocalX = lGunSlot.ox * cos_t - lGunSlot.oy * sin_t;
				float lMemberLocalY = lGunSlot.ox * sin_t + lGunSlot.oy * cos_t;

				float lWorldPosX = lTankPosX + lMemberLocalX;
				float lWorldPosY = lTankPosY + lMemberLocalY;

				// Face the closest enemy
				CellEntity lEnemy = mGameScreen.enemyManager().getClosestEnemy(lWorldPosX, lWorldPosY, MAX_GUNNER_SIGHT_RANGE);
				if (lEnemy != null) {
					float lToEnemyVecX = lEnemy.xx - lWorldPosX;
					float lToEnemyVecY = lEnemy.yy - lWorldPosY;

					float lShotAngle = (float) Math.atan2(lToEnemyVecY, lToEnemyVecX);
					lMember.rot = lShotAngle + (float) Math.toRadians(-90);

					// Randomise the shot vector a little
					final float RAND_AMT = 30;
					lShotAngle += (float) Math.toRadians(mRandom.nextFloat() * RAND_AMT - RAND_AMT / 2);

					final float VELOCITY = 2000;
					float lShotVecX = (float) (Math.cos(lShotAngle)) * VELOCITY;
					float lShotVecY = (float) (Math.sin(lShotAngle)) * VELOCITY;

					float lNextShotTime = (lMember.mCurrentShotNum < lMember.mShotsPerBurst) ? 50
							: lMember.mTimeBetweenShots;

					if (lMember.mShootTimer > lNextShotTime * 1f /* mod */) {
						mGameScreen.playerBullets().addParticle(lWorldPosX, lWorldPosY, lShotVecX, lShotVecY, 2000f);

						lMember.mCurrentShotNum++;
						if (lMember.mCurrentShotNum >= lMember.mShotsPerBurst + 1) {
							lMember.mCurrentShotNum = 0;
						}

						lMember.mShootTimer = 0;
					}

				}

			}

		}

		if (mPlayerEntity.tank().mGunnerBackExt.mMannedBy != null
				&& mPlayerEntity.tank().mGunnerBackExt.mMannedBy.health > 0) {
			TankCrew lMember = mPlayerEntity.tank().mGunnerBackExt.mMannedBy;

			if (lMember.numBullets > 0) {

				TankComponent lGunSlot = mPlayerEntity.tank().mGunnerBackExt;

				// Work out the actual position of the crew member (relative to
				// the tank)
				float sin_t = (float) Math.sin(mPlayerEntity.tank().heading());
				float cos_t = (float) Math.cos(mPlayerEntity.tank().heading());

				float lTankPosX = mPlayerEntity.tank().xx;
				float lTankPosY = mPlayerEntity.tank().yy;

				// Check if already engaged ??
				float lMemberLocalX = lGunSlot.ox * cos_t - lGunSlot.oy * sin_t;
				float lMemberLocalY = lGunSlot.ox * sin_t + lGunSlot.oy * cos_t;

				float lWorldPosX = lTankPosX + lMemberLocalX;
				float lWorldPosY = lTankPosY + lMemberLocalY;

				// Face the closest enemy
				CellEntity lEnemy = mGameScreen.enemyManager().getClosestEnemy(lWorldPosX, lWorldPosY, MAX_GUNNER_SIGHT_RANGE);
				if (lEnemy != null) {
					float lToEnemyVecX = lEnemy.xx - lWorldPosX;
					float lToEnemyVecY = lEnemy.yy - lWorldPosY;

					float lShotAngle = (float) Math.atan2(lToEnemyVecY, lToEnemyVecX);
					lMember.rot = lShotAngle + (float) Math.toRadians(-90);

					// Randomise the shot vector a little
					final float RAND_AMT = 30;
					lShotAngle += (float) Math.toRadians(mRandom.nextFloat() * RAND_AMT - RAND_AMT / 2);

					final float VELOCITY = 2000;
					float lShotVecX = (float) (Math.cos(lShotAngle)) * VELOCITY;
					float lShotVecY = (float) (Math.sin(lShotAngle)) * VELOCITY;

					float lNextShotTime = (lMember.mCurrentShotNum < lMember.mShotsPerBurst) ? 50
							: lMember.mTimeBetweenShots;

					if (lMember.mShootTimer > lNextShotTime * 1f /* mod */) {
						mGameScreen.playerBullets().addParticle(lWorldPosX, lWorldPosY, lShotVecX, lShotVecY, 2000f);

						lMember.mCurrentShotNum++;
						if (lMember.mCurrentShotNum >= lMember.mShotsPerBurst + 1) {
							lMember.mCurrentShotNum = 0;
						}

						lMember.mShootTimer = 0;
					}

				}

			}

		}

	}

	private void updateDrive(GameTime pGameTime) {

		float lDelta = (float) pGameTime.elapseGameTime() / 1000.0f;

		if (mPlayerEntity.tank().fuelAmt <= 0) {

			return; // cannot drive
		}

		// The amount of throttle and turn speed to be applied will depend on
		// the Fresnel term (faster if facing the target)
		final float lHeadingVecX = mPlayerEntity.tank().goingVector().x - mPlayerEntity.tank().xx;
		final float lHeadingVecY = mPlayerEntity.tank().goingVector().y - mPlayerEntity.tank().yy;

		// 1. get the lengths of the two vectors (Fresnel must be normalized)
		float lLengthTar0 = (float) Math.sqrt((lHeadingVecX * lHeadingVecX) + (lHeadingVecY * lHeadingVecY));

		float lHeadingVecNorX = lHeadingVecX / lLengthTar0;
		float lHeadingVecNorY = lHeadingVecY / lLengthTar0;

		final float PLAYER_ROT = mPlayerEntity.tank().heading();

		float lTargetHeadingNorX = (float) Math.cos(PLAYER_ROT);
		float lTargetHeadingNorY = (float) Math.sin(PLAYER_ROT);

		final float FRESNEL_MINIMUM_TERM = 0.4f;
		float lFresnel = Vector2f.dot(lHeadingVecNorX, lHeadingVecNorY, lTargetHeadingNorX, lTargetHeadingNorY);
		float lFresnelInv = (1f - lFresnel);
		if (lFresnelInv < FRESNEL_MINIMUM_TERM)
			lFresnelInv = FRESNEL_MINIMUM_TERM;
		if (lFresnelInv > 1f)
			lFresnelInv = 1f;

		final float MOVE_SPEED = mPlayerEntity.tank().tankMaxSpeed() * lFresnel * lFresnel;

		float lTurnSpeedTemp = mPlayerEntity.tank().tankTurnSpeed();
		final float TURN_SPEED = clamp(lTurnSpeedTemp, -mPlayerEntity.tank().tankTurnSpeed(),
				mPlayerEntity.tank().tankTurnSpeed());

		// Only consider driving if the tank is not ON on the target vector
		if (Math.sqrt((lHeadingVecX * lHeadingVecX) + (lHeadingVecY * lHeadingVecY)) > 128) {

			// Calculate the new rotation angles to apply to the turret and tank
			// based on the turn speeds

			// TODO: time dependant ??
			float lHeadingAngle = turnToFace(lHeadingVecX, lHeadingVecY, mPlayerEntity.tank().heading(),
					TURN_SPEED * lFresnelInv);

			mPlayerEntity.tank().headingMod(lHeadingAngle);

			// Drive the tank forwards
			mPlayerEntity.tank().dx += (float) Math.cos(mPlayerEntity.tank().heading()) * MOVE_SPEED * lDelta;
			mPlayerEntity.tank().dy += (float) Math.sin(mPlayerEntity.tank().heading()) * MOVE_SPEED * lDelta;

			mPlayerEntity.tank().fuelAmt -= (MOVE_SPEED * lDelta) * 0.3f;

		} else {

		}

	}

	private void updateTurrets(GameTime pGameTime) {
		// Turret can only turn if manned and operational
		if (/* mPlayerEntity.tank().mTurret.isOperational() && */ mPlayerEntity.tank().mGunnerInt.mMannedBy != null) {
			final float lShootingVecX = mPlayerEntity.tank().shootingVector().x - mPlayerEntity.tank().xx;
			final float lShootingVecY = mPlayerEntity.tank().shootingVector().y - mPlayerEntity.tank().yy;

			float lTurretAngle = turnToFace(lShootingVecX, lShootingVecY, mPlayerEntity.tank().shootingRot(),
					mPlayerEntity.tank().turrentTurnSpeed());
			mPlayerEntity.tank().shootingMod(lTurretAngle);

		}

		// TODO: Shoot the bloody turret already !!

		if (!mPlayerEntity.tank().turretReloaded && mPlayerEntity.tank().mLoader.mMannedBy != null) {
			if (!mPlayerEntity.tank().turretReloadStarted && mPlayerEntity.tank().mRocketStore.health > 0) {
				// Start the reload and use a rocket
				mPlayerEntity.tank().mRocketStore.health--;
				mPlayerEntity.tank().turretReloadStarted = true;
				mPlayerEntity.tank().turretReloadTimer = 0;
			}

			if (mPlayerEntity.tank().turretReloadStarted) {
				mPlayerEntity.tank().turretReloadTimer += pGameTime.elapseGameTime();

				if (mPlayerEntity.tank().turretReloadTimer > mPlayerEntity.tank().turretReloadTime) {
					mPlayerEntity.tank().turretReloaded = true;
					mPlayerEntity.tank().turretReloadStarted = false;
					mPlayerEntity.tank().turretReloadTimer = 0;

				}

			}

		}

		if (mPlayerEntity.tank().turretReloaded && mPlayerEntity.tank().mGunnerInt.mMannedBy != null) {
			if (mPlayerEntity.tank().mTurret.isOperational()) {

				//
				if (mPlayerEntity.tank().turretFireCommandRecieved || mPlayerEntity.tank().mFireAtWill) {
					mPlayerEntity.tank().turretFireCommandRecieved = false;
					mPlayerEntity.tank().turretReloaded = false;

					float lShootingVecX = (float) Math.cos(mPlayerEntity.tank().shootingRot()); // mPlayerEntity.tank().shootingVector().x
																								// -
																								// mPlayerEntity.tank().x;
					float lShootingVecY = (float) Math.sin(mPlayerEntity.tank().shootingRot()); // mPlayerEntity.tank().shootingVector().y
																								// -
																								// mPlayerEntity.tank().y;

					float xx = mPlayerEntity.tank().shootingVector().x - mPlayerEntity.tank().xx;
					float yy = mPlayerEntity.tank().shootingVector().y - mPlayerEntity.tank().yy;

					float lDistToEnemy = (float) Math.sqrt((xx * xx) + (yy * yy));

					mPlayerEntity.tank().turretXOff = -lShootingVecX * 100;
					mPlayerEntity.tank().turretYOff = -lShootingVecY * 100;

					lShootingVecX *= 5000;
					lShootingVecY *= 5000;

					float life = lDistToEnemy / 5;

					if (life < MAX_TURRET_BULLET_LIFE)
						life = MAX_TURRET_BULLET_LIFE;

					System.out.println("life: " + life);
					mGameScreen.playerRockets().addParticle(mPlayerEntity.tank().xx, mPlayerEntity.tank().yy,
							lShootingVecX, lShootingVecY, life);

					// TODO:

				}

			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	// ref:
	// http://xbox.create.msdn.com/en-US/education/catalog/sample/chase_evade
	static float turnToFace(float pHeadingX, float pHeadingY, float pCurrentAngle, float pTurnSpeed) {
		float desiredAngle = (float) Math.atan2(pHeadingY, pHeadingX);

		float difference = wrapAngle(desiredAngle - pCurrentAngle);

		// clamp
		difference = clamp(difference, -pTurnSpeed, pTurnSpeed);

		return wrapAngle(difference);

	}

	/** wraps to -PI / PI */
	public static float wrapAngle(float radians) {
		while (radians < -Math.PI) {
			radians += Math.PI * 2;
		}
		while (radians > Math.PI) {
			radians -= Math.PI * 2;
		}
		return radians;
	}

	static float clamp(float v, float min, float max) {
		return Math.max(min, Math.min(max, v));
	}

	public static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

}
