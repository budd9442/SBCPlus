package skyblockclient.features

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ChatComponentText
import net.minecraftforge.event.world.ChunkWatchEvent.Watch
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.Sys
import skyblockclient.SkyblockClient.Companion.config
import skyblockclient.SkyblockClient.Companion.keyBinds
import skyblockclient.utils.Utils.addMsg
import skyblockclient.utils.Utils.isNpc
import skyblockclient.utils.Utils.rightClick
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object MacroSafety {
    var wasRecastFailEnabled = false
    val playerList = arrayListOf("")
    var tickCounter = 0
    var paused = false
    fun checkForPlayers(): Boolean {
        //val whitelist: List<String> = Arrays.asList(NotEnoughUpdates.INSTANCE.config.macroSafety.whitelist.split(","))
        val pos = (Minecraft.getMinecraft()).thePlayer.positionVector
        val range: Int = 20

        val ab = AxisAlignedBB(
            pos.xCoord - range, pos.yCoord - range, pos.zCoord - range,
            pos.xCoord + range, pos.yCoord + range, pos.zCoord + range
        )
        for (entity1 in (Minecraft.getMinecraft()).theWorld.getEntitiesWithinAABB<EntityPlayer>(
            EntityPlayer::class.java, ab
        )) {
            if (!isNpc(entity1) && entity1.name !== Minecraft.getMinecraft().thePlayer.name) {
                if (!playerList.contains(entity1.name)) {
                    (Minecraft.getMinecraft()).thePlayer.addChatMessage(
                        ChatComponentText(
                            ChatFormatting.BLUE.toString() + "+" + entity1
                                .name
                        )
                    )
                }
                playerList.add(entity1.name)
                return true
            }
        }
        playerList.add(Minecraft.getMinecraft().thePlayer.name)
        return false
    }


    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        tickCounter++
        if (config.pauseOnPlayer && config.autoFishing && config.antiAFK ) {
            if (tickCounter % 10 == 0) {
                if (!paused && checkForPlayers() ) {
                    paused = true
                    config.autoFishing = false
                    config.antiAFK = false
                    config.autoKill = false
                    pause()


                    val scheduler = Executors.newSingleThreadScheduledExecutor()
                    val task = scheduler.scheduleAtFixedRate({
                        if (!checkForPlayers() && paused) {
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




    fun pause(){
        wasRecastFailEnabled = AutoFisher.failsafeRecastEnabled
        AutoFisher.failsafeRecastEnabled = false
        addMsg("Player detected! pausing")
        (Minecraft.getMinecraft()).thePlayer.inventory.currentItem = config.weaponSlot
        Executors.newSingleThreadScheduledExecutor().schedule({
            rightClick()
        }, (Random.nextInt(500) + 300).toLong(), TimeUnit.MILLISECONDS)
    }

    fun resume(){

        addMsg("Resuming")
        (Minecraft.getMinecraft()).thePlayer.inventory.currentItem = config.rodSlot
        Executors.newSingleThreadScheduledExecutor().schedule({
            rightClick()
            AutoFisher.lastReCast = System.currentTimeMillis()
            if(wasRecastFailEnabled)AutoFisher.failsafeRecastEnabled = true
        }, (Random.nextInt(500) + 300).toLong(), TimeUnit.MILLISECONDS)
    }
}
