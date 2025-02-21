package fi.espoo.vekkuli.views.employee

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.util.HtmlUtils
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@Component
class SanitizationUtil {
    fun sanitize(input: Any?): Any? =
        when (input) {
            is String -> htmlEscape(input) // Sanitize strings
            is Collection<*> -> input.map { sanitize(it) } // Recursively sanitize collections
            is Map<*, *> -> input.mapValues { sanitize(it.value) } // Recursively sanitize maps
            is Enum<*> -> input // Do not sanitize enums
            is Int -> input // Do not sanitize integers
            else -> sanitizeObject(input) // Sanitize objects
        }

    private fun <T : Any> sanitizeObject(obj: T?): T? {
        if (obj == null) return null

        // Get the class of the object
        val kClass: KClass<out T> = obj::class

        // Use the primary constructor to create a sanitized copy
        val constructor = kClass.primaryConstructor ?: return obj

        val params =
            constructor.parameters.associateWith { parameter ->
                // Go through the properties of the object
                val property = kClass.declaredMemberProperties.find { it.name == parameter.name }
                property?.let {
                    // Make the property accessible and get the value
                    it.isAccessible = true
                    val value = it.getter.call(obj) ?: return@associateWith null
                    when (it.returnType.classifier) {
                        String::class -> htmlEscape(value as String) // Sanitize strings
                        Collection::class -> sanitize(value) // Recursively sanitize collections
                        List::class -> sanitize(value) // Recursively sanitize lists
                        Map::class -> sanitize(value) // Recursively sanitize maps
                        else -> if (value::class.isData) sanitize(value) else value // Recursively sanitize objects
                    }
                }
            }
        // Return the new sanitized object using the primary constructor
        return constructor.callBy(params)
    }

    private fun htmlEscape(input: String) = HtmlUtils.htmlEscape(input, "UTF-8")
}

@Aspect
@Component
class SanitizationAspect
    @Autowired
    constructor(
        private val sanitizationUtil: SanitizationUtil
    ) {
        // This advice will be applied to all methods that have a parameter annotated with @SanitizeInput
        @Around("execution(* *(.., @SanitizeInput (*), ..))")
        fun sanitizeInputs(joinPoint: ProceedingJoinPoint): Any? {
            val methodSignature = joinPoint.signature as MethodSignature
            val parameters = methodSignature.method.parameters
            val args = joinPoint.args

            // Sanitize the arguments based on the custom annotation
            val sanitizedArgs =
                args
                    .mapIndexed { index, arg ->
                        if (parameters[index].getAnnotation(SanitizeInput::class.java) != null) {
                            val sanitizedArg = sanitizationUtil.sanitize(arg)
                            sanitizedArg
                        } else {
                            arg
                        }
                    }.toTypedArray() // Convert back to an array to use with proceed

            // Proceed with the sanitized arguments
            return joinPoint.proceed(sanitizedArgs)
        }
    }

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SanitizeInput
