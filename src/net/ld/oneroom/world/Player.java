package net.ld.oneroom.world;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.time.GameTime;
import net.ld.oneroom.screens.GameScreen;

public class Player {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int SCRAP_MAX = 15;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TankEntity mTank;
	private List<TankCrew> mCrewToUpdate;
	private List<TankCrew> mCrew;
	private int mAmountOfScrap;
	private boolean mChaseCam;
	private boolean mIsCommanderPresent;
	private boolean isInitialised = false;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isCommanderPresent() {
		return mIsCommanderPresent;
	}

	public int scrapAmount() {
		return mAmountOfScrap;
	}

	public void scrapAmount(int pNewValue) {
		mAmountOfScrap = pNewValue;
	}

	public boolean scrapAmountMod(int pModAmt) {
		int lOldValue = mAmountOfScrap;
		mAmountOfScrap += pModAmt;

		if (mAmountOfScrap < 0) {
			mAmountOfScrap = lOldValue; // revert
			return false;
		}

		if (mAmountOfScrap > SCRAP_MAX) {
			mAmountOfScrap = lOldValue; // revert
			return false;
		}

		return true;

	}

	public boolean chaseCamActive() {
		return mChaseCam;
	}

	/** Sets whether the player has chase cam active */
	public void chaseCamActive(boolean pNewValue) {
		mChaseCam = pNewValue;
	}

	/**
	 * Sets whether the player has chase cam active. If there is no commander
	 * present in the tank, chase cam cannot be deactivated.
	 */
	public boolean chaseCamActiveConfirm(boolean pNewValue) {
		if (!pNewValue) {
			if (isCommanderPresent()) {
				mChaseCam = false;
				return true;
			} else {
				return false; // Cannot turn chase cam off without commander
			}
		}

		mChaseCam = true;
		return true;

	}

	/** Returns the player's {@link TankEntity} instance. */
	public TankEntity tank() {
		return mTank;
	}

	/**
	 * Returns a list of {@link TankCrew} members currently assigned to the
	 * player's {@link TankEntity}.
	 */
	public List<TankCrew> crew() {
		return mCrew;
	}

	public int remainingCrew() {
		int lResult = 0;
		final int lTotalCrew = crew().size();
		for (int i = 0; i < lTotalCrew; i++) {
			if (crew().get(i).isAlive())
				lResult++;
		}

		return lResult;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	/** ctor. */
	public Player() {
		mTank = new TankEntity();
		mCrew = new ArrayList<>();
		mCrewToUpdate = new ArrayList<>();

		mChaseCam = true;
		mTank.mFireAtWill = false;
		mTank.mDrawCupola = false;
		mTank.mLockTurret = false;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise() {
		isInitialised = true;

		TankCrew lCommander = new TankCrew("Jools");
		mTank.mTurret.mMannedBy = lCommander;
		mCrew.add(lCommander);

		TankCrew lDriver = new TankCrew("Jops");
		mTank.mGunnerFrontExt.mMannedBy = lDriver;
		mCrew.add(lDriver);

		TankCrew lGunner = new TankCrew("Stoo");
		mTank.mGunnerBackExt.mMannedBy = lGunner;
		mCrew.add(lGunner);

	}

	public void update(GameTime pGameTime) {
		if (!isInitialised)
			throw new RuntimeException("Player not initialised - should never happen in this app");

		mIsCommanderPresent = mTank.mCommander.mMannedBy != null;

		mCrewToUpdate.clear();
		final int lCrewCount = mCrew.size();
		for (int i = 0; i < lCrewCount; i++) {
			if (mCrew.get(i).isAlive())
				mCrewToUpdate.add(mCrew.get(i));

		}

		final int lCrewUpdateCount = mCrewToUpdate.size();
		for (int i = 0; i < lCrewUpdateCount; i++) {

			TankCrew lCrewMemeber = mCrewToUpdate.get(i);
			lCrewMemeber.update(pGameTime);

			if (!lCrewMemeber.isAlive()) {
				mCrew.remove(lCrewMemeber);
			}

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void toggleChaseCam() {
		mChaseCam = !mChaseCam;
	}

	public void setStartPosition(float pWorldX, float pWorldY) {
		mTank.setGoingVector(pWorldX, pWorldY);
		mTank.setShootingVector(pWorldX + 512, pWorldY);

		mTank.setPosition(pWorldX, pWorldY, GameScreen.CELL_SIZE);

	}

}
