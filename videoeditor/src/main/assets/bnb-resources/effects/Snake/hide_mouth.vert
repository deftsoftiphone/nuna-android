#version 300 es

layout( location = 0 ) in vec3 attrib_pos;

layout(std140) uniform glfx_GLOBAL
{
	mat4 glfx_MVP;
	mat4 glfx_PROJ;
	mat4 glfx_MV;
};
layout(std140) uniform glfx_INSTANCES
{
	vec4 glfx_IDATA[8];
};
uniform uint glfx_CURRENT_I;
#define glfx_T_SPAWN (glfx_IDATA[glfx_CURRENT_I].x)
#define glfx_T_ANIM (glfx_IDATA[glfx_CURRENT_I].y)
#define glfx_ANIMKEY (glfx_IDATA[glfx_CURRENT_I].z)

uniform sampler2D glfx_MORPH;
vec2 glfx_morph_coord( vec3 v )
{
	const float half_angle = radians(104.);
	const float y0 = -110.;
	const float y1 = 112.;
	float x = atan( v.x, v.z )/half_angle;
	float y = ((v.y-y0)/(y1-y0))*2. - 1.;
	return vec2(x,y);
}
vec3 glfx_auto_morph( vec3 v )
{
	vec2 morph_uv = glfx_morph_coord(v)*0.5 + 0.5;
	vec3 translation = texture( glfx_MORPH, morph_uv ).xyz;
	return v + translation;
}

void main()
{
	vec3 vpos = attrib_pos;
	vpos = glfx_auto_morph( vpos );
	gl_Position = glfx_MVP * vec4(vpos,1.);
}
