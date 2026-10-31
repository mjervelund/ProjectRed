// Based on article by Alexander Christensen:
// https://learnopengl.com/Guest-Articles/2022/Phys.-Based-Bloom
//
// 3x3 tent-filter upsample (Call Of Duty method, ACM Siggraph 2014). The radius is in
// input texels, so it grows with each mip level.

#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform UpsampleConfig {
    float FilterRadius;
};

layout(location = 0) in vec2 texCoord;

layout(location = 0) out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    float x = oneTexel.x * FilterRadius;
    float y = oneTexel.y * FilterRadius;

    vec4 a = texture(InSampler, vec2(texCoord.x - x, texCoord.y + y));
    vec4 b = texture(InSampler, vec2(texCoord.x,     texCoord.y + y));
    vec4 c = texture(InSampler, vec2(texCoord.x + x, texCoord.y + y));

    vec4 d = texture(InSampler, vec2(texCoord.x - x, texCoord.y));
    vec4 e = texture(InSampler, vec2(texCoord.x,     texCoord.y));
    vec4 f = texture(InSampler, vec2(texCoord.x + x, texCoord.y));

    vec4 g = texture(InSampler, vec2(texCoord.x - x, texCoord.y - y));
    vec4 h = texture(InSampler, vec2(texCoord.x,     texCoord.y - y));
    vec4 i = texture(InSampler, vec2(texCoord.x + x, texCoord.y - y));

    //  1   | 1 2 1 |
    // -- * | 2 4 2 |
    // 16   | 1 2 1 |
    vec4 color = e * 4.0;
    color += (b + d + f + h) * 2.0;
    color += (a + c + g + i);
    fragColor = color * (1.0 / 16.0);
}
