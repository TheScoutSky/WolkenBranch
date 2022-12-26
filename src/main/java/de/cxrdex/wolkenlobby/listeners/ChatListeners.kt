package de.cxrdex.wolkenlobby.listeners

import com.mongodb.client.MongoClients
import org.bson.Document
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class ChatListeners: Listener {
    val TOKEN = "mongodb+srv://theskyscout:RokBhzFJkPBcNz4T@cluster0.scfbc0h.mongodb.net/?retryWrites=true&w=majority"
    val client = MongoClients.create(TOKEN)
    val chat = client.getDatabase("wolkenlos").getCollection("chat")

    @EventHandler
    fun onChat(event: PlayerChatEvent) {
        val player = event.player
        val text = event.message
        chat.insertOne(Document().append("user", "${player.name}").append("text", "${text}"))
    }
}