package protobuf.dsl.option2

import com.google.cloud.vision.v1.*
import com.google.protobuf.ByteString

@DslMarker
annotation class ProtobufDsl

fun annotateImageRequest(@ProtobufDsl block: AnnotateImageRequest.Builder.() -> Unit)
        : AnnotateImageRequest {
    val builder = AnnotateImageRequest.newBuilder()

    builder.block()

    // Builder object is now ready, go ahead and build the object
    return builder.build()
}

fun AnnotateImageRequest.Builder.features(@ProtobufDsl block : Feature.Builder.() -> Unit) {
    val builder = Feature.newBuilder()
    builder.block()

    this.addFeatures(builder)
}


fun AnnotateImageRequest.Builder.image(@ProtobufDsl block : Image.Builder.() -> Unit) {
    val builder = Image.newBuilder()

    builder.block()

    this.setImage(builder)
}

fun Image.Builder.source(block : ImageSource.Builder.() -> Unit) {
    val builder = ImageSource.newBuilder()

    builder.block()

    this.setSource(builder)
}

fun main(args: Array<String>) {
    val request = annotateImageRequest {
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