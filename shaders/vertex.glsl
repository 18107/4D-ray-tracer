#version 430

in vec2 vertex;
out vec2 texcoord;

void main() {
	gl_Position = vec4(vertex, 0, 1);
	texcoord = vertex;
}