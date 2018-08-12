package kprotoc

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.compiler.PluginProtos
import com.salesforce.jprotoc.Generator
import com.salesforce.jprotoc.ProtoTypeMap
import com.salesforce.jprotoc.ProtocPlugin
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.streams.toList

class KProtocGenerator : Generator() {
    fun DescriptorProtos.FileDescriptorProto.toFileDescriptor(): Descriptors.FileDescriptor =
            Descriptors.FileDescriptor.buildFrom(this,
                    this.dependencyList.stream()
                            .map { dependency ->
                                DescriptorProtos.FileDescriptorProto.newBuilder()
                                        .setName(dependency)
                                        .build().toFileDescriptor()
                            }.toList().toTypedArray())

    override fun generate(request: PluginProtos.CodeGeneratorRequest): Stream<PluginProtos.CodeGeneratorResponse.File> {
        val protoTypes = ProtoTypeMap.of(request.protoFileList);

        return request.protoFileList.stream()
                .filter {
                    request.fileToGenerateList.contains(it.name)
                }
                .map { file -> file.toFileDescriptor() }
                .map { file -> extractKotlinExtensionContext(protoTypes, file) }
                .map { buildFile(it) }
    }

    fun buildFile(ctx: KotlinExtensionContext) =
            PluginProtos.CodeGeneratorResponse.File.newBuilder().apply {
                name = ctx.absoluteFileName()
                content = ctx.generateContent()
            }.build()

    fun DescriptorProtos.FileDescriptorProto.packageName() =
            if (!options.javaPackage.isNullOrBlank()) {
                options.javaPackage
            } else {
                `package`
            }

    fun Descriptors.FieldDescriptor.toJavaClassName(protoTypes: ProtoTypeMap): String =
            when (javaType) {
                Descriptors.FieldDescriptor.JavaType.INT -> "Int"
                Descriptors.FieldDescriptor.JavaType.LONG -> "Long"
                Descriptors.FieldDescriptor.JavaType.FLOAT -> "Long"
                Descriptors.FieldDescriptor.JavaType.DOUBLE -> "Double"
                Descriptors.FieldDescriptor.JavaType.BOOLEAN -> "Boolean"
                Descriptors.FieldDescriptor.JavaType.STRING -> "String"
                Descriptors.FieldDescriptor.JavaType.BYTE_STRING -> "ByteString"
                Descriptors.FieldDescriptor.JavaType.ENUM -> enumType.fullName
                Descriptors.FieldDescriptor.JavaType.MESSAGE -> if (isMapField) {
                    val keyType = messageType.findFieldByName("key").toJavaClassName(protoTypes)
                    val valueType = messageType.findFieldByName("value").toJavaClassName(protoTypes)
                    "Map<$keyType,$valueType>"
                } else {
                    this.messageType.fullName
                }
            }

    fun String.stripProtoExtension() =
            if (endsWith(".protodevel")) {
                removeSuffix(".protodevel")
            } else {
                removeSuffix(".proto")
            }

    fun String.beginWithUpperCase(): String {
        return when (this.length) {
            0 -> ""
            1 -> this.toUpperCase()
            else -> this[0].toUpperCase() + this.substring(1)
        }
    }

    fun String.toCamelCase(): String {
        return this.split('_').map {
            it.beginWithUpperCase()
        }.joinToString("")
    }

    fun extractKotlinExtensionContext(protoTypes: ProtoTypeMap, file: Descriptors.FileDescriptor) =
            KotlinExtensionContext(
                    packageName = file.`package`,
                    fileName = Paths.get(file.name).fileName.toString().stripProtoExtension().toCamelCase() + ".kt",
                    messageTypes = file.messageTypes.stream()
                            .map {
                                MessageTypeContext(
                                        className = it.name,
                                        name = it.name,
                                        javaClassName = it.name,
                                        deprecated = it.options.deprecated ?: false,
                                        repeatedFields = it.fields.stream()
                                                .filter {
                                                    val className = it.toJavaClassName(protoTypes)
                                                    System.err.println(className + " -> " + protoTypes.toJavaTypeName(className))
                                                    it.isRepeated && !it.isMapField
                                                }.map {
                                                    ListFieldContext(
                                                            name = it.name,
                                                            repeatedClassName = it.toJavaClassName(protoTypes),
                                                            nameCamelcase = it.name.toCamelCase())
                                                }.toList(),
                                        mapFields = it.fields.stream()
                                                .filter {
                                                    it.isMapField
                                                }
                                                .map {
                                                    MapFieldContext(
                                                            name = it.name,
                                                            nameCamelcase = it.name.toCamelCase(),
                                                            mapType = it.toJavaClassName(protoTypes)
                                                    )
                                                }
                                                .toList()
                                )
                            }.toList()
            )

    fun KotlinExtensionContext.absoluteFileName() =
            if (packageName.isBlank()) {
                fileName
            } else {
                packageName.replace('.', '/') + "/" + fileName
            }

    fun KotlinExtensionContext.generateContent() = applyTemplate("KotlinExtension.mustache", this)
}

data class KotlinExtensionContext(
        val packageName: String,
        val fileName: String,
        val messageTypes: List<MessageTypeContext>
)

data class MessageTypeContext(
        val name: String,
        val className: String,
        val javaClassName: String,
        val deprecated: Boolean,
        val repeatedFields: List<ListFieldContext>,
        val mapFields: List<MapFieldContext>
)

data class ListFieldContext(
        val name: String,
        val nameCamelcase: String,
        val repeatedClassName: String
)

data class MapFieldContext(
        val name: String,
        val nameCamelcase: String,
        val mapType : String
)

fun main(args: Array<String>) {
    ProtocPlugin.generate(KProtocGenerator())
}
