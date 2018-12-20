/**
 * /**
 * @modified by Denis Niklas Risken
 * Die Methoden der Klasse habe ich übernommen aus der Datei JoglShapesPP.zip, die sich auf der Lernplattform befindet.
 * Bei der Initialisierung der Kerzen habe ich eigenständig Größe und Position überlegt und in die Szene eingebaut (siehe StartRendererPP)
 *
 * @author Karsten Lehn
 * @version 21.10.2017, 27.10.2017
 *
 */

public class DrawCandle {
    private int horizontalResolution;
    private int noOfIndices;

    public DrawCandle(int horizontalResolution) {
        this.horizontalResolution = horizontalResolution;
        noOfIndices = noOfIndicesForCone();
    }

    public float[] makeVertices(float radiusT, float radiusB, float lengthV, float yT, float yB, float[] color) {

        // vertices for the top and bottom circles are duplicated
        // for correct normal vector orientation
        int noOfComponents = 3 + 3 + 3; // 3 position coordinates, 3 color coordinates, 3 normal coordinates
        float[] vertices = new float[(1 + (4 * horizontalResolution) + 1) * noOfComponents];
        int vertexNumberInc = 3 + 3 + 3; // three position coordinates, three color values, three normal coordinates
        int vertexNumber = 0; // initialize vertex count

        float radiusTop = radiusT;
        float radiusBottom = radiusB;
        float length = lengthV;
        // y Coordinate of top circle
        float yTop = yT;
        // y Coordinate of bottom circle
        float yBottom = yB;
        // normal vector for top circle
        float[] topNormal = {0, 1, 0};
        // top center of circle
        vertices[vertexNumber] = 0f;
        vertices[vertexNumber+1] = yTop;
        vertices[vertexNumber+2] = 0f;
        // color coordinates (for all vertices the same)
        vertices[vertexNumber+3] = color[0];
        vertices[vertexNumber+4] = color[1];
        vertices[vertexNumber+5] = color[2];
        // normal vector coordinates
        vertices[vertexNumber+6] = topNormal[0];
        vertices[vertexNumber+7] = topNormal[1];
        vertices[vertexNumber+8] = topNormal[2];
        vertexNumber += vertexNumberInc;

        // vertices for the top circle
        float angleTop = 0;
        float angleTopInc = (float) (2 * Math.PI / horizontalResolution);
        for (int angleIndex = 0; angleIndex < horizontalResolution; angleIndex++) {
            // position coordinates
            vertices[vertexNumber] = radiusTop * (float) (Math.cos(angleTop));
            vertices[vertexNumber + 1] = yTop;
            vertices[vertexNumber + 2] = radiusTop * (float) Math.sin(angleTop);
            // color coordinates (for all vertices the same)
            vertices[vertexNumber + 3] = color[0];
            vertices[vertexNumber + 4] = color[1];
            vertices[vertexNumber + 5] = color[2];
            // normal vector coordinates
            vertices[vertexNumber+6] = topNormal[0];
            vertices[vertexNumber+7] = topNormal[1];
            vertices[vertexNumber+8] = topNormal[2];
            vertexNumber += vertexNumberInc;
            angleTop += angleTopInc;
        }

        // vertices for the top edge of the surface
        angleTop = 0;
        angleTopInc = (float) (2 * Math.PI / horizontalResolution);
        // y component of normal vector coordinates for top edge of surface
        float yNormalTop = radiusTop * (radiusBottom - radiusTop) / length;
        for (int angleIndex = 0; angleIndex < horizontalResolution; angleIndex++) {
            // position coordinates
            float xPos = radiusTop * (float) (Math.cos(angleTop));
            float yPos = yTop;
            float zPos = radiusTop * (float) Math.sin(angleTop);
            vertices[vertexNumber] = xPos;
            vertices[vertexNumber + 1] = yPos;
            vertices[vertexNumber + 2] = zPos;

            float normalizationFactor =
                    1 / (float) Math.sqrt((xPos * xPos) + (yNormalTop * yNormalTop) + (zPos * zPos));
            vertices[vertexNumber+6] = xPos * normalizationFactor;
            vertices[vertexNumber+7] = 0f;
            vertices[vertexNumber+8] = zPos * normalizationFactor;
            vertexNumber += vertexNumberInc;
            angleTop += angleTopInc;
        }

        // vertices for the bottom edge of the surface
        float angleBottom = 0;
        float angleBottomInc = (float) (2 * Math.PI / horizontalResolution);
        // y component of normal vector coordinates for bottom edge of surface
        float yNormalBottom = radiusBottom * (radiusBottom - radiusTop) / length;
        for (int angleIndex = 0; angleIndex < horizontalResolution; angleIndex++) {
            // position coordinates
            float xPos = radiusBottom * (float) (Math.cos(angleBottom));
            float yPos = yBottom;
            float zPos = radiusBottom * (float) Math.sin(angleBottom);
            vertices[vertexNumber] = xPos;
            vertices[vertexNumber + 1] = yPos;
            vertices[vertexNumber + 2] = zPos;

            // normalize normal vector
            float normalizationFactor =
                    1 / (float) Math.sqrt((xPos * xPos) + (yNormalBottom * yNormalBottom) + (zPos * zPos));
            vertices[vertexNumber+5] = xPos * normalizationFactor;
            vertices[vertexNumber+6] = 0f;
            vertices[vertexNumber+7] = zPos * normalizationFactor;
            vertexNumber += vertexNumberInc;
            angleBottom += angleBottomInc;
        }

        // vertices for the bottom circle
        // normal vector for bottom circle
        float[] bottomNormal = {0, -1, 0};
        angleBottom = 0;
        angleBottomInc = (float) (2 * Math.PI / horizontalResolution);
        for (int angleIndex = 0; angleIndex < horizontalResolution; angleIndex++) {
            // position coordinates
            vertices[vertexNumber] = radiusBottom * (float) (Math.cos(angleBottom));
            vertices[vertexNumber + 1] = yBottom;
            vertices[vertexNumber + 2] = radiusBottom * (float) Math.sin(angleBottom);

            vertexNumber += vertexNumberInc;
            angleBottom += angleBottomInc;
            // normal vector coordinates
            vertices[vertexNumber+6] = bottomNormal[0];
            vertices[vertexNumber+7] = bottomNormal[1];
            vertices[vertexNumber+8] = bottomNormal[2];
        }

        // bottom center of circle
        vertices[vertexNumber] = 0f;
        vertices[vertexNumber+1] = yBottom;
        vertices[vertexNumber+2] = 0f;

        // normal vector coordinates
        vertices[vertexNumber+6] = bottomNormal[0];
        vertices[vertexNumber+7] = bottomNormal[1];
        vertices[vertexNumber+8] = bottomNormal[2];

        return vertices;
    }

    public int[] makeIndicesForTriangleStrip() {

        // Indices to refer to the number of the cone (frustum) vertices
        // defined in makeVertices()
        int[] indices = new int[noOfIndices];

        // BEGIN: Indices for top circle
        int topCenterIndex = 0;
        int firstTopCircleEdgeIndex = 1;
        int lastTopCircleEdgeIndex = horizontalResolution;

        int index = 0;
        // first index, center of top circle
        // draw twice to get the front face orientation right
        indices[index] = topCenterIndex;
        index++;
        indices[index] = topCenterIndex;
        index++;

        for (int hIndex = 1; hIndex <= horizontalResolution; hIndex++) {
            indices[index] = hIndex;
            index++;
            indices[index] = topCenterIndex;
            index++;
        }
        // close the top circle with a final triangle
        indices[index] = firstTopCircleEdgeIndex;
        index++;
        // END: Indices for top circle

        // BEGIN: Indices for surface
        int firstSurfaceTopIndex = horizontalResolution + 1;
        int firstSurfaceBottomIndex = (2 * horizontalResolution) + 1;
        for (int hIndex = 0; hIndex < horizontalResolution; hIndex++) {
            indices[index] = firstSurfaceTopIndex + hIndex;
            index++;
            indices[index] = firstSurfaceBottomIndex + hIndex;
            index++;
        }
        // Close the surface
        indices[index] = firstSurfaceTopIndex;
        index++;
        indices[index] = firstSurfaceBottomIndex;
        index++;
        // END: Indices for surface

        // BEGIN: Indices for bottom circle
        int bottomCenterIndex = (4 * horizontalResolution) + 1;
        int firstBottomCircleEdgeIndex = (3 * horizontalResolution) + 1;

        // picking up from surface
        indices[index] = firstBottomCircleEdgeIndex;
        index++;
        // first index, center of top circle
        indices[index] = bottomCenterIndex;
        index++;

        for (int hIndex = 0; hIndex < horizontalResolution; hIndex++) {
            indices[index] = firstBottomCircleEdgeIndex + hIndex;
            index++;
            indices[index] = bottomCenterIndex;
            index++;
        }
        // close the top circle with a final triangle
        indices[index] = firstBottomCircleEdgeIndex;
        return indices;
    }

    private int noOfIndicesForCone() {
        int noOfIndicesForCircle =
                1 + // center of the circle
                        // additional vertices for drawing with TRIANGLE_STRIP instead of TRIANGLE_FAN
                        (2 * horizontalResolution) +
                        1; // closing the circle

        return  1+ // reverse back/front faces
                noOfIndicesForCircle + // top circle
                (2 * horizontalResolution) +  // surface
                2 + // close the surface
                1 + // picking up the bottom circle
                noOfIndicesForCircle;// bottom circle
    }

    public int getNoOfIndices() {
        return noOfIndices;
    }
}
