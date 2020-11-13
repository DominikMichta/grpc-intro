package org.grpc.basic

import io.grpc.Server
import io.grpc.ServerBuilder

class HelloWorldService : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
    override suspend fun sayHello(request: Hello.HelloRequest): Hello.HelloReply {
        println("Received name ${request.name}")
        return Hello.HelloReply
            .newBuilder()
            .setMessage("Hello ${request.name}")
            .build()
    }
}

val server: Server = ServerBuilder
    .forPort(50051)
    .addService(HelloWorldService())
    .build()

fun main() {
    server.start()
    server.awaitTermination()
}

