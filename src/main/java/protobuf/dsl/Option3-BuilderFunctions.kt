package protobuf.dsl.option3

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageSource

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

@ProtobufDsl
class FEATURES() {
    val list = ArrayList<Feature>()
    operator fun Feature.unaryPlus() {
        list += this
    }
    @Deprecated(level = DeprecationLevel.ERROR, message = "Incorrect context")
    fun features(init : FEATURES.() -> Unit){}
}

fun AnnotateImageRequest.Builder.features(block : FEATURES.() -> Unit) {
    val features = FEATURES()
    this.addAllFeatures(features.apply(block).list)
}

var AnnotateImageRequest.Builder.features : List<Feature>
get() {
    return this.featuresList
}
set(value) {
    this.addAllFeatures(value)
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

       // :D falls in line w/ the above idioms
       features = listOf(
               Feature {
                   type = Feature.Type.WEB_DETECTION
               }
       )

       // :( Neat trick, but can be confusing
       features {
           +Feature {
               type = Feature.Type.LANDMARK_DETECTION
           }

           +Feature {
               type = Feature.Type.WEB_DETECTION
           }

           /* Not possible to call features in this nested call
           features {  }
           */
       }

   }

   println(request)
}

