package net.ld.oneroom.screens;

import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.ld.library.controllers.camera.CameraZoomController;
import net.ld.library.core.camera.ShakeCamera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.Screen;
import net.ld.library.screenmanager.ScreenManager;
import net.ld.oneroom.controllers.CameraController;
import net.ld.oneroom.controllers.EnemyController;
import net.ld.oneroom.controllers.PlayerController;
import net.ld.oneroom.controllers.WorldController;
import net.ld.oneroom.hud.HUDInterface;
import net.ld.oneroom.hud.HelpOverlay;
import net.ld.oneroom.particles.BulletSystem;
import net.ld.oneroom.particles.ParticleSystem;
import net.ld.oneroom.particles.RotToTarget;
import net.ld.oneroom.particles.RotateModifier;
import net.ld.oneroom.particles.ScaleInitialiser;
import net.ld.oneroom.particles.ScaleModifier;
import net.ld.oneroom.views.EnemyView;
import net.ld.oneroom.views.PickupView;
import net.ld.oneroom.views.PlayerView;
import net.ld.oneroom.views.WorldView;
import net.ld.oneroom.world.EnemyManager;
import net.ld.oneroom.world.EnemyManager.EnemyEntity;
import net.ld.oneroom.world.GameWorld;
import net.ld.oneroom.world.Player;
import net.ld.oneroom.world.TankCrew;

public class GameScreen extends Screen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int CELL_SIZE = 128;
	public static final int CELLS_WIDE = 64;
	public static final int CELLS_HIGH = 64;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private HUDInterface mHudinterface;
	private HelpOverlay mHelpOverlay;

	// Controllers
	private PlayerController mPlayerController;
	
	private CameraController mCameraController;
	private CameraZoomController mCameraZoomController;
	
	private WorldController mWorldController;
	private EnemyController mEnemyController;

	// Data / Model
	private Player mPlayer;
	private GameWorld mGameWorld;
	private EnemyManager mEnemyManager;

	// Views
	private WorldView mWorldView;
	private PlayerView mPlayerView;
	private EnemyView mEnemyView;
	private PickupView mPickupView;

	// Particle Systems
	BulletSystem mEnemyBullets;
	BulletSystem mPlayerBullets;
	BulletSystem mPlayerRockets;
	BulletSystem mGroundCrators;

	ParticleSystem mSmokeParticles;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public GameWorld gameWorld() {
		return mGameWorld;
	}

	public EnemyManager enemyManager() {
		return mEnemyManager;
	}

	public BulletSystem playerBullets() {
		return mPlayerBullets;
	}

	public BulletSystem playerRockets() {
		return mPlayerRockets;
	}

	public BulletSystem enemyBullets() {
		return mEnemyBullets;
	}

	public ParticleSystem smoke() {
		return mSmokeParticles;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mGameWorld = new GameWorld(CELL_SIZE, CELLS_WIDE, CELLS_HIGH);
		mPlayer = new Player();
		mEnemyManager = new EnemyManager();

		mShowInBackground = true;

		// Controllers
		mPlayerController = new PlayerController();
		mCameraController = new CameraController();
		mCameraZoomController = new CameraZoomController();
		mWorldController = new WorldController();
		mEnemyController = new EnemyController();

		// Views
		mWorldView = new WorldView();
		mPlayerView = new PlayerView();
		mEnemyView = new EnemyView();
		mPickupView = new PickupView();

		mHudinterface = new HUDInterface();
		mHelpOverlay = new HelpOverlay();
		mHelpOverlay.showHelp();

		mPlayerBullets = new BulletSystem("res/textures/particles.png", 64) {

			@Override
			protected void checkCollision(GameWorld pGameWorld, Particle pPart) {
				// Check collision against enemy
				List<EnemyEntity> lEnemies = mEnemyManager.enemies();

				final int lNumEnemies = lEnemies.size();
				for (int i = 0; i < lNumEnemies; i++) {
					EnemyEntity lEnemyUnit = lEnemies.get(i);

					if (!lEnemyUnit.isAlive)
						continue;

					float lMaxDist = 16f + lEnemyUnit.radius;
					float lDistSqr = (lEnemyUnit.xx - pPart.x) * (lEnemyUnit.xx - pPart.x)
							+ (lEnemyUnit.yy - pPart.y) * (lEnemyUnit.yy - pPart.y);

					if (lDistSqr <= lMaxDist * lMaxDist) {
						lEnemyUnit.health--;

						if (lEnemyUnit.health < 0) {
							lEnemyUnit.kill();
						}

						pPart.kill();

					}

				}

			}

		};

		mPlayerRockets = new BulletSystem("res/textures/particles.png", 3) {

			@Override
			protected void checkCollision(GameWorld pGameWorld, Particle pPart) {
				// Check collision against enemy
				List<EnemyEntity> lEnemies = mEnemyManager.enemies();

				final int lNumEnemies = lEnemies.size();
				for (int i = 0; i < lNumEnemies; i++) {
					EnemyEntity lEnemyUnit = lEnemies.get(i);

					if (!lEnemyUnit.isAlive)
						continue;

					float lMaxDist = 16f + lEnemyUnit.radius;
					float lDistSqr = (lEnemyUnit.xx - pPart.x) * (lEnemyUnit.xx - pPart.x)
							+ (lEnemyUnit.yy - pPart.y) * (lEnemyUnit.yy - pPart.y);

					if (lDistSqr <= lMaxDist * lMaxDist) {
						lEnemyUnit.health -= 10; // brown bread

						if (lEnemyUnit.health < 0) {
							lEnemyUnit.kill();
						}

						// pPart.kill();
						// pPart.life *= 0.5f;

					}

				}

			}

			@Override
			protected void particleEndLife(Particle pPart) {
				super.particleEndLife(pPart);

				// Need a big explosion
				mGroundCrators.addParticle(pPart.x, pPart.y, 0, 0, 18000);

				if (mGameCamera instanceof ShakeCamera) {
					ShakeCamera lShakeCamera = (ShakeCamera) mGameCamera;
					lShakeCamera.shake(600f, 50.0f);
				}

				List<EnemyEntity> lEnemies = mEnemyManager.enemies();

				final int lNumEnemies = lEnemies.size();
				for (int i = 0; i < lNumEnemies; i++) {
					EnemyEntity lEnemyUnit = lEnemies.get(i);

					if (!lEnemyUnit.isAlive)
						continue;

					float lMaxDist = 256f; // radius of blast
					float lDistSqr = (lEnemyUnit.xx - pPart.x) * (lEnemyUnit.xx - pPart.x)
							+ (lEnemyUnit.yy - pPart.y) * (lEnemyUnit.yy - pPart.y);

					if (lDistSqr <= lMaxDist * lMaxDist) {
						lEnemyUnit.health -= 10; // brown bread

						float lDirX = (lEnemyUnit.x - 16) - pPart.x;
						float lDirY = (lEnemyUnit.y - 16) - pPart.y;

						float forceX = (float) Math.cos(lDirX) * 20f;
						float forceY = (float) Math.sin(lDirY) * 20f;

						lEnemyUnit.dx += forceX;
						lEnemyUnit.dy += forceY;

					}

				}

				for (int j = 0; j < 30; j++) {
					mSmokeParticles.addParticle(pPart.x, pPart.y, -15f, -15f, 1500.0f);

				}

			}

		};
		mPlayerRockets.setTextureArea(16, 0, 32, 32, 32, 32);

		mEnemyBullets = new BulletSystem("res/textures/particles.png", 128) {

			@Override
			protected void checkCollision(GameWorld pGameWorld, Particle pPart) {

				// Gunner 0
				if (mPlayer.tank().mGunnerFrontExt.mMannedBy != null) {

					float rad = mPlayer.tank().mGunnerFrontExt.r;
					float xx = (mPlayer.tank().x + mPlayer.tank().mGunnerFrontExt.x) - pPart.x;
					float yy = (mPlayer.tank().y + mPlayer.tank().mGunnerFrontExt.y) - pPart.y;

					if (Math.sqrt(xx * xx + yy * yy) <= (16f + rad)) {
						TankCrew lCrew = mPlayer.tank().mGunnerFrontExt.mMannedBy;

						pPart.kill();

						if (lCrew.health > 0) {
							lCrew.health--;

						}

					}

				}

				// Gunner 0
				if (mPlayer.tank().mGunnerBackExt.mMannedBy != null) {

					float rad = mPlayer.tank().mGunnerBackExt.r;
					float xx = (mPlayer.tank().x + mPlayer.tank().mGunnerBackExt.x) - pPart.x;
					float yy = (mPlayer.tank().y + mPlayer.tank().mGunnerBackExt.y) - pPart.y;

					if (Math.sqrt(xx * xx + yy * yy) <= (16f + rad)) {
						TankCrew lCrew = mPlayer.tank().mGunnerBackExt.mMannedBy;

						pPart.kill();

						if (lCrew.health > 0) {
							lCrew.health--;

						}

					}

				}

				// Engine
				if (Math.sqrt((mPlayer.tank().mEngine.x - pPart.x) * (mPlayer.tank().mEngine.x - pPart.x)
						+ (mPlayer.tank().mEngine.y - pPart.y) * (mPlayer.tank().mEngine.y - pPart.y)) <= (16f
								+ mPlayer.tank().mEngine.r)) {

					// Damage hull (if heights match)
					if (pPart.height < mPlayer.tank().mEngine.height) {
						pPart.kill();

						if (mPlayer.tank().mEngine.health > 0) {
							final float lDamageAmt = 1f;
							mPlayer.tank().mEngine.applyDamage(lDamageAmt);

						}

					}

				}

				// Turret
				if (Math.sqrt((mPlayer.tank().mTurret.x - pPart.x) * (mPlayer.tank().mTurret.x - pPart.x)
						+ (mPlayer.tank().mTurret.y - pPart.y) * (mPlayer.tank().mTurret.y - pPart.y)) <= (16f
								+ mPlayer.tank().mTurret.r)) {

					// Damage hull (if heights match)
					if (pPart.height < mPlayer.tank().mTurret.height) {
						pPart.kill();

						if (mPlayer.tank().mTurret.health > 0) {
							// TODO: weapon damage types
							final float lDamageAmt = 1f;
							mPlayer.tank().mTurret.applyDamage(lDamageAmt);

						}

					}

				}

				// Main hull
				if (Math.sqrt((mPlayer.tank().x - pPart.x) * (mPlayer.tank().x - pPart.x)
						+ (mPlayer.tank().y - pPart.y) * (mPlayer.tank().y - pPart.y)) <= (16f
								+ mPlayer.tank().hitRadius)) {

					// Damage hull (if heights match)
					if (pPart.height < mPlayer.tank().mHull.height) {
						pPart.kill();

						if (mPlayer.tank().mHull.health > 0) {
							// TODO: weapon damage types
							final float lDamageAmt = 1f;
							mPlayer.tank().mHull.applyDamage(lDamageAmt);

							if (mGameCamera instanceof ShakeCamera) {
								ShakeCamera lShakeCamera = (ShakeCamera) mGameCamera;
								lShakeCamera.shake(200f, 2.0f);
							}

						}

					}

				}

			}

		};

		mPlayerBullets.addInitialise(new RotToTarget());
		mPlayerRockets.addInitialise(new RotToTarget());
		mEnemyBullets.addInitialise(new RotToTarget());
		mPlayerBullets.addInitialise(new ScaleInitialiser(2f));
		mPlayerRockets.addInitialise(new ScaleInitialiser(2f));
		mEnemyBullets.addInitialise(new ScaleInitialiser(2f));

		mGroundCrators = new BulletSystem("res/textures/particles.png", 128);
		mGroundCrators.setTextureArea(48, 0, 32, 32, 64, 64);
		mGroundCrators.addInitialise(new ScaleInitialiser(2f));

		mSmokeParticles = new ParticleSystem("res/textures/particles.png", 200);
		mSmokeParticles.setTextureArea(80, 0, 16, 16, 16, 16);

		mSmokeParticles.addInitialise(new ScaleInitialiser(4f));
		mSmokeParticles.addModifier(new RotateModifier(3f));
		mSmokeParticles.addModifier(new ScaleModifier(1.015f));

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		mPlayerController.initialise(this, mPlayer, mCameraController);
		mCameraController.initialise(mScreenManager.gameCamera(), mPlayer);
		mCameraZoomController.setZoomConstraints(0.8f, 1.4f);
		mCameraZoomController.setCamera(mScreenManager.gameCamera());
		mWorldController.initialise(mGameWorld, mScreenManager.gameCamera());
		mEnemyController.initialise(this, mEnemyManager, mPlayer.tank());

		mPlayerBullets.initialise(mScreenManager.gameCamera(), mGameWorld);
		mPlayerRockets.initialise(mScreenManager.gameCamera(), mGameWorld);
		mEnemyBullets.initialise(mScreenManager.gameCamera(), mGameWorld);
		mGroundCrators.initialise(mScreenManager.gameCamera(), mGameWorld);
		mSmokeParticles.initialise(mScreenManager.gameCamera());

		mWorldView.initialise(mGameWorld);
		mPlayerView.initialise(mGameWorld, mPlayer);
		mEnemyView.initialise(mGameWorld, mEnemyManager);
		mPickupView.initialise(mGameWorld);

		mPlayer.initialise();

		mEnemyManager.initialise(mGameWorld, mPlayer.tank(), mScreenManager.gameCamera());

		mHudinterface.initialise(mPlayer, mScreenManager.HUD());

		// Setup world
		mGameWorld.initialise(mPlayer, mEnemyManager);

		// Setup the player
		mGameWorld.addEntity(mPlayer.tank());
		mPlayer.setStartPosition(0, 0);
		mScreenManager.gameCamera().setAbsPosition(0, 0);

		// Reset the camera

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {

		mWorldView.loadGLContent(pResourceManager);
		mPlayerView.loadGLContent(pResourceManager);
		mEnemyView.loadGLContent(pResourceManager);
		mPickupView.loadGLContent(pResourceManager);

		mHudinterface.loadGLContent(pResourceManager);
		mHelpOverlay.loadGLContent(pResourceManager);

		mPlayerBullets.loadGLContent(pResourceManager);
		mPlayerRockets.loadGLContent(pResourceManager);
		mEnemyBullets.loadGLContent(pResourceManager);
		mGroundCrators.loadGLContent(pResourceManager);
		mSmokeParticles.loadGLContent(pResourceManager);

		mIsLoaded = true;
	}

	@Override
	public void unloadGLContent() {
		mWorldView.unloadGLContent();
		mPlayerView.unloadGLContent();
		mEnemyView.unloadGLContent();
		mPickupView.unloadGLContent();

		mHudinterface.unloadGLContent();
		mHelpOverlay.unloadGLContent();

		mPlayerBullets.unloadGLContent();
		mPlayerRockets.unloadGLContent();
		mEnemyBullets.unloadGLContent();
		mGroundCrators.unloadGLContent();
		mSmokeParticles.unloadGLContent();

	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pGameTime, pInputState, pAcceptMouse, pAcceptKeyboard);

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			mScreenManager.addScreen(new PauseScreen(mScreenManager));

		}

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_SPACE)) {
			if (mCameraController != null) {
				if (mCameraController.gameCamera() instanceof ShakeCamera) {
					ShakeCamera lShakeCamera = (ShakeCamera) mCameraController.gameCamera();
					lShakeCamera.shake(1000f, 30.0f);
				}
			}

		}

		if (!pAcceptMouse)
			return;

		if (mHudinterface.handleInput(pInputState)) {
			return;
		}

		mHelpOverlay.handleInput(pInputState);
		mPlayerController.handleInput(pInputState);
		mCameraController.handleInput(pInputState);
		
		mCameraZoomController.handleInput(pInputState);

	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (pCoveredByOtherScreen)
			return;

		mHudinterface.update(pGameTime);
		mHelpOverlay.update(pGameTime);
		mCameraController.update(pGameTime);

		mPlayerController.update(pGameTime);
		mPlayerView.update(pGameTime);
		mPlayer.update(pGameTime);
		mEnemyController.update(pGameTime);
		mGameWorld.update(pGameTime);
		mEnemyManager.update(pGameTime);

		mPlayerBullets.update(pGameTime);
		mPlayerRockets.update(pGameTime);
		mEnemyBullets.update(pGameTime);
		mGroundCrators.update(pGameTime);
		mSmokeParticles.update(pGameTime);

		mCameraZoomController.update(pGameTime);
		
		mPlayer.update(pGameTime);

		checkEndConditions(pGameTime);

	}

	@Override
	public void draw(RenderState pRenderState) {
		if (mPlayer == null)
			return;

		boolean lIsCommanderPresent = (mPlayer.tank().mCommander.mMannedBy != null);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		if (lIsCommanderPresent) {
			mWorldView.draw(pRenderState);
			mEnemyView.draw(pRenderState);
			mPickupView.draw(pRenderState);

		} else {
			// Draw fog
			// --->
		}

		mPlayerView.draw(pRenderState);

		if (lIsCommanderPresent) {
			mGroundCrators.draw(pRenderState);
			mPlayerBullets.draw(pRenderState);
			mPlayerRockets.draw(pRenderState);
			mEnemyBullets.draw(pRenderState);
			mSmokeParticles.draw(pRenderState);

		}

		mHelpOverlay.draw(pRenderState);
		mHudinterface.draw(pRenderState);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void checkEndConditions(GameTime pGameTime) {
		// Check hull destroyed
		if (mPlayer.tank().mHull.health <= 0) {
			mScreenManager.addScreen(new GameOverScreen(mScreenManager, GameOverScreen.GAME_OVER_REAONS.dead_tank));
		}

		// Check for dead crew
		if (mPlayer.remainingCrew() <= 0) {
			mScreenManager.addScreen(new GameOverScreen(mScreenManager, GameOverScreen.GAME_OVER_REAONS.dead_crew));
		}

		// All enemy destroyed
		// if (mEnemyManager.getNumAliveEnemies() <= 0) {
		// mScreenManager.addScreen(new GameOverScreen(mScreenManager,
		// GameOverScreen.GAME_OVER_REAONS.won));
		// }

	}

}
