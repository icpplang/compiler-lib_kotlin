package ru.landgrafhomyak.icpp.ast.builders._generator

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSTypeReference
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
import com.squareup.kotlinpoet.ksp.toTypeName
import java.util.LinkedList
import jdk.javadoc.internal.doclets.toolkit.taglets.snippet.Style.Link
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
                inDepth = false
            )
            .forEach { i -> this.interfaces.add(this.assert(i) ?: return@forEach) }

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
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("\"ObjectPropertyName\"")
                    .addMember("\"UNCHECKED_CAST\"")
                    .addMember("\"RemoveRedundantQualifierName\"")
                    .build()
            )
            .initializer(initializer)
            .build()

        val file = FileSpec.builder(dstPackageName, "_AutoMockableStatesList_data")
            .addProperty(obj)
            .build()

        val generator = this.environment.codeGenerator
        generator.createNewFile(
            dependencies = Dependencies.ALL_FILES,
            packageName = dstPackageName,
            fileName = "_AutoMockableStatesList_data"
        ).use { os ->
            os.writer().use { osw ->
                file.writeTo(osw)
            }
        }
    }

    private fun isInherits(classToCheck: KSClassDeclaration, expectedBaseClass: KClass<*>): Boolean {
        val q = classToCheck.superTypes.toMutableSet()
        while (q.isNotEmpty()) {
            val baseRef = q.first().also(q::remove)
            val base = baseRef.resolve()
            if (base.toClassName() == expectedBaseClass.asClassName())
                return true
            (base.declaration as KSClassDeclaration).superTypes.forEach(q::add)
        }
        return false
    }

    private fun KSPLogger.error(e: KSNode, msg: String): Nothing? {
        this.error(message = msg, symbol = e)
        return null
    }

    private fun assert(e: KSAnnotated): KSClassDeclaration? {
        if (e !is KSClassDeclaration)
            return this.environment.logger.error(e, "@${_IcppParserState_AutoGenerateMock::class.simpleName} must be used only on class or interface declaration")
        if (!e.isPublic())
            return this.environment.logger.error(e, "Mockable interface must be public")
        if (e.classKind != ClassKind.INTERFACE)
            return this.environment.logger.error(e, "@${_IcppParserState_AutoGenerateMock::class.simpleName} must be used only on interfaces")
        if (!this.isInherits(e, BuilderState::class))
            return this.environment.logger.error(e, "Mockable interface must inherit '${BuilderState::class.qualifiedName}'")
        return e
    }
}