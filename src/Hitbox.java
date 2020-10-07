
public class Hitbox {

	private final float size[];
	
	public Hitbox(float x, float y, float z, float w) {
		size = new float[] {x, y, z, w};
	}
	
	public void move(Camera camera, float... delta) {
		float chunk[] = WorldManager.getWorld();
		float pos[] = new float[] {camera.x, camera.y, camera.z, camera.w, 0, 0, 0, 0};
		boolean check[] = new boolean[8];
		float distance[] = new float[4];
		float t[] = new float[4];
		for (int e = 0; e < 4; e++) {
			float posLast = pos[e] + (delta[e] > 0 ? size[e]/2 : -size[e]/2);
			pos[4+e] = pos[e] + delta[e] + (delta[e] > 0 ? size[e]/2 : -size[e]/2);
			check[4+e] = (int)Math.floor(pos[4+e]) != (int)Math.floor(posLast);
			distance[e] = delta[e] > 0 ? (float)Math.ceil(posLast) - posLast : posLast - (float)Math.floor(posLast);
			t[e] = Math.abs(distance[e] / delta[e]);
		}
		
		int order[] = new int[] {0, 1, 2, 3};
		for (int i = 0; i < 2; i++) {
			if (t[order[1]] < t[order[0]]) {
				int temp = order[0];
				order[0] = order[1];
				order[1] = temp;
			}
			if (t[order[3]] < t[order[2]]) {
				int temp = order[2];
				order[2] = order[3];
				order[3] = temp;
			}
			if (t[order[2]] < t[order[1]]) {
				int temp = order[1];
				order[1] = order[2];
				order[2] = temp;
			}
		}
		
		int index[][] = new int[][] {
			{order[0]+4, order[1]+0, order[2]+0, order[3]+0},
			{order[0]+0, order[1]+4, order[2]+0, order[3]+0},
			{order[0]+0, order[1]+0, order[2]+4, order[3]+0},
			{order[0]+0, order[1]+0, order[2]+0, order[3]+4},
			
			{order[0]+4, order[1]+4, order[2]+0, order[3]+0},
			{order[0]+4, order[1]+0, order[2]+4, order[3]+0},
			{order[0]+4, order[1]+0, order[2]+0, order[3]+4},
			{order[0]+0, order[1]+4, order[2]+4, order[3]+0},
			{order[0]+0, order[1]+4, order[2]+0, order[3]+4},
			{order[0]+0, order[1]+0, order[2]+4, order[3]+4},
			
			{order[0]+4, order[1]+4, order[2]+4, order[3]+0},
			{order[0]+4, order[1]+4, order[2]+0, order[3]+4},
			{order[0]+4, order[1]+0, order[2]+4, order[3]+4},
			{order[0]+0, order[1]+4, order[2]+4, order[3]+4},
			
			{order[0]+4, order[1]+4, order[2]+4, order[3]+4}};
			
		boolean canChange[] = check.clone();
			
		for (int array[] : index) {
			if (check[array[0]] || check[array[1]] || check[array[2]] || check[array[3]]) {
				int temp[] = new int[] {0, 1, 2, 3};
				for (int e = 0; e < 4; e++) {
					for (int i = 0; i < 4; i++) {
						if (array[i] == e+4) {
							temp[e] += 4;
							break;
						}
					}
				}
				int blockPos[] = new int[] {
						(int)Math.floor(pos[temp[0]]),
						(int)Math.floor(pos[temp[1]]),
						(int)Math.floor(pos[temp[2]]),
						(int)Math.floor(pos[temp[3]])};
				checkBlock(blockPos, chunk, pos, delta, array, canChange, distance, check);
			}
		}
		
		camera.x += delta[0];
		camera.y += delta[1];
		camera.z += delta[2];
		camera.w += delta[3];
	}
	
	private void checkBlock(int blockPos[], float chunk[], float pos[],
			float delta[], int currentIndex[], boolean canChange[], float distance[], boolean check[]) {
		if (chunk[blockPos[3]*16*16*16*4 + blockPos[2]*16*16*4 + blockPos[1]*16*4 + blockPos[0]*4 + 3] != 0) {
			int changeIndex = -1;
			for (int i = 0; i < 4; i++) {
				if (canChange[currentIndex[i]]) {
					canChange[currentIndex[i]] = false;
					changeIndex = currentIndex[i] - 4;
					break;
				}
			}
			delta[changeIndex] = delta[changeIndex] > 0 ? distance[changeIndex] - 0.0001f : -distance[changeIndex] + 0.0001f;
			pos[4+changeIndex] = pos[changeIndex] + delta[changeIndex] + (delta[changeIndex] > 0 ? size[changeIndex]/2 : -size[changeIndex]/2);
			check[4+changeIndex] = false;
		}
	}
}
