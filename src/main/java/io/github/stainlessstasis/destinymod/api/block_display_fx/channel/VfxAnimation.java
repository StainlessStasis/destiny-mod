package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import org.joml.Quaternionf;

/**
 * Contains channels which define start and ending values, along with an easing function, for making the vfx entity change over time.
 * @param translationChannel Movement from one position to another, in local space. Self-explanatory.
 * @param scaleChannel Size of the vfx entity. Self-explanatory.
 * @param rotationChannel Accepts any Channel of type Quaternionf.<br><br>
 *                        For rotations which directly use a starting and ending quaternion, see {@link RotationChannel}.
 *                        Note that the end rotation will be the modulus of its degrees.
 *                        E.g. a rotation from 0 to pi*2 will not rotate at all, since 0 and 360 are effectively the same angle.<br><br>
 *                        For rotations which should be defined in total degrees, see {@link RotationDegreesChannel}. Use this
 *                        for cases where a rotation >=360 degrees is desired.
 */
public record VfxAnimation(Vector3fChannel translationChannel, Vector3fChannel scaleChannel, Channel<Quaternionf> rotationChannel) { }
