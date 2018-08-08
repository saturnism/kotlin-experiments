package protobuf.dsl.option4

import com.google.cloud.vision.v1.*

@DslMarker
annotation class ProtobufDsl

fun annotateImageRequest(block: AnnotateImageRequestDsl.() -> Unit)
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

    fun image(block: ImageDsl.() -> Unit) {
        val localBuilder = Image.newBuilder();
        val dsl = ImageDsl(localBuilder)

        dsl.block()

        this.builder.setImage(localBuilder.build())
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
    fun feature(block : Feature.Builder.() -> Unit) {
        val builder = Feature.newBuilder()

        builder.block();

        features.add(builder.build())
    }
}

@ProtobufDsl
class ImageContextDsl(private val builder: ImageContext.Builder) {
    var languageHints : Collection<String> = emptyList()
        set(value) {
            this.builder.clearLanguageHints()
            this.builder.addAllLanguageHints(value)
        }
}

@ProtobufDsl
class ImageDsl(private val builder: Image.Builder) {
    fun source(source: ImageSource.Builder.() -> Unit) {
        val localBuilder = ImageSource.newBuilder()

        this.builder.setSource(localBuilder.build())
    }
}

fun main(args: Array<String>) {
    val request = annotateImageRequest {
        image {
            source {
                imageUri = "gs://my-bucket/hello.jpg'"
            }
        }
        features {
            feature {
                type = Feature.Type.DOCUMENT_TEXT_DETECTION
            }
            feature {
                type = Feature.Type.LABEL_DETECTION
            }
        }
        imageContext {
            languageHints = listOf("en", "zh")
        }
    }

    println(request)
}