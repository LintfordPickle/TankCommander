package net.ld.oneroom.particles;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.time.GameTime;
import net.ld.oneroom.world.GameWorld;

public class BulletSystem extends ParticleSystem {

	private GameWorld mGameWorld;

	public BulletSystem(String pTexturePath, int pNumParts) {
		super(pTexturePath, pNumParts);

	}

	public void initialise(Camera pGameCamera, GameWorld pGameWorld) {
		super.initialise(pGameCamera);

		mGameWorld = pGameWorld;

	}

	@Override
	public void update(GameTime pGameTime) {
		super.update(pGameTime);

		// Check for collisions (only with player ??)
		final int lPartCount = mParticles.size();
		for (int i = 0; i < lPartCount; i++) {
			Particle lPart = mParticles.get(i);
			if (lPart.isFree)
				continue;

			checkCollision(mGameWorld, lPart);

		}

	}

}
