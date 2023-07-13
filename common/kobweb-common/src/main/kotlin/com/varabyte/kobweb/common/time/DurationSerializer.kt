package com.varabyte.kobweb.common.time

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

// NOTE: I thought duration serialization supposed to be included in Kotlin 1.7.20 and beyond, but I'm just not seeing
// it. Therefore, we just implement our own solution.
// You probably want to use it something like `@file:UseSerializers(DurationSerializer::class)` or
// `typealias SerializableDuration = @Serializable(with = DurationSerializer::class) Duration`
class DurationSerializer : KSerializer<Duration> {
    private val delegateSerializer = String.serializer()
    override val descriptor =
        PrimitiveSerialDescriptor("com.varabyte.kobweb.common.type.DurationSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        delegateSerializer.serialize(encoder, value.toString())
    }

    override fun deserialize(decoder: Decoder): Duration {
        return Duration.parse(delegateSerializer.deserialize(decoder))
    }
}
