package net.ld.oneroom.screens;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
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

		mSpriteBatch.begin(pRenderState.hudCamera());
		mSpriteBatch.draw(0, 0, 96, 96, 0, 0, 1f, 96, 96, 1, TextureManager.CORE_TEXTURE);
		mSpriteBatch.end();
		
	}

}
