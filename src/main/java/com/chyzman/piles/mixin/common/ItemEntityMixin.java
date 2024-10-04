package com.chyzman.piles.mixin.common;

import com.chyzman.piles.util.ItemScalingUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow @Final public float uniqueOffset;

    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getRotation", at = @At("HEAD"), cancellable = true)
    private void noSpeen(float tickDelta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(uniqueOffset);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        var stack = getStack();
        var properties = ItemScalingUtil.getItemModelProperties(stack);
        var scales = Stream.of(properties.size().x, properties.size().y, properties.size().z).sorted().toList();
        return EntityDimensions.changing((float) Math.sqrt(scales.get(1) * scales.get(1) + scales.get(2) * scales.get(2)), (float) ((scales.getFirst() > 0.15 ? scales.getFirst() / 3 : scales.getFirst()) * (stack.getCount()))).getBoxAt(getPos());
    }
}
