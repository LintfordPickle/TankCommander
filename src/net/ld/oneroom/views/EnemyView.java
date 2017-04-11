package net.ld.oneroom.views;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.circlebatch.CircleBatch;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.oneroom.world.EnemyManager;
import net.ld.oneroom.world.EnemyManager.EnemyEntity;
import net.ld.oneroom.world.GameWorld;

public class EnemyView {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EnemyManager mEnemyManager;

	private TextureBatch mSpriteBatch;
	private CircleBatch mCircleBatch;
	private Texture mEntityTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EnemyView() {
		mSpriteBatch = new TextureBatch();
		mCircleBatch = new CircleBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(GameWorld pGameWorld, EnemyManager pEnemyManager) {
		mEnemyManager = pEnemyManager;

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

		if(DRAW_COLLS)
			mCircleBatch.begin(pRenderState.gameCamera());
		
		// loop through enemies and render them
		final int lEnemyCount = mEnemyManager.enemies().size();
		for (int i = 0; i < lEnemyCount; i++) {
			EnemyEntity lEnemy = mEnemyManager.enemies().get(i);
			// Drop shadow
			
			final float SCALE = 1.5f;
			
			// Enemy sprite
			switch(lEnemy.state()){
			case attacking:
				mSpriteBatch.draw(96, 0, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 64, 64, 1f, 1f, 1f, 1f, 0, 32, 32, 1f, 1f, mEntityTexture);
				mSpriteBatch.draw(64, 256, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 32, 32, 1f, 1f, 1f, 1f, lEnemy.rotation, 16f, 16f, SCALE, SCALE, mEntityTexture);
				break;
				
			case idle:
				mSpriteBatch.draw(96, 0, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 64, 64, 1f, 1f, 1f, 1f, 0, 32, 32, 1f, 1f, mEntityTexture);
				mSpriteBatch.draw(64, 224, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 32, 32, 1f, 1f, 1f, 1f, lEnemy.rotation, 16f, 16f, SCALE, SCALE, mEntityTexture);
				break;
				
			case dead_Shot:
			case dead_squashed:
				mSpriteBatch.draw(96, 0, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 64, 64, 1f, 1f, 1f, 1f, 0, 32, 44, 1f, 1f, mEntityTexture);
				mSpriteBatch.draw(64, 288, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 32, 32, 1f, 1f, 1f, 1f, (float)Math.toRadians(45f), 16f, 16f, SCALE, SCALE, mEntityTexture);
				break;
			
			default:
				mSpriteBatch.draw(96, 0, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 64, 64, 1f, 1f, 1f, 1f, 0, 32, 32, 1f, 1f, mEntityTexture);
				mSpriteBatch.draw(64, 224, 32, 32, lEnemy.x, lEnemy.y, 0.15f, 32, 32, 1f, 1f, 1f, 1f, lEnemy.rotation, 16f, 16f, SCALE, SCALE, mEntityTexture);
				break;
			
			}
			// IDLE : 
			
			
			if(DRAW_COLLS)
				mCircleBatch.draw(lEnemy.x, lEnemy.y, lEnemy.radius, 3f);

		}

		mSpriteBatch.end();
		
		if(DRAW_COLLS)
			mCircleBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}