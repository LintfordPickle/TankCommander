package net.ld.oneroom.hud;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.graphics.linebatch.LineBatch;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;

public class HelpOverlay {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TextureBatch mSpriteBatch;
	private LineBatch mLineBatch;

	protected FontUnit mMenuFont;
	private Texture mHUDTexture;

	private boolean mDisplayOverlay;
	private boolean mDisplayToolTip = true;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public HelpOverlay() {
		mSpriteBatch = new TextureBatch();
		mLineBatch = new LineBatch();

		mDisplayOverlay = false;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);
		mLineBatch.loadGLContent(pResourceManager);

		mHUDTexture = TextureManager.textureManager().loadTextureFromFile("HUDTexture", "res/textures/hud.png", GL11.GL_NEAREST);

		mMenuFont = pResourceManager.fontManager().loadFontFromResource("HelpFont", "/res/fonts/pixel.ttf", 22);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		mLineBatch.unloadGLContent();
		mMenuFont.unloadGLContent();

	}

	public boolean handleInput(InputState pInputState) {
		// Toggle the help display on and off
		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_F1)) {
			mDisplayOverlay = !mDisplayOverlay;

		}

		return false;

	}

	public void update(GameTime pGameTime) {

	}

	public void draw(RenderState pRenderState) {
		
		Rectangle lHUDrect = pRenderState.hudCamera().boundingRectangle();
		
		if(mDisplayOverlay){
			mSpriteBatch.begin(pRenderState.hudCamera());
			mSpriteBatch.draw(0, 0, 32, 32, lHUDrect.left(), lHUDrect.top(), 2f, lHUDrect.width - 200, lHUDrect.height, 1f, 0.6f, mHUDTexture);
			mSpriteBatch.end();
		}
		
		if(mDisplayToolTip){
			
			mSpriteBatch.begin(pRenderState.hudCamera());
			mSpriteBatch.draw(64, 0, 32, 32, lHUDrect.left() + 15, lHUDrect.top() + 8, 2f, 32, 32, 1f, mHUDTexture);
			mSpriteBatch.end();
			
			mMenuFont.begin(pRenderState.hudCamera());
			mMenuFont.draw("F1", lHUDrect.left() + 15 + 10, lHUDrect.top() + 8 + 10, 2.1f, 1f);
			mMenuFont.draw("Toggle this help overlay on/off", lHUDrect.left() + 64, lHUDrect.top() + 8 + 10, 2.1f, 1f);
			mMenuFont.end();
		}
		
		
		if (!mDisplayOverlay)
			return;


		mMenuFont.begin(pRenderState.hudCamera());
		mSpriteBatch.begin(pRenderState.hudCamera());
		mLineBatch.begin(pRenderState.hudCamera());

		float lControlsX = lHUDrect.left() + 64;
		float lControlsY = lHUDrect.top() + 70;

		// W
		mSpriteBatch.draw(64, 0, 32, 32, lControlsX, lControlsY, 2f, 32, 32, 1f, mHUDTexture);
		mMenuFont.draw("W", lControlsX + 9, lControlsY + 6, 2.1f, 1f);

		// S
		mSpriteBatch.draw(64, 0, 32, 32, lControlsX, lControlsY + 34, 2f, 32, 32, 1f, mHUDTexture);
		mMenuFont.draw("S", lControlsX + 9, lControlsY + 34 + 6, 2.1f, 1f);

		// A
		mSpriteBatch.draw(64, 0, 32, 32, lControlsX - 34, lControlsY + 34, 2f, 32, 32, 1f, mHUDTexture);
		mMenuFont.draw("A", lControlsX - 34 + 9, lControlsY + 34 + 6, 2.1f, 1f);

		// D
		mSpriteBatch.draw(64, 0, 32, 32, lControlsX + 34, lControlsY + 34, 2f, 32, 32, 1f, mHUDTexture);
		mMenuFont.draw("D", lControlsX + 34 + 9, lControlsY + 34 + 6, 2.1f, 1f);

		mMenuFont.draw("Manual Camera control", lControlsX + 96, lControlsY + 34 + 6, 2.1f, 1f);

		// A bit messy, not easy to refactor ...
		// SCRAP RESOURCE
		mMenuFont.draw("SCRAP: used for repairs", lHUDrect.right() - 200 - 170, lHUDrect.top() + 40, 2.1f, 1f);
		
		mMenuFont.draw("Toggle Chase Camera", lControlsX + 96, lHUDrect.bottom() - 335 + 7, 2.1f, 1f);
		mLineBatch.draw(lControlsX, lHUDrect.bottom() - 335 + 12, lControlsX + 80, lHUDrect.bottom() - 335 + 12, 2.1f);
		
		mMenuFont.draw("Toggle Fire at Will", lControlsX + 96, lHUDrect.bottom() - 295 + 7, 2.1f, 1f);
		mLineBatch.draw(lControlsX, lHUDrect.bottom() - 295 + 12, lControlsX + 80, lHUDrect.bottom() - 295 + 12, 2.1f);

		mMenuFont.draw("Toggle Lock Turret", lControlsX + 96, lHUDrect.bottom() - 255 + 7, 2.1f, 1f);
		mLineBatch.draw(lControlsX, lHUDrect.bottom() - 255 + 12, lControlsX + 80, lHUDrect.bottom() - 255 + 12, 2.1f);
		
		mMenuFont.draw("Toggle Cupola On / Off", lControlsX + 96, lHUDrect.bottom() - 215 + 7, 2.1f, 1f);
		mLineBatch.draw(lControlsX, lHUDrect.bottom() - 215 + 12, lControlsX + 80, lHUDrect.bottom() - 215 + 12, 2.1f);

		mMenuFont.draw(
				"F - Amount of fuel remaining. Collect more on the map. \nR - Number of rockets in store. Collect more on the map.\nE - Engine damage levels (Cannot drive without engines) \nA - Tank armour levels (you will die when this runs out) \nT - Turret Damage levels (you cannot shoot without turrets.",
				lControlsX + 96, lHUDrect.bottom() - 128, 2.1f, 1f);

		mMenuFont.draw("Use the crew roster\nto assign your men. Click\nto 'pick up' and click again\nto place.", lHUDrect.right() - 180 - 200, lHUDrect.top() + 256, 2.1f, 1f);
		mLineBatch.draw(lHUDrect.right() - 180 - 200, lHUDrect.top() + 128 + 64, lHUDrect.right() - 64 - 256, lHUDrect.top() + 128, 2.1f);
		mLineBatch.draw(lHUDrect.right() - 180 - 200, lHUDrect.top() + 128 + 64, lHUDrect.right() - 180 - 200, lHUDrect.top() + 128 + 110, 2.1f);
		mLineBatch.draw(lHUDrect.right() - 180 + 60 - 200, lHUDrect.top() + 128, lHUDrect.right() - 10 - 200, lHUDrect.top() + 128, 2.1f);

		
		mSpriteBatch.draw(96, 0, 64, 96, lControlsX - 16, lHUDrect.top() + 150, 2f, 64, 96, 1f, mHUDTexture);
		mMenuFont.draw("Left Mouse: Drive To", lControlsX + 96, lControlsY + 34 + 70, 2.1f, 1f);
		mMenuFont.draw("Right Mouse: Shoot At", lControlsX + 96, lControlsY + 34 + 91, 2.1f, 1f);
		mMenuFont.draw("Middle Mouse: Camera Zoom", lControlsX + 96, lControlsY + 34 + 112, 2.1f, 1f);

		mLineBatch.end();
		mSpriteBatch.end();
		mMenuFont.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------
	
	public void showHelp(){
		mDisplayOverlay = true;
	}
	
}
