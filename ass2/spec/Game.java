package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
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

import com.jogamp.newt.event.InputEvent;
import com.jogamp.opengl.util.FPSAnimator;

import ass2.objects.*;



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
    private static final int NUM_TEXTURES = 5;
    private Texture[] myTextures;
    private TerrainGameObject terrain;
    private CubeObject[] cubes = new CubeObject[]{};
    private SphereObject[] spheres = new SphereObject[]{};
    private float[] mySun;
    
    private static final double myHeight 	= 1.2;
    
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
		  
		  terrain = new TerrainGameObject(GameObject.ROOT, myTerrain);
//		  drawWorldObjects();
//		  drawTrees(myTerrain.trees());
		  
		  
//		  terrain.translate(5, 0, 5);
		  
		  myCamera = new Camera(GameObject.ROOT);
		  myCamera.translate(0, 0.5, 0);
		  myCamera.scale(2);
		  myCamera.rotate(new double[]{0,-90,0});	//face in the +x direction
		  myCamera.setBackground(new float[]{1f,1f,1f,1f});

		  // Add the keyListener
		  panel.addKeyListener(this);
		  
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();
          myCamera.setPosition(8, myHeight, 1);
          myCamera.setRotation(new double[]{0,-270,0});
          getContentPane().add(panel);
          setSize(640, 480);        
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
//        GameObject axes = new Axes(GameObject.ROOT);
//        axes.scale(2);
        
//        SphereObject sphere = new SphereObject(GameObject.ROOT);
//        sphere.translate(0, 2, 0);
//        sphere.scale(4);
//        spheres = new SphereObject[]{sphere};
    }
        
    private void update() {
        // compute the time since the last frame
        long time = System.currentTimeMillis();
        double dt = (time - myTime) / 1000.0;
        myTime = time;
        mySun[0] = (myTerrain.getSunlight()[0] + (float)Math.sin(time/1000.0  *Math.PI*2))*(-100);
        mySun[1] = (myTerrain.getSunlight()[1] + (float)Math.sin(time/1000.0  *Math.PI*2))*(-100);
        mySun[2] = (myTerrain.getSunlight()[2] + (float)Math.sin(time/1000.0  *Math.PI*2))*(-100);
        System.out.printf("x = %f, y= %f\n", mySun[0], mySun[1]);
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
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION , mySun, 0);
		float[] ambient = new float[]{0.0f,0.0f,0.0f,0.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, ambient, 0);
        // Add textures to objects
        terrain.setTexture(myTextures[1]);
        terrain.setTreeTextures(myTextures[3], myTextures[2]);
        terrain.setRoadTexture(myTextures[4]);
        
        for (CubeObject c: cubes) {
        	c.setTexture(myTextures[1]);
        }
        for (SphereObject s: spheres) {
        	s.setTexture(myTextures[0]);
        }

        GameObject.ROOT.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
//		drawable.destroy();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		myTime = System.currentTimeMillis();
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glFrontFace( GL2.GL_CCW);
		gl.glPolygonOffset(1.0f, 1.0f);
		// Big points for debugging
		gl.glPointSize(5.0f);
		myTextures = new Texture[NUM_TEXTURES];
		String filename = "./textures/grass_texture.png";
		myTextures[0] = new Texture(gl, filename, "png", true);
		filename = "./textures/grass.png";
		myTextures[1] = new Texture(gl, filename, "png", true);
		filename = "./textures/wood.png";
		myTextures[2] = new Texture(gl, filename, "png", true);
		filename = "./textures/branches.png";
		myTextures[3] = new Texture(gl, filename, "png", true);
		filename = "./textures/road.png";
		myTextures[4] = new Texture(gl, filename, "png", true);
		
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
		
		gl.glEnable(GL2.GL_LIGHTING);
		// Specified as a direction
		mySun = new float[]{myTerrain.getSunlight()[0], myTerrain.getSunlight()[1], myTerrain.getSunlight()[2], 0};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION , mySun, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        
		gl.glEnable(GL2.GL_LIGHT0);
		
    	// Material property vectors.

    	float matAmbAndDif2[] = {0f, 0f, 0f, 0f};
    	float matSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    	float matShine[] = { 0.0f };
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
		double speed = 1;
		if (e.getModifiers() == InputEvent.SHIFT_MASK) {
			speed = 5;
		}
//		System.out.println(speed);
		myCamera.setTransSpeed(speed);
		myCamera.setRotSpeed(speed);
		switch(key) {
			case KeyEvent.VK_UP   : myCamera.enableMovement(); myCamera.setTransDirection(-1);	break;
			case KeyEvent.VK_DOWN : myCamera.enableMovement(); myCamera.setTransDirection(1); break;
			case KeyEvent.VK_LEFT : myCamera.enableTurning(); myCamera.setRotDirection(1);	break;
			case KeyEvent.VK_RIGHT: myCamera.enableTurning(); myCamera.setRotDirection(-1); break;
			case KeyEvent.VK_F	  : myCamera.togglePerspective();
		}
		double xdim, zdim;
		double[] my_pos = myCamera.getGlobalPosition();
		double[] t_pos = terrain.getGlobalPosition();
		xdim = myTerrain.size().getWidth();
		zdim = myTerrain.size().getHeight();
		// Am I in the terrain?
//		System.out.println(my_pos[1]);
//		System.out.printf("mpx = %f, mpz = %f, tpx = %f, tpz = %f\n", my_pos[0], my_pos[2], t_pos[0], t_pos[2]);
		if ((my_pos[0] > t_pos[0] && my_pos[0] < t_pos[0] + xdim) &&
			(my_pos[2] > t_pos[2] && my_pos[2] < t_pos[2] + zdim)) {
			myCamera.setPosition(my_pos[0], myTerrain.altitude(my_pos[0], my_pos[2]) + myHeight, my_pos[2]);
//			System.out.printf("interp = %f\n", myTerrain.altitude(my_pos[0], my_pos[2]));
		} else {
			myCamera.setPosition(my_pos[0], myHeight, my_pos[2]);
		}
//		System.out.println(myCamera.getGlobalPosition()[1]);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		double speed = 1;
		if (e.getModifiers() == InputEvent.SHIFT_MASK) {
			speed = 5;
		}
		myCamera.setTransSpeed(speed);
		myCamera.setRotSpeed(speed);
		switch(key) {
			case KeyEvent.VK_UP   : 
			case KeyEvent.VK_DOWN : myCamera.disableMovement(); break;
			case KeyEvent.VK_LEFT : 
			case KeyEvent.VK_RIGHT: myCamera.disableTurning(); break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
