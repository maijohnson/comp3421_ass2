package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private Terrain myTerrain;
    private static Camera myCamera;
    private long myTime;
    private static final int NUM_TEXTURES = 2;
    private Texture[] myTextures;
    private TerrainGameObject terrain;
    private CubeObject[] cubes;
    
    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
    }
    
    public void setCamera(Camera camera) {
    	myCamera = camera;
    }
    
    /** 
     * Run the game.
     */
    public void run() {
		  GLProfile glp = GLProfile.getDefault();
		  GLCapabilities caps = new GLCapabilities(glp);
		  GLJPanel panel = new GLJPanel(caps);
		  panel.addGLEventListener(this);
		  
		  terrain = new TerrainGameObject(GameObject.ROOT);
		  terrain.setTerrain(myTerrain);
		  terrain.generateMesh(myTerrain);
		          
		  
		  drawWorldObjects();
		  drawTrees(myTerrain.trees());
		  
		  
		  terrain.translate(5, 0, 5);
		  
		  myCamera = new Camera(GameObject.ROOT);
		  myCamera.translate(0, 0.5, 0);
		  myCamera.scale(2);
		  myCamera.setBackground(new float[]{1f,1f,1f,1f});

		  // Add the keyListener
		  panel.addKeyListener(this);
		  
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 600);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
    	game.run();
    }
    
    public void drawWorldObjects() {
    	CubeObject cubeFront = new CubeObject(GameObject.ROOT);
    	CubeObject cubeBack = new CubeObject(GameObject.ROOT);
    	CubeObject cubeLeft = new CubeObject(GameObject.ROOT);
    	CubeObject cubeRight = new CubeObject(GameObject.ROOT);
        cubeFront.translate(0, 0, -3);
        cubeBack.translate(0, 0, 3);
        cubeLeft.translate(-3, 0, 0);
        cubeRight.translate(3, 0, 0);
        cubes = new CubeObject[]{cubeFront, cubeBack, cubeLeft, cubeRight};
        GameObject axes = new Axes(GameObject.ROOT);
        axes.scale(2);
    }
        
    public void drawTrees(List<Tree> t) {
    	
    }
    
    private void update() {
        // compute the time since the last frame
        long time = System.currentTimeMillis();
        double dt = (time - myTime) / 1000.0;
        myTime = time;
        
        // take a copy of the ALL_OBJECTS list to avoid errors 
        // if new objects are created in the update
        List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
        
        // update all objects
        for (GameObject g : objects) {
            g.update(dt);
        }        
    }
    
	@Override
	public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        myCamera.setView(gl);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
        update();
        // Add textures to objects
        terrain.setTexture(myTextures[1]);
        for (CubeObject c: cubes) {
        	c.setTexture(myTextures[1]);
        }
        GameObject.ROOT.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		myTime = System.currentTimeMillis();
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glFrontFace( GL2.GL_CCW);
		myTextures = new Texture[NUM_TEXTURES];
		String filename = "./textures/grass_texture.png";
		myTextures[0] = new Texture(gl, filename, "png", false);
		filename = "./textures/grass.png";
		myTextures[1] = new Texture(gl, filename, "png", false);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_LIGHTING);
		float[] sunDir = new float[]{myTerrain.getSunlight()[0], myTerrain.getSunlight()[1], myTerrain.getSunlight()[2], 0};
		gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_POSITION , sunDir, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        
		gl.glEnable(GL2.GL_LIGHT0);
		
    	// Material property vectors.

    	float matAmbAndDif2[] = {0.0f, 0.9f, 0.0f, 1.0f};
    	float matSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    	float matShine[] = { 50.0f };
//
//    	// Material property vectors.
    	float matAmbAndDif1[] = {1.0f, 1.0f, 1.0f, 1.0f};
//
//    	// Material properties.
    	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif1,0);
    	gl.glMaterialfv(GL2.GL_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif2,0);
    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, matSpec,0);
    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, matShine,0);
//
//    	// Specify how texture values combine with current surface color values.
    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE); 

    	// Turn on OpenGL texturing.
    	gl.glEnable(GL2.GL_TEXTURE_2D);
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		myCamera.reshape(gl, x, y, width, height);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
			case KeyEvent.VK_UP   : myCamera.translate(0, 0, -0.1); break;
			case KeyEvent.VK_DOWN : myCamera.translate(0, 0, 0.1); break;
			case KeyEvent.VK_LEFT : myCamera.rotate(new double[]{0,  5, 0}); break;
			case KeyEvent.VK_RIGHT: myCamera.rotate(new double[]{0, -5, 0}); break;
			case KeyEvent.VK_B	  : myCamera.scale(2); break;
			case KeyEvent.VK_S	  : myCamera.scale(0.5); break;

//			case KeyEvent.VK_SPACE: swap_texture = !swap_texture; break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
