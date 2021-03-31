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
	vec4 glfx_IDATA[48];
};
uniform uint glfx_CURRENT_I;
#define glfx_T_SPAWN (glfx_IDATA[glfx_CURRENT_I].x)

out vec3 shadow_coord;

invariant gl_Position;

vec3 spherical_proj( vec2 half_fov, float zn, float zf, vec3 v )
{
	vec2 xy = atan( v.xy, v.zz )/half_fov;
	float z = (length(v)-(zn+zf)*0.5)/((zf-zn)*0.5);
	return vec3( xy, z );
}

vec3 spherical_proj( vec2 fovM, vec2 fovP, float zn, float zf, vec3 v )
{
	vec2 xy = (atan( v.xy, v.zz )-(fovP+fovM)*0.5)/((fovP-fovM)*0.5);
	float z = (length(v)-(zn+zf)*0.5)/((zf-zn)*0.5);
	return vec3( xy, z );
}

void main()
{
	gl_Position = glfx_MVP * vec4( attrib_pos, 1. );
	shadow_coord = vec3( spherical_proj(
		vec2(-radians(60.),-radians(20.)),vec2(radians(60.),radians(100.)),
		400.,70.,
		attrib_pos+vec3(0.,100.,50.))*0.5+0.5 );
}
