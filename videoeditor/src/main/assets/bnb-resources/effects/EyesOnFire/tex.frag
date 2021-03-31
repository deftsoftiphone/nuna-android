#version 300 es

precision lowp float;

in vec2 var_uv;
in vec2 var_bg_uv;
in float var_curr_alpha;

layout( location = 0 ) out vec4 frag_color;

vec4 blend_screen(vec4 a, vec4 b) 
{
    return 1.0 - (1.0 - a) * (1.0 - b);
}

uniform sampler2D tex;
uniform sampler2D glfx_BACKGROUND;
void main()
{
	vec4 color = texture(tex, var_uv);

	if (color.g >= 0.98 && color.b >= 0.98) {
		discard;
	}

	vec4 bg = texture(glfx_BACKGROUND, var_bg_uv);
	vec4 final_color = blend_screen(bg, color);
	// float alpha = 1.0;
	frag_color = vec4((bg * color).rgb, var_curr_alpha);
}
