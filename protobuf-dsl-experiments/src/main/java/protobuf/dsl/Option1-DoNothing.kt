package dsl.option1

import com.google.cloud.vision.v1.*

fun main(args: Array<String>) {
    val request = with (AnnotateImageRequest.newBuilder()) {
        addFeatures(with (Feature.newBuilder()) {
            type = Feature.Type.DOCUMENT_TEXT_DETECTION
            build()
        })
        addFeatures(with (Feature.newBuilder()) {
            type = Feature.Type.LABEL_DETECTION
            build()
        })
        image = with (Image.newBuilder()) {
            source = with (ImageSource.newBuilder()) {
                imageUri = "gs://my-bucket/hello.png"
                build()
            }
            build()
        }
        build()
    }

    println(request)
}