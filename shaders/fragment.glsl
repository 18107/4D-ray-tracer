#version 430

#define M_PI 3.1415927

in vec2 texcoord;
out vec4 color;

uniform vec4 cameraPos;
uniform vec4 cameraRot;

uniform vec4 backgroundColor;
uniform int maxDepth;

layout(std430, binding = 1) buffer chunk
{
	vec4 block[];
};

vec4 getRaySphere() {
	return vec4(
		sin(texcoord.x*M_PI)*cos(texcoord.y*M_PI/2),
		sin(texcoord.y*M_PI/2),
		-cos(texcoord.x*M_PI)*cos(texcoord.y*M_PI/2),
		0
		);
}

vec4 getRayFlat() {
	return vec4(
		texcoord.x*2,
		texcoord.y,
		-1,
		0
		);
}

vec4 rotate(vec4 ray, vec4 rotation) {
	float x;
	float y;
	float z;
	float w;

	//rotate zy
	y = cos(rotation.x)*ray.y - sin(rotation.x)*ray.z;
	z = cos(rotation.x)*ray.z + sin(rotation.x)*ray.y;
	ray.y = y;
	ray.z = z;

	//rotate zx
	x = cos(rotation.y)*ray.x - sin(rotation.y)*ray.z;
  z = cos(rotation.y)*ray.z + sin(rotation.y)*ray.x;
	ray.x = x;
	ray.z = z;

	//rotate zw
	z = cos(rotation.w)*ray.z - sin(rotation.w)*ray.w;
	w = cos(rotation.w)*ray.w + sin(rotation.w)*ray.z;
	ray.z = z;
	ray.w = w;

	//rotate xw
	x = cos(rotation.z)*ray.x - sin(rotation.z)*ray.w;
	w = cos(rotation.z)*ray.w + sin(rotation.z)*ray.x;
	ray.x = x;
	ray.w = w;

	return ray;
}

bool reflect(inout ivec4 current, inout vec4 nearestCube, inout vec4 inc, inout ivec4 iinc, vec4 blockColor) {
	if (blockColor.r < 0.75 || blockColor.g > 0.25) {
		return true;
	}

	vec4 lastCube = nearestCube-inc;
	if (lastCube.x > lastCube.y) {
		if (lastCube.x > lastCube.z) {
			if (lastCube.x > lastCube.w) {
				iinc.x = -iinc.x;
				nearestCube.x -= inc.x;
			} else {
				iinc.w = -iinc.w;
				nearestCube.w -= inc.w;
			}
		} else {
			if (lastCube.z > lastCube.w) {
				iinc.z = -iinc.z;
				nearestCube.z -= inc.z;
			} else {
				iinc.w = -iinc.w;
				nearestCube.w -= inc.w;
			}
		}
	} else {
		if (lastCube.y > lastCube.z) {
			if (lastCube.y > lastCube.w) {
				iinc.y = -iinc.y;
				nearestCube.y -= inc.y;
			} else {
				iinc.w = -iinc.w;
				nearestCube.w -= inc.w;
			}
		} else {
			if (lastCube.z > lastCube.w) {
				iinc.z = -iinc.z;
				nearestCube.z -= inc.z;
			} else {
				iinc.w = -iinc.w;
				nearestCube.w -= inc.w;
			}
		}
	}
	return false;
}

vec4 trace(ivec4 current, vec4 nearestCube, vec4 inc, ivec4 iinc) {
	int depth = 0;
	while (depth < maxDepth) {
		depth++;
		if (current.x < 0 || current.x >= 16 || current.y < 0 || current.y >= 16 || current.z < 0 || current.z >= 16 || current.w < 0 || current.w >= 16) {
			return backgroundColor;
		}

		vec4 blockColor = block[current.w*16*16*16 + current.z*16*16 + current.y*16 + current.x];
		if (blockColor.a != 0) {
			if (reflect(current, nearestCube, inc, iinc, blockColor)) {
				return blockColor;
			}
		}

		if (nearestCube.x < nearestCube.y) {
			if (nearestCube.x < nearestCube.z) {
				if (nearestCube.x < nearestCube.w) {
					nearestCube.x += inc.x;
					current.x += iinc.x;
				} else { //w
					nearestCube.w += inc.w;
					current.w += iinc.w;
				}
			} else { //z
				if (nearestCube.z < nearestCube.w) {
					nearestCube.z += inc.z;
					current.z += iinc.z;
				} else { //w
					nearestCube.w += inc.w;
					current.w += iinc.w;
				}
			}
		} else { //y
			if (nearestCube.y < nearestCube.z) {
				if (nearestCube.y < nearestCube.w) {
					nearestCube.y += inc.y;
					current.y += iinc.y;
				} else { //w
					nearestCube.w += inc.w;
					current.w += iinc.w;
				}
			} else { //z
				if (nearestCube.z < nearestCube.w) {
					nearestCube.z += inc.z;
					current.z += iinc.z;
				} else { //w
					nearestCube.w += inc.w;
					current.w += iinc.w;
				}
			}
		}
	}
	return backgroundColor;
}

void main() {
	vec4 rayDir = rotate(getRaySphere(), cameraRot);
	vec4 point = cameraPos;
	ivec4 current = ivec4(floor(cameraPos));
	vec4 nearestCube;
	{
		ivec4 nextDir;
		if (rayDir.x > 0) {
			nextDir.x = int(ceil(point.x));
		} else {
			nextDir.x = int(floor(point.x));
		}
		if (rayDir.y > 0) {
			nextDir.y = int(ceil(point.y));
		} else {
			nextDir.y = int(floor(point.y));
		}
		if (rayDir.z > 0) {
			nextDir.z = int(ceil(point.z));
		} else {
			nextDir.z = int(floor(point.z));
		}
		if (rayDir.w > 0) {
			nextDir.w = int(ceil(point.w));
		} else {
			nextDir.w = int(floor(point.w));
		}
		nearestCube = abs((nextDir - point)/rayDir);
	}
	vec4 inc = abs(1/rayDir);
	ivec4 iinc;
	if (rayDir.x > 0) {
		iinc.x = 1;
	} else {
		iinc.x = -1;
	}
	if (rayDir.y > 0) {
		iinc.y = 1;
	} else {
		iinc.y = -1;
	}
	if (rayDir.z > 0) {
		iinc.z = 1;
	} else {
		iinc.z = -1;
	}
	if (rayDir.w > 0) {
		iinc.w = 1;
	} else {
		iinc.w = -1;
	}

	color = trace(current, nearestCube, inc, iinc);
}
