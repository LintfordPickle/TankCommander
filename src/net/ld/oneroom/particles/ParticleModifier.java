package net.ld.oneroom.particles;

import net.ld.library.core.time.GameTime;
import net.ld.oneroom.particles.ParticleSystem.Particle;

public abstract class ParticleModifier {

	public abstract void update(Particle pParticle, GameTime pGameTime);
	
}
