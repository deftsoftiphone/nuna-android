#version 300 es

precision highp float;

#define Y_OFFSET 0.3
#define Y_SCALE 1. - Y_OFFSET
#define X_OFFSET 0.1
in vec2 var_uv;
in vec2 var_bgmask_uv;
in vec2 video_uv;

layout( location = 0 ) out vec4 F;

uniform sampler2D glfx_BACKGROUND;
uniform sampler2D glfx_VIDEO;
uniform sampler2D glfx_BG_MASK;

float filtered_bg_simple( sampler2D mask_tex, vec2 uv )
{
	float bg1 = texture( mask_tex, uv ).x;
	if( bg1 > 0.98 || bg1 < 0.02 )
		return bg1;

	vec2 o = 1./vec2(textureSize(mask_tex,0));
	float bg2 = texture( mask_tex, uv + vec2(o.x,0.) ).x;
	float bg3 = texture( mask_tex, uv - vec2(o.x,0.) ).x;
	float bg4 = texture( mask_tex, uv + vec2(0.,o.y) ).x;
	float bg5 = texture( mask_tex, uv - vec2(0.,o.y) ).x;

	return 0.2*(bg1+bg2+bg3+bg4+bg5);
}

void main()
{
	vec2 uv = video_uv;
	uv.x += 0.25;
	vec3 bg = texture(glfx_BACKGROUND, var_uv).xyz;
	vec4 bg_with_video = texture(glfx_VIDEO, uv) + vec4(bg, 1.0);
	float mask = filtered_bg_simple( glfx_BG_MASK, var_bgmask_uv );
	F = mix(vec4(bg,1.0), bg_with_video, mask);
}