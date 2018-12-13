
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;
import de.hshl.obj.loader.OBJLoader;
import de.hshl.obj.loader.objects.TriangleObject;
import de.hshl.obj.loader.objects.TriangleSurface;

import static com.jogamp.opengl.GL.*;

public class StartRendererPP extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;

    final String shaderPath = ".\\resources\\";
    final String vertexShaderFileName = "BlinnPhongPointTex.vert";

    final String fragmentShaderFileName = "BlinnPhongPointTex.frag";
    final String fragmentShaderFileNameCard = "BlinnPhongPointTexCard.frag";
    final String fragmentShaderFileNameBottom = "BlinnPhongPointTexFloor.frag";
    final String fragmentShaderFileNameWall = "BlinnPhongPointTexWall.frag";
    final String fragmentShaderFileNameWindow = "BlinnPhongPointTexWindow.frag";
    final String fragmentShaderFileNameSky = "BlinnPhongPointTexSky.frag";
    final String fragmentShaderFileNameCandleRed = "BlinnPhongPointTexCandleRed.frag";
    final String fragmentShaderFileNameCandleWick = "BlinnPhongPointTexCandleWick.frag";
    final String fragmentShaderFileNameBullet = "BlinnPhongPointTexBullet.frag";


    private ShaderProgram shaderProgram;
    private ShaderProgram shaderCard;
    private ShaderProgram shaderBottom;
    private ShaderProgram shaderWall;
    private ShaderProgram shaderRoomWindow;
    private ShaderProgram shaderSky;
    private ShaderProgram shaderCandleRed;
    private ShaderProgram shaderCandleWick;
    private ShaderProgram shaderBullet;

    private LightSource light0;
    private Material material0;
    private Material materialTable;
    private Material materialTableLeg;
    private Material materialCard;
    private Material materialChair;
    private Material materialWall;
    private Material materialBottom;
    private Material materialCandle;
    private Material materialBullet;

    private DrawCandle candle1;
    private DrawCandle candle2;
    private DrawCandle candleWick1;
    private DrawCandle candleWick2;

    private float[] vertexCoordinates;

    private DrawBullet bullet;

    private LoadTexture texture;

    private float angle;


    // Pointers for data transfer and handling on GPU
    private int[] vaoName;  // Name of vertex array object
    private int[] vboName;	// Name of vertex buffer object
    private int[] iboName;	// Name of index buffer object

    InteractionHandler interactionHandler;
    PMVMatrix pmvMatrix;

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

        int noOfObjects = 30;
        vaoName = new int[noOfObjects];
        for (int i = 0; i < vaoName.length; i++) {
            gl.glGenVertexArrays(noOfObjects, vaoName, 0);
            if (vaoName[i] < 1)
                System.err.println("Error allocating vertex array object (VAO).");
            gl.glBindVertexArray(vaoName[i]);
        }

        vboName = new int[noOfObjects];
        for (int i = 0; i < vboName.length; i++) {
            gl.glGenBuffers(noOfObjects, vboName, 0);
            if (vboName[i] < 1)
                System.err.println("Error allocating vertex buffer object (VBO).");
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
        }

        iboName = new int[noOfObjects];
        for (int i = 0; i < iboName.length; i++) {
            gl.glGenBuffers(noOfObjects, iboName, 0);
            if (iboName[i] < 1)
                System.err.println("Error allocating index buffer object.");
        }

        //lightparameters
        float[] lightPosition = {0.0f, 2.9f, 0.0f, 1.0f};
        float[] lightAmbientColor = {2.0f, 2.0f, 2.0f, 1.0f};
        float[] lightDiffuseColor = {2.0f, 2.0f, 2.0f, 1.0f};
        float[] lightSpecularColor = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightSpot = {0,0,-1};
        light0 = new LightSource(lightPosition, lightAmbientColor,
                lightDiffuseColor, lightSpecularColor, lightSpot);


        pmvMatrix = new PMVMatrix();
        interactionHandler.setEyeZ(0.5f);

        initMainTable(gl);
        initTableLegVR(gl);
        initTableLegVL(gl);
        initTableLegHL(gl);
        initTableLegHR(gl);

        initCard(gl);

        initRoomBack(gl);
        initRoomLeft(gl);
        initRoomRight(gl);
        initRoomFront(gl);
        initRoomBottom(gl);
        initRoomTop(gl);

        initChairLegVL(gl);
        initChairLegVR(gl);
        initChairLegHL(gl);
        initChairLegHR(gl);
        initChairSeat(gl);
        initChairLean1(gl);
        initChairLean2(gl);

        initRoomWindowTop(gl);
        initRoomWindowBottom(gl);
        initRoomWindowLeft(gl);
        initRoomWindowRight(gl);
        initRoomWindowOutside(gl);

        initCandle01(gl);
        initCandle02(gl);
        initCandleWick01(gl);
        initCandleWick02(gl);

        initBullet(gl);

       // initOBJ(gl);

        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        gl.glUseProgram(shaderProgram.getShaderProgramID());

        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluLookAt(0f, 2f, interactionHandler.getEyeZ(),
                0f, 0f, 0f,
                0f, 1.0f, 0f);
        pmvMatrix.glTranslatef(interactionHandler.getxPosition(), interactionHandler.getyPosition(), 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);

        //display Table
        pmvMatrix.glPushMatrix();
        displayMainTable(gl);
        displayTableLegVR(gl);
        displayTableLegVL(gl);
        displayTableLegHL(gl);
        displayTableLegHR(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        if (angle ==180) {
            displayCard(gl);
        }
        pmvMatrix.glTranslatef(0.0f, 0.05f, +0.0f);
        if (angle<=180) {
            pmvMatrix.glRotatef(angle, 0, 0, 1);
            angle++;
        }
        pmvMatrix.glTranslatef(-0.20f, -0.05f, +0.1f);

        displayCard(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        displayRoomBack(gl);
        displayRoomLeft(gl);
        displayRoomRight(gl);
        displayRoomFront(gl);
        displayRoomBottom(gl);
        displayRoomTop(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-1.5f,0.6f,-0.2f);
        pmvMatrix.glRotatef(-45f, 0,1,0);
        pmvMatrix.glTranslatef(0.2f,-0.6f,-2f);
        displayChairLegVL(gl);
        displayChairLegVR(gl);
        displayChairLegHL(gl);
        displayChairLegHR(gl);
        displayChairSeat(gl);
        displayChairLean1(gl);
        displayChairLean2(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        displayRoomWindowTop(gl);
        displayRoomWindowBottom(gl);
        displayRoomWindowLeft(gl);
        displayRoomWindowRight(gl);
        displayRoomWindowOutside(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.6f, 0f, -0.4f);
        displayCandle01(gl);
        displayCandleWick01(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.55f, 0f, -0.3f);
        displayCandle02(gl);
        displayCandleWick02(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.6f, 0.11f, -0.1f);
        displayBullet(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        //displayOBJ(gl);
        pmvMatrix.glPopMatrix();

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

        float[] matEmission = {0.1f, 0.1f, 0.1f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.2f, 0.2f, 0.2f, 1.0f};
        float matShininess = 200.0f;

        materialTable = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE0);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/holz-struktur.jpg");
    }

    private void displayMainTable(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialTable.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialTable.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialTable.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialTable.getSpecular(), 0);
        gl.glUniform1f(10, materialTable.getShininess());

        gl.glUniform4fv(11,1, light0.getLightSpot(), 0);

        gl.glBindVertexArray(vaoName[0]);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawTable.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initTableLegVR(GL3 gl) {
        gl.glBindVertexArray(vaoName[1]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawTable.tableLegVRVerticices(color0);
        int[] tableIndices = DrawTable.makeVRLegIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[1]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[1]);

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

        float[] matEmission = {0.1f, 0.1f, 0.1f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.2f, 0.2f, 0.2f, 1.0f};
        float matShininess = 200.0f;

        materialTableLeg = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE0);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/holz-struktur.jpg");
    }

    private void displayTableLegVR(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialTableLeg.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialTableLeg.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialTableLeg.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialTableLeg.getSpecular(), 0);
        gl.glUniform1f(10, materialTable.getShininess());

        gl.glBindVertexArray(vaoName[1]);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawTable.noOfIndicesForVRLeg(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initTableLegVL(GL3 gl) {
        gl.glBindVertexArray(vaoName[2]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawTable.tableLegVLVerticices(color0);
        int[] tableIndices = DrawTable.makeVLLegIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[2]);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[2]);

            gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                    FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

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

    private void displayTableLegVL(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[2]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawTable.noOfIndicesForVLLeg(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initTableLegHL(GL3 gl) {
        gl.glBindVertexArray(vaoName[3]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawTable.tableLegHLVerticices(color0);
        int[] tableIndices = DrawTable.makeHLLegIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[3]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[3]);

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

    private void displayTableLegHL(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[3]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawTable.noOfIndicesForHLLeg(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initTableLegHR(GL3 gl) {
        gl.glBindVertexArray(vaoName[4]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawTable.tableLegHRVerticices(color0);
        int[] tableIndices = DrawTable.makeHRLegIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[4]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[4]);

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

    private void displayTableLegHR(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[4]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawTable.noOfIndicesForHRLeg(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initCard(GL3 gl) {
        gl.glBindVertexArray(vaoName[5]);
        shaderCard = new ShaderProgram(gl);
        shaderCard.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameCard);

        float[] color0 = {0.0f, 0.0f, 0.0f};
        float[] cubeVertices = DrawCard.makeCardVertices(color0);
        int[] tableIndices = DrawCard.makeCardIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[5]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[5]);

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

        float[] matEmission = {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 200.0f;

        materialCard = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        //texture
        gl.glActiveTexture(GL_TEXTURE1);
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Karte.JPG");

    }

    private void displayCard(GL3 gl) {
        gl.glUseProgram(shaderCard.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, material0.getEmission(), 0);
        gl.glUniform4fv(7, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(8, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, material0.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());


        gl.glBindVertexArray(vaoName[5]);
        gl.glActiveTexture(GL_TEXTURE1);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawCard.noOfIndicesForCard(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomBack(GL3 gl) {
        gl.glBindVertexArray(vaoName[6]);
        shaderWall = new ShaderProgram(gl);
        shaderWall.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWall);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoom.makeRoomBackVertices(color0);
        int[] tableIndices = DrawRoom.makeRoomIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[6]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[6]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matSpecular = {0.1f, 0.1f, 0.1f, 1.0f};
        float matShininess = 20.0f;

        materialWall = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE3);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Wand.jpg");
    }

    private void displayRoomBack(GL3 gl) {
        gl.glUseProgram(shaderWall.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialWall.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialWall.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialWall.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialWall.getSpecular(), 0);
        gl.glUniform1f(10, materialWall.getShininess());

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glBindVertexArray(vaoName[6]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoom.noOfIndicesForRoom(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomLeft(GL3 gl) {
        gl.glBindVertexArray(vaoName[7]);
        shaderWall = new ShaderProgram(gl);
        shaderWall.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWall);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoom.makeRoomLeftVertices(color0);
        int[] tableIndices = DrawRoom.makeRoomIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[7]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[7]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matSpecular = {0.4f, 0.4f, 0.4f, 1.0f};
        float matShininess = 200.0f;

        materialWall = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE3);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Wand.jpg");
    }

    private void displayRoomLeft(GL3 gl) {
        gl.glUseProgram(shaderWall.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialWall.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialWall.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialWall.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialWall.getSpecular(), 0);
        gl.glUniform1f(10, materialWall.getShininess());

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glBindVertexArray(vaoName[7]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoom.noOfIndicesForRoom(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomRight(GL3 gl) {
        gl.glBindVertexArray(vaoName[8]);
        shaderWall = new ShaderProgram(gl);
        shaderWall.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWall);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoom.makeRoomRightVertices(color0);
        int[] tableIndices = DrawRoom.makeRoomIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[8]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[8]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matSpecular = {0.4f, 0.4f, 0.4f, 1.0f};
        float matShininess = 200.0f;

        materialWall = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE3);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Wand.jpg");
    }

    private void displayRoomRight(GL3 gl) {
        gl.glUseProgram(shaderWall.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialWall.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialWall.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialWall.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialWall.getSpecular(), 0);
        gl.glUniform1f(10, materialWall.getShininess());

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glBindVertexArray(vaoName[8]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoom.noOfIndicesForRoom(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomFront(GL3 gl) {
        gl.glBindVertexArray(vaoName[9]);
        shaderWall = new ShaderProgram(gl);
        shaderWall.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWall);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoom.makeRoomFrontVertices(color0);
        int[] tableIndices = DrawRoom.makeRoomIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[9]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[9]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matSpecular = {0.4f, 0.4f, 0.4f, 1.0f};
        float matShininess = 200.0f;

        materialWall = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE3);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Wand.jpg");
    }

    private void displayRoomFront(GL3 gl) {
        gl.glUseProgram(shaderWall.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialWall.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialWall.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialWall.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialWall.getSpecular(), 0);
        gl.glUniform1f(10, materialWall.getShininess());

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glBindVertexArray(vaoName[9]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoom.noOfIndicesForRoom(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomBottom(GL3 gl) {
        gl.glBindVertexArray(vaoName[10]);
        shaderBottom = new ShaderProgram(gl);
        shaderBottom.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameBottom);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoom.makeRoomBottomVertices(color0);
        int[] tableIndices = DrawRoom.makeRoomIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[10]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[10]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matSpecular = {0.4f, 0.4f, 0.4f, 1.0f};
        float matShininess = 200.0f;

        materialBottom = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        //texture
        gl.glActiveTexture(GL_TEXTURE2);
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Fußboden.jpg");
    }

    private void displayRoomBottom(GL3 gl) {
        gl.glUseProgram(shaderBottom.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialBottom.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialBottom.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialBottom.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialBottom.getSpecular(), 0);
        gl.glUniform1f(10, materialBottom.getShininess());


        gl.glActiveTexture(GL_TEXTURE2);
        gl.glBindVertexArray(vaoName[10]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoom.noOfIndicesForRoom(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomTop(GL3 gl) {
        gl.glBindVertexArray(vaoName[11]);
        shaderWall = new ShaderProgram(gl);
        shaderWall.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWall);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoom.makeRoomTopVertices(color0);
        int[] tableIndices = DrawRoom.makeRoomIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[11]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[11]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.3f, 0.3f, 0.3f, 1.0f};
        float[] matDiffuse =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matSpecular = {0.4f, 0.4f, 0.4f, 1.0f};
        float matShininess = 200.0f;

        materialWall = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE3);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Wand.jpg");
    }

    private void displayRoomTop(GL3 gl) {
        gl.glUseProgram(shaderWall.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialWall.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialWall.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialWall.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialWall.getSpecular(), 0);
        gl.glUniform1f(10, materialWall.getShininess());

        gl.glActiveTexture(GL_TEXTURE3);
        gl.glBindVertexArray(vaoName[11]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoom.noOfIndicesForRoom(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairLegVL(GL3 gl) {
        gl.glBindVertexArray(vaoName[12]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelegVLVertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[12]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[12]);

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

        float[] matEmission = {0.1f, 0.1f, 0.1f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.2f, 0.2f, 0.2f, 1.0f};
        float matShininess = 100.0f;

        materialChair = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE0);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/holz-struktur.jpg");


    }

    private void displayChairLegVL(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialChair.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialChair.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialChair.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialChair.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindVertexArray(vaoName[12]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairLegVR(GL3 gl) {
        gl.glBindVertexArray(vaoName[13]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelegVRVertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[13]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[13]);

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

    private void displayChairLegVR(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[13]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairLegHR(GL3 gl) {
        gl.glBindVertexArray(vaoName[14]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelegHRVertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[14]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[14]);

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

    private void displayChairLegHR(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[14]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairLegHL(GL3 gl) {
        gl.glBindVertexArray(vaoName[15]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelegHLVertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[15]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[15]);

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

    private void displayChairLegHL(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[15]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairSeat(GL3 gl) {
        gl.glBindVertexArray(vaoName[16]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelegseatVertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[16]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[16]);

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

    private void displayChairSeat(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[16]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairLean1(GL3 gl) {
        gl.glBindVertexArray(vaoName[17]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelean1Vertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[17]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[17]);

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

    private void displayChairLean1(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[17]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initChairLean2(GL3 gl) {
        gl.glBindVertexArray(vaoName[18]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileName);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawChair.makelean2Vertices(color0);
        int[] tableIndices = DrawChair.makeChairIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[18]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[18]);

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

    private void displayChairLean2(GL3 gl) {
        gl.glUseProgram(shaderProgram.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[18]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawChair.noOfIndicesForChair(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomWindowTop(GL3 gl) {
        gl.glBindVertexArray(vaoName[19]);
        shaderRoomWindow= new ShaderProgram(gl);
        shaderRoomWindow.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWindow);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoomWindow.WindowTopVerticices(color0);
        int[] tableIndices = DrawRoomWindow.makeRWIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[19]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[19]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 200.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE4);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/fenster.jpg");
    }

    private void displayRoomWindowTop(GL3 gl) {
        gl.glUseProgram(shaderRoomWindow.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, material0.getEmission(), 0);
        gl.glUniform4fv(7, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(8, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, material0.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());

        gl.glActiveTexture(GL_TEXTURE4);
        gl.glBindVertexArray(vaoName[19]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoomWindow.noOfIndicesForRW(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomWindowBottom(GL3 gl) {
        gl.glBindVertexArray(vaoName[20]);
        shaderRoomWindow= new ShaderProgram(gl);
        shaderRoomWindow.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWindow);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoomWindow.WindowBottomVerticices(color0);
        int[] tableIndices = DrawRoomWindow.makeRWIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[20]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[20]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 200.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE4);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/fenster.jpg");
    }

    private void displayRoomWindowBottom(GL3 gl) {
        gl.glUseProgram(shaderRoomWindow.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, material0.getEmission(), 0);
        gl.glUniform4fv(7, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(8, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, material0.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());

        gl.glActiveTexture(GL_TEXTURE4);
        gl.glBindVertexArray(vaoName[20]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoomWindow.noOfIndicesForRW(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomWindowLeft(GL3 gl) {
        gl.glBindVertexArray(vaoName[21]);
        shaderRoomWindow= new ShaderProgram(gl);
        shaderRoomWindow.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWindow);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoomWindow.WindowLeftVerticices(color0);
        int[] tableIndices = DrawRoomWindow.makeRWIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[21]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[21]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 200.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE4);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/fenster.jpg");
    }

    private void displayRoomWindowLeft(GL3 gl) {
        gl.glUseProgram(shaderRoomWindow.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, material0.getEmission(), 0);
        gl.glUniform4fv(7, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(8, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, material0.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());

        gl.glActiveTexture(GL_TEXTURE4);
        gl.glBindVertexArray(vaoName[21]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoomWindow.noOfIndicesForRW(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomWindowRight(GL3 gl) {
        gl.glBindVertexArray(vaoName[22]);
        shaderRoomWindow= new ShaderProgram(gl);
        shaderRoomWindow.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameWindow);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoomWindow.WindowRightVerticices(color0);
        int[] tableIndices = DrawRoomWindow.makeRWIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[22]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[22]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 200.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE4);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/fenster.jpg");
    }

    private void displayRoomWindowRight(GL3 gl) {
        gl.glUseProgram(shaderRoomWindow.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, material0.getEmission(), 0);
        gl.glUniform4fv(7, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(8, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, material0.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());

        gl.glActiveTexture(GL_TEXTURE4);
        gl.glBindVertexArray(vaoName[22]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoomWindow.noOfIndicesForRW(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initRoomWindowOutside(GL3 gl) {
        gl.glBindVertexArray(vaoName[23]);
        shaderSky= new ShaderProgram(gl);
        shaderSky.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameSky);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        float[] cubeVertices = DrawRoomWindow.WindowOutsideVerticices(color0);
        int[] tableIndices = DrawRoomWindow.makeRWIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[23]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[23]);

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

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 500.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE5);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/sky.jpg");
    }

    private void displayRoomWindowOutside(GL3 gl) {
        gl.glUseProgram(shaderSky.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, material0.getEmission(), 0);
        gl.glUniform4fv(7, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(8, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, material0.getSpecular(), 0);
        gl.glUniform1f(10, material0.getShininess());

        gl.glActiveTexture(GL_TEXTURE5);
        gl.glBindVertexArray(vaoName[23]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, DrawRoomWindow.noOfIndicesForRW(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initCandle01(GL3 gl) {
        gl.glBindVertexArray(vaoName[24]);
        shaderCandleRed= new ShaderProgram(gl);
        shaderCandleRed.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameCandleRed);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        candle1 = new DrawCandle(64);
        float[] cubeVertices = candle1.makeVertices(0.05f, 0.05f, 1f, 0.25f, 0.05f, color0);
        int[] tableIndices = candle1.makeIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[24]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[24]);

        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, tableIndices.length * 4,
                IntBuffer.wrap(tableIndices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 9*4, 9*4);

        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
        float matShininess = 500.0f;

        materialCandle = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE6);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Kerzenrot.jpg");
    }

    private void displayCandle01(GL3 gl) {
        gl.glUseProgram(shaderCandleRed.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialCandle.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialCandle.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialCandle.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialCandle.getSpecular(), 0);
        gl.glUniform1f(10, materialCandle.getShininess());

        gl.glActiveTexture(GL_TEXTURE6);
        gl.glBindVertexArray(vaoName[24]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, candle1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initCandle02(GL3 gl) {
        gl.glBindVertexArray(vaoName[25]);
        shaderCandleRed= new ShaderProgram(gl);
        shaderCandleRed.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameCandleRed);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        candle2 = new DrawCandle(64);
        float[] cubeVertices = candle2.makeVertices(0.04f, 0.04f, 1f, 0.2f, 0.05f, color0);
        int[] tableIndices = candle2.makeIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[25]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[25]);

        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, tableIndices.length * 4,
                IntBuffer.wrap(tableIndices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 9*4, 9*4);


    }

    private void displayCandle02(GL3 gl) {
        gl.glUseProgram(shaderCandleRed.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialCandle.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialCandle.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialCandle.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialCandle.getSpecular(), 0);
        gl.glUniform1f(10, materialCandle.getShininess());

        gl.glActiveTexture(GL_TEXTURE6);
        gl.glBindVertexArray(vaoName[25]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, candle2.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initCandleWick01(GL3 gl) {
        gl.glBindVertexArray(vaoName[26]);
        shaderCandleWick= new ShaderProgram(gl);
        shaderCandleWick.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameCandleWick);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        candleWick1 = new DrawCandle(64);
        float[] cubeVertices = candleWick1.makeVertices(0.005f, 0.005f, 1f, 0.27f, 0.05f, color0);
        int[] tableIndices = candleWick1.makeIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[26]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[26]);

        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, tableIndices.length * 4,
                IntBuffer.wrap(tableIndices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);


        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.3f, 0.3f, 0.3f, 1.0f};
        float matShininess = 500.0f;

        materialCandle = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE7);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Kerzendocht.jpg");


    }

    private void displayCandleWick01(GL3 gl) {
        gl.glUseProgram(shaderCandleWick.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialCandle.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialCandle.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialCandle.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialCandle.getSpecular(), 0);
        gl.glUniform1f(10, materialCandle.getShininess());

        gl.glActiveTexture(GL_TEXTURE7);
        gl.glBindVertexArray(vaoName[26]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, candleWick1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initCandleWick02(GL3 gl) {
        gl.glBindVertexArray(vaoName[27]);
        shaderCandleWick= new ShaderProgram(gl);
        shaderCandleWick.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameCandleWick);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        candleWick2 = new DrawCandle(64);
        float[] cubeVertices = candleWick2.makeVertices(0.005f, 0.005f, 1f, 0.21f, 0.05f, color0);
        int[] tableIndices = candleWick2.makeIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[27]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[27]);

        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, tableIndices.length * 4,
                IntBuffer.wrap(tableIndices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);


        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.4f, 0.4f, 0.4f, 1.0f};
        float[] matSpecular = {0.1f, 0.1f, 0.1f, 1.0f};
        float matShininess = 200.0f;

        materialCandle = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE7);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/Kerzendocht.jpg");


    }

    private void displayCandleWick02(GL3 gl) {
        gl.glUseProgram(shaderCandleWick.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialCandle.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialCandle.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialCandle.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialCandle.getSpecular(), 0);
        gl.glUniform1f(10, materialCandle.getShininess());

        gl.glActiveTexture(GL_TEXTURE7);
        gl.glBindVertexArray(vaoName[27]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, candleWick1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void initBullet(GL3 gl) {
        gl.glBindVertexArray(vaoName[28]);
        shaderBullet= new ShaderProgram(gl);
        shaderBullet.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameBullet);

        float[] color0 = {0.5f, 0.5f, 0.5f};
        bullet = new DrawBullet(64, 64);
        float[] bulletVertices = bullet.makeVertices(0.07f, color0);
        int[] bulletIndices = bullet.makeIndicesForTriangleStrip();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[28]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, bulletVertices.length * 4,
                FloatBuffer.wrap(bulletVertices), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[28]);

        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, bulletIndices.length * 4,
                IntBuffer.wrap(bulletIndices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);


        float[] matEmission = {0.1f, 0.1f, 0.1f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.7f, 0.7f, 0.7f, 1.0f};
        float[] matSpecular = {1f, 1f, 1f, 1.0f};
        float matShininess = 1000.0f;

        materialBullet = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glActiveTexture(GL_TEXTURE8);
        //texture
        texture = new LoadTexture();
        texture.loadTexture(gl, "resources/billiard.jpg");
    }

    private void displayBullet(GL3 gl) {
        gl.glUseProgram(shaderBullet.getShaderProgramID());

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(2, 1, light0.getPosition(), 0);
        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        gl.glUniform4fv(6, 1, materialBullet.getEmission(), 0);
        gl.glUniform4fv(7, 1, materialBullet.getAmbient(), 0);
        gl.glUniform4fv(8, 1, materialBullet.getDiffuse(), 0);
        gl.glUniform4fv(9, 1, materialBullet.getSpecular(), 0);
        gl.glUniform1f(10, materialBullet.getShininess());

        gl.glActiveTexture(GL_TEXTURE8);
        gl.glBindVertexArray(vaoName[28]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, bullet.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    /*private void initOBJ (GL3 gl) {
        gl.glBindVertexArray(vaoName[29]);
        shaderBullet= new ShaderProgram(gl);
        shaderBullet.loadShaderAndCreateProgram(shaderPath, vertexShaderFileName, fragmentShaderFileNameBullet);

        TriangleSurface surface = loadOBJVertexCoordinates(Paths.get("objData/probe.obj"));
        vertexCoordinates = surface.shape.vertices;

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[29]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexCoordinates.length * Float.BYTES,
                FloatBuffer.wrap(vertexCoordinates), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
    }*/

    /*private void displayOBJ(GL3 gl) {

        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[29]);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexCoordinates.length / 6);

    }
    */


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

    private static TriangleSurface loadOBJVertexCoordinates(Path path){
        try {
            List<TriangleObject> objects = new OBJLoader()
                    .useTextureCoordinates(false) // do not load uv coordinates
                    .useNormals(true) // as a replacement for vertex colors
                    .useStructures(false) // load everything into a single de.hshl.obj.objects, instead of separate objects
                    .useMaterials(false) // do not load any materials, which would divide our meshes into sub-meshes
                    .useIndexedVertices(false) // do not use indexed vertices but only a single vertices float array
                    .loadFromLocalResource(path);

            // An object contains surfaces
            TriangleObject object = objects.get(0);

            // A triangleSurface contains vertices and a material
            return object.surfaces.get(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
