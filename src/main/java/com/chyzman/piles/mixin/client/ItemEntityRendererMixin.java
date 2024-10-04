package com.chyzman.piles.mixin.client;

import com.chyzman.piles.util.ItemScalingUtil;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    @WrapWithCondition(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private boolean noBob(MatrixStack instance, float x, float y, float z) {
        return false;
    }

    @Inject(method = "renderStack(Lnet/minecraft/client/render/item/ItemRenderer;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;ZLnet/minecraft/util/math/random/Random;)V",
            at = @At(value = "HEAD"), cancellable = true)
    private static void replaceRendering(
            ItemRenderer itemRenderer,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            ItemStack stack,
            BakedModel model,
            boolean depth,
            Random random,
            CallbackInfo ci
    ) {
        matrices.push();
        var properties = ItemScalingUtil.getItemModelProperties(stack);
        var scales = Stream.of(properties.size().z, properties.size().x, properties.size().y).sorted().toList();

        matrices.translate(0, -properties.offset().y + (scales.getFirst() / 2) * (properties.isGenerated() ? 1 / 16f : 1), 0);

        var tripled = scales.getFirst() > 0.15;
        for (int i = 0; i < (tripled ? stack.getCount() / 3 : stack.getCount()); i++) {
            matrices.push();

            if (tripled) {
                matrices.push();
                matrices.translate(properties.size().x / 2, 0, 0);
                rotateAndRender(itemRenderer, matrices, vertexConsumers, light, stack, model, scales, properties, random);
                matrices.translate(-properties.size().x, 0, -properties.size().z / 2);
                rotateAndRender(itemRenderer, matrices, vertexConsumers, light, stack, model, scales, properties, random);
                matrices.translate(0, 0, properties.size().z);
                rotateAndRender(itemRenderer, matrices, vertexConsumers, light, stack, model, scales, properties, random);
                matrices.pop();
            } else {
                rotateAndRender(itemRenderer, matrices, vertexConsumers, light, stack, model, scales, properties, random);
            }

            matrices.pop();
            matrices.translate(properties.offset().x, properties.offset().y, properties.offset().z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((tripled ? 180 : 0) + (random.nextFloat() * 40 - 20)));
            matrices.translate(-properties.offset().x, -properties.offset().y, -properties.offset().z);
            matrices.translate(0, scales.getFirst(), 0);
        }
        matrices.pop();
        ci.cancel();
    }

    @Unique
    private static void rotateAndRender(ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, BakedModel model, List<Double> scales, ItemScalingUtil.ItemModelProperties properties, Random random) {
        matrices.push();
        matrices.translate(
                properties.offset().x,
                properties.offset().y,
                properties.offset().z
        );
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((random.nextFloat() * 4 - 2)));
        if (random.nextInt(100) >= 99) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        if (scales.getFirst() != properties.size().y) {
            if (scales.getFirst() == properties.size().x) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
            } else if (scales.getFirst() == properties.size().z) {
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
            }
        }
        matrices.translate(
                -properties.offset().x,
                -properties.offset().y,
                -properties.offset().z
        );
        itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
        matrices.pop();
    }
}
