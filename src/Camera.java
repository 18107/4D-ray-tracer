import org.lwjgl.input.Keyboard;

public class Camera {
	
	private static final float speed = 1.5f;

	public float x = 7.5f;
	public float y = 7.5f;
	public float z = 4.5f;
	public float w = 0.5f;
	
	public float rzy = 0;
	public float rzx = 0;
	public float ryw = 0;
	
	private long lastTime = System.nanoTime();
	
	public void update() {
		long time = System.nanoTime();
		float delta = (time - lastTime)/1000000000f;
		lastTime = time;
		
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
		
		x += X*speed*delta*(float)Math.cos(rzx) + Z*speed*delta*(float)Math.sin(rzx);
		z += Z*speed*delta*(float)Math.cos(rzx) - X*speed*delta*(float)Math.sin(rzx);
		y += Y*speed*delta;
		w += W*speed*delta;
		
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
