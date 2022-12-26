package de.cxrdex.wolkenlobby

import de.cxrdex.wolkenlobby.listeners.ChatListeners
import de.cxrdex.wolkenlobby.listeners.JoinListeners
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class Wolkenlobby : JavaPlugin() {

    companion object {
        lateinit var plugin: Wolkenlobby
    }

    override fun onEnable() {
        registerEvents()
        plugin = this
    }

    fun registerEvents() {
        var manager = Bukkit.getPluginManager()
        manager.registerEvents(JoinListeners(), this)
        manager.registerEvents(ChatListeners(), this)
    }

}