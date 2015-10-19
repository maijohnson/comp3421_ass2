package ass2.spec;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class TerrainGameObject extends GameObject {
	private Terrain myTerrain;
	private Mesh myMesh;
	public TerrainGameObject(GameObject parent) {
		super(parent);
	}
	
	public void setTerrain(Terrain t) {
		myTerrain = t;
	}
	
	public void generateMesh(Terrain t) {
		Mesh m = new Mesh();
		int width = (int)t.size().getWidth();
		int height = (int)t.size().getHeight();
		
		// Add all the vertices and normals
		for (int i = 0; i < t.size().getWidth(); i++) {
			for (int j = 0; j < t.size().getHeight(); j++) {
				m.addVertex(new double[]{i, t.getGridAltitude(i, j), j});
			}
		}
		
		// TODO: Add all the normals
		m.addNormal(new double[]{0, 0, 0});
		
		// Add all the faces
		for (int i = 0; i < t.size().getWidth(); i++) {
			for (int j = 0; j < t.size().getHeight(); j++) {
				m.addFace(new int[]{(int) (i*width+j), 
									(int) (Integer.min(i+1, width-1)*width+j), 
									(int) (i*width+Integer.min(j+1, height-1))}, 
									// Face normal
									0);
			}
		}
		myMesh = m;
	}
	
	@Override 
	public void draw(GL2 gl){
		if (myTerrain == null) {
			System.out.println("ERR: Terrain not set");
			return;
		}

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        // Transform the object before drawing it
        gl.glTranslated(getPosition()[0], getPosition()[1], getPosition()[2]);
		gl.glRotated(getRotation()[0], 1, 0, 0);
		gl.glRotated(getRotation()[1], 0, 1, 0);
		gl.glRotated(getRotation()[2], 0, 0, 1);
		gl.glScaled(getScale(), getScale(), getScale());
		gl.glLightModelfv(
				GL2.GL_LIGHT_MODEL_AMBIENT, myTerrain.getSunlight(), 0);
        
//		GLProfile glp = GLProfile.getDefault();
//		TextureData texture= null;
//		String filename = "./textures/grass_texture.png";
//		try {
//			texture = TextureIO.newTextureData(glp,new File(filename),true, "png");
//		} catch (IOException exc) {
//			System.err.println(filename);
//            exc.printStackTrace();
//            System.exit(1);
//		}
//		
		myMesh.draw(gl, null);
		
	}

}
