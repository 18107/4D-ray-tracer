import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

public class Camera {
	
	private static final float moveSpeed = 2f;
	private static final float mouseSpeed = 0.005f;

	public float x = 7.5f;
	public float y = 7.5f;
	public float z = 7.5f;
	public float w = 7.5f;
	
	public float rzy = 0;
	public float rzx = 0;
	public float rxw = 0;
	public float rzw = 0;
	
	private long lastTime = System.nanoTime();
	
	private Hitbox hitbox = new Hitbox(0.2f, 0.99f, 0.2f, 0.2f);
	
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
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				rxw += Mouse.getDX()*mouseSpeed;
				rzw += Mouse.getDY()*mouseSpeed;
			} else {
				rzx += Mouse.getDX()*mouseSpeed;
				rzy += Mouse.getDY()*mouseSpeed;
			}
			if (rzx > Math.PI*2) rzx -= Math.PI*2;
			if (rzx < 0) rzx += Math.PI*2;
			if (rzy > Math.PI/2) rzy = (float) (Math.PI/2);
			if (rzy < -Math.PI/2) rzy = (float) (-Math.PI/2);
			if (rxw > Math.PI/2) rxw = (float) (Math.PI/2);
			if (rxw < -Math.PI/2) rxw = (float) (-Math.PI/2);
			if (rzw > Math.PI/2) rzw = (float) (Math.PI/2);
			if (rzw < -Math.PI/2) rzw = (float) (-Math.PI/2);
			
			if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
				rxw *= 0.94f;
				rzw *= 0.94f;
			}
			
			int X = 0;
			int Y = 0;
			int Z = 0;
			int W = 0;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) Z--;
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) Z++;
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) X--;
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) X++;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) Y--;
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) Y++;
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) W--;
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) W++;
			
			Vector4f dirx = new Vector4f(1, 0, 0, 0);
			Vector4f diry = new Vector4f(0, 1, 0, 0);
			Vector4f dirz = new Vector4f(0, 0, 1, 0);
			Vector4f dirw = new Vector4f(0, 0, 0, 1);
			rotate(dirx);
			rotate(diry);
			rotate(dirz);
			rotate(dirw);
			
			hitbox.move(this,
					moveSpeed*delta* (X*dirx.x + Y*diry.x + Z*dirz.x + W*dirw.x),
					moveSpeed*delta* (X*dirx.y + Y*diry.y + Z*dirz.y + W*dirw.y),
					moveSpeed*delta* (X*dirx.z + Y*diry.z + Z*dirz.z + W*dirw.z),
					moveSpeed*delta* (X*dirx.w + Y*diry.w + Z*dirz.w + W*dirw.w));
			//x += moveSpeed*delta* (X*dirx.x + Y*diry.x + Z*dirz.x + W*dirw.x);
			//y += moveSpeed*delta* (X*dirx.y + Y*diry.y + Z*dirz.y + W*dirw.y);
			//z += moveSpeed*delta* (X*dirx.z + Y*diry.z + Z*dirz.z + W*dirw.z);
			//w += moveSpeed*delta* (X*dirx.w + Y*diry.w + Z*dirz.w + W*dirw.w);
			
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
	
	private void rotate(Vector4f vector) {
		float t1;
		float t2;
		
		t1 = cos(rzx)*vector.x + sin(rzx)*vector.z;
		t2 = cos(rzx)*vector.z - sin(rzx)*vector.x;
		vector.x = t1;
		vector.z = t2;
		
		t1 = cos(rzw)*vector.z + sin(rzw)*vector.w;
		t2 = cos(rzw)*vector.w - sin(rzw)*vector.z;
		vector.z = t1;
		vector.w = t2;
		
		t1 = cos(rxw)*vector.x - sin(rxw)*vector.w;
		t2 = cos(rxw)*vector.w + sin(rxw)*vector.x;
		vector.x = t1;
		vector.w = t2;
	}
	
	private float sin(float angle) {
		return (float)Math.sin(angle);
	}
	
	private float cos(float angle) {
		return (float)Math.cos(angle);
	}
}
