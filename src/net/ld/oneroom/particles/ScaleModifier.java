package net.ld.oneroom.particles;

import net.ld.library.core.time.GameTime;
import net.ld.oneroom.particles.ParticleSystem.Particle;

public class ScaleModifier extends ParticleModifier {

	float mAmtPerFrame;

	public ScaleModifier(float pAmt) {
		mAmtPerFrame = pAmt;
	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {
		pParticle.scale *= mAmtPerFrame;

	}

}
