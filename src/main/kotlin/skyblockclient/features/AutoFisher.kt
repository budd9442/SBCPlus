package skyblockclient.features

import net.minecraft.client.Minecraft
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.init.Items
import net.minecraft.util.ChatComponentText
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import skyblockclient.SkyblockClient.Companion.config
import skyblockclient.SkyblockClient.Companion.keyBinds
import skyblockclient.features.WormFishing.getSilverFishCount
import skyblockclient.utils.MouseUtils
import skyblockclient.utils.Utils
import skyblockclient.utils.Utils.addMsg
import skyblockclient.utils.Utils.rand
import skyblockclient.utils.Utils.rightClick
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object AutoFisher {

    var count = 0
    var lastYaw = 0f
    var lastPitch = 0f
    var tickCounter = 0
    var queued = false
    var fishCountdown = 30
    var incoming = false
    var antiAFKToggled = false;
    var lastReCast = System.currentTimeMillis()
    var failsafeRecastEnabled = false
    var fishCount = 0
    var lastIncoming = System.currentTimeMillis()
    var fails = 0
    @SubscribeEvent
    fun onCatch(event: net.minecraftforge.event.entity.EntityEvent) {

        if (Minecraft.getMinecraft().thePlayer == null || !config.autoFishing) return;
        val heldItem = Minecraft.getMinecraft().thePlayer.heldItem
        if (heldItem == null || heldItem.item != Items.fishing_rod) return

        if (event.entity is EntityArmorStand) {
            if (event.entity.hasCustomName() && event.entity.customNameTag.contains("!!!")) {
                if (!event.entity.customNameTag.endsWith("§r")) {
                    event.entity.customNameTag += "§r"
                    Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("Fish Hooked"))
                    fails = 0
                    fishCount++
                    fishCountdown = -1
                    incoming =false
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        rightClick()
                    }, (Random.nextInt(150) + 50).toLong(), TimeUnit.MILLISECONDS)

                    Executors.newSingleThreadScheduledExecutor().schedule({
                        rightClick()
                        lastReCast = System.currentTimeMillis()
                    }, (Random.nextInt(config.recastDelay, config.recastDelay+200)).toLong(), TimeUnit.MILLISECONDS)
                }
            }
        }
    }

    @SubscribeEvent
    fun onIncoming(event: net.minecraftforge.event.entity.EntityEvent) {

        if (Minecraft.getMinecraft().thePlayer == null || !config.autoFishing) return;
        val heldItem = Minecraft.getMinecraft().thePlayer.heldItem
        if (heldItem == null || heldItem.item != Items.fishing_rod) return

        if (event.entity is EntityArmorStand) {
            if (event.entity.hasCustomName() && event.entity.customNameTag.contains("?")) {
                //addMsg("Incoming")
                lastIncoming = System.currentTimeMillis()
                fishCountdown+=1
                incoming = true
            }
        }
    }
    @SubscribeEvent
    fun onWorldLoad(event:WorldEvent.Load){
        config.antiAFK = false
        failsafeRecastEnabled = false
        fishCount = 0
    }
    @SubscribeEvent
    fun onWorldUnload(event:WorldEvent.Unload){
        config.antiAFK = false
        failsafeRecastEnabled=false
        fishCount = 0
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {

        if (Minecraft.getMinecraft().thePlayer == null || !config.autoFishing) return;
        val heldItem = Minecraft.getMinecraft().thePlayer.heldItem
        if (heldItem == null || heldItem.item != Items.fishing_rod) return




        if (event.phase == TickEvent.Phase.END) {
            tickCounter++
            if(fails>6){
                fails = 0
                config.antiAFK = false
                config.autoKill = false
                config.autoFishing = false
                failsafeRecastEnabled = false
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = config.weaponSlot
                addMsg("Disabling")
            }

            fishCountdown--



            if(failsafeRecastEnabled && ((System.currentTimeMillis() - lastReCast > config.autoRecastOnFail*1000) ) ){
                lastReCast = System.currentTimeMillis()
                incoming = false
                fishCountdown = config.fishCountdown
                Executors.newSingleThreadScheduledExecutor().schedule({

                    rightClick()

                    incoming = false
                    fails++
                    addMsg("Fail $fails")

                }, (Random.nextInt(300) + config.recastDelay).toLong(), TimeUnit.MILLISECONDS)
            }

            if(!config.antiAFK) return
            //anti afk
//            if(tickCounter%rand(config.antiAFKDelay, config.antiAFKDelay+50) == 0){
//                addMsg("looking")
//                if(lastYaw!=0f){
//                    MouseUtils.look(-lastYaw,-lastPitch, rand(3000, config.mouseMoveTime.toInt()))
//                    lastYaw=0f
//                    lastPitch=0f
//
//                }else{
//                    val yawJitter = Utils.randFloat(-1f, 1f)  // Tiny side-to-side movements
//                    val pitchJitter = Utils.randFloat(-1f,1f ) // Tiny up-down movements
//                    lastYaw = yawJitter
//                    lastPitch= pitchJitter
//                    MouseUtils.look(yawJitter,pitchJitter, rand(2000, config.mouseMoveTime.toInt()))
//
//                }
//            }
            if(fishCount> rand(2,6)){
                //addMsg("Jittering")
                if(lastYaw!=0f){
                    MouseUtils.look(-lastYaw,-lastPitch, rand(config.mouseMoveTime.toInt(),100+ config.mouseMoveTime.toInt()))
                    lastYaw=0f
                    lastPitch=0f

                }else{
                    val yawJitter = Utils.randFloat(-0.5f, 0.5f)  // Tiny side-to-side movements
                    val pitchJitter = Utils.randFloat(-0.5f,0.5f ) // Tiny up-down movements
                    lastYaw = yawJitter
                    lastPitch= pitchJitter
                    MouseUtils.look(yawJitter,pitchJitter, rand(config.mouseMoveTime.toInt(),100+ config.mouseMoveTime.toInt()))
                }
                fishCount=0

            }

        }
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (keyBinds[6].isPressed) {
            config.antiAFK = !config.antiAFK
            addMsg("Anti AFK : ${config.antiAFK}")
            if(config.antiAFK) WormFishing.tickCounter = 0
        }
        if(keyBinds[8].isPressed){
            config.wormFishingLavaESP = !config.wormFishingLavaESP
            addMsg("wormFishingLavaESP : ${config.wormFishingLavaESP}")
        }
    }

    @SubscribeEvent
    fun onKey2(event: InputEvent.KeyInputEvent) {
        if (keyBinds[7].isPressed) {
            if(failsafeRecastEnabled){
                failsafeRecastEnabled = false
                queued =false
                addMsg("failsafe recast disabled")
            }else{
                queued = true
                addMsg("failsafe recast will enable on next cast" )
            }


        }
    }

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR &&
            event.entityPlayer === Minecraft.getMinecraft().thePlayer
        ) {
            val heldItem = event.entityPlayer.heldItem

            if (heldItem != null && heldItem.item === Items.fishing_rod) {
                if(queued) {
                    failsafeRecastEnabled = true
                    addMsg("failsafe recast enabled")
                    queued = false
                    fishCountdown = config.fishCountdown
                }

                    lastReCast = System.currentTimeMillis()
                fishCountdown = config.fishCountdown
                incoming = false



            }
        }
    }


}