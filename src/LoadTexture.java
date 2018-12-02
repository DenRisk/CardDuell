import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

import static com.jogamp.opengl.GL.*;

public class LoadTexture {

    public void loadTexture (GL3 gl, String path) {

        Texture texture = null;
        String texturePath = path;
        try {
            File textureFile = new File(texturePath);
            texture = TextureIO.newTexture(textureFile, true);

            texture.setTexParameteri(gl, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
            texture.setTexParameteri(gl, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
            texture.setTexParameteri(gl, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
            texture.setTexParameteri(gl, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (texture != null)
            System.out.println("Texture loaded successfully from: " + texturePath);
        else
            System.err.println("Error loading texture.");
        System.out.println("  Texture height: " + texture.getImageHeight());
        System.out.println("  Texture width: " + texture.getImageWidth());
        System.out.println("  Texture object: " + texture.getTextureObject(gl));
        System.out.println("  Estimated memory size of texture: " + texture.getEstimatedMemorySize());

        texture.enable(gl);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texture.getTextureObject(gl));
    }

    public void deactivateTexture(GL3 gl) {
        gl.glBindTexture(GL_TEXTURE_2D, 0);
    }




}
