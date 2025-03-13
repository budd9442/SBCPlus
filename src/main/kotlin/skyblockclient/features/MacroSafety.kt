package skyblockclient.features

import com.mojang.realmsclient.gui.ChatFormatting
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ChatComponentText
import net.minecraftforge.event.world.ChunkWatchEvent.Watch
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.Sys
import skyblockclient.SkyblockClient.Companion.config
import skyblockclient.SkyblockClient.Companion.keyBinds
import skyblockclient.features.AutoFisher.fails
import skyblockclient.features.AutoFisher.failsafeRecastEnabled
import skyblockclient.features.AutoFisher.fishCount
import skyblockclient.utils.DiscordUtils
import skyblockclient.utils.Utils.addMsg
import skyblockclient.utils.Utils.isNpc
import skyblockclient.utils.Utils.rightClick
import java.awt.Color
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object MacroSafety {
    val mc = Minecraft.getMinecraft()
    var wasRecastFailEnabled = false
    val playerList = arrayListOf("")
    var tickCounter = 0
    var paused = false
    var lastPause = System.currentTimeMillis()

    @OptIn(DelicateCoroutinesApi::class)
    fun checkForPlayers(): Boolean {

        val thePlayer = mc.thePlayer
        val pos = thePlayer.positionVector
        val range: Int = config.playerRange

        val ab = AxisAlignedBB(
            pos.xCoord - range, pos.yCoord - range, pos.zCoord - range,
            pos.xCoord + range, pos.yCoord + range, pos.zCoord + range
        )

        val currentPlayers = mc.theWorld.getEntitiesWithinAABB<EntityPlayer>(
            EntityPlayer::class.java, ab
        ).filter {
            !isNpc(it) && it.name != thePlayer.name && it.name !in config.whitelist
        }.map { it.name }.toSet()

        val playersToAdd = currentPlayers - playerList
        val playersToRemove = playerList - currentPlayers

        for (player in playersToAdd) {
            thePlayer.addChatMessage(ChatComponentText(ChatFormatting.BLUE.toString() + "+" + player))
            GlobalScope.launch {
                DiscordUtils.sendWebhookText("+$player",false)

            }
        }

        for (player in playersToRemove) {
            thePlayer.addChatMessage(ChatComponentText(ChatFormatting.RED.toString() + "-" + player))
            GlobalScope.launch {
                DiscordUtils.sendWebhookText("-$player",false)

            }
        }

        playerList.removeAll(playersToRemove)
        playerList.addAll(playersToAdd)

        return currentPlayers.isNotEmpty()
    }


    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        tickCounter++
        if (config.pauseOnPlayer && config.autoFishing && config.antiAFK ) {
            if (tickCounter % 10 == 0) {
                if (!paused && checkForPlayers() && System.currentTimeMillis()-lastPause>5000) {
                    paused = true
                    config.autoFishing = false
                    config.antiAFK = false
                    config.autoKill = false
                    playerList.clear()
                    pause()



                    val scheduler = Executors.newSingleThreadScheduledExecutor()
                    val task = scheduler.scheduleAtFixedRate({
                        if (!checkForPlayers() && paused && System.currentTimeMillis()- lastPause > 3000) {
                            paused = false
                            config.autoFishing = true
                            config.antiAFK = true
                            config.autoKill = true
                            resume()

                            scheduler.shutdown() // Terminates the task
                        }
                    }, 500, 500, TimeUnit.MILLISECONDS) // Runs every 500ms
                }
            }
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    @SubscribeEvent
    fun onWorldLoad(event:WorldEvent.Load){
        if( config.worldChangeAlert && config.antiAFK){
            GlobalScope.launch {
            DiscordUtils.sendWebhook("Alert","World changed ",Color.RED, config.pingUser)

            }
        }

        config.antiAFK = false
        failsafeRecastEnabled = false
        fishCount = 0
    }
    @OptIn(DelicateCoroutinesApi::class)
    @SubscribeEvent
    fun onWorldUnload(event:WorldEvent.Unload){
        if( config.worldChangeAlert && config.antiAFK){
            GlobalScope.launch {
                DiscordUtils.sendWebhook(
                    "Alert",
                    "World changed " ,
                    Color.RED,
                    config.pingUser
                )
            }
        }
        config.antiAFK = false
        failsafeRecastEnabled=false
        fishCount = 0
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun pause(){
         lastPause = System.currentTimeMillis()
        if(config.playerInRangeAlert)
        GlobalScope.launch {
            DiscordUtils.sendWebhook("Alert","Player in range! Pausing" , Color.RED, config.pingUser)
        }
        wasRecastFailEnabled = AutoFisher.failsafeRecastEnabled
        AutoFisher.failsafeRecastEnabled = false
        addMsg("Player detected! pausing")
        (Minecraft.getMinecraft()).thePlayer.inventory.currentItem = config.weaponSlot
        Executors.newSingleThreadScheduledExecutor().schedule({
            rightClick()
        }, (Random.nextInt(500) + 300).toLong(), TimeUnit.MILLISECONDS)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun resume(){
        if(config.playerInRangeAlert)
        GlobalScope.launch {
            DiscordUtils.sendWebhook("Alert","No players in range! Resuming", Color.GREEN, false)
        }
        addMsg("Resuming")
        (Minecraft.getMinecraft()).thePlayer.inventory.currentItem = config.rodSlot
        Executors.newSingleThreadScheduledExecutor().schedule({
            rightClick()
            AutoFisher.lastReCast = System.currentTimeMillis()
            if(wasRecastFailEnabled)AutoFisher.failsafeRecastEnabled = true
        }, (Random.nextInt(500) + 300).toLong(), TimeUnit.MILLISECONDS)
    }
}
