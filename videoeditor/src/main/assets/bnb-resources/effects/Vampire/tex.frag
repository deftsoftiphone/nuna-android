#version 300 es
precision lowp float;
in vec2 var_uv;
layout(location=0) out vec4 F;
uniform sampler2D tex;
void main(){F=texture(tex,var_uv);}
