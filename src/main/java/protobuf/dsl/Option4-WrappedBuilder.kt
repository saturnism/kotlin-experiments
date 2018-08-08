package protobuf.dsl.option4

import com.google.cloud.vision.v1.*
import com.google.protobuf.ByteString

@DslMarker
annotation class ProtobufDsl

fun AnnotateImageRequest(block: AnnotateImageRequestDsl.() -> Unit)
        : AnnotateImageRequest {
    // Create the real builder here, but not accessible from within the DSL
    // DSL methods delegates to this builder
    val builder = AnnotateImageRequest.newBuilder()

    val dsl = AnnotateImageRequestDsl(builder)
    dsl.block()

    // Builder object is now ready, go ahead and build the object
    return builder.build()
}

// Set the `builder` field private so that it's not accessible from DSL
@ProtobufDsl
class AnnotateImageRequestDsl(private val builder: AnnotateImageRequest.Builder) {
    fun features(block : FeaturesDsl.() -> Unit) {
        val features = mutableSetOf<Feature>()
        val dsl = FeaturesDsl(features)

        dsl.block()

        this.builder.addAllFeatures(features)
    }

    fun image(bytes: ByteArray) {
        this.builder.image = Image.newBuilder().setContent(ByteString.copyFrom(bytes)).build()
    }

    fun image(imageUri: String = "") {
        this.builder.image = Image.newBuilder().setSource(ImageSource.newBuilder()
                .setImageUri(imageUri)
                .build()).build()
    }

    fun imageContext(block: ImageContextDsl.() -> Unit) {
        val builder = ImageContext.newBuilder()
        val dsl = ImageContextDsl(builder)

        dsl.block()

        this.builder.imageContext = builder.build()
    }
}

@ProtobufDsl
class FeaturesDsl(private val features: MutableSet<Feature>) {
    fun feature(type : Feature.Type, model : String = "", maxResults : Int = 0) {
        val builder = Feature.newBuilder()
        builder.setType(type)
        builder.setModel(model)
        builder.setMaxResults(maxResults)

        features.add(builder.build())
    }
}

@ProtobufDsl
class ImageContextDsl(private val builder: ImageContext.Builder) {
    fun languageHints(hints : Collection<String>) {
        this.builder.addAllLanguageHints(hints)
    }
}

fun main(args: Array<String>) {
    val request = AnnotateImageRequest {
        features {
            feature(Feature.Type.LABEL_DETECTION)
            feature(Feature.Type.DOCUMENT_TEXT_DETECTION)
        }
        image("gs://my-bucket/hello.jpg")
        imageContext {
            languageHints(listOf("en", "zh"))
        }
    }

    println(request)
}