package net.ld.oneroom.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.world.GameWorld;

public class ParticleSystem {

	Random mRandom = new Random();

	public class Particle {
		public float x, y, w, h, vx, vy, scale;
		public float height; // off ground
		public float life;
		public float rotation;
		boolean isFree;

		public Particle() {
			reset();
		}

		public void init(float pX, float pY, float pVX, float pVY, float pW, float pH, float pLife) {
			x = pX;
			y = pY;
			vx = pVX;
			vy = pVY;
			life = pLife;

			height = mRandom.nextFloat() * 100f;

			w = pW;
			h = pH;
			isFree = false;

		}

		public void kill() {
			isFree = true;
		}

		public void reset() {
			x = y = vx = vy = 0;
			h = w = 16;
			life = 0f;
			scale = 1f;

			isFree = true;
		}

		public void update(GameTime pGameTime) {
			if (isFree)
				return;

			float lDelta = (float) pGameTime.elapseGameTime() / 1000f;

			life -= (float) pGameTime.elapseGameTime();
			if (life <= 0) {

				isFree = true;
				particleEndLife(this);
				return;
			}

			x += vx * lDelta;
			y += vy * lDelta;

			return;
		}
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected List<Particle> mParticles;

	protected List<ParticleInitialiser> mInitialisers; // at spawn
	protected List<ParticleModifier> mModifiers; // per frame

	protected TextureBatch mSpriteBatch;
	protected Camera mGameCamera; // coord screen -> world coords
	protected Texture mTexture;
	protected String mTexturePath;

	public float sx, sy = 0;
	public float sw, sh = 16;
	public float dw, dh = 16;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ParticleSystem(String pTexturePath, int pNumParts) {
		mParticles = new ArrayList<>(64);

		mInitialisers = new ArrayList<>();
		mModifiers = new ArrayList<>();

		mTexturePath = pTexturePath;
		mSpriteBatch = new TextureBatch();

		allocateInstances(pNumParts);

		sx = 0;
		sy = 0;
		sw = 16;
		sh = 16;
		dw = 16;
		dh = 16;

	}

	public void setTextureArea(float psx, float psy, float psw, float psh, float pdw, float pdh) {
		sx = psx;
		sy = psy;
		sw = psw;
		sh = psh;
		dw = pdw;
		dh = pdh;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(Camera pGameCamera) {
		mGameCamera = pGameCamera;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

		mTexture = TextureManager.textureManager().loadTextureFromFile(mTexturePath, mTexturePath, GL11.GL_NEAREST);
	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	public void update(GameTime pGameTime) {
		final int lPartCount = mParticles.size();
		for (int i = 0; i < lPartCount; i++) {
			Particle lPart = mParticles.get(i);
			if (lPart.isFree)
				continue;

			final int lModNum = mModifiers.size();
			for (int j = 0; j < lModNum; j++) {
				mModifiers.get(j).update(lPart, pGameTime);

			}

			lPart.update(pGameTime);

		}
	}

	public void draw(RenderState pRenderState) {

		mSpriteBatch.begin(pRenderState.gameCamera());

		final int lPartCount = mParticles.size();
		for (int i = 0; i < lPartCount; i++) {
			Particle lPart = mParticles.get(i);
			if (lPart.isFree)
				continue;

			mSpriteBatch.draw(sx, sy, sw, sh, lPart.x, lPart.y, 1.6f, dw, dh, 1f, 1f, 1f, 1f, lPart.rotation, dw / 2, dh / 2, lPart.scale, lPart.scale, mTexture);

		}

		mSpriteBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private Particle allocateInstances(int pAmt) {
		if (pAmt <= 0)
			pAmt = 4;

		Particle lReturn = new Particle();

		for (int i = 0; i < pAmt; i++) {
			Particle lnewInstance = new Particle();
			mParticles.add(lnewInstance);
		}

		// Return this without adding it to the pool
		return lReturn;
	}

	public Particle getFreeInstance() {
		final int lPoolCount = mParticles.size();
		for (int i = 0; i < lPoolCount; i++) {
			if (mParticles.get(i).isFree) {
				return mParticles.get(i);
			}

		}

		return null;
	}

	public void addParticle(float pX, float pY, float pVX, float pVY, float pLife) {
		// Get the next free particle in the system
		Particle lPart = getFreeInstance();

		if (lPart == null)
			return; // do nothing

		// init
		lPart.init(pX, pY, pVX, pVY, 16f, 16f, pLife);

		// Apply initialisers
		final int lInitisersCount = mInitialisers.size();
		for (int i = 0; i < lInitisersCount; i++) {
			mInitialisers.get(i).initialise(lPart);

		}

		// track
		if (!mParticles.contains(lPart)) {
			mParticles.add(lPart);
		}

	}

	public void addInitialise(ParticleInitialiser pInitialiser) {
		if (!mInitialisers.contains(pInitialiser)) {
			mInitialisers.add(pInitialiser);
		}
	}

	public void addModifier(ParticleModifier pModifier) {
		if (!mModifiers.contains(pModifier)) {
			mModifiers.add(pModifier);
		}
	}

	protected void checkCollision(GameWorld pGameWorld, Particle pPart) {

	}

	protected void particleEndLife(Particle pPart) {

	}

}
