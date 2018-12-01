import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class DrawTable {

    public static float[] makeBoxVertices(float width, float height, float depth, float[] color) {

        //position
        float[] p0 = {width, height, depth};
        float[] p1 = {width, -height, depth};
        float[] p2 = {-width, -height, depth};
        float[] p3 = {-width, height, depth};
        float[] p4 = {width, height, -depth};
        float[] p5 = {width, -height, -depth};
        float[] p6 = {-width, -height, -depth};
        float[] p7 = {-width, height, -depth};

        //color
        float[] c = color;

        //normalvector
        float[] nf = { 0,  0,  1}; // 0 front
        float[] nb = { 0,  0, -1}; // 0 back
        float[] nl = {-1,  0,  0}; // 0 left
        float[] nr = { 1,  0,  0}; // 0 right
        float[] nu = { 0,  1,  0}; // 0 up (top)
        float[] nd = { 0, -1,  0}; // 0 down (bottom)

        // Definition of texture coordinates for cuboid surfaces
        float[] uv00 = { 0,  0}; // u = 0, v = 0
        float[] uv01 = { 0,  1}; // u = 0, v = 1
        float[] uv10 = { 1,  0}; // u = 1, v = 0
        float[] uv11 = { 1,  1}; // u = 1, v = 1

        float[] verticies = {
                // front surface
                // index: 0
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],    // color
                nf[0], nf[1], nf[2],   // normal
                uv01[0], uv01[1],      // texture coordinate
                // index: 1
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 2
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 3
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // back surface
                // index: 4
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],    // color
                nb[0], nb[1], nb[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 5
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 6
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 7
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                uv10[0], uv10[1],      // texture coordinates


                // left surface
                // index: 8
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],    // color
                nl[0], nl[1], nl[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 9
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 10
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 11
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // right surface
                // index: 12
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],    // color
                nr[0], nr[1], nr[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 13
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 14
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 15
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // top surface
                // index: 16
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],    // color
                nu[0], nu[1], nu[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
                // index: 17
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 18
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 19
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                uv10[0], uv10[1],      // texture coordinates

                // bottom surface
                // index: 20
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                uv10[0], uv10[1],      // texture coordinates
                // index: 21
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv11[0], uv11[1],      // texture coordinates
                // index: 22
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv00[0], uv00[1],      // texture coordinates
                // index: 23
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                uv01[0], uv01[1],      // texture coordinates
        };
        return verticies;
    }

    public static int[] makeBoxIndicesForTriangleStrip() {

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

    public static int noOfIndicesForBox() {
        return 28;
    }

    public void tischbeinVR(GL2 gl) {

        float[] v0 = {0.75f, -0.05f, 0.55f}; //top
        float[] v1 = {0.7f, -0.05f, 0.55f};
        float[] v2 = {0.7f, -0.05f, 0.5f};
        float[] v3 = {0.75f, -0.05f, 0.5f};
        float[] v4 = {0.75f, -0.6f, 0.55f}; //bot
        float[] v5 = {0.7f, -0.6f, 0.55f};
        float[] v6 = {0.7f, -0.6f, 0.5f};
        float[] v7 = {0.75f, -0.6f, 0.5f};

        gl.glBegin(GL.GL_TRIANGLE_STRIP);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v6,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v3,0);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v4,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v3,0);
        gl.glEnd();
    }
    public void tischbeinVL(GL2 gl) {

        float[] v0 = {-0.7f, -0.05f, 0.55f}; //top
        float[] v1 = {-0.75f, -0.05f, 0.55f};
        float[] v2 = {-0.75f, -0.05f, 0.5f};
        float[] v3 = {-0.7f, -0.05f, 0.5f};
        float[] v4 = {-0.7f, -0.6f, 0.55f}; //top
        float[] v5 = {-0.75f, -0.6f, 0.55f};
        float[] v6 = {-0.75f, -0.6f, 0.5f};
        float[] v7 = {-0.7f, -0.6f, 0.5f};


        gl.glBegin(GL.GL_TRIANGLE_STRIP);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v6,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v3,0);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v4,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v3,0);
        gl.glEnd();

    }

    public void tischbeinHL(GL2 gl) {
        float[] v0 = {-0.7f, -0.05f, -0.5f}; //top
        float[] v1 = {-0.75f, -0.05f, -0.5f};
        float[] v2 = {-0.75f, -0.05f, -0.55f};
        float[] v3 = {-0.7f, -0.05f, -0.55f};
        float[] v4 = {-0.7f, -0.6f, -0.5f}; //top
        float[] v5 = {-0.75f, -0.6f, -0.5f};
        float[] v6 = {-0.75f, -0.6f, -0.55f};
        float[] v7 = {-0.7f, -0.6f, -0.55f};

        gl.glBegin(GL.GL_TRIANGLE_STRIP);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v6,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v3,0);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v4,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v3,0);
        gl.glEnd();
    }

    public void tischbeinHR(GL2 gl) {
        float[] v0 = {0.75f, -0.05f, -0.5f}; //top
        float[] v1 = {0.7f, -0.05f, -0.5f};
        float[] v2 = {0.7f, -0.05f, -0.55f};
        float[] v3 = {0.75f, -0.05f, -0.55f};
        float[] v4 = {0.75f, -0.6f, -0.5f}; //bot
        float[] v5 = {0.7f, -0.6f, -0.5f};
        float[] v6 = {0.7f, -0.6f, -0.55f};
        float[] v7 = {0.75f, -0.6f, -0.55f};

        gl.glBegin(GL.GL_TRIANGLE_STRIP);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v6,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v2,0);
        gl.glVertex3fv(v3,0);
        gl.glVertex3fv(v1,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v5,0);
        gl.glVertex3fv(v4,0);
        gl.glVertex3fv(v7,0);
        gl.glVertex3fv(v0,0);
        gl.glVertex3fv(v3,0);
        gl.glEnd();
    }
}
