package net.ld.oneroom.screens;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.LoadingScreen;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.ScreenManager;
import net.ld.library.screenmanager.entries.MenuEntry;

public class GameOverScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public enum GAME_OVER_REAONS {
		dead_crew, dead_tank, won;
	}

	private static final int EXIT_BUTTON_ID = 2;

	GAME_OVER_REAONS mReason;
	private TextureBatch mSpriteBatch;
	private String mReasonString;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameOverScreen(ScreenManager pScreenManager, GAME_OVER_REAONS pReason) {
		super(pScreenManager, "Game Over");

		// Cannot exit this baby
		mESCBackEnabled = false;

		mReason = pReason;

		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Exit to Menu");

		lExitButton.registerClickListener(this, EXIT_BUTTON_ID);

		menuEntries().add(lExitButton);

		mSpriteBatch = new TextureBatch();

		switch (pReason) {
		case dead_crew:
			mReasonString = "All of your crew perished, you have failed";
			break;

		case dead_tank:
			mReasonString = "Your tank was destroyed";
			break;

		default:
			mReasonString = "You died!";
			break;
		}

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mSpriteBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mSpriteBatch.unloadGLContent();

	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pGameTime, pInputState, pAcceptMouse, pAcceptKeyboard);

	}

	@Override
	public void draw(RenderState pRenderState) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Rectangle lHUDRect = pRenderState.hudCamera().boundingRectangle();
		Texture lHUDTexture = TextureManager.textureManager().getTexture(ScreenManager.SCREEN_MANAGER_TEXTURE_NAME);

		mSpriteBatch.begin(pRenderState.hudCamera());
		mSpriteBatch.draw(0, 0, 16, 16, lHUDRect.left(), lHUDRect.top(), 1.5f, lHUDRect.width, lHUDRect.height, 1f, 0.6f, 0.6f, 0.6f, 0.6f, lHUDTexture);
		mSpriteBatch.end();

		super.draw(pRenderState);

		mMenuFont.begin(pRenderState.hudCamera());

		final float lStringWidth = mMenuFont.bitmap().getStringWidth(mReasonString);
		mMenuFont.draw(mReasonString, -lStringWidth / 2, 0, 3f, 1f, 1f, 1f, 1f, 1f, -1);

		mMenuFont.end();

		GL11.glEnable(GL11.GL_DEPTH_TEST);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case EXIT_BUTTON_ID:
			LoadingScreen.load(mScreenManager, false, new BackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));

			break;

		}

	}

}
