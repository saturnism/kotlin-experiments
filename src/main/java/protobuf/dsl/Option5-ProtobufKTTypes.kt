package protobuf.dsl.option5

import com.google.cloud.vision.v1.*

data class AnnotateImageRequestKt(
        val features: Collection<FeatureKt> = emptyList()
) {
    fun toProto() : AnnotateImageRequest {
        return with (AnnotateImageRequest.newBuilder()) {
            features.forEach {
                addFeatures(it.toProto())
            }
            build()
        }
    }
}

data class FeatureKt(val type : Feature.Type = Feature.Type.TYPE_UNSPECIFIED,
                     val maxResults : Int = 0,
                     val model : String = "") {
    fun toProto(): Feature {
        return Feature.newBuilder()
                .setType(type)
                .setModel(model)
                .setMaxResults(maxResults)
                .build()
    }
}

annotation class ProtobufDsl

fun main(args: Array<String>) {
    val request = AnnotateImageRequestKt(
            features = listOf(
                    FeatureKt(Feature.Type.LABEL_DETECTION),
                    FeatureKt(Feature.Type.DOCUMENT_TEXT_DETECTION)
            )
    ).toProto()

    println(request)
}