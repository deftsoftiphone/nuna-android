#version 300 es

precision lowp float;

in vec2 var_uv;
in float var_alpha;

layout( location = 0 ) out vec4 frag_color;

vec4 blend_screen(vec4 a, vec4 b) 
{
    return 1.0 - (1.0 - a) * (1.0 - b);
}

uniform sampler2D tex;
uniform sampler2D glfx_BACKGROUND;
void main()
{
	vec4 texColor = texture(tex,var_uv);
	vec4 bgColor = texture(glfx_BACKGROUND,var_uv);
	vec4 color = bgColor * texColor;
	frag_color = vec4(color.rgb, var_alpha);
}
