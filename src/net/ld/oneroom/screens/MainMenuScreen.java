package net.ld.oneroom.screens;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.screenmanager.LoadingScreen;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.ScreenManager;
import net.ld.library.screenmanager.entries.MenuEntry;

public class MainMenuScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int START_BUTTON_ID = 1;
	private static final int CREDITS_BUTTON_ID = 2;
	private static final int EXIT_BUTTON_ID = 3;

	private FontUnit mCreditsFont;

	private TextureBatch mTextureBatch;
	private Texture mTitleLogoTexture;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MainMenuScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "");

		mESCBackEnabled = false;

		MenuEntry lStartButton = new MenuEntry(mScreenManager, this, "Play");
		MenuEntry lCreditsButton = new MenuEntry(mScreenManager, this, "Credits");
		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Exit");

		lStartButton.registerClickListener(this, START_BUTTON_ID);
		lCreditsButton.registerClickListener(this, CREDITS_BUTTON_ID);
		lExitButton.registerClickListener(this, EXIT_BUTTON_ID);

		menuEntries().add(lStartButton);
		menuEntries().add(lCreditsButton);
		menuEntries().add(lExitButton);

		mTextureBatch = new TextureBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mCreditsFont = pResourceManager.fontManager().loadFontFromFile("default", "res/fonts/Germania.otf", 28);

		mTitleLogoTexture = TextureManager.textureManager().loadTextureFromFile("TitleLogo", "res/textures/titleLogo.png");
		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mCreditsFont.unloadGLContent();
		mTextureBatch.unloadGLContent();

	}

	@Override
	public void draw(RenderState pRenderState) {
		super.draw(pRenderState);

		mEntryOffsetFromTop = 200f;

		mCreditsFont.begin(pRenderState.hudCamera());

		Rectangle HUDRect = pRenderState.hudCamera().boundingHUDRectange();

		mTextureBatch.begin(pRenderState.hudCamera());
		mTextureBatch.draw(0, 0, 800, 128, -400, HUDRect.top() + 20, 0.2f, 800, 128, 1f, mTitleLogoTexture);
		mTextureBatch.end();

		final String mLine1 = "Created by John Hampson (2017)";

		final float lStringWidth1 = mCreditsFont.bitmap().getStringWidth(mLine1);
		mCreditsFont.draw(mLine1, -lStringWidth1 / 2, HUDRect.bottom() - 40, 0, 0f, 0f, 0f, 1f, 1f, -1);
		mCreditsFont.draw(mLine1, -lStringWidth1 / 2, HUDRect.bottom() - 45, 3f, 1f, 1f, 1f, 1f, 1f, -1);

		mCreditsFont.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {

		switch (mClickAction.consume()) {
		case START_BUTTON_ID:
			LoadingScreen.load(mScreenManager, false, new GameScreen(mScreenManager));

			break;

		case CREDITS_BUTTON_ID:
			mScreenManager.addScreen(new CreditsScreen(mScreenManager));
			break;

		case EXIT_BUTTON_ID:
			mScreenManager.exitGame();

			break;

		default:
			break;
		}

	}

}
