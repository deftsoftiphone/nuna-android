#version 300 es

precision highp float;
precision lowp sampler2D;

in vec2 var_uv;
in float var_time;

layout( location = 0 ) out vec4 frag_color;

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898,12.1414))) * 83758.5453);
}

float noise(vec2 n) {
    const vec2 d = vec2(0.0, 1.0);
    vec2 b = floor(n);
    vec2 f = mix(vec2(0.0), vec2(1.0), fract(n));
    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

vec3 ramp(float t) {
	return t <= .1 ? vec3( 30. - t * .1, .1, .1 ) / t : vec3( .3 * (30. - t) * .1, .1, .1 ) / t;
}

float fire(vec2 n) {
    return noise(n) + noise(n * 2.1) * .6 + noise(n * 5.4) * .42;
}

vec2 rotateUV(vec2 uv, float rotation, vec2 mid)
{
    return vec2(
      cos(rotation) * (uv.x - mid.x) + sin(rotation) * (uv.y - mid.y) + mid.x,
      cos(rotation) * (uv.y - mid.y) - sin(rotation) * (uv.x - mid.x) + mid.y
    );
}

void main()
{
    float t = var_time;
    vec2 uv = var_uv;
    uv = rotateUV(uv, radians(90.0), vec2(0.5, 0.5));

    uv.x += uv.y < .5 ? 23.0 + t * .35 : -11.0 + t * .3;    
    uv.y = abs(uv.y - .5);
    uv *= 5.0;

    float q = fire(uv - t * .013) / 2.0;
    vec2 r = vec2(fire(uv + q / 2.0 + t - uv.x - uv.y), fire(uv + q - t));
    vec3 fireColor = vec3(1.0 / (pow(vec3(0.5, 0.0, .1) + 1.61, vec3(4.0))));

    float grad = pow((0.75 * r.y) * max(.0, uv.y) + .1, 4.0);
    fireColor = ramp(grad);
    fireColor /= (1.50 + max(vec3(0), fireColor));

    vec4 effect_color = vec4(fireColor, 1.0);
    
    frag_color = vec4(effect_color.rgb, 1.0);
}