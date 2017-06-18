package net.ld.oneroom.views;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.circlebatch.CircleBatch;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.oneroom.world.GameWorld;
import net.ld.oneroom.world.GameWorld.Pickup;

public class PickupView {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TextureBatch mSpriteBatch;
	private CircleBatch mCircleBatch;
	private Texture mEntityTexture;
	private GameWorld mGameWorld;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PickupView() {
		mSpriteBatch = new TextureBatch();
		mCircleBatch = new CircleBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameWorld pGameWorld) {
		mGameWorld = pGameWorld;
	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);
		mCircleBatch.loadGLContent(pResourceManager);

		mEntityTexture = TextureManager.textureManager().loadTextureFromFile("EntityTexture", "res/textures/entities.png", GL11.GL_NEAREST);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		mCircleBatch.unloadGLContent();

	}

	public void draw(RenderState pRenderState) {

		final boolean DRAW_COLLS = false;
		mSpriteBatch.begin(pRenderState.gameCamera());

		if (DRAW_COLLS)
			mCircleBatch.begin(pRenderState.gameCamera());

		// loop through enemies and render them
		final int lPickupCount = mGameWorld.pickups().size();
		for (int i = 0; i < lPickupCount; i++) {
			Pickup lPickup = mGameWorld.pickups().get(i);
			// Drop shadow

			mSpriteBatch.draw(
					lPickup.srcX, lPickup.srcY, lPickup.srcW, lPickup.srcH, 
					lPickup.xx, lPickup.yy, 0.2f, lPickup.dstW, lPickup.dstH, 
					1f, 
					mEntityTexture);
			

			if (DRAW_COLLS)
				mCircleBatch.draw(lPickup.xx, lPickup.yy, lPickup.radius, 3f);

		}

		mSpriteBatch.end();

		if (DRAW_COLLS)
			mCircleBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}