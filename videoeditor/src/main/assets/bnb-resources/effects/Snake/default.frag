#version 300 es

precision mediump float;

in vec2 var_uv;
in vec3 var_t;
in vec3 var_b;
in vec3 var_n;

uniform sampler2D s_d;
uniform sampler2D s_n;

layout( location = 0 ) out vec4 F;

void main()
{
	vec3 n = normalize(mat3(var_t,var_b,var_n)*(texture( s_n, var_uv ).xyz*2. - 1. + vec3(0.,0.,-0.25)));
	vec3 l = vec3(0.,0.8,0.6);
	vec4 ds = texture( s_d, var_uv );
	float diff = dot(n,l)*0.5+0.5;
	vec3 h_dir = normalize( l + vec3(0.,0.,1.) );
	float spec = ds.w*pow( max( dot(h_dir,n), 0. ), 16. );
	F = vec4(vec3(diff+spec)*ds.xyz,1.);
}
