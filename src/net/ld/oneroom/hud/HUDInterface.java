package net.ld.oneroom.hud;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.camera.HUD;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.world.Player;
import net.ld.oneroom.world.TankCrew;
import net.ld.oneroom.world.TankEntity;
import net.ld.oneroom.world.TankEntity.TankComponent;

public class HUDInterface extends Rectangle {

	public class CrewPortrait extends Rectangle {

		public String roleName;
		public TankComponent mTankComponent;

		public boolean isAssigned() {
			return mTankComponent.mMannedBy != null;
		}

		public void tankComponent(TankComponent pCrew) {
			mTankComponent = pCrew;
		}

		public TankComponent tankComponent() {
			return mTankComponent;
		}

		public CrewPortrait(TankComponent pFunction) {
			mTankComponent = pFunction;
		}

	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int HUD_WIDTH = 200;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TextureBatch mSpriteBatch;
	private Texture mHUDTexture;
	private Player mPlayer;

	private Bar mGas;
	private Bar mRockets;
	private Bar mEngine;
	private Bar mArmour;
	private Bar mTurret;

	private TankCrew mCrewonMouse;
	private List<CrewPortrait> mPortraits;
	private HUD mHUDCamera;

	FontUnit mHUDFont;
	private float mouseX, mouseY;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public HUDInterface() {
		mGas = new Bar();
		mRockets = new Bar();
		mEngine = new Bar();
		mArmour = new Bar();
		mTurret = new Bar();

		mPortraits = new ArrayList<>();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(Player pPlayer, HUD pCamera) {
		mPlayer = pPlayer;
		mHUDCamera = pCamera;

		mSpriteBatch = new TextureBatch();

		mGas.fullAmount(TankEntity.MAX_FUEL_AMT); // TODO: Gas bar
		mRockets.fullAmount(mPlayer.tank().mRocketStore.max_health);
		mEngine.fullAmount(mPlayer.tank().mEngine.max_health);
		mArmour.fullAmount(mPlayer.tank().mHull.max_health);
		mTurret.fullAmount(mPlayer.tank().mTurret.max_health);

		set(pCamera.boundingHUDRectange().right() - HUD_WIDTH, pCamera.boundingHUDRectange().top(), HUD_WIDTH, pCamera.boundingHUDRectange().height);

		final float portOffY = 145;
		final float portSizeY = 50;

		// Create the portraits and link them with the tank roles
		CrewPortrait lCommander = new CrewPortrait(pPlayer.tank().mCommander);
		lCommander.roleName = "Commander";
		lCommander.x = x;
		lCommander.y = y + portOffY;
		lCommander.width = 128f;
		lCommander.height = 32f;

		CrewPortrait lDriver = new CrewPortrait(pPlayer.tank().mDriver);
		lDriver.roleName = "Driver";
		lDriver.x = x;
		lDriver.y = y + portOffY + portSizeY;
		lDriver.width = 128f;
		lDriver.height = 32f;

		CrewPortrait lLoader = new CrewPortrait(pPlayer.tank().mLoader);
		lLoader.roleName = "Loader";
		lLoader.x = x;
		lLoader.y = y + portOffY + portSizeY * 2f;
		lLoader.width = 128f;
		lLoader.height = 32f;

		CrewPortrait lGunner = new CrewPortrait(pPlayer.tank().mGunnerInt);
		lGunner.roleName = "Gunner";
		lGunner.x = x;
		lGunner.y = y + portOffY + portSizeY * 3f;
		lGunner.width = 128f;
		lGunner.height = 32f;

		CrewPortrait lEngine = new CrewPortrait(pPlayer.tank().mEngine);
		lEngine.roleName = "Fix Engines";
		lEngine.x = x;
		lEngine.y = y + portOffY + portSizeY * 4f;
		lEngine.width = 128f;
		lEngine.height = 32f;

		CrewPortrait lTurret = new CrewPortrait(pPlayer.tank().mTurret);
		lTurret.roleName = "Fix Turret";
		lTurret.x = x;
		lTurret.y = y + portOffY + portSizeY * 5f;
		lTurret.width = 128f;
		lTurret.height = 32f;

		CrewPortrait lHull = new CrewPortrait(pPlayer.tank().mHull);
		lHull.roleName = "Fix Hull Body";
		lHull.x = x;
		lHull.y = y + portOffY + portSizeY * 6f;
		lHull.width = 128f;
		lHull.height = 32f;

		CrewPortrait lGunnerExt1 = new CrewPortrait(pPlayer.tank().mGunnerFrontExt);
		lGunnerExt1.roleName = "Ext Gun Front";
		lGunnerExt1.x = x;
		lGunnerExt1.y = y + portOffY + portSizeY * 7f;
		lGunnerExt1.width = 128f;
		lGunnerExt1.height = 32f;

		CrewPortrait lGunnerExt2 = new CrewPortrait(pPlayer.tank().mGunnerBackExt);
		lGunnerExt2.roleName = "Ext Gun Back";
		lGunnerExt2.x = x;
		lGunnerExt2.y = y + portOffY + portSizeY * 8f;
		lGunnerExt2.width = 128f;
		lGunnerExt2.height = 32f;

		mPortraits.add(lCommander);
		mPortraits.add(lDriver);

		mPortraits.add(lLoader);
		mPortraits.add(lGunner);

		mPortraits.add(lEngine);
		mPortraits.add(lTurret);
		mPortraits.add(lHull);

		mPortraits.add(lGunnerExt1);
		mPortraits.add(lGunnerExt2);

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);
		mHUDTexture = TextureManager.textureManager().loadTextureFromFile("HUDTexture", "res/textures/hud.png", GL11.GL_NEAREST);

		mHUDFont = pResourceManager.fontManager().loadFontFromResource("PIXEL", "/res/fonts/pixel.ttf", 25);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		mHUDFont.unloadGLContent();
	}

	public boolean handleInput(InputState pInputState) {

		mouseX = pInputState.HUD().getMouseCameraSpaceX();
		mouseY = pInputState.HUD().getMouseCameraSpaceY();

		// Wrong place really, but no time.
		// Here we will handle assigning crew to slots ....
		if (intersects(mHUDCamera.getMouseCameraSpace())) {
			if (pInputState.isMouseTimedLeftClickAvailable()) {

				// If there is a crew member 'on-the-mouse', check what to do with him.
				if (mCrewonMouse != null) {
					final int lPortraitCount = mPortraits.size();
					for (int i = 0; i < lPortraitCount; i++) {
						CrewPortrait lPortrait = mPortraits.get(i);

						if (lPortrait.intersects(mHUDCamera.getMouseCameraSpace())) {

							if (lPortrait.mTankComponent.mMannedBy != null) {
								System.out.println("role already assigned");
								mCrewonMouse = null; // leave crew in current role

							} else {

								lPortrait.mTankComponent.mMannedBy = mCrewonMouse;
								mCrewonMouse = null;

								pInputState.setLeftMouseClickHandled();
								return true;

							}

						}

					}
				}

				// Otherwise, check if a crew member was selected (none were previously)
				else {

					final int lPortraitCount = mPortraits.size();
					for (int i = 0; i < lPortraitCount; i++) {
						CrewPortrait lPortrait = mPortraits.get(i);

						if (lPortrait.intersects(mHUDCamera.getMouseCameraSpace())) {
							System.out.println("Reassigning " + lPortrait.roleName);

							if (mCrewonMouse == null) {
								// put the crew on the mouse
								mCrewonMouse = lPortrait.mTankComponent.mMannedBy;
								lPortrait.mTankComponent.mMannedBy = null;
								pInputState.setLeftMouseClickHandled();
								return true;

							}

						}

					}

				}

				pInputState.tryAquireLeftClickOwnership(hashCode()); // lock
				pInputState.setLeftMouseClickHandled();
				return true;
			}

		}

		return false;

	}

	public void update(GameTime pGameTime) {
		// update the bars
		mGas.currentAmount(mPlayer.tank().fuelAmt);
		mRockets.currentAmount(mPlayer.tank().mRocketStore.health);
		mEngine.currentAmount(mPlayer.tank().mEngine.health);
		mArmour.currentAmount(mPlayer.tank().mHull.health);
		mTurret.currentAmount(mPlayer.tank().mTurret.health);

		final int lPortraitCount = mPortraits.size();
		for (int i = 0; i < lPortraitCount; i++) {
			CrewPortrait lPortrait = mPortraits.get(i);

			if (lPortrait.mTankComponent.mMannedBy != null) {
				if (!lPortrait.mTankComponent.mMannedBy.isAlive()) {
					lPortrait.mTankComponent.mMannedBy = null;

				}

			}

		}

	}

	public void draw(RenderState pRenderState) {

		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();

		final float PANEL_X = lHUDrect.right() - HUD_WIDTH;
		final float PANEL_Y = lHUDrect.y;

		mSpriteBatch.begin(pRenderState.hudCamera());
		// Draw the background panel
		mSpriteBatch.draw(352, 0, 160, 512, PANEL_X, PANEL_Y, 2f, HUD_WIDTH, lHUDrect.height, 1f, mHUDTexture);
		mSpriteBatch.draw(32, 256, 320, 256, PANEL_X, PANEL_Y, 2f, 384 / 2, 128, 1f, mHUDTexture);
		mSpriteBatch.end();

		drawErrors(pRenderState);

		drawRoster(pRenderState);

		drawStateBars(pRenderState);

		// Draw controls to toggle cupola and turret lock
		drawControls(pRenderState);

		drawResources(pRenderState);

		if (mCrewonMouse != null) {
			mSpriteBatch.begin(pRenderState.hudCamera());
			mSpriteBatch.draw(96, 96, 32, 32, mouseX + 32f, mouseY + 32, 2.2f, 32f, 32f, 1f, 1f, mHUDTexture);
			mSpriteBatch.end();
		}

	}

	private void drawErrors(RenderState pRenderState) {
		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();

		mHUDFont.begin(pRenderState.hudCamera());
		mSpriteBatch.begin(pRenderState.hudCamera());

		if (mPlayer.tank().mEngine.health < mPlayer.tank().mTurret.operational_health) {
			mSpriteBatch.draw(256, 0, 32, 32, lHUDrect.right() - HUD_WIDTH - 16 - 32, lHUDrect.bottom() - 128f, 1.95f, 32, 32, 1f, mHUDTexture);
			mHUDFont.draw("Engine Damaged", lHUDrect.right() - HUD_WIDTH - 16 - 162, lHUDrect.bottom() - 114f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		}

		if (mPlayer.tank().mHull.health < mPlayer.tank().mHull.max_health - 15) {
			mSpriteBatch.draw(256, 0, 32, 32, lHUDrect.right() - HUD_WIDTH - 16 - 32, lHUDrect.bottom() - 160f, 1.95f, 32, 32, 1f, mHUDTexture);
			mHUDFont.draw("Hull Critical", lHUDrect.right() - HUD_WIDTH - 16 - 166, lHUDrect.bottom() - 150f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		} else

		if (mPlayer.tank().mHull.health < mPlayer.tank().mHull.max_health - 10) {
			mSpriteBatch.draw(256, 0, 32, 32, lHUDrect.right() - HUD_WIDTH - 16 - 32, lHUDrect.bottom() - 160f, 1.95f, 32, 32, 1f, mHUDTexture);
			mHUDFont.draw("Hull Damaged", lHUDrect.right() - HUD_WIDTH - 16 - 166, lHUDrect.bottom() - 150f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		}

		if (mPlayer.tank().mTurret.health < mPlayer.tank().mTurret.operational_health) {
			mSpriteBatch.draw(256, 0, 32, 32, lHUDrect.right() - HUD_WIDTH - 16 - 32, lHUDrect.bottom() - 96f, 1.95f, 32, 32, 1f, mHUDTexture);
			mHUDFont.draw("Turret Destroyed", lHUDrect.right() - HUD_WIDTH - 16 - 180, lHUDrect.bottom() - 84f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		} else if (mPlayer.tank().mTurret.health < mPlayer.tank().mTurret.max_health - 10) {
			mSpriteBatch.draw(256, 0, 32, 32, lHUDrect.right() - HUD_WIDTH - 16 - 32, lHUDrect.bottom() - 96f, 1.95f, 32, 32, 1f, mHUDTexture);
			mHUDFont.draw("Turret Damaged", lHUDrect.right() - HUD_WIDTH - 16 - 166, lHUDrect.bottom() - 84f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		}

		if (mPlayer.tank().fuelAmt < 10) {
			mSpriteBatch.draw(256, 0, 32, 32, lHUDrect.right() - HUD_WIDTH - 16 - 32, lHUDrect.bottom() - 64f, 1.95f, 32, 32, 1f, mHUDTexture);
			mHUDFont.draw("Out of Fuel", lHUDrect.right() - HUD_WIDTH - 16 - 128, lHUDrect.bottom() - 51, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		}

		if (mPlayer.tank().turretReloaded) {
			final float lWidth = 192;

			if (!mPlayer.tank().mTurret.isOperational()) {
				mHUDFont.draw("Turret broken!", lHUDrect.centerX() - 175, lHUDrect.bottom() - 96f + 7f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
			} else if (mPlayer.tank().mGunnerInt.mMannedBy == null) {
				mHUDFont.draw("Turret reloaded - You need to man it", lHUDrect.centerX() - 175, lHUDrect.bottom() - 96f + 7f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
			} else {
				mHUDFont.draw("Turret reloaded - Ready to fire!", lHUDrect.centerX() - 175, lHUDrect.bottom() - 96f + 7f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
			}

			mSpriteBatch.draw(0, 160, lWidth, 32, lHUDrect.centerX() - 64 - lWidth / 2, lHUDrect.bottom() - 64f, 1.95f, lWidth, 32, 1f, mHUDTexture);
			mHUDFont.draw("[SPACE]", lHUDrect.centerX() - 85, lHUDrect.bottom() - 64f + 9f, 1.96f, 1f, 1f, 1f, 1f, 1.0f, -1);
		}

		mSpriteBatch.end();
		mHUDFont.end();
	}

	private void drawResources(RenderState pRenderState) {

		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();

		mHUDFont.begin(pRenderState.hudCamera());
		mSpriteBatch.begin(pRenderState.hudCamera());

		// Draw scrap
		mSpriteBatch.draw(0, 128, 32, 32, lHUDrect.right() - HUD_WIDTH - 32, lHUDrect.top() + 4, 2.2f, 32f, 32f, 1.0f, 1.0f, mHUDTexture);
		mHUDFont.draw("" + mPlayer.scrapAmount(), lHUDrect.right() - HUD_WIDTH - 32 - 8 - 16, lHUDrect.top() + 18, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);

		mSpriteBatch.end();
		mHUDFont.end();

	}

	private void drawRoster(RenderState pRenderState) {

		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();

		final float PANEL_X = lHUDrect.right() - HUD_WIDTH;
		final float PANEL_Y = lHUDrect.y + 20;

		mHUDFont.begin(pRenderState.hudCamera());
		mHUDFont.draw("CREW ROSTER", PANEL_X + 32, PANEL_Y + 100, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);

		mSpriteBatch.begin(pRenderState.hudCamera());

		final int lPortraitCount = mPortraits.size();
		for (int i = 0; i < lPortraitCount; i++) {
			CrewPortrait lPortrait = mPortraits.get(i);

			// Draw empty portraits
			mSpriteBatch.draw(64, 96, 32, 32, lPortrait.x + 16f, lPortrait.y, 2.2f, 32f, 32f, 1f, 1f, mHUDTexture);
			mHUDFont.draw(lPortrait.roleName, lPortrait.x + 58f, lPortrait.y, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);

			// Draw face if assigned
			if (lPortrait.tankComponent().mMannedBy != null) {

				mHUDFont.draw(lPortrait.tankComponent().mMannedBy.name, lPortrait.x + 58f, lPortrait.y + 18f, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);
				mSpriteBatch.draw(96, 96, 32, 32, lPortrait.x + 16f, lPortrait.y, 2.2f, 32f, 32f, 1f, 1f, mHUDTexture);

			} else {
				mHUDFont.draw("<unmanned>", lPortrait.x + 58f, lPortrait.y + 18f, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);
			}

		}

		mSpriteBatch.end();
		mHUDFont.end();

	}

	private void drawStateBars(RenderState pRenderState) {

		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();

		final float PANEL_X = lHUDrect.left() + 15;
		final float PANEL_Y = lHUDrect.bottom() - 167;

		mHUDFont.begin(pRenderState.hudCamera());
		mHUDFont.draw("SYSTEM LEVELS", PANEL_X, PANEL_Y, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);
		mHUDFont.draw(" F     R     E    A     T", PANEL_X, PANEL_Y + 18, 2.2f, 1f, 1f, 1f, 1f, 1f, -1);
		mHUDFont.end();

		mSpriteBatch.begin(pRenderState.hudCamera());

		final float paddingX = 23f;

		// Draw the bars
		// Gas
		float lGasAmt = (128f / mGas.fullAmount() * mGas.currentAmount());
		mSpriteBatch.draw(16, 64, 16, 64, PANEL_X, PANEL_Y + 35 + 128, 2.0f, 16, -lGasAmt, 1f, 0.2f, 1.0f, 0.1f, 1f, mHUDTexture);
		mSpriteBatch.draw(0, 64, 16, 64, PANEL_X, PANEL_Y + 35, 2.1f, 16, 128, 1f, 1f, mHUDTexture);

		// Rockets
		float lNumRockets = (128f / mRockets.fullAmount() * mRockets.currentAmount());
		mSpriteBatch.draw(16, 64, 16, 64, PANEL_X + paddingX, PANEL_Y + 35 + 128, 2.0f, 16, -lNumRockets, 1f, 1f, mHUDTexture);
		mSpriteBatch.draw(0, 64, 16, 64, PANEL_X + paddingX, PANEL_Y + 35, 2.1f, 16, 128, 1f, 1f, mHUDTexture);

		// Engine
		float lEngineAmt = (128f / mEngine.fullAmount() * mEngine.currentAmount());
		float engine_r = 1f;
		float engine_g = 1f;
		float engine_b = 1f;
		if (mEngine.currentAmount() <= 10) {
			engine_r = 1f;
			engine_g = 0;
			engine_b = 0;
		} else if (mEngine.currentAmount() <= 20) {
			engine_r = 1f;
			engine_g = 1f;
			engine_b = 0;
		} else {
			engine_r = 0;
			engine_g = 1f;
			engine_b = 0;
		}
		mSpriteBatch.draw(16, 64, 16, 64, PANEL_X + paddingX * 2f, PANEL_Y + 35 + 128, 2.0f, 16, -lEngineAmt, 1f, engine_r, engine_g, engine_b, 1f, mHUDTexture);
		mSpriteBatch.draw(0, 64, 16, 64, PANEL_X + paddingX * 2f, PANEL_Y + 35, 2.1f, 16, 128, 1f, 1f, mHUDTexture);

		// Armour
		float lArmourAmt = (128f / mArmour.fullAmount() * mArmour.currentAmount());
		float armour_r = 1f;
		float armour_g = 1f;
		float armour_b = 1f;
		if (mArmour.currentAmount() <= 15) {
			armour_r = 1f;
			armour_g = 0;
			armour_b = 0;
		} else if (mArmour.currentAmount() <= 25) {
			armour_r = 1f;
			armour_g = 1f;
			armour_b = 0;
		} else {
			armour_r = 0;
			armour_g = 1f;
			armour_b = 0;
		}

		mSpriteBatch.draw(16, 64, 16, 64, PANEL_X + paddingX * 3f, PANEL_Y + 35 + 128, 2.0f, 16, -lArmourAmt, 1f, armour_r, armour_g, armour_b, 1f, mHUDTexture);
		mSpriteBatch.draw(0, 64, 16, 64, PANEL_X + paddingX * 3f, PANEL_Y + 35, 2.1f, 16, 128, 1f, 1f, mHUDTexture);

		// Turret TODO: Draw turret state on HUD
		float lTurretAmt = (128f / mTurret.fullAmount() * mTurret.currentAmount());
		mSpriteBatch.draw(16, 64, 16, 64, PANEL_X + paddingX * 4f, PANEL_Y + 35 + 128, 2.0f, 16, -lTurretAmt, 1f, 1f, mHUDTexture);
		mSpriteBatch.draw(0, 64, 16, 64, PANEL_X + paddingX * 4f, PANEL_Y + 35, 2.1f, 16, 128, 1f, 1f, mHUDTexture);

		mSpriteBatch.end();
	}

	private void drawControls(RenderState pRenderState) {
		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();
		mHUDFont.begin(pRenderState.hudCamera());

		mSpriteBatch.begin(pRenderState.hudCamera());

		// Cupola toggle
		if (mPlayer.tank().drawCupola()) {
			mSpriteBatch.draw(64, 32, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 215f, 2f, 32, 32, 1f, mHUDTexture);

		} else {
			mSpriteBatch.draw(64, 64, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 215f, 2f, 32, 32, 1f, mHUDTexture);
		}
		mHUDFont.draw("T", lHUDrect.left() + 15f + 10f, lHUDrect.bottom() - 215f + 7f, 2.2f, 1f, 1f, 1f, 1f, 1.2f, -1);

		// Turret Lock toggle
		if (mPlayer.tank().mLockTurret) {
			mSpriteBatch.draw(64, 32, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 255f, 2f, 32, 32, 1f, mHUDTexture);

		} else {
			mSpriteBatch.draw(64, 64, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 255f, 2f, 32, 32, 1f, mHUDTexture);
		}
		mHUDFont.draw("L", lHUDrect.left() + 15f + 10f, lHUDrect.bottom() - 255f + 7f, 2.2f, 1f, 1f, 1f, 1f, 1.2f, -1);

		// Open Fire toggle
		if (mPlayer.tank().mFireAtWill) {
			mSpriteBatch.draw(64, 32, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 295f, 2f, 32, 32, 1f, mHUDTexture);

		} else {
			mSpriteBatch.draw(64, 64, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 295f, 2f, 32, 32, 1f, mHUDTexture);

		}
		mHUDFont.draw("F", lHUDrect.left() + 15f + 10f, lHUDrect.bottom() - 295f + 7f, 2.2f, 1f, 1f, 1f, 1f, 1.2f, -1);

		if (mPlayer.chaseCamActive()) {
			mSpriteBatch.draw(64, 32, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 335f, 2f, 32, 32, 1f, mHUDTexture);

		} else {
			mSpriteBatch.draw(64, 64, 32, 32, lHUDrect.left() + 15f, lHUDrect.bottom() - 335f, 2f, 32, 32, 1f, mHUDTexture);
		}

		mHUDFont.draw("C", lHUDrect.left() + 15f + 10f, lHUDrect.bottom() - 335f + 7f, 2.2f, 1f, 1f, 1f, 1f, 1.2f, -1);

		mSpriteBatch.end();
		mHUDFont.end();
	}

}
