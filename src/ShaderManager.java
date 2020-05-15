import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

public class ShaderManager {

	private int vertexShader;
	private int fragmentShader;
	private int program;
	
	private int chunkBuffer;
	
	private float array[] = new float[16*16*16*16*4];
	
	private void setBlock(int x, int y, int z, int w, float r, float g, float b, float a) {
		int id = w*16*16*16*4 + z*16*16*4 + y*16*4 + x*4;
		array[id] = r;
		array[id+1] = g;
		array[id+2] = b;
		array[id+3] = a;
	}
	
	private void setupWorld() {
		Random rand = new Random(18107);
		for (int w = 0; w < 16; w++) {
			for (int y = 0; y < 16; y++) {
				for (int x = 0; x < 16; x++) {
					int r = rand.nextInt(2);
					int g = rand.nextInt(2);
					int b = rand.nextInt(2);
					int a = (rand.nextInt(10) != 0 || (r == 0 && g == 0 && b == 0)) ? 0 : 1;
					setBlock(x, y, 1, w, r, g, b, a);
				}
			}
		}
		
		//draw xy plane
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				setBlock(x, y, 0, 0, 0, y/15f, 0.5f, 1);
				setBlock(x, y, 15, 0, 0, y/15f, 1, 1);
				setBlock(x, y, 0, 15, 1, y/15f, 0.5f, 1);
				setBlock(x, y, 15, 15, 1, y/15f, 1, 1);
			}
		}
		//draw xz plane
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				setBlock(x, 0, z, 0, 0, 0, 0.5f+z/30f, 1);
				setBlock(x, 15, z, 0, 0, 1, 0.5f+z/30f, 1);
				setBlock(x, 0, z, 15, 1, 0, 0.5f+z/30f, 1);
				setBlock(x, 15, z, 15, 1, 1, 0.5f+z/30f, 1);
			}
		}
		//draw xw plane
		for (int x = 0; x < 16; x++) {
			for (int w = 0; w < 16; w++) {
				setBlock(x, 0, 0, w, w/15f, 0, 0.5f, 1);
				setBlock(x, 15, 0, w, w/15f, 1, 0.5f, 1);
				setBlock(x, 0, 15, w, w/15f, 0, 1, 1);
				setBlock(x, 15, 15, w, w/15f, 1, 1, 1);
			}
		}
		//draw yz plane
		for (int y = 0; y < 16; y++) {
			for (int z = 0; z < 16; z++) {
				setBlock(0, y, z, 0, 0, y/15f, 0.5f+z/30f, 1);
				setBlock(15, y, z, 0, 0, y/15f, 0.5f+z/30f, 1);
				setBlock(0, y, z, 15, 1, y/15f, 0.5f+z/30f, 1);
				setBlock(15, y, z, 15, 1, y/15f, 0.5f+z/30f, 1);
			}
		}
		//draw yw plane
		for (int y = 0; y < 16; y++) {
			for (int w = 0; w < 16; w++) {
				setBlock(0, y, 0, w, w/15f, y/15f, 0.5f, 1);
				setBlock(15, y, 0, w, w/15f, y/15f, 0.5f, 1);
				setBlock(0, y, 15, w, w/15f, y/15f, 1, 1);
				setBlock(15, y, 15, w, w/15f, y/15f, 1, 1);
			}
		}
		//draw zw plane
		for (int z = 0; z < 16; z++) {
			for (int w = 0; w < 16; w++) {
				setBlock(0, 0, z, w, w/15f, 0, 0.5f+z/30f, 1);
				setBlock(15, 0, z, w, w/15f, 0, 0.5f+z/30f, 1);
				setBlock(0, 15, z, w, w/15f, 1, 0.5f+z/30f, 1);
				setBlock(15, 15, z, w, w/15f, 1, 0.5f+z/30f, 1);
			}
		}
	}
	
	public void createShaderProgram() {
		vertexShader = createShader("shaders/vertex.glsl", GL20.GL_VERTEX_SHADER);
		fragmentShader = createShader("shaders/fragment.glsl", GL20.GL_FRAGMENT_SHADER);
		program = GL20.glCreateProgram();
		
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		
		GL20.glLinkProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program));
			throw new RuntimeException();
		}
		
		GL20.glValidateProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program));
			throw new RuntimeException();
		}
		
		setupWorld();
		
		FloatBuffer floatChunkBuffer = BufferUtils.createFloatBuffer(16*16*16*16*4);
		floatChunkBuffer.put(array);
		floatChunkBuffer.flip();
		
		chunkBuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, chunkBuffer);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, floatChunkBuffer, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, chunkBuffer);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}
	
	public void deleteShaderProgram() {
		GL15.glDeleteBuffers(chunkBuffer);
		chunkBuffer = 0;
		GL20.glDetachShader(program, fragmentShader);
		GL20.glDetachShader(program, vertexShader);
		GL20.glDeleteProgram(program);
		program = 0;
		GL20.glDeleteShader(fragmentShader);
		fragmentShader = 0;
		GL20.glDeleteShader(vertexShader);
		vertexShader = 0;
	}
	
	private int createShader(String fileName, int shaderType) {
		int shader = 0;
		shader = GL20.glCreateShader(shaderType);
		
		if (shader == 0)
			throw new RuntimeException("shader id 0");
		
		GL20.glShaderSource(shader, readFileAsString(fileName));
		GL20.glCompileShader(shader);
		
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
			throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
		
		return shader;
	}
	
	private static String getLogInfo(int shader) {
		return GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
	}
	
	private String readFileAsString(String fileName) {
		try {
			StringBuilder sb = new StringBuilder();
			FileInputStream fs = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			String line = br.readLine();
			while (line != null) {
				sb.append(line).append('\n');
				line = br.readLine();
			}
			br.close();
			fs.close();
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getProgram() {
		return program;
	}
}
