package net.ld.oneroom.particles;

import net.ld.oneroom.particles.ParticleSystem.Particle;

/** rotates the particles towards the target (on spawn) */
public class ScaleInitialiser extends ParticleInitialiser {

	float mAmt;

	public ScaleInitialiser(float pAmt) {
		mAmt = pAmt;

	}

	@Override
	public void initialise(Particle pParticle) {
		pParticle.scale = mAmt;

	}

}
