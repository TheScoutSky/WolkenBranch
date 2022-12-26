package de.cxrdex.wolkenlobby.utils

import com.mongodb.client.MongoClients

object Vars {
    val PREFIX = "§b§lCardinal §8|  §7"
    val TOKEN = "mongodb+srv://theskyscout:RokBhzFJkPBcNz4T@cluster0.scfbc0h.mongodb.net/?retryWrites=true&w=majority"
    val client = MongoClients.create(TOKEN)
    val user = client.getDatabase("wolkenlos").getCollection("user")
    val chat = client.getDatabase("wolkenlos").getCollection("chat")
}