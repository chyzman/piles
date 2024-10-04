package com.chyzman.piles.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.HashMap;
import java.util.Map;

public class Mpoatv implements VertexConsumerProvider {
    public float minX = Float.MAX_VALUE;
    public float minY = Float.MAX_VALUE;
    public float minZ = Float.MAX_VALUE;
    public float maxX = -Float.MAX_VALUE;
    public float maxY = -Float.MAX_VALUE;
    public float maxZ = -Float.MAX_VALUE;

    public Float meanx;
    public Float meany;
    public Float meanz;

    private final Map<RenderLayer, VertexConsumerImpl> consumers = new HashMap<>();

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        return consumers.computeIfAbsent(layer, unused -> new VertexConsumerImpl());
    }

    public class VertexConsumerImpl implements VertexConsumer {
        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);

            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);

            meanx = meanx == null ? x : (meanx + x) / 2;
            meany = meany == null ? y : (meany + y) / 2;
            meanz = meanz == null ? z : (meanz + z) / 2;

            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }
    }
}
