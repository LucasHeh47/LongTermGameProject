package com.lucasj.gamedev.mathutils;

import java.util.Random;

import java.util.Random;

public class PerlinNoise {

    private static final int[] PERMUTATION = new int[512];

    static {
        Random rand = new Random();
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }
        for (int i = 0; i < 256; i++) {
            int swap = rand.nextInt(256);
            int temp = p[i];
            p[i] = p[swap];
            p[swap] = temp;
        }
        for (int i = 0; i < 512; i++) {
            PERMUTATION[i] = p[i % 256];
        }
    }

    public static float[][] generatePerlinNoise(int width, int height, int octaves, float persistence) {
        float[][] perlinNoise = new float[width][height];
        float[][][] smoothNoise = new float[octaves][][];

        // Generate smooth noise
        for (int i = 0; i < octaves; i++) {
            smoothNoise[i] = generateSmoothNoise(width, height, (int) Math.pow(2, i));
        }

        // Blend noise together
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        for (int octave = octaves - 1; octave >= 0; octave--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    perlinNoise[x][y] += smoothNoise[octave][x][y] * amplitude;
                }
            }
        }

        // Normalize the result to the range [0, 1]
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                perlinNoise[x][y] /= totalAmplitude;
            }
        }

        return perlinNoise;
    }

    private static float[][] generateSmoothNoise(int width, int height, int period) {
        float[][] smoothNoise = new float[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sampleX1 = (x / period) * period;
                int sampleY1 = (y / period) * period;

                int sampleX2 = (sampleX1 + period) % width;
                int sampleY2 = (sampleY1 + period) % height;

                float horizontalBlend = (float) (x - sampleX1) / period;
                float verticalBlend = (float) (y - sampleY1) / period;

                float top = interpolate(
                        getNoise(sampleX1, sampleY1),
                        getNoise(sampleX2, sampleY1),
                        horizontalBlend
                );

                float bottom = interpolate(
                        getNoise(sampleX1, sampleY2),
                        getNoise(sampleX2, sampleY2),
                        horizontalBlend
                );

                smoothNoise[x][y] = interpolate(top, bottom, verticalBlend);
            }
        }

        return smoothNoise;
    }

    private static float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float) (1 - Math.cos(theta)) * 0.5f;
        return a * (1 - f) + b * f;
    }

    private static float getNoise(int x, int y) {
        int hash = PERMUTATION[(PERMUTATION[x & 255] + y) & 255];
        return hash / 255.0f;
    }
}

