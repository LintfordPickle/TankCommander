package net.ld.oneroom.views;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.oneroom.world.GameWorld;

public class WorldView {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TextureBatch mSpriteBatch;
	private GameWorld mGameWorld;

	private Texture mWorldTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public WorldView() {
		mSpriteBatch = new TextureBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameWorld pGameWorld) {
		mGameWorld = pGameWorld;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mWorldTexture = TextureManager.textureManager().loadTextureFromFile("WorldTexture", "res/textures/world.png", GL11.GL_NEAREST);

		if (mWorldTexture == null) {
			throw new RuntimeException("Unable to load world textures");
		}

		mSpriteBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	public void draw(RenderState pRenderState) {
		if (mGameWorld == null)
			return;

		mSpriteBatch.begin(pRenderState.gameCamera());

		mSpriteBatch.draw(0, 0, 32, 32, 0, 0, 0.1f, 32, 32, 1f, mWorldTexture);
		mSpriteBatch.draw(0, 0, 32, 32, 400, 300, 0.1f, 64, 64, 1f, mWorldTexture);
		

		int lMinX = (int) (pRenderState.gameCamera().getMinX() / mGameWorld.cellSize);
		int lMinY = (int) (pRenderState.gameCamera().getMinY() / mGameWorld.cellSize);

		int lTilesWide = (int) (pRenderState.gameCamera().getWidth() / mGameWorld.cellSize);
		int lTilesHigh = (int) (pRenderState.gameCamera().getHeight() / mGameWorld.cellSize);

		// Boundaries
		if (lMinX < 0)
			lMinX = 0;
		if (lMinY < 0)
			lMinY = 0;

		if (lMinX + lTilesWide > mGameWorld.cellsWide)
			lTilesWide = mGameWorld.cellsWide - lMinX;
		if (lMinY + lTilesHigh > mGameWorld.cellsHigh)
			lTilesHigh = mGameWorld.cellsHigh - lMinY;

		// 1 cell boundary around edges of screen (if allowed)
		if (lMinX > 0)
			lMinX--;
		if (lMinY > 0)
			lMinY--;

		if (lTilesWide < mGameWorld.cellsWide - 4)
			lTilesWide += 4;
		if (lTilesHigh < mGameWorld.cellsHigh - 4)
			lTilesHigh += 4;

		// Only draw the tiles on the screen
		for (int x = lMinX; x < lMinX + lTilesWide; x++) {
			for (int y = lMinY; y < lMinY + lTilesHigh; y++) {
				boolean off = ((y % 2) == 1) ? ((x % 2) == 1) : ((x % 2) == 0);
				mSpriteBatch.draw(off ? 0 : 32, 0, 32, 32, x * mGameWorld.cellSize, y * mGameWorld.cellSize, 0.1f, mGameWorld.cellSize, mGameWorld.cellSize, 1f, mWorldTexture);
			}

		}

		mSpriteBatch.end();

		// Render spawn points
		mSpriteBatch.begin(pRenderState.gameCamera());
		mSpriteBatch.draw(0, 32, 128, 96, 64, 64, 1.5f, 128, 96, 2f, mWorldTexture);
		mSpriteBatch.end();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
