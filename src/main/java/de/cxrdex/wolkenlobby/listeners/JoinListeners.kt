package de.cxrdex.wolkenlobby.listeners

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import de.cxrdex.wolkenlobby.Wolkenlobby
import de.cxrdex.wolkenlobby.utils.Vars
import net.kyori.adventure.text.Component
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*


class JoinListeners : Listener {
    val TOKEN = "mongodb+srv://theskyscout:RokBhzFJkPBcNz4T@cluster0.scfbc0h.mongodb.net/?retryWrites=true&w=majority"
    val client = MongoClients.create(TOKEN)
    val db = client.getDatabase("wolkenlos").getCollection("con")
    val db2 = client.getDatabase("wolkenlos").getCollection("conttry")
    val betakey = client.getDatabase("wolkenlos").getCollection("activated")
    var taskID = 0

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val array = ByteArray(7) // length is bounded by 7
        Random().nextBytes(array)
        val generatedString: String = generateStr()
        val player = event.player
        val resaults = db.find(eq("mc", "${player.uniqueId}")).first()
        if(resaults == null) {
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 255))
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 99999, 255))
            player.playSound(player.location, Sound.BLOCK_ANVIL_BREAK, 5F, 5F)
            player.sendMessage("§8--------------------------------------------------")
            player.sendMessage(Vars.PREFIX + "§cOoops §7Dein um die Bet zu nutzen\n" +
                    "§7musst du diesen Code an unseren Discord vot senden\n" +
                    "§a${generatedString}")
            db2.insertOne(Document().append("mc", "${player.uniqueId}").append("key", "${generatedString}").append("username", player.name))
            player.sendMessage("§8--------------------------------------------------")
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Wolkenlobby.plugin, Runnable{
                val resaults = db.find(eq("mc", "${player.uniqueId}")).first()
                if(resaults != null) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS)
                    player.removePotionEffect(PotionEffectType.SLOW)
                    player.sendMessage("§8--------------------------------------------------")
                    player.sendMessage(Vars.PREFIX + "§7Danke das du dein Minecraft Account mit Discord verbunden hast!\n" +
                            "§7Verbundener Discord Account: §a ${resaults["dcname"]}")
                    player.sendMessage("§8--------------------------------------------------")
                    Bukkit.getScheduler().cancelTask(taskID)
                    val resault2 = betakey.find(eq("dc", resaults["dc"])).first()
                    if(resault2 == null) {
                        player.kick(Component.text("§cDu besitzt keinen Betakey"))
                    }
                }
            }, 0,20)
        } else {
        val resault2 = betakey.find(eq("dc", resaults["dc"])).first()
        if(resault2 == null) {
            player.kick(Component.text("§cDu besitzt keinen Betakey"))
        }
        }
    }

    fun generateStr(): String {
        val alphabet = "ABCDEFGHIJKLMadaw1234567890-=OPQRSTUVWXYZ"
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
}