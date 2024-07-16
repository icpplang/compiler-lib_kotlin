package ru.landgrafhomyak.icpp.ast.builders._generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.Import
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass
import ru.landgrafhomyak.icpp.ast.builders._IcppParserState_AutoGenerateMock
import ru.landgrafhomyak.icpp.parser.environment.BuilderState
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos

class AutoMockableStatesCollector(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val interfaces = ArrayList<KSClassDeclaration>()

    override fun process(resolver: Resolver): List<KSAnnotated> {

        resolver
            .getSymbolsWithAnnotation(
                annotationName = _IcppParserState_AutoGenerateMock::class.qualifiedName ?: throw RuntimeException("Something went wrong, can't get qualname of annotation"),
                inDepth = false // todo
            )
            .forEach { i ->
                if (i !is KSClassDeclaration) TODO()
                this.interfaces.add(i)
            }

        return emptyList()
    }

    override fun finish() {
        val dstPackageName = _IcppParserState_AutoGenerateMock::class.java.`package`.name
        val wildcard = WildcardTypeName.producerOf(Any::class.asClassName().copy(nullable = true))
        val elemType = KClass::class
            .asClassName()
            .plusParameter(
                BuilderState::class.asClassName()
                    .plusParameter(wildcard)
                    .plusParameter(wildcard)
                    .plusParameter(wildcard)
            )

        val initializer = buildString {
            append("hashSetOf")
            append("(")
            for (i in this@AutoMockableStatesCollector.interfaces) {
                append("\n")
                append(" ".repeat(8))
                append(i.toClassName().canonicalName)
                append("::class")
                append("\n")
                append(" ".repeat(8))
                append(" as ")
                append(elemType.toString())
                append(",")
            }
            append("\n)")
        }


        val obj = PropertySpec
            .builder("_AutoMockableStatesList_data", Collection::class.asClassName().parameterizedBy(elemType))
            .addModifiers(KModifier.INTERNAL)
            .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("\"ObjectPropertyName\"").addMember("\"UNCHECKED_CAST\"").build())
            .initializer(initializer)
            .build()

        val file = FileSpec.builder(dstPackageName, "_AutoMockableStatesList_data.kt")
            .addProperty(obj)
//            .also { f -> this.interfaces.map { i -> i }.toSet().forEach { i -> f.addImport(i.toClassName()) } }
            .build()

        val generator = this.environment.codeGenerator
        generator.createNewFile(
            dependencies = Dependencies.ALL_FILES,
            packageName = dstPackageName,
            fileName = "_AutoMockableStatesList_data.kt"
        ).use { os ->
            os.writer().use { osw ->
                file.writeTo(osw)
            }
        }
    }
}