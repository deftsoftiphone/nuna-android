#version 300 es

layout( location = 0 ) in vec3 attrib_pos;
layout( location = 3 ) in vec2 attrib_uv;


layout(std140) uniform glfx_GLOBAL
{
	mat4 glfx_MVP;
	mat4 glfx_PROJ;
	mat4 glfx_MV;

	vec4 glfx_QUAT;

	vec4 js_platform_data;
};

layout(std140) uniform glfx_BASIS_DATA
{
	vec4 unused;
	vec4 glfx_SCREEN;
	vec4 glfx_BG_MASK_T[2];
	vec4 glfx_HAIR_MASK_T[2];
	vec4 glfx_LIPS_MASK_T[2];
	vec4 glfx_L_EYE_MASK_T[2];
	vec4 glfx_R_EYE_MASK_T[2];
	vec4 glfx_SKIN_MASK_T[2];
	vec4 glfx_OCCLUSION_MASK_T[2];
};

out vec2 var_uv;
out vec2 video_uv;

out vec2 var_bgmask_uv;

void main()
{
	vec2 v = attrib_pos.xy;
	gl_Position = vec4( v, 1., 1. );
	var_uv = v*0.5 + 0.5;
	if (js_platform_data.x > 0.5) {
		var_bgmask_uv = vec2(-sign(glfx_PROJ[0][0]),-sign(glfx_PROJ[1][1]))*v.yx*0.5 + 0.5;
	} else {
		mat2x3 bg_basis = mat2x3(glfx_BG_MASK_T[0].xyz, glfx_BG_MASK_T[1].xyz);
		var_bgmask_uv = vec3(v,1.)*bg_basis;
	}

  video_uv = attrib_uv;
}