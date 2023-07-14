package app.framework.controllers.authentication

import org.koin.ktor.ext.inject

import io.ktor.server.routing.post
import io.ktor.server.routing.Route
import io.ktor.server.request.receive
import io.ktor.server.application.call

import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.plugins.requestvalidation.RequestValidationConfig

import app.domain.usecases.authentication.SignInUseCase

import app.framework.server.response

import app.framework.views.authentication.toParameter
import app.framework.views.authentication.SignInInView

import app.framework.mappers.message.MessageViewMapper
import app.framework.mappers.authentication.AuthenticationViewMapper

fun Route.signInController(configure: RequestValidationConfig) {

    val useCase by inject<SignInUseCase>()
    val failure by inject<MessageViewMapper>()
    val success by inject<AuthenticationViewMapper>()

    configure.validate<SignInInView> { body ->
        body.toParameter().isValid().fold(
            { ValidationResult.Invalid(it) },
            { ValidationResult.Valid }
        )
    }

    post("/sign/in") {
        val body = call.receive<SignInInView>()
        val parameters = body.toParameter()

        useCase(parameters).collect { response ->
            response.fold(
                { (status, data) -> call.response(status, failure(data)) },
                { (status, data) -> call.response(status, success(data)) },
            )
        }
    }
}
