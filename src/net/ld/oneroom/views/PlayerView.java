package net.ld.oneroom.views;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.circlebatch.CircleBatch;
import net.ld.library.core.graphics.sprites.AnimatedSprite;
import net.ld.library.core.graphics.sprites.Sprite;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.world.GameWorld;
import net.ld.oneroom.world.Player;
import net.ld.oneroom.world.TankCrew;

public class PlayerView {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final float TANK_Z_DEPTH = 0.2f;

	public static final float TANK_CREW_INSIDE_Z_DEPTH = 1f;

	public static final float TANK_CUPOLA_Z_DEPTH = 0.8f;

	public static final float TANK_CREW_OUTSIDE_Z_DEPTH = 0.9f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Player mPlayerEntity;

	private TextureBatch mSpriteBatch;
	private CircleBatch mCircleBatch;
	private Texture mEntityTexture;

	private AnimatedSprite mEngineAnimation;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlayerView() {
		mSpriteBatch = new TextureBatch();
		mCircleBatch = new CircleBatch();

		mEngineAnimation = new AnimatedSprite();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameWorld pGameWorld, Player pPlayer) {
		mPlayerEntity = pPlayer;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);
		mCircleBatch.loadGLContent(pResourceManager);

		mEntityTexture = TextureManager.textureManager().loadTextureFromFile("EntityTexture",
				"res/textures/entities.png", GL11.GL_NEAREST);
		mEngineAnimation.addFrame(new Sprite(0, 160, 32, 64));
		mEngineAnimation.addFrame(new Sprite(32, 160, 32, 64));
		mEngineAnimation.addFrame(new Sprite(64, 160, 32, 64));

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		mCircleBatch.unloadGLContent();

	}

	public void update(GameTime pGameTime) {
		// update when vehicle is moving
		// Animation speed increases with vehicle speed
		mEngineAnimation.update(pGameTime, 1f);
	}

	public void draw(RenderState pRenderState) {
		if (mPlayerEntity == null)
			return;

		final float lPlayerPositionX = mPlayerEntity.tank().x;
		final float lPlayerPositionY = mPlayerEntity.tank().y;

		drawTankLower(pRenderState);

		// if (!mPlayerEntity.tank().drawCupola()) {
		drawCrew(pRenderState);

		drawTankUpper(pRenderState);

		// }

		final boolean DRAW_COLLS = false;
		if (DRAW_COLLS) {
			mCircleBatch.begin(pRenderState.gameCamera());

			mCircleBatch.draw(lPlayerPositionX, lPlayerPositionY, mPlayerEntity.tank().radius, 1f);

			// engine
			mCircleBatch.draw(mPlayerEntity.tank().mEngine.x, mPlayerEntity.tank().mEngine.y,
					mPlayerEntity.tank().mEngine.r, 1f);

			// turret
			mCircleBatch.draw(mPlayerEntity.tank().mTurret.x, mPlayerEntity.tank().mTurret.y,
					mPlayerEntity.tank().mTurret.r, 1f);

			// main
			mCircleBatch.draw(lPlayerPositionX, lPlayerPositionY, mPlayerEntity.tank().hitRadius, 1f);

			mCircleBatch.end();
		}

	}

	private void drawCrew(RenderState pRenderState) {

		List<TankCrew> lMotleyCrew = mPlayerEntity.crew();

		mSpriteBatch.begin(pRenderState.gameCamera());

		final int lCrewCount = lMotleyCrew.size();
		for (int i = 0; i < lCrewCount; i++) {
			TankCrew lMember = lMotleyCrew.get(i);

			// Draw the crew member at the station he is assigned too
			float offX = lMember.x;
			float offY = lMember.y;

			float lRot = mPlayerEntity.tank().heading();
			float cos_t = (float) Math.cos(lRot);
			float sin_t = (float) Math.sin(lRot);

			float lstationX = mPlayerEntity.tank().x + offX * cos_t - offY * sin_t;
			float lstationY = mPlayerEntity.tank().y + offX * sin_t + offY * cos_t;

			final float SCALE = 1.5f;

			{

				// Depending on stance ..
				switch (lMember.mSTANCE) {

				case prone:
					if (mPlayerEntity.tank().drawCupola()) {
						if (lMember.health <= 0)
							mSpriteBatch.draw(128 + 32, 128, 32, 32, lstationX, lstationY, TANK_CREW_OUTSIDE_Z_DEPTH,
									32f, 32f, 1f, 1f, 1f, 1f, lMember.rot, 16f, 32f, SCALE, SCALE, mEntityTexture);
						else
							mSpriteBatch.draw(128, 224, 32, 64, lstationX, lstationY, TANK_CREW_OUTSIDE_Z_DEPTH, 32f,
									64f, 1f, 1f, 1f, 1f, lMember.rot, 16f, 32f, SCALE, SCALE, mEntityTexture);
					}
					break;

				case standing:
				default:
					if (!mPlayerEntity.tank().drawCupola()) {
						if (lMember.health <= 0)
							mSpriteBatch.draw(128 + 32, 128, 32, 32, lstationX, lstationY, TANK_CREW_INSIDE_Z_DEPTH,
									32f, 32f, 1f, 1f, 1f, 1f, lRot, 16f, 16f, SCALE, SCALE, mEntityTexture);
						else
							mSpriteBatch.draw(128, 128, 32, 32, lstationX, lstationY, TANK_CREW_INSIDE_Z_DEPTH, 32f,
									32f, 1f, 1f, 1f, 1f, lRot, 16f, 16f, SCALE, SCALE, mEntityTexture);
					}
					break;
				}

			}

		}

		mSpriteBatch.end();

	}

	private void drawTankLower(RenderState pRenderState) {
		final float lGoingX = mPlayerEntity.tank().goingVector().x;
		final float lGoingY = mPlayerEntity.tank().goingVector().y;

		final float lShootingX = mPlayerEntity.tank().shootingVector().x;
		final float lShootingY = mPlayerEntity.tank().shootingVector().y;

		mSpriteBatch.begin(pRenderState.gameCamera());

		final float TILE_SIZE = 64;
		// Render the vectors
		mSpriteBatch.draw(32, 0, 32, 32, lGoingX - TILE_SIZE / 2, lGoingY - TILE_SIZE / 2, 0.2f, TILE_SIZE, TILE_SIZE,
				1f, mEntityTexture);
		mSpriteBatch.draw(0, 0, 32, 32, lShootingX - TILE_SIZE / 2, lShootingY - TILE_SIZE / 2, 0.2f, TILE_SIZE,
				TILE_SIZE, 1f, mEntityTexture);

		final float lPlayerPositionX = mPlayerEntity.tank().x + mPlayerEntity.tank().hullXOff;
		final float lPlayerPositionY = mPlayerEntity.tank().y + mPlayerEntity.tank().hullYOff;

		// Render the player
		final float TANK_SIZE_X = 128;
		final float TANK_SIZE_Y = 92;

		// Offset of the turret from the main body
		final float TURRET_MAIN_OFFSET_X = 31 + mPlayerEntity.tank().turretXOff;
		final float TURRET_MAIN_OFFSET_Y = 31 + mPlayerEntity.tank().turretYOff;

		final float TURRET_SIZE_X = 160;
		final float TURRET_SIZE_Y = 64;

		// Origin rotation center
		final float TURRET_ROT_CENTER_X = 31;
		final float TURRET_ROT_CENTER_Y = 31;

		final float TURRET_FINAL_POS_X = lPlayerPositionX + TURRET_MAIN_OFFSET_X - TURRET_ROT_CENTER_X;
		final float TURRET_FINAL_POS_Y = lPlayerPositionY + TURRET_MAIN_OFFSET_Y - TURRET_ROT_CENTER_Y;

		final float lSCALE = 2f;

		// ---> Tracks

		// normal turret 256, 32
		float lTurretYOffset = 32;
		if (!mPlayerEntity.tank().mTurret.isOperational()) {
			if (mPlayerEntity.tank().mTurret.health < 5) {
				// destroyed turret 256, 160
				lTurretYOffset = 160;
			} else {
				// Damage turret 256, 96
				lTurretYOffset = 96;
			}
		}

		// Turret Ground Shadow
		mSpriteBatch.draw(256, lTurretYOffset, TURRET_SIZE_X, TURRET_SIZE_Y, // source
				TURRET_FINAL_POS_X - 5, TURRET_FINAL_POS_Y + 10, TANK_Z_DEPTH, // pos
				TURRET_SIZE_X, TURRET_SIZE_Y, // size
				0f, 0f, 0f, .65f, // color
				mPlayerEntity.tank().shootingRot(), TURRET_ROT_CENTER_X, TURRET_ROT_CENTER_Y, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		// Draw the hull
		mSpriteBatch.draw(0, 32, TANK_SIZE_X, TANK_SIZE_Y, // source
				lPlayerPositionX + 5, lPlayerPositionY + 5, TANK_Z_DEPTH, // pos
				TANK_SIZE_X, TANK_SIZE_Y, // size
				0f, 0f, 0f, .65f, // color
				mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f, TANK_SIZE_Y / 2f, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		// Innards
		if (!mPlayerEntity.tank().drawCupola()) {
			mSpriteBatch.draw(128, 32, 128, 96, // source
					lPlayerPositionX, lPlayerPositionY, TANK_Z_DEPTH, // pos
					TANK_SIZE_X, TANK_SIZE_Y, // size
					1f, 1f, 1f, 1f, // color
					mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f, TANK_SIZE_Y / 2f, // rot
					lSCALE, lSCALE, // scale
					mEntityTexture);

		}

		// Engine
		if (!mPlayerEntity.tank().drawCupola()) {
			// TODO: Movement speed determines animation speed
			if (mPlayerEntity.tank().mEngine
					.isOperational() /* && Movement speed */) {
				mSpriteBatch.draw(mEngineAnimation.getSprite().getX(), mEngineAnimation.getSprite().getY(),
						mEngineAnimation.getSprite().getWidth(), mEngineAnimation.getSprite().getHeight(), // source
						lPlayerPositionX, lPlayerPositionY, TANK_Z_DEPTH, // pos
						32, 64, // size
						1f, 1f, 1f, 1f, // color
						mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f - 7, TANK_SIZE_Y / 2f - 7, // rot
						lSCALE, lSCALE, // scale
						mEntityTexture);

			} else {
				if (mPlayerEntity.tank().mEngine.health < 5) {
					// Draw completely broken engine
					mSpriteBatch.draw(128, 160, 32, 64, // source
							lPlayerPositionX, lPlayerPositionY, TANK_Z_DEPTH, // pos
							32, 64, // size
							1f, 1f, 1f, 1f, // color
							mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f - 7, TANK_SIZE_Y / 2f - 7, // rot
							lSCALE, lSCALE, // scale
							mEntityTexture);
				} else {
					// Draw light damage
					mSpriteBatch.draw(96, 160, 32, 64, // source
							lPlayerPositionX, lPlayerPositionY, 0.2f, // pos
							32, 64, // size
							1f, 1f, 1f, 1f, // color
							mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f - 7, TANK_SIZE_Y / 2f - 7, // rot
							lSCALE, lSCALE, // scale
							mEntityTexture);

				}

			}

		}

		float lAlpha = 1.0f;
		if (!mPlayerEntity.tank().drawCupola()) {
			lAlpha = 0.1f;
		}

		// Draw the body
		mSpriteBatch.draw(0, 32, TANK_SIZE_X, TANK_SIZE_Y, // source
				lPlayerPositionX, lPlayerPositionY, TANK_CUPOLA_Z_DEPTH, // pos
				TANK_SIZE_X, TANK_SIZE_Y, // size
				1f, 1f, 1f, lAlpha, // color
				mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f, TANK_SIZE_Y / 2f, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		// Turret Self Shadow
		mSpriteBatch.draw(256, lTurretYOffset, TURRET_SIZE_X, TURRET_SIZE_Y, // source
				TURRET_FINAL_POS_X - 2, TURRET_FINAL_POS_Y + 4, TANK_CUPOLA_Z_DEPTH, // pos
				TURRET_SIZE_X, TURRET_SIZE_Y, // size
				0f, 0f, 0f, .62f * lAlpha, // color
				mPlayerEntity.tank().shootingRot(), TURRET_ROT_CENTER_X, TURRET_ROT_CENTER_Y, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		// Draw the cupola and turret
		mSpriteBatch.draw(256, lTurretYOffset, TURRET_SIZE_X, TURRET_SIZE_Y, // source
				TURRET_FINAL_POS_X, TURRET_FINAL_POS_Y, TANK_CUPOLA_Z_DEPTH, // pos
				TURRET_SIZE_X, TURRET_SIZE_Y, // size
				1f, 1f, 1f, lAlpha, // color
				mPlayerEntity.tank().shootingRot(), TURRET_ROT_CENTER_X, TURRET_ROT_CENTER_Y, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		mSpriteBatch.end();
	}

	private void drawTankUpper(RenderState pRenderState) {
		final float lGoingX = mPlayerEntity.tank().goingVector().x;
		final float lGoingY = mPlayerEntity.tank().goingVector().y;

		final float lShootingX = mPlayerEntity.tank().shootingVector().x;
		final float lShootingY = mPlayerEntity.tank().shootingVector().y;

		mSpriteBatch.begin(pRenderState.gameCamera());

		final float TILE_SIZE = 64;
		// Render the vectors
		mSpriteBatch.draw(32, 0, 32, 32, lGoingX - TILE_SIZE / 2, lGoingY - TILE_SIZE / 2, 0.2f, TILE_SIZE, TILE_SIZE,
				1f, mEntityTexture);
		mSpriteBatch.draw(0, 0, 32, 32, lShootingX - TILE_SIZE / 2, lShootingY - TILE_SIZE / 2, 0.2f, TILE_SIZE,
				TILE_SIZE, 1f, mEntityTexture);

		final float lPlayerPositionX = mPlayerEntity.tank().x;
		final float lPlayerPositionY = mPlayerEntity.tank().y;

		// Render the player
		final float TANK_SIZE_X = 128;
		final float TANK_SIZE_Y = 92;
		// Offset of the turret from the main body
		final float TURRET_MAIN_OFFSET_X = 31 + mPlayerEntity.tank().turretXOff;
		final float TURRET_MAIN_OFFSET_Y = 31 + mPlayerEntity.tank().turretYOff;

		final float TURRET_SIZE_X = 160;
		final float TURRET_SIZE_Y = 64;

		// Origin rotation center
		final float TURRET_ROT_CENTER_X = 31;
		final float TURRET_ROT_CENTER_Y = 31;

		final float TURRET_FINAL_POS_X = lPlayerPositionX + TURRET_MAIN_OFFSET_X - TURRET_ROT_CENTER_X;
		final float TURRET_FINAL_POS_Y = lPlayerPositionY + TURRET_MAIN_OFFSET_Y - TURRET_ROT_CENTER_Y;

		final float lSCALE = 2f;

		// ---> Tracks

		// normal turret 256, 32
		float lTurretYOffset = 32;
		if (!mPlayerEntity.tank().mTurret.isOperational()) {
			if (mPlayerEntity.tank().mTurret.health < 5) {
				// destroyed turret 256, 160
				lTurretYOffset = 160;
			} else {
				// Damage turret 256, 96
				lTurretYOffset = 96;
			}
		}

		float lAlpha = 1.0f;
		if (!mPlayerEntity.tank().drawCupola()) {
			lAlpha = 0.1f;
		}

		// Draw the body
		mSpriteBatch.draw(0, 32, TANK_SIZE_X, TANK_SIZE_Y, // source
				lPlayerPositionX, lPlayerPositionY, TANK_CUPOLA_Z_DEPTH, // pos
				TANK_SIZE_X, TANK_SIZE_Y, // size
				1f, 1f, 1f, lAlpha, // color
				mPlayerEntity.tank().heading(), TANK_SIZE_X / 2f, TANK_SIZE_Y / 2f, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		// Turret Self Shadow
		mSpriteBatch.draw(256, lTurretYOffset, TURRET_SIZE_X, TURRET_SIZE_Y, // source
				TURRET_FINAL_POS_X - 2, TURRET_FINAL_POS_Y + 4, TANK_CUPOLA_Z_DEPTH, // pos
				TURRET_SIZE_X, TURRET_SIZE_Y, // size
				0f, 0f, 0f, .62f * lAlpha, // color
				mPlayerEntity.tank().shootingRot(), TURRET_ROT_CENTER_X, TURRET_ROT_CENTER_Y, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		// Draw the cupola and turret
		mSpriteBatch.draw(256, lTurretYOffset, TURRET_SIZE_X, TURRET_SIZE_Y, // source
				TURRET_FINAL_POS_X, TURRET_FINAL_POS_Y, TANK_CUPOLA_Z_DEPTH, // pos
				TURRET_SIZE_X, TURRET_SIZE_Y, // size
				1f, 1f, 1f, lAlpha, // color
				mPlayerEntity.tank().shootingRot(), TURRET_ROT_CENTER_X, TURRET_ROT_CENTER_Y, // rot
				lSCALE, lSCALE, // scale
				mEntityTexture);

		mSpriteBatch.end();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}