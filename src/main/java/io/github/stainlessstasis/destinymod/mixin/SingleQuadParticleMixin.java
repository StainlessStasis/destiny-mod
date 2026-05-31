package io.github.stainlessstasis.destinymod.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(SingleQuadParticle.class)
public abstract class SingleQuadParticleMixin {
    @Shadow
    protected float rCol;
    @Shadow
    protected float gCol;
    @Shadow
    protected float bCol;

    @Inject(method = "extract", at = @At("TAIL"))
    public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTickTime, CallbackInfo ci) {
        
        rCol = color.getRed();
        gCol = color.getGreen();
        bCol = color.getBlue();
    }
}
