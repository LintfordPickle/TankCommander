package net.ld.oneroom.hud;

public class Bar {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private float mFullAmount;
	private float mCurrentAmount;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public float fullAmount() {
		return mFullAmount;
	}

	public void fullAmount(float pNewValue) {
		mFullAmount = pNewValue;
	}

	public float currentAmount() {
		return mCurrentAmount;
	}

	public void currentAmount(float pNewValue) {
		mCurrentAmount = pNewValue;
	}

	public void currentAmountMod(float pModAmt) {
		mCurrentAmount += pModAmt;

		if (mCurrentAmount > mFullAmount)
			mCurrentAmount = mFullAmount;

		if (mCurrentAmount < 0)
			mCurrentAmount = 0;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public Bar() {
		mFullAmount = 50f;
		mCurrentAmount = 25f;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

}
