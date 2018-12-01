
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;

public class StartRendererPP extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;

    final String shaderPath = ".\\resources\\";
    final String vertexShaderFileName = "Basic.vert";
    final String fragmentShaderFileName = "Basic.frag";
    private ShaderProgram shaderProgram;

    private LightSource light0;

    // Pointers for data transfer and handling on GPU
    private int[] vaoName;  // Name of vertex array object
    private int[] vboName;	// Name of vertex buffer object
    private int[] iboName;	// Name of index buffer object

    InteractionHandler interactionHandler; //Object for handling keyboard and mouse interaction
    PMVMatrix pmvMatrix; // Projection model view matrix tool

    public StartRendererPP(GLCapabilities capabilities) {
        super(capabilities);
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    private void createAndRegisterInteractionHandler() {
        interactionHandler = new InteractionHandler();
        this.addKeyListener(interactionHandler);
        this.addMouseListener(interactionHandler);
        this.addMouseMotionListener(interactionHandler);
        this.addMouseWheelListener(interactionHandler);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        // Verify if VBO-Support is available
        if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
            System.out.println("Error: VBO support is missing");
        else
            System.out.println("VBO support is available");

        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath,
                vertexShaderFileName, fragmentShaderFileName);

        vaoName = new int[1];
        gl.glGenVertexArrays(1, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");
        gl.glBindVertexArray(vaoName[0]);

        vboName = new int[1];
        gl.glGenBuffers(1, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[0]);

        iboName = new int[1];
        gl.glGenBuffers(1, iboName, 0);
        if (iboName[0] < 1)
            System.err.println("Error allocating index buffer object.");

        initMainTable(gl);

        pmvMatrix = new PMVMatrix();
        interactionHandler.setEyeZ(0.5f);

        //gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    private void initMainTable(GL3 gl) {
        gl.glBindVertexArray(vaoName[0]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawTable.makeBoxVertices(0.8f,0.05f,0.6f, color0);
        int[] tableIndices = DrawTable.makeBoxIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[0]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[0]);

        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, tableIndices.length * 4,
                IntBuffer.wrap(tableIndices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 11*4, 0);

        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 11*4, 3*4);

        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 11*4, 6*4);

        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 11*4, 9*4);
    }

    private void displayMainTable(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[0]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawTable.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        gl.glUseProgram(shaderProgram.getShaderProgramID());

        System.out.println("Camera: z = " + interactionHandler.getEyeZ() + ", " +
                "x-Rot: " + interactionHandler.getAngleXaxis() +
                ", y-Rot: " + interactionHandler.getAngleYaxis() +
                ", x-Translation: " + interactionHandler.getxPosition()+
                ", y-Translation: " + interactionHandler.getyPosition());// definition of translation of model (Model/Object Coordinates --> World Coordinates)

        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluLookAt(0f, 2f, interactionHandler.getEyeZ(),
                            0f, 0f, 0f,
                            0f, 1.0f, 0f);
        pmvMatrix.glTranslatef(interactionHandler.getxPosition(), interactionHandler.getyPosition(), 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);

        pmvMatrix.glPushMatrix();
        displayMainTable(gl);
        pmvMatrix.glPopMatrix();
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.1f, 100f);
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Deleting allocated objects, incl. shader program.");
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        System.exit(0);
    }
}
