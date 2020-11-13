package org.grpc.full

import Hello
import HelloServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.grpc.full.HelloWorldClient.Companion.NAMES
import java.io.Closeable
import java.util.concurrent.TimeUnit

class HelloWorldClient(private val channel: ManagedChannel) : Closeable {
    private val stub = HelloServiceGrpcKt.HelloServiceCoroutineStub(channel)

    suspend fun sayHello(name: String) {
        val request = Hello.HelloRequest.newBuilder().setName(name).build()
        val response = stub.sayHello(request)
        println("Received: ${response.message}")
    }

    suspend fun sayHelloManyTimes(name: String) {
        val request = Hello.HelloRequest.newBuilder().setName(name).build()
        val response = stub.sayHelloManyTimes(request)
        response.collect { single -> println("Received: ${single.message}") }
    }

    suspend fun sayHelloToAll(names: Array<String>) {
        val request = generateNamesFlow(names)
        val response = stub.sayHelloToAll(request)
        println("Received: ${response.message}")
    }

    private fun generateNamesFlow(names: Array<String>) = flow {
        names.forEach {
            emit(
                Hello.HelloRequest.newBuilder().setName(it).build()
            )
            delay(500)
        }
    }

    suspend fun sayHelloBidirectional(names: Array<String>) {
        val request = generateNamesFlow(names)
        val response = stub.sayHelloBidirectional(request)
        response.collect { single -> println("Received: ${single.message}") }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    companion object {
        val NAMES = arrayOf("Karolina", "Michał", "Wojtek", "Yurii", "Szymon")
    }
}

suspend fun main() {
    val port = 50051
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    val client = HelloWorldClient(channel)

    val user = "Dominik"
//    client.sayHello(user)
//    client.sayHelloManyTimes(user)
//    client.sayHelloToAll(NAMES)
    client.sayHelloBidirectional(NAMES)
}

