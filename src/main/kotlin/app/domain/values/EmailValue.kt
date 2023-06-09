package app.domain.values

import arrow.core.left
import arrow.core.right
import arrow.core.Either

@JvmInline
value class EmailValue(val value: String) : ValueObject {

    override operator fun invoke(): Either<String, String> {
        val (isValid, message) = rule(value)

        return if (isValid) value.right() else message.left()
    }

    private fun rule(value: String): Pair<Boolean, String> {
        val pattern = "^[a-zA-Z\\d+_.-]+@[a-zA-Z\\d.-]+\\.[a-zA-z]{2,3}\$".toRegex()

        return when {
            value.isEmpty() -> false to "Preencha seu e-mail."
            pattern.matches(value).not() -> false to "O e-mail precisa ser válido."
            else -> true to value
        }
    }
}
