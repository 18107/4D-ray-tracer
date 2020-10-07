import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WorldManager {

	private static float array[];
	
	public static float[] getWorld() {
		if (array != null) {
			return array;
		}
		try {
			byte[] byteData = Files.readAllBytes(Paths.get("world/0,0.4D"));
			array  = new float[16*16*16*16*4];
			if (byteData.length != array.length)
				throw new RuntimeException("Filesize: " + byteData.length + ", expected: " + array.length);
			for (int i = 0; i < array.length; i++) {
				array[i] = ((int)(byteData[i])&255)/255f;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}
}
