package net.ld.oneroom.controllers;

import org.lwjgl.glfw.GLFW;

import net.ld.library.controllers.camera.CameraZoomController;
import net.ld.library.core.camera.Camera;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.world.Player;

/**
 * Controls the camera, if their is a commander present, then the camera can be
 * manually controlled.
 */
public class CameraController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final float CAMERA_MAN_MOVE_SPEED = 0.2f;
	private static final float CAMERA_MAN_MOVE_SPEED_MAX = 1f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Camera mGameCamera;
	private Player mPlayer;
	private boolean mTrackPlayer;
	private Vector2f mVelocity;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public Camera gameCamera() {
		return mGameCamera;
	}

	public boolean trackPlayer() {
		return mTrackPlayer;
	}

	public void trackPlayer(boolean pNewValue) {
		mTrackPlayer = pNewValue;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraController() {
		mVelocity = new Vector2f();

		// default to track player
		mTrackPlayer = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(Camera pGameCamera, Player pPlayer) {
		mGameCamera = pGameCamera;
		mPlayer = pPlayer;
		// mGameCamera.setZoomConstraints(0.3f, 1.4f);

	}

	public boolean handleInput(InputState pInputState) {
		if (mGameCamera == null)
			return false;

		final float speed = CAMERA_MAN_MOVE_SPEED;

		// Just listener for clicks - couldn't be easier !!?!
		if (pInputState.keyDown(GLFW.GLFW_KEY_A)) {
			if (mPlayer.chaseCamActiveConfirm(false)) {
				mVelocity.x -= speed;
				mTrackPlayer = false;

			}
		}

		if (pInputState.keyDown(GLFW.GLFW_KEY_D)) {
			if (mPlayer.chaseCamActiveConfirm(false)) {
				mVelocity.x += speed;
				mTrackPlayer = false;

			}
		}

		if (pInputState.keyDown(GLFW.GLFW_KEY_S)) {
			if (mPlayer.chaseCamActiveConfirm(false)) {
				mVelocity.y += speed;
				mTrackPlayer = false;

			}
		}

		if (pInputState.keyDown(GLFW.GLFW_KEY_W)) {
			if (mPlayer.chaseCamActiveConfirm(false)) {
				mVelocity.y -= speed;
				mTrackPlayer = false;

			}
		}

		return false;

	}

	public void update(GameTime pGameTime) {

		if (!mPlayer.isCommanderPresent()) {
			mPlayer.chaseCamActive(true);
			// mGameCamera.setAbsPosition(mPlayer.tank().x + 100,
			// mPlayer.tank().y);

		}

		if (mPlayer.chaseCamActive()) {
			// mGameCamera.setPosition(mPlayer.tank().x + HUDInterface.HUD_WIDTH
			// / 2, mPlayer.tank().y);

		} else {
			// Cap
			if (mVelocity.x < -CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.x = -CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.x > CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.x = CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.y < -CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.y = -CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.y > CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.y = CAMERA_MAN_MOVE_SPEED_MAX;

			float elapsed = (float) pGameTime.elapseGameTime();

			// Apply
			float lCurX = mGameCamera.getPosition().x;
			float lCurY = mGameCamera.getPosition().y;

			mGameCamera.setAbsPosition(lCurX + mVelocity.x * elapsed, lCurY + mVelocity.y * elapsed);
			
		}

		// DRAG
		mVelocity.x *= 0.917f;
		mVelocity.y *= 0.917f;

		// There are minimums for the camera

	}

	public void zoomIn() {
		mGameCamera.setZoomFactor(CameraZoomController.MAX_CAMERA_ZOOM);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
