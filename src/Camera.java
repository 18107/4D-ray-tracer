import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Camera {
	
	private static final float moveSpeed = 2f;
	private static final float mouseSpeed = 0.005f;

	public float x = 7.5f;
	public float y = 7.5f;
	public float z = 7.5f;
	public float w = 7.5f;
	
	public float rzy = 0;
	public float rzx = 0;
	public float ryw = 0;
	
	private long lastTime = System.nanoTime();
	
	public void update() {
		long time = System.nanoTime();
		float delta = (time - lastTime)/1000000000f;
		lastTime = time;
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				if (Keyboard.getEventKeyState()) {
					Mouse.setGrabbed(!Mouse.isGrabbed());
					Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
				}
			}
		}
		
		if (Mouse.isGrabbed()) {
			rzx += Mouse.getDX()*mouseSpeed;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				ryw += Mouse.getDY()*mouseSpeed;
			} else {
				rzy += Mouse.getDY()*mouseSpeed;
			}
			if (rzx > Math.PI*2) rzx -= Math.PI*2;
			if (rzx < 0) rzx += Math.PI*2;
			if (rzy > Math.PI/2) rzy = (float) (Math.PI/2);
			if (rzy < -Math.PI/2) rzy = (float) (-Math.PI/2);
			if (ryw > Math.PI/2) ryw = (float) (Math.PI/2);
			if (ryw < -Math.PI/2) ryw = (float) (-Math.PI/2);
			
			int X = 0;
			int Y = 0;
			int Z = 0;
			int W = 0;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) Z--;
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) Z++;
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) X--;
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) X++;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) Y--;
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) Y++;
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) W--;
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) W++;
			
			x += X*moveSpeed*delta*(float)Math.cos(rzx) - Z*moveSpeed*delta*(float)Math.sin(rzx);
			z += Z*moveSpeed*delta*(float)Math.cos(rzx) + X*moveSpeed*delta*(float)Math.sin(rzx);
			y += Y*moveSpeed*delta;
			w += W*moveSpeed*delta;
			
			if (x < 0.01f) x = 0.01f;
			if (x > 15.99f) x = 15.99f;
			if (y < 0.01f) y = 0.01f;
			if (y > 15.99f) y = 15.99f;
			if (z < 0.01f) z = 0.01f;
			if (z > 15.99f) z = 15.99f;
			if (w < 0.01f) w = 0.01f;
			if (w > 15.99f) w = 15.99f;
		}
	}
}
