#version 300 es

#define SHADOW_MULT 0.2

precision mediump float;
precision mediump sampler2DShadow;

layout( location = 0 ) out vec4 F;
in vec3 shadow_coord;
uniform sampler2DShadow glfx_SHADOW;
void main()
{
	const vec2 offsets[] = vec2[](
		vec2( -0.94201624, -0.39906216 ),
		vec2( 0.94558609, -0.76890725 ),
		vec2( -0.094184101, -0.92938870 ),
		vec2( 0.34495938, 0.29387760 )
	);

	float s = 0.;
	for( int i = 0; i != offsets.length(); ++i )
		s += texture( glfx_SHADOW, shadow_coord + vec3(offsets[i]/110.,0.) );
	s *= 0.125*SHADOW_MULT;

	if( s < 1./255. )
		discard;

	F = vec4( 0., 0., 0., s );
}
