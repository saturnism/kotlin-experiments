package dsl.option2

import com.google.cloud.vision.v1.*

@DslMarker
annotation class ProtobufDsl

fun AnnotateImageRequest(@ProtobufDsl block: AnnotateImageRequest.Builder.() -> Unit) =
        AnnotateImageRequest.newBuilder().apply(block).build()

fun AnnotateImageRequest.Builder.features(@ProtobufDsl block : Feature.Builder.() -> Unit) {
    this.addFeatures(Feature.newBuilder().apply(block))
}

fun AnnotateImageRequest.Builder.image(@ProtobufDsl block : Image.Builder.() -> Unit) {
    this.setImage(Image.newBuilder().apply(block))
}

fun Image.Builder.source(@ProtobufDsl block : ImageSource.Builder.() -> Unit) {
    this.setSource(ImageSource.newBuilder().apply(block))
}

fun main(args: Array<String>) {
    val request = AnnotateImageRequest {
        features {
            type = Feature.Type.DOCUMENT_TEXT_DETECTION
            maxResults = 4
        }
        features {
            type = Feature.Type.FACE_DETECTION
        }

        image {
            // CON: Can't control w/ DSL Marker
            features {

            }

            source {
                imageUri = "gs://my-bucket"
            }
        }
    }

    println(request)
}