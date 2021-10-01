# Sodium-Reforged
https://www.curseforge.com/minecraft/mc-mods/sodium-reforged
This is an Unofficial port of CaffeineMC's "Sodium" Mod, Ported from Fabric to Forge.

 

Currently it is in 100% parity with Fabric Sodium 0.2.0, however I intend to migrate to 0.3.0 soon.

 

Sodium is a free and open-source rendering engine replacement for the Minecraft client that greatly improves frame rates, reduces micro-stutter, and fixes graphical issues in Minecraft. It boasts wide compatibility with the Fabric Forge mod ecosystem when compared to other mods and doesn't compromise on how the game looks, giving you that authentic block game feel.

 

Features:

A modern OpenGL rendering pipeline for chunk rendering that takes advantage of multi-draw techniques, allowing for a significant reduction in CPU overhead (~90%) when rendering the world. This can make a huge difference to frame rates for most computers that are not bottle-necked by the GPU or other components. Even if your GPU can't keep up, you'll experience much more stable frame times thanks to the CPU being able to work on other rendering tasks while it waits.

Vertex data for rendered chunks is made much more compact, allowing for video memory and bandwidth requirements to be cut by almost 40%.

Nearby block updates now take advantage of multi-threading, greatly reducing lag spikes caused by chunks needing to be updated.

Chunk faces which are not visible (or facing away from the camera) are culled very early in the rendering process, eliminating a ton of geometry that would have to be processed on the GPU only to be immediately discarded. For integrated GPUs, this can greatly reduce memory bandwidth requirements and provide a modest speedup even when GPU-bound.

Plentiful optimizations for chunk loading and block rendering, making chunk loading significantly faster and less damaging to frame rates. 

Many optimizations for vertex building and matrix transformations, speeding up block entity, mob, and item rendering significantly for when you get carried away placing too many chests in one room.

Many improvements to how the game manages memory and allocates objects, which in turn reduces memory consumption and lag spikes caused by garbage collector activity.

Many graphical fixes for smooth lighting effects, making the game run better while still applying a healthy amount of optimization.

Smooth lighting for fluids and other special blocks. 

Smooth biome blending for blocks and fluids, providing greatly improved graphical quality that is significantly less computationally intensive. 

Animated textures which are not visible in the world are not updated, speeding up texture updating on most hardware (especially AMD cards.)

... and much more, this list is still being written after the initial release.

 

 

 

This is an unofficial port, this is not affiliated with The Original Sodium, Halogen or Chlorine.

Credit goes to The Sodium Dev Team for the original mod. Fabric Version

Please do NOT report issues to the official Sodium Github or Discord, they cannot provide support for this port. To report issues, report them to the Github I have linked

 

While I have reached out to Jellysquid, if there is any issue with this port existing, please contact me.

 

Mod Compatibility:

While, there's bound to be crashes, due to it staying so close to the original mod, Sodium should be relatively stable on first release. I have tested this with the "Better Minecraft" Modpack without any noticeable issues.

 

For now, this mod will be maintained in 1.16.5 and 1.18+.
