package protobuf.dsl.option3

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageSource

@DslMarker
annotation class ProtobufDsl

fun AnnotateImageRequest(@ProtobufDsl block: AnnotateImageRequest.Builder.() -> Unit) =
        AnnotateImageRequest.newBuilder().apply(block).build()

fun Image(@ProtobufDsl block: Image.Builder.() -> Unit): Image =
        Image.newBuilder().apply(block).build()

fun ImageSource(@ProtobufDsl block : ImageSource.Builder.() -> Unit) : ImageSource =
        ImageSource.newBuilder().apply(block).build()

fun Feature(@ProtobufDsl block : Feature.Builder.() -> Unit) : Feature =
        Feature.newBuilder().apply(block).build()

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

   }

   println(request)
}

