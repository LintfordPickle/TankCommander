package net.ld.oneroom.screens;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.fonts.FontUnit;
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
	
	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MainMenuScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "Main Menu");

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

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);
		
		mCreditsFont = pResourceManager.fontManager().loadFontFromResource("CreditsFont", "/res/fonts/pixel.ttf", 25);
	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();
		
		mCreditsFont.unloadGLContent();
		
	}

	@Override
	public void draw(RenderState pRenderState) {
		super.draw(pRenderState);
		
		mCreditsFont.begin(pRenderState.hudCamera());

		final String mLine0 = "Tank Commander LD";
		final String mLine1 = "Created by John Hampson (2016) for Ludum Dare #37";
		
		final float lStringWidth0 = mCreditsFont.bitmap().getStringWidth(mLine0);
		final float lStringWidth1 = mCreditsFont.bitmap().getStringWidth(mLine1);
		mCreditsFont.draw(mLine0, -lStringWidth0 / 2, 100, 0, 1f, 1f, 1f, 1f, 1f, -1);
		mCreditsFont.draw(mLine1, -lStringWidth1 / 2, 130, 3f, 1f, 1f, 1f, 1f, 1f, -1);

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
