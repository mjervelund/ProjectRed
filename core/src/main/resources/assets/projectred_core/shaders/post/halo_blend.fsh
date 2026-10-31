#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D InSampler;
uniform sampler2D HaloSampler;

layout(location = 0) in vec2 texCoord;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 background = texture(InSampler, texCoord);
    vec4 halo = texture(HaloSampler, texCoord);

    // Empty pixels: background unchanged
    if (halo.a == 0.0) {
        fragColor = vec4(background.rgb, 1.0);
        return;
    }

    // Overlay the bloom layer (premultiplied-style: dst * (1 - a) + src)
    fragColor = vec4(background.rgb * (1.0 - halo.a) + halo.rgb, 1.0);
}
