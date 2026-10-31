// Based on article by Alexander Christensen:
// https://learnopengl.com/Guest-Articles/2022/Phys.-Based-Bloom
//
// 13-tap downsample (Call Of Duty method, ACM Siggraph 2014). Sample the input with
// bilinear filtering and edge clamping.

#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(location = 0) in vec2 texCoord;

layout(location = 0) out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    float x = oneTexel.x;
    float y = oneTexel.y;

    // a - b - c
    // - j - k -
    // d - e - f
    // - l - m -
    // g - h - i
    vec4 a = texture(InSampler, vec2(texCoord.x - 2.0 * x, texCoord.y + 2.0 * y));
    vec4 b = texture(InSampler, vec2(texCoord.x,           texCoord.y + 2.0 * y));
    vec4 c = texture(InSampler, vec2(texCoord.x + 2.0 * x, texCoord.y + 2.0 * y));

    vec4 d = texture(InSampler, vec2(texCoord.x - 2.0 * x, texCoord.y));
    vec4 e = texture(InSampler, vec2(texCoord.x,           texCoord.y));
    vec4 f = texture(InSampler, vec2(texCoord.x + 2.0 * x, texCoord.y));

    vec4 g = texture(InSampler, vec2(texCoord.x - 2.0 * x, texCoord.y - 2.0 * y));
    vec4 h = texture(InSampler, vec2(texCoord.x,           texCoord.y - 2.0 * y));
    vec4 i = texture(InSampler, vec2(texCoord.x + 2.0 * x, texCoord.y - 2.0 * y));

    vec4 j = texture(InSampler, vec2(texCoord.x - x, texCoord.y + y));
    vec4 k = texture(InSampler, vec2(texCoord.x + x, texCoord.y + y));
    vec4 l = texture(InSampler, vec2(texCoord.x - x, texCoord.y - y));
    vec4 m = texture(InSampler, vec2(texCoord.x + x, texCoord.y - y));

    // Energy-preserving weights: 0.125*5 + 0.03125*4 + 0.0625*4 = 1
    vec4 color = e * 0.125;
    color += (a + c + g + i) * 0.03125;
    color += (b + d + f + h) * 0.0625;
    color += (j + k + l + m) * 0.125;
    fragColor = color;
}
