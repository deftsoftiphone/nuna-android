#version 300 es

precision highp float;

in vec2 var_uv;

layout( location = 0 ) out vec4 frag_color;

uniform sampler2D tex;

void main()
{
	vec4 c = texture(tex,var_uv);
	frag_color = c;
}
