import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Main {
	
	private static void init(int width, int height, ShaderManager shaderManager) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("4D rt sh");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		shaderManager.createShaderProgram();
	}
	
	private static void draw(int shaderProgram, Camera camera, int maxDepth) {
		GL20.glUseProgram(shaderProgram);
		camera.update();
		int cameraPositionUniform = GL20.glGetUniformLocation(shaderProgram, "cameraPos");
		GL20.glUniform4f(cameraPositionUniform, camera.x, camera.y, camera.z, camera.w);
		int cameraRotationUniform = GL20.glGetUniformLocation(shaderProgram, "cameraRot");
		GL20.glUniform4f(cameraRotationUniform, camera.rzy, camera.rzx, camera.rxw, camera.rzw);
		int backgroundUniform = GL20.glGetUniformLocation(shaderProgram, "backgroundColor");
		GL20.glUniform4f(backgroundUniform, 0.529411765f, 0.807843137f, 0.921568627f, 0);
		int maxDepthUniform = GL20.glGetUniformLocation(shaderProgram, "maxDepth");
		GL20.glUniform1i(maxDepthUniform, maxDepth);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(-1, 1);
		GL11.glVertex2f(1, 1);
		GL11.glVertex2f(1, -1);
		GL11.glVertex2f(-1, -1);
		GL11.glEnd();
		GL20.glUseProgram(0);
	}
	
	private static void run(int shaderProgram, int fpsMax, int maxDepth) {
		Camera camera = new Camera();
		//long startTime;
		//long endTime;
		while (!Display.isCloseRequested()) {
			//startTime = System.nanoTime();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			draw(shaderProgram, camera, maxDepth);
			Display.sync(fpsMax);
			Display.update();
			//endTime = System.nanoTime();
			//System.out.println(1000000000/(endTime - startTime));
		}
	}
	
	private static void end(ShaderManager sm) {
		sm.deleteShaderProgram();
		Display.destroy();
	}

	public static void main(String[] args) {
		int width = 1600;
		int height = 800;
		int fpsMax = 60;
		int maxDepth = 100;
		ShaderManager shaderManager = new ShaderManager();
		try {
			init(width, height, shaderManager);
			run(shaderManager.getProgram(), fpsMax, maxDepth);
		} finally {
			end(shaderManager);
		}
	}
}
