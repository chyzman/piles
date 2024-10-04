package com.chyzman.piles.mixin.client.sodium;

import com.chyzman.piles.asm.OnlyWithMod;
import com.chyzman.piles.util.Mpoatv;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@OnlyWithMod(modid = "sodium")
@Mixin(Mpoatv.VertexConsumerImpl.class)
public abstract class MpoatvMixin implements VertexBufferWriter {
    @Shadow
    public abstract VertexConsumer vertex(float x, float y, float z);

    @Override
    public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
        long stride = format.getVertexSizeByte();
        long offsetPosition = format.getOffset(VertexFormatElement.POSITION);

        for(int vertexIndex = 0; vertexIndex < count; ++vertexIndex) {
            float x = PositionAttribute.getX(ptr + offsetPosition);
            float y = PositionAttribute.getY(ptr + offsetPosition);
            float z = PositionAttribute.getZ(ptr + offsetPosition);

            vertex(x, y, z);

            ptr += stride;
        }
    }
}
