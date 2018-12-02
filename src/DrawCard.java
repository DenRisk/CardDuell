public class DrawCard {

    private static float[] setVerticies(float[] p0, float[] p1, float[] p2, float[] p3, float[] p4, float[] p5, float[] p6, float[] p7, float[] c, float[] nf, float[] nb, float[] nl, float[] nr, float[] nu, float[] nd,
                                        float[] uv00, float[] uv01, float[] uv10, float[] uv11) {

        float[] verticies = {
                // front surface
                // index: 0
                p0[0], p0[1], p0[2],   // position
                c[0], c[1], c[2],    // color
                nf[0], nf[1], nf[2],   // normal
                uv01[0], uv01[1],      // texture coordinate
                // index: 1
                p3[0], p3[1], p3[2],   // position
                c[0], c[1], c[2],      // color
                nf[0], nf[1], nf[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 2
                p1[0], p1[1], p1[2],   // position
                c[0], c[1], c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 3
                p2[0], p2[1], p2[2],   // position
                c[0], c[1], c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // back surface
                // index: 4
                p5[0], p5[1], p5[2],   // position
                c[0], c[1], c[2],    // color
                nb[0], nb[1], nb[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 5
                p6[0], p6[1], p6[2],   // position
                c[0], c[1], c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 6
                p4[0], p4[1], p4[2],   // position
                c[0], c[1], c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 7
                p7[0], p7[1], p7[2],   // position
                c[0], c[1], c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                uv10[0], uv10[1],      // texture coordinates


                // left surface
                // index: 8
                p4[0], p4[1], p4[2],   // position
                c[0], c[1], c[2],    // color
                nl[0], nl[1], nl[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 9
                p7[0], p7[1], p7[2],   // position
                c[0], c[1], c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 10
                p0[0], p0[1], p0[2],   // position
                c[0], c[1], c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 11
                p3[0], p3[1], p3[2],   // position
                c[0], c[1], c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // right surface
                // index: 12
                p1[0], p1[1], p1[2],   // position
                c[0], c[1], c[2],    // color
                nr[0], nr[1], nr[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 13
                p2[0], p2[1], p2[2],   // position
                c[0], c[1], c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 14
                p5[0], p5[1], p5[2],   // position
                c[0], c[1], c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 15
                p6[0], p6[1], p6[2],   // position
                c[0], c[1], c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // top surface
                // index: 16
                p4[0], p4[1], p4[2],   // position
                c[0], c[1], c[2],    // color
                nu[0], nu[1], nu[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 17
                p0[0], p0[1], p0[2],   // position
                c[0], c[1], c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 18
                p5[0], p5[1], p5[2],   // position
                c[0], c[1], c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 19
                p1[0], p1[1], p1[2],   // position
                c[0], c[1], c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // bottom surface
                // index: 20
                p3[0], p3[1], p3[2],   // position
                c[0], c[1], c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                uv10[0], uv10[1],      // texture coordinates
                // index: 21
                p7[0], p7[1], p7[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 22
                p2[0], p2[1], p2[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 23
                p6[0], p6[1], p6[2],   // position
                c[0], c[1], c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
        };
        return verticies;
    }

    public static float[] makeCardVertices(float[] color) {

        //position
        float[] p0 = {0.4f, 0.055f, 0.1f};
        float[] p1 = {0.30f, 0.055f, 0.1f};
        float[] p2 = {0.30f, 0.055f, -0.1f};
        float[] p3 = {0.4f, 0.055f, -0.1f};
        float[] p4 = {0.4f, 0.05f, 0.1f};
        float[] p5 = {0.30f, 0.05f, 0.1f};
        float[] p6 = {0.30f, 0.05f, -0.1f};
        float[] p7 = {0.4f, 0.05f, -0.1f};

        //color
        float[] c = color;

        //normalvector
        float[] nf = {0, 0, 1}; // 0 front
        float[] nb = {0, 0, -1}; // 0 back
        float[] nl = {-1, 0, 0}; // 0 left
        float[] nr = {1, 0, 0}; // 0 right
        float[] nu = {0, 1, 0}; // 0 up (top)
        float[] nd = {0, -1, 0}; // 0 down (bottom)

        // Definition of texture coordinates for cuboid surfaces
        float[] uv00 = {0, 0}; // u = 0, v = 0
        float[] uv01 = {0, 1}; // u = 0, v = 1
        float[] uv10 = {1, 0}; // u = 1, v = 0
        float[] uv11 = {1, 1}; // u = 1, v = 1

        float[] verticies = setVerticies(p0, p1, p2, p3, p4, p5, p6, p7, c, nf, nb, nl, nr, nu, nd, uv00, uv01, uv10, uv11);

        return verticies;
    }

    public static int[] makeCardIndicesForTriangleStrip() {

        int[] indices = {
                // Note: back faces are drawn,
                // but drawing is faster than using "GL_TRIANGLES"
                21, 23, 20, 22,         // down (bottom)
                1, 3, 0, 2, 2, 3,       // front
                12, 13, 14, 15,         // right
                4, 5, 6, 7,             // back
                8, 9, 10, 11, 10, 10,   // left
                16, 17, 18, 19          // up (top)
        };
        return indices;
    }

    public static int noOfIndicesForCard() {
        return 28;
    }


}
