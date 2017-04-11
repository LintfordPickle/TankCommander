package net.ld.oneroom.particles;

import net.ld.library.core.time.GameTime;
import net.ld.oneroom.particles.ParticleSystem.Particle;

public class RotateModifier extends ParticleModifier {

	float mAmtPerFrame;

	public RotateModifier(float pAmt) {
		mAmtPerFrame = pAmt;
	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {
		pParticle.rotation += mAmtPerFrame * pGameTime.elapseGameTime();

	}

}
