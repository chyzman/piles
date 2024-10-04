package com.chyzman.piles.mixin.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At(value = "RETURN"))
    private static void betterHitboxes(
            MatrixStack matrices,
            VertexConsumer vertices,
            Entity entity,
            float tickDelta,
            float red,
            float green,
            float blue,
            CallbackInfo ci
    ) {
        if (!(entity instanceof ItemEntity)) return;
        var box = entity.getVisibilityBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, box, 1, 0, 0, 1);
    }
}
