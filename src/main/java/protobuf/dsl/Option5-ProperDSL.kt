package protobuf.dsl.option5

import com.google.cloud.vision.v1.*
import jdk.nashorn.internal.objects.NativeRegExp.source

@DslMarker
annotation class ProtobufDsl

fun AnnotateImageRequest(block: AnnotateImageRequestDsl.() -> Unit)
        : AnnotateImageRequest {
    // Create the real builder here, but not accessible from within the DSL
    // DSL methods delegates to this builder
    val builder = AnnotateImageRequest.newBuilder()

    AnnotateImageRequestDsl(builder).apply(block)

    // Builder object is now ready, go ahead and build the object
    return builder.build()
}

// Set the `builder` field private so that it's not accessible from DSL
@ProtobufDsl
class AnnotateImageRequestDsl(private val builder: AnnotateImageRequest.Builder) {
    fun features(block : FeaturesDsl.() -> Unit) {
        val features = mutableSetOf<Feature>()
        FeaturesDsl(features).apply(block)
        this.builder.addAllFeatures(features)
    }

    fun image(block: ImageDsl.() -> Unit) {
        val localBuilder = Image.newBuilder();
        ImageDsl(localBuilder).apply(block)
        this.builder.setImage(localBuilder.build())
    }

    fun imageContext(block: ImageContextDsl.() -> Unit) {
        val builder = ImageContext.newBuilder()
        ImageContextDsl(builder).apply(block)
        this.builder.imageContext = builder.build()
    }
}

@ProtobufDsl
class FeaturesDsl(private val features: MutableSet<Feature>) {
    fun feature(block : FeatureDsl.() -> Unit) {
        val builder = Feature.newBuilder()
        val dsl = FeatureDsl(builder).apply(block)

        features.add(builder
                .setType(dsl.type)
                .setMaxResults(dsl.maxResults)
                .setModel(dsl.model)
                .build())
    }
}

@ProtobufDsl
class FeatureDsl(private val builder : Feature.Builder,
                 var type : Feature.Type = Feature.Type.TYPE_UNSPECIFIED,
                 var maxResults : Int = 0,
                 var model : String = ""
) {
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
    val request = AnnotateImageRequest {
        image {
            source {
                imageUri = "gs://my-bucket/hello.jpg'"
            }
        }
        features {
            feature {
                type = Feature.Type.DOCUMENT_TEXT_DETECTION

                /* :) doesn't work here by using Dsl type
                build()
                */
            }
            feature {
                type = Feature.Type.LABEL_DETECTION
            }
        }
        imageContext {
            languageHints = listOf("en", "zh")

            /* :) can't use things out of context
            features { }
            */
        }
    }

    println(request)
}