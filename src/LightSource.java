
public class LightSource {
    private float[] position;
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;
    private float[] lightSpot;

    public LightSource(float[] position, float[] ambient, float[] diffuse, float[] specular) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public LightSource(float[] position, float[] ambient, float[] diffuse, float[] specular, float[] lightSpot) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.lightSpot = lightSpot;
    }

    public float[] getPosition() {
        return position;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public void setAmbient(float[] ambient) {
        this.ambient = ambient;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float[] diffuse) {
        this.diffuse = diffuse;
    }

    public float[] getSpecular() {
        return specular;
    }

    public void setSpecular(float[] specular) {
        this.specular = specular;
    }

    public float[] getLightSpot() {
        return lightSpot;
    }

    public void setLightSpot(float[] lightSpot) {
        this.lightSpot = lightSpot;
    }


}
