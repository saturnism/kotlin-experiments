package protobuf.dsl.option3

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageSource
import protobuf.dsl.option2.features

@DslMarker
annotation class ProtobufDsl

fun AnnotateImageRequest(@ProtobufDsl block: AnnotateImageRequest.Builder.() -> Unit)
        : AnnotateImageRequest {
    val builder = AnnotateImageRequest.newBuilder()

    builder.block()

    // Builder object is now ready, go ahead and build the object
    return builder.build()
}

fun Image(@ProtobufDsl block: Image.Builder.() -> Unit): Image {
    val builder = Image.newBuilder()

    builder.apply(block)

    return builder.build()
}

fun ImageSource(@ProtobufDsl block : ImageSource.Builder.() -> Unit) : ImageSource {
    val builder = ImageSource.newBuilder()

    builder.apply(block)

    return builder.build()
}

fun Feature(@ProtobufDsl block : Feature.Builder.() -> Unit) : Feature {
    val builder = Feature.newBuilder()

    builder.apply(block)

    return builder.build()
}

operator fun AnnotateImageRequest.Builder.plus(feature : Feature) {
    this.addFeatures(feature)
}


fun main(args: Array<String>) {
   val request = AnnotateImageRequest {
       image = Image {
           // :D neat!
           source = ImageSource {
               imageUri = "gs://hello-stuff"
           }

           // :( eak
           image = Image {}

           // :( doh
           build()
       }

       // :( calling it multiple times, unclear which methods should be called multiple times
       features {
           type = Feature.Type.IMAGE_PROPERTIES
       }

       features {
           type = Feature.Type.LANDMARK_DETECTION
       }

       // :( not intuitable
       this + Feature {
           type = Feature.Type.DOCUMENT_TEXT_DETECTION
       }

       this + Feature {
           type = Feature.Type.FACE_DETECTION
       }
   }


   println(request)
}

