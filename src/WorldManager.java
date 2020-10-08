import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class WorldManager {

	private static float array[];
	
	public static float[] getWorld() {
		if (array != null) {
			return array;
		}
		try {
			byte[] byteData = Files.readAllBytes(Paths.get("world/0.4D"));
			int version = ((byteData[0]&0xFF)<<24) + ((byteData[1]&0xFF)<<16) + ((byteData[2]&0xFF)<<8) + (byteData[3]&0xFF);
			if (version != 0) {
				throw new RuntimeException("Expected version 0, got version" + version);
			}
			String adjacent[] = new String[8];
			int offset = 4;
			for (int i = 0; i < 8; i++) {
				int length = ((byteData[0+offset]&0xFF)<<24) + ((byteData[1+offset]&0xFF)<<16) + ((byteData[2+offset]&0xFF)<<8) + (byteData[3+offset]&0xFF);
				if (length > 0) {
					adjacent[i] = new String(Arrays.copyOfRange(byteData, offset, offset+length), "UTF-8");
				}
				offset += 4 + length;
			}
			array  = new float[16*16*16*16*4];
			for (int i = 0; i < array.length; i++) {
				array[i] = ((int)(byteData[i+offset])&255)/255f;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}
}
