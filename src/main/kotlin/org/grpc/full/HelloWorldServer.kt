package org.grpc.full

import Hello
import HelloServiceGrpcKt
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class HelloWorldServer(private val port: Int) {
    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(HelloWorldService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@HelloWorldServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class HelloWorldService : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
        override suspend fun sayHello(request: Hello.HelloRequest): Hello.HelloReply = Hello.HelloReply
            .newBuilder()
            .setMessage("Hello ${request.name}")
            .build()

        override fun sayHelloManyTimes(request: Hello.HelloRequest) = flow {
            emit(createReply(request.name))
            delay(500)

            emit(createReply(request.name))
            delay(500)

            emit(createReply(request.name))
        }

        override suspend fun sayHelloToAll(requests: Flow<Hello.HelloRequest>): Hello.HelloReply {
            requests.collect { request ->
                println("Received name ${request.name}")
                delay(500)
            }
            return Hello.HelloReply
                .newBuilder()
                .setMessage("Hello everyone")
                .build()
        }

        override fun sayHelloBidirectional(requests: Flow<Hello.HelloRequest>): Flow<Hello.HelloReply> {
            return flow {
                requests.collect { request ->
                    println("Received name ${request.name}")
                    emit(createReply(request.name))
                    delay(500)
                }
            }
        }

        private fun createReply(name: String) = Hello.HelloReply
            .newBuilder()
            .setMessage("Hello $name")
            .build()
    }
}

fun main() {
    val port = 50051
    val server = HelloWorldServer(port)
    server.start()
    server.blockUntilShutdown()
}
