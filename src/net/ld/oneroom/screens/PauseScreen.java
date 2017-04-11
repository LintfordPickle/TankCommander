package net.ld.oneroom.screens;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.LoadingScreen;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.ScreenManager;
import net.ld.library.screenmanager.entries.MenuEntry;

public class PauseScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int RESUME_BUTTON_ID = 1;
	private static final int EXIT_BUTTON_ID = 2;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PauseScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "");

		MenuEntry lResumeButton = new MenuEntry(mScreenManager, this, "Resume");
		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Exit to Menu");

		lResumeButton.registerClickListener(this, RESUME_BUTTON_ID);
		lExitButton.registerClickListener(this, EXIT_BUTTON_ID);

		menuEntries().add(lResumeButton);
		menuEntries().add(lExitButton);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pGameTime, pInputState, pAcceptMouse, pAcceptKeyboard);

	}

	@Override
	public void draw(RenderState pRenderState) {
		super.draw(pRenderState);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case RESUME_BUTTON_ID:
			exitScreen();

			break;

		case EXIT_BUTTON_ID:
			LoadingScreen.load(mScreenManager, false, new BackgroundScreen(mScreenManager),
					new MainMenuScreen(mScreenManager));

			break;

		}

	}

}
