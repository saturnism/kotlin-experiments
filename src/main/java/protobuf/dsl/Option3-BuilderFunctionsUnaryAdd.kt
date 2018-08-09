package protobuf.dsl.option3

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageSource

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

