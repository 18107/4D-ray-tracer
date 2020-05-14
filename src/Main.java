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
		
		GL11.glViewport(0, 0, width, height);
		//TODO remove
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-1, 1, -1, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glClearColor(0, 0, 0, 1);
		
		shaderManager.createShaderProgram();
	}
	
	private static void draw(int shaderProgram, Camera camera) {
		GL20.glUseProgram(shaderProgram);
		camera.update();
		int cameraPositionUniform = GL20.glGetUniformLocation(shaderProgram, "cameraPos");
		GL20.glUniform4f(cameraPositionUniform, camera.x, camera.y, camera.z, camera.w);
		int cameraRotationUniform = GL20.glGetUniformLocation(shaderProgram, "cameraRot");
		GL20.glUniform3f(cameraRotationUniform, camera.rzy, camera.rzx, camera.ryw);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(-1, 1);
		GL11.glVertex2f(1, 1);
		GL11.glVertex2f(1, -1);
		GL11.glVertex2f(-1, -1);
		GL11.glEnd();
		GL20.glUseProgram(0);
	}
	
	private static void run(int fpsMax, int shaderProgram) {
		Camera camera = new Camera();
		while (!Display.isCloseRequested()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			draw(shaderProgram, camera);
			Display.sync(fpsMax);
			Display.update();
		}
	}
	
	private static void end(ShaderManager sm) {
		sm.deleteShaderProgram();
		Display.destroy();
	}

	public static void main(String[] args) {
		int width = 800;
		int height = 600;
		int fpsMax = 60;
		ShaderManager shaderManager = new ShaderManager();
		try {
			init(width, height, shaderManager);
			run(fpsMax, shaderManager.getProgram());
		} finally {
			end(shaderManager);
		}
	}
}
