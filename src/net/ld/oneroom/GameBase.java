package net.ld.oneroom;

import org.lwjgl.opengl.GL11;

import net.ld.library.GameInfo;
import net.ld.library.core.LWJGLCore;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.ScreenManager;
import net.ld.oneroom.screens.BackgroundScreen;
import net.ld.oneroom.screens.MainMenuScreen;

public class GameBase extends LWJGLCore {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ScreenManager mScreenManager;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameBase(GameInfo pGameInfo) {
		super(pGameInfo);

		// Auto load changed textures
		mResourceManager.watchTextureDirectory("res/textures");

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void onInitialiseApp() {
		mScreenManager = new ScreenManager(mDisplayConfig);
		mScreenManager.initialise(mInputState, mGameTime, mHUDCamera, mGameCamera);

		// Start the initial screens to the screen manager
		mScreenManager.addScreen(new BackgroundScreen(mScreenManager));
		mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

	}

	@Override
	public void onInitialiseGL() {
		// Enable depth testing
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		GL11.glClearColor(10.0f / 255.0f, 13.0f / 255.0f, 9.0f / 255.0f, 1.0f);
		
		// Set the clear colour to corn flower blue
		GL11.glClearColor(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f, 1.0f);

	}

	@Override
	protected void onLoadGLContent() {
		super.onLoadGLContent();

		// Load the GL Content
		mScreenManager.loadGLContent(mResourceManager);

	}

	@Override
	protected void onUnloadGLContent() {
		super.onUnloadGLContent();

		mScreenManager.unloadGLContent();

	}

	@Override
	protected void onUpdate(GameTime pGameTime) {
		super.onUpdate(pGameTime);

		mScreenManager.update(pGameTime);

	}

	@Override
	protected void onDraw(final RenderState pRenderState) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		mScreenManager.draw();

	}

	// ---------------------------------------------
	// Entry-Point
	// ---------------------------------------------

	public static void main(String args[]) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String windowTitle() {
				return "Panzerkampf";
			}

			@Override
			public String applicationName() {
				return "Panzerkampf";
			}

			@Override
			public int windowWidth() {
				return 800;
			}

			@Override
			public int windowHeight() {
				return 600;
			}

		};

		// Start a new instance of the game
		GameBase lGameBase = new GameBase(lGameInfo);
		if(lGameBase.createWindow()){
			lGameBase.onRunGameLoop();
			
		}

	}

}
