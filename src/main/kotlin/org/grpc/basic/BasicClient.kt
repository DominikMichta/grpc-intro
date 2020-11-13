package org.grpc.basic

import Hello
import HelloServiceGrpcKt
import io.grpc.ManagedChannelBuilder

val channel = ManagedChannelBuilder
    .forAddress("localhost", 50051)
    .usePlaintext()
    .build()

val stub = HelloServiceGrpcKt.HelloServiceCoroutineStub(channel)

suspend fun main() {
    val response = stub.sayHello(Hello.HelloRequest.newBuilder().setName("Test").build())
    println(response.message)
}

