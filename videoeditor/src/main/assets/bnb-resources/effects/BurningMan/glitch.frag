#version 300 es

#define COLOR_COEFF 0.1
#define M_PI 3.1415926535897932384626433832795

#define X_FREQ 1.0
#define X_AMP 0.1

#define Y_FREQ 1.0
#define Y_AMP 0.

#define PULSE_FREQ 3.
#define PULSE_AMP 0.25

precision highp float;

in vec2 var_uv;
in vec2 var_bgmask_uv;

in vec4 random;

layout( location = 0 ) out vec4 F;

uniform sampler2D glfx_BACKGROUND;
uniform sampler2D glfx_BG_MASK;
uniform sampler2D luttex;

float time;

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

float rand_pulse() {
	return smoothstep(1. + PULSE_AMP,0.5*sin(time * PULSE_FREQ),1.);
}

float rand_x () {
    return sin(time * X_FREQ) * X_AMP;
}

float rand_y () {
    return sin(time * Y_FREQ) * Y_AMP;
}

void main()
{	
	time = random.x;

	vec2 uvR = var_uv;
	vec2 uvG = var_uv;
	vec2 uvB = var_uv;

	vec2 uvR_bg = var_bgmask_uv;
	vec2 uvB_bg = var_bgmask_uv;

	uvR_bg.y = var_bgmask_uv.y * 1.0 + (rand_x() + rand_pulse()) * 0.152;
	uvB_bg.y = var_bgmask_uv.y * 1.0 - (rand_x() + rand_pulse()) * 0.152;

	uvR.x = var_uv.x * 1.0 - (rand_x() + rand_pulse()) * 0.152;
	uvB.x = var_uv.x * 1.0 + (rand_x() + rand_pulse()) * 0.152;

	uvR.y = var_uv.y * 1.0 - rand_y() * 0.152;
	uvB.y = var_uv.y * 1.0 + rand_y() * 0.152;

	uvR_bg.x = var_bgmask_uv.x * 1.0 + rand_y() * 0.152;
	uvB_bg.x = var_bgmask_uv.x * 1.0 - rand_y() * 0.152;

	vec4 c;
	c.r = mix(texture(glfx_BACKGROUND, uvR).r, texture(glfx_BACKGROUND, var_uv).r, COLOR_COEFF);
	c.r = mix (c.r, texture(glfx_BACKGROUND, var_uv).r, filtered_bg_simple( glfx_BG_MASK, uvR_bg ));

	c.g = texture(glfx_BACKGROUND, uvG).g;

	c.b = mix (texture(glfx_BACKGROUND, uvB).b, texture(glfx_BACKGROUND, var_uv).b, COLOR_COEFF);
	c.b = mix (c.b, texture(glfx_BACKGROUND, var_uv).b, filtered_bg_simple( glfx_BG_MASK, uvB_bg ));
	
	c.w = 1.0;

	F = c;
}
