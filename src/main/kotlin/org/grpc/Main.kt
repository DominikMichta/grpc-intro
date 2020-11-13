package org.grpc

fun main() {
    val bytes = Hello.HelloRequest
        .newBuilder()
        .setName("SomeName")
        .build()
        .toByteArray()



    println(Hello.HelloRequest.parseFrom(bytes).name)
}
