#version 300 es

precision highp float;

in vec2 var_uv;
in vec2 var_bg_uv;

layout( location = 0 ) out vec4 frag_color;

uniform sampler2D tex;
uniform sampler2D glfx_BACKGROUND;

void main()
{
	vec2 uv = var_uv;

	vec4 bg_color = texture(glfx_BACKGROUND, var_bg_uv);
	// bg_color.x = 1.0;
	
	vec4 mask_color = texture(tex, uv);
	mask_color.a *= 2.0;
	frag_color.a = mask_color.a;
	frag_color.rgb = bg_color.rgb;
}
