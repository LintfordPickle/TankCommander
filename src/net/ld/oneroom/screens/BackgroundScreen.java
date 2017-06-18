package net.ld.oneroom.screens;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.screenmanager.Screen;
import net.ld.library.screenmanager.ScreenManager;

public class BackgroundScreen extends Screen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TextureBatch mSpriteBatch;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BackgroundScreen(ScreenManager pScreenManager) {
		super(pScreenManager);
		//
		mShowInBackground = true;

		mSpriteBatch = new TextureBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	@Override
	public void draw(RenderState pRenderState) {

		final float lWindowWidth = pRenderState.displayConfig().windowWidth();
		final float lWindowHeight = pRenderState.displayConfig().windowHeight();

		Texture lBackgroundTexture = TextureManager.textureManager().loadTextureFromFile("BackgroundTitle", "res/textures/titleScreen.png");

		mSpriteBatch.begin(pRenderState.hudCamera());
		mSpriteBatch.draw(0, 0, 1920, 1080, -lWindowWidth * 0.5f, -lWindowHeight * 0.5f, 0f, lWindowWidth, lWindowHeight, 1, lBackgroundTexture);
		mSpriteBatch.end();

	}

}
