package net.ld.oneroom.particles;

import net.ld.oneroom.particles.ParticleSystem.Particle;

/** rotates the particles towards the target (on spawn) */
public class RotToTarget extends ParticleInitialiser {

	@Override
	public void initialise(Particle pParticle) {
		
		// Get angle towards target
		
		float lX = (pParticle.vx * 10) - pParticle.x;
		float lY = (pParticle.vy * 10) - pParticle.y;
		
		float lAng = (float)Math.atan2(lY,  lX);
		
		pParticle.rotation = lAng;
		
		
	}
	
}
