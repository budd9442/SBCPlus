//package skyblockclient.features
//
//import net.minecraft.client.Minecraft
//import net.minecraft.client.gui.inventory.GuiChest
//import net.minecraft.inventory.Slot
//import net.minecraftforge.client.event.ClientChatReceivedEvent
//import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent
//import net.minecraftforge.fml.client.config.GuiUtils
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
//import org.kodein.di.contextFinder
//import skyblockclient.config.Config
//import skyblockclient.utils.Utils
//import java.util.concurrent.Executors
//import java.util.concurrent.TimeUnit
//
//
//object Slayer {
//    var mc = Minecraft.getMinecraft()
//
//    private fun clickSlot(slot: Int, windowAdd: Int) {
//        Minecraft.getMinecraft().playerController.windowClick(
//            Minecraft.getMinecraft().thePlayer.openContainer.windowId + windowAdd,
//            slot,
//            0,
//            0,
//            Minecraft.getMinecraft().thePlayer
//        )
//    }
//    @SubscribeEvent
//    fun onChat(event: ClientChatReceivedEvent) {
//        val executor = Executors.newSingleThreadScheduledExecutor()
//        if (Config.autoBatphone && event.type.toInt() == 0) {
//            if (event.message.unformattedText.contains("BOSS SLAIN")) {
//                Utils.addMsg("calling maddox to restart slayer")
//                var batphoneSlot = Utils.findItemInHotbar("batphone")
//                if(batphoneSlot==-1) return
//                else{
//                    executor.schedule({
//                       Minecraft.getMinecraft().thePlayer.inventory.currentItem = batphoneSlot  // Switch to slot 2
//                    }, 200, TimeUnit.MILLISECONDS)
//
//                    executor.schedule({
//                       Utils.rightClick()
//                    }, 400, TimeUnit.MILLISECONDS)
//                }
//            }
//        }
//    }
//    @SubscribeEvent
//    fun onBackgroundDrawn(event: BackgroundDrawnEvent) {
//        val executor = Executors.newSingleThreadScheduledExecutor()
//        if (mc.thePlayer == null || mc.theWorld == null) return
//        if (!Config.autoBatphone) return
//        Utils.addMsg("GUI OPENED")
//        if (Utils.getInventoryName(event.gui).contains("Slayer")) {
//            val chestInventory: List<Slot> =
//                (mc.currentScreen as GuiChest).inventorySlots.inventorySlots
//            if (!chestInventory[13].hasStack) return
//
//            val displayName: String = chestInventory[13].getStack().getDisplayName()
//            Utils.addMsg("Displayname : $displayName")
//            if (displayName.contains("Complete") || displayName.contains("Failed")) {
//                Utils.addMsg("COMPLETE OR FAILED")
//                executor.schedule({
//                    clickSlot(13, 0)
//                }, 200, TimeUnit.MILLISECONDS)
//
//                executor.schedule({
//                    clickSlot(10 + 2, 1)
//                }, 400, TimeUnit.MILLISECONDS)
//                executor.schedule({
//                    clickSlot(11 + 2, 2)
//                }, 600, TimeUnit.MILLISECONDS)
//                executor.schedule({
//                    clickSlot(11, 3) //confirm
//                }, 800, TimeUnit.MILLISECONDS)
//
//
//            } else {
//                executor.schedule({
//                    clickSlot(10 + 2, 0)
//                }, 400, TimeUnit.MILLISECONDS)
//
//                executor.schedule({
//                    clickSlot(11 + 2, 1)
//                }, 600, TimeUnit.MILLISECONDS)
//
//                executor.schedule({
//                    clickSlot(11, 2) //confirm
//                }, 800, TimeUnit.MILLISECONDS)
//
//            }
//        }
//
//
//
//    }
//
//}