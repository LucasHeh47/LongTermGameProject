package com.lucasj.gamedev.mathutils;

import java.util.Random;

public class SimplexNoise {
    private static final int GRADIENT_SIZE = 256;
    private static final int[] perm = new int[GRADIENT_SIZE * 2];
    private static final double[][] grad3 = {
        {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
        {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
        {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    static {
        initializePerm(new Random().nextInt()); // Default random seed
    }

    public static void initializePerm(int seed) {
        Random rand = new Random(seed);
        for (int i = 0; i < GRADIENT_SIZE; i++) {
            perm[i] = i;
        }
        for (int i = 0; i < GRADIENT_SIZE; i++) {
            int j = rand.nextInt(GRADIENT_SIZE);
            int temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
        }
        for (int i = 0; i < GRADIENT_SIZE; i++) {
            perm[GRADIENT_SIZE + i] = perm[i];
        }
    }

    private static double dot(double[] g, double x, double y) {
        return g[0] * x + g[1] * y;
    }

    private static double noise(double x, double y) {
        double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

        double s = (x + y) * F2;
        int i = (int) Math.floor(x + s);
        int j = (int) Math.floor(y + s);

        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = x - X0;
        double y0 = y - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1; j1 = 0;
        } else {
            i1 = 0; j1 = 1;
        }

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        int ii = i & 255;
        int jj = j & 255;

        double[] g0 = grad3[perm[ii + perm[jj]] % 12];
        double[] g1 = grad3[perm[ii + i1 + perm[jj + j1]] % 12];
        double[] g2 = grad3[perm[ii + 1 + perm[jj + 1]] % 12];

        double t0 = 0.5 - x0 * x0 - y0 * y0;
        double n0 = t0 < 0 ? 0.0 : (t0 * t0) * (t0 * t0) * dot(g0, x0, y0);

        double t1 = 0.5 - x1 * x1 - y1 * y1;
        double n1 = t1 < 0 ? 0.0 : (t1 * t1) * (t1 * t1) * dot(g1, x1, y1);

        double t2 = 0.5 - x2 * x2 - y2 * y2;
        double n2 = t2 < 0 ? 0.0 : (t2 * t2) * (t2 * t2) * dot(g2, x2, y2);

        return 70.0 * (n0 + n1 + n2);
    }

    public static double[][] generateNoise(int width, int height, int octaves, double persistence, double scale) {
        double[][] noise = new double[width][height];
        double maxAmplitude = 0;
        double amplitude = 1.0;

        for (int octave = 0; octave < octaves; octave++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    noise[x][y] += noise(x * scale, y * scale) * amplitude;
                }
            }
            scale *= 2.0;  // Double frequency for next octave
            maxAmplitude += amplitude;
            amplitude *= persistence;  // Reduce amplitude for next octave
        }

        // Normalize the noise to range [0, 1]
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noise[x][y] /= maxAmplitude;
            }
        }

        return noise;
    }
}

