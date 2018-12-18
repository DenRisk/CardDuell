public class DrawStar {

    public static float[] makeStarVertices(float[] color) {

        // Definition of positions of vertices for a cuboid
        float[] p0 = {-0.2f, 0.08f, 0.0f};
        float[] p1 = {-0.22f, 0.083f, 0.0f};
        float[] p2 = {-0.24f, 0.08f, 0.0f};

        float[] p3 = {-0.26f, 0.077f, 0.0f};
        float[] p4 = {-0.28f, 0.08f, 0.0f};

        float[] p5 = {-0.32f, 0.08f, 0.0f};
        float[] p6 = {-0.30f, 0.083f, 0.0f};

        float[] p7 = {-0.28f, 0.089f, 0.0f};
        float[] p8 = {-0.32f, 0.089f, 0.0f};

        float[] p9 = {-0.26f, 0.092f, 0.0f};
        float[] p10 = {-0.24f, 0.086f, 0.0f};
        float[] p11 = {-0.2f, 0.086f, 0.0f};


        // color vector
        float[] c = color;
        // Definition of normal vectors for cuboid surfaces

        float[] nd = { 0, 0,  1}; // 0 down (bottom)

        // Definition of texture coordinates for cuboid surfaces
        float[] uv00 = {0, 0}; // u = 0, v = 0
        float[] uv01 = {0, 1}; // u = 0, v = 1
        float[] uv10 = {1, 0}; // u = 1, v = 0
        float[] uv11 = {1, 1}; // u = 1, v = 1

        float[] verticies = {
                //index: 0
                p0[0], p0[1], p0[2],   // position
                c[0], c[1], c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                uv10[0], uv10[1],      // texture coordinate
                // index: 1
                p1[0], p1[1], p1[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 2
                p2[0], p2[1], p2[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv11[0], uv11[1],      // texture coordinates

                //index: 3
                p3[0], p3[1], p3[2],   // position
                c[0], c[1], c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                uv10[0], uv10[1],      // texture coordinate
                // index: 4
                p4[0], p4[1], p4[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 5
                p5[0], p5[1], p5[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv11[0], uv11[1],      // texture coordinates

                //index: 6
                p6[0], p6[1], p6[2],   // position
                c[0], c[1], c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                uv10[0], uv10[1],      // texture coordinate
                // index: 7
                p7[0], p7[1], p7[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 8
                p8[0], p8[1], p8[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv11[0], uv11[1],      // texture coordinates

                p9[0], p9[1], p9[2],   // position
                c[0], c[1], c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                uv10[0], uv10[1],      // texture coordinate
                // index: 7
                p10[0], p10[1], p10[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 8
                p11[0], p11[1], p11[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
        };
        return verticies;
    }

    public static int[] makeStarIndicesForTriangleStrip() {

        int[] indices = {

                0, 1, 2, 4, 3, 4, 5, 6, 4, 7, 1, 10, 11, 1, 10, 7, 9
        };
        return indices;
    }

    public static int noOfIndicesForStar() {
        return 17;
    }
}
