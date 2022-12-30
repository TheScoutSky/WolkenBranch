package de.cxrdex.wolkenlobby.listeners

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import de.cxrdex.wolkenlobby.Wolkenlobby
import de.cxrdex.wolkenlobby.utils.Vars
import net.kyori.adventure.text.Component
import net.kyori.adventure.Adventure
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*


class JoinListeners : Listener {
    val TOKEN = "mongodb+srv://theskyscout:RokBhzFJkPBcNz4T@cluster0.scfbc0h.mongodb.net/?retryWrites=true&w=majority"
    val client = MongoClients.create(TOKEN)
    val db = client.getDatabase("wolkenlos").getCollection("user")
    var taskID = 0
    var notRegistered = ArrayList<Player>()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val array = ByteArray(7) // length is bounded by 7
        Random().nextBytes(array)
        val generatedString: String = generateStr()
        val player = event.player
        val resaults = db.find(eq("mc", "${player.uniqueId}")).first()
        if(resaults == null) {
            notRegistered.add(player)
            val component: TextComponent = TextComponent("${generatedString}")
            component.isBold = true
            component.color = ChatColor.GREEN
            component.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, generatedString)
            component.setHoverEvent(
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ComponentBuilder("Click to Copy!").color(net.md_5.bungee.api.ChatColor.GRAY).create()
                )
            )
            player.sendMessage("§8--------------------------------------------------")
            player.sendMessage(Vars.PREFIX + "<gradient:red:#bd6a17>Oops</gradient> §7Du musst zu erst dein Account verifzieren\n" +
                    "§7sende Folgenden Code an unseren Discord Bot")
            player.sendMessage(component)
            player.sendMessage("§8--------------------------------------------------")
            db.insertOne(Document().append("mc", "${player.uniqueId}").append("key", "${generatedString}").append("username", player.name))
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Wolkenlobby.plugin, Runnable{
                val resaults = db.find(eq("mc", "${player.uniqueId}")).first()
                if(resaults["linked"] != null) {
                    notRegistered.remove(player)
                    player.sendMessage("§8--------------------------------------------------")
                    player.sendMessage(Vars.PREFIX + "§7Danke das du dein Minecraft Account mit Discord verbunden hast!\n" +
                            "§7Verbundener Discord Account: §a ${resaults["dc-name"]}")
                    player.sendMessage("§8--------------------------------------------------")
                    val betakey = db.find(eq("mc", "${player.uniqueId}")).first()
                    if(!betakey.equals("true")) {
                        player.kick(Component.text("§cDu besitzt keinen Betakey"))
                    }
                    Bukkit.getScheduler().cancelTask(taskID)
                }
            }, 0,20)
        } else if(resaults["linked"] == null) {
            notRegistered.add(player)
            val component: TextComponent = TextComponent("${resaults["key"]}")
            component.isBold = true
            component.color = ChatColor.GREEN
            component.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, generatedString)
            component.setHoverEvent(
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ComponentBuilder("Click to Copy!").color(net.md_5.bungee.api.ChatColor.GRAY).create()
                )
            )
            notRegistered.add(player)
            player.sendMessage("§8--------------------------------------------------")
            player.sendMessage(Vars.PREFIX + "§cOoops §7Du musst zu erst dein Account verifzieren\n" +
                    "§7sende Folgenden Code an unseren Discord Bot")
            player.sendMessage(component)
            player.sendMessage("§8--------------------------------------------------")
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Wolkenlobby.plugin, Runnable{
                val resaults = db.find(eq("mc", "${player.uniqueId}")).first()
                if(resaults?.get("linked") != null) {
                    notRegistered.remove(player)
                    player.sendMessage("§8--------------------------------------------------")
                    player.sendMessage(Vars.PREFIX + "§7Danke das du dein Minecraft Account mit Discord verbunden hast!\n" +
                            "§7Verbundener Discord Account: §a ${resaults["dc-name"]}")
                    player.sendMessage("§8--------------------------------------------------")
                    val betakey = db.find(eq("mc", "${player.uniqueId}")).first()
                    if(!betakey["activated"]?.equals("true")!!) {
                        player.kick(Component.text("§cDu besitzt keinen Betakey"))
                    }
                    Bukkit.getScheduler().cancelTask(taskID)
                }
            }, 0,20)
        } else {
            val betakey = db.find(eq("mc", "${player.uniqueId}")).first()
            val act = betakey["activated"]
            if(act == null) {
                player.kick(Component.text("§cDu besitzt keinen Betakey"))
            }
            Bukkit.getScheduler().cancelTask(taskID)
        }
    }

    fun generateStr(): String {
        val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-!@#$%&*()"
        val sb = StringBuilder()
        val random = Random()
        val length = 7
        var randomString = ""
        for (i in 0 until length) {
            val index = random.nextInt(alphabet.length)
            val randomChar = alphabet[index]
            sb.append(randomChar)
            randomString = sb.toString()
        }
        return randomString
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if(notRegistered.contains(event.player)) {
            event.isCancelled = true
        }
    }
}