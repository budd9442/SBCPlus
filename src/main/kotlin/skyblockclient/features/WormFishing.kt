package skyblockclient.features

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntitySilverfish
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ChatComponentText
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import skyblockclient.SkyblockClient.Companion.config
import skyblockclient.SkyblockClient.Companion.keyBinds
import skyblockclient.features.MacroSafety.checkForPlayers
import skyblockclient.features.MacroSafety.playerList

import skyblockclient.utils.MouseUtils
import skyblockclient.utils.TabListUtils.getTabListPlayersSkyblock
import skyblockclient.utils.Utils
import skyblockclient.utils.Utils.addMsg
import skyblockclient.utils.Utils.isNpc
import skyblockclient.utils.Utils.rand
import skyblockclient.utils.Utils.rightClick
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.times


object WormFishing {

    var count = 0
    var tickCounter = 0
    var antiAFKToggled = false;
    var mobCap = false


    @SubscribeEvent
    fun onEntityJoinWorld(event: net.minecraftforge.event.entity.EntityEvent) {


    }
    @SubscribeEvent
    fun onWorldLoad(event:WorldEvent.Load){

    }
    @SubscribeEvent
    fun onWorldUnload(event:WorldEvent.Unload){

    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if(Minecraft.getMinecraft().thePlayer == null || !config.antiAFK || !config.autoFishing || !config.autoKill ) return

        if ( event.phase == TickEvent.Phase.END) {
            tickCounter++
            if(getSilverFishCount()>18 && !mobCap){
                mobCap = true
                tickCounter = config.autoKillDelay*20
            }

            //addMsg("$tickCounter % ${config.autoKillDelay} : ${tickCounter% (config.autoKillDelay*20)}" )


            if(tickCounter % (config.autoKillDelay*20) == 0){
                addMsg("swapping")
                (Minecraft.getMinecraft()).thePlayer.inventory.currentItem = config.weaponSlot
            }

            if(tickCounter % (config.autoKillDelay*20+20) == 0){
                rightClick()
            }

            if(tickCounter % (config.autoKillDelay*20+50) == 0){
                (Minecraft.getMinecraft()).thePlayer.inventory.currentItem = config.rodSlot
            }

            if(tickCounter % (config.autoKillDelay*20+100) == 0){
                rightClick()
                tickCounter = 0
                mobCap = false
            }


        }
    }

//    @SubscribeEvent
//    fun onSilverfish(event: net.minecraftforge.event.entity.player.) {
//
//        if (Minecraft.getMinecraft().thePlayer == null || !config.autoFishing || !config.autoKillAt15 || !config.antiAFK) return;
//        val heldItem = Minecraft.getMinecraft().thePlayer.heldItem
//        if (heldItem == null || heldItem.item != Items.fishing_rod) return
//
//        if (event.entity is EntitySilverfish) {
//
//
//        }
//    }

    fun getSilverFishCount(): Int {
        var count =0
        val pos = (Minecraft.getMinecraft()).thePlayer.positionVector
        val range: Int = 4
        val ab = AxisAlignedBB(
            pos.xCoord - range, pos.yCoord - range, pos.zCoord - range,
            pos.xCoord + range, pos.yCoord + range, pos.zCoord + range
        )
        for (entity1 in (Minecraft.getMinecraft()).theWorld.getEntitiesWithinAABB<EntitySilverfish>(
            EntitySilverfish::class.java, ab
        )) {
            count++
        }
        return count

    }





}