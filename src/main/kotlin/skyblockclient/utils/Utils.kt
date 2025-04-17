package skyblockclient.utils

import gg.essential.universal.UChat
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChatComponentText
import net.minecraft.util.StringUtils
import skyblockclient.SkyblockClient.Companion.CHAT_PREFIX
import skyblockclient.SkyblockClient.Companion.mc
import java.util.*
import kotlin.math.round


object Utils {
    fun findItemInHotbar(name: String): Int {
        val inv: InventoryPlayer = Minecraft.getMinecraft().thePlayer.inventory
        for (i in 0..8) {
            val curStack = inv.getStackInSlot(i)
            if (curStack != null) {
                if (curStack.displayName.contains(name)) {
                    return i
                }
            }
        }
        return -1
    }
    fun getInventoryName(gui: GuiScreen?): String {
        return if (gui is GuiChest) {
            ((gui as GuiChest).inventorySlots as ContainerChest).lowerChestInventory.displayName.unformattedText
        } else ""
    }
    // Generates a random number between min and max

    // Generates a small random float (for jitter effect)
    fun randFloat(min: Float, max: Float): Float {
        return min + Random().nextFloat() * (max - min)
    }
    fun Any?.equalsOneOf(vararg other: Any): Boolean = other.any { this == it }

    fun String.removeFormatting(): String = StringUtils.stripControlCodes(this)

    val ItemStack.extraAttributes: NBTTagCompound?
        get() = this.getSubCompound("ExtraAttributes", false)

    val ItemStack.itemID: String
        get() = this.extraAttributes?.getString("id") ?: ""

    val ItemStack.lore: List<String>
        get() = this.tagCompound?.getCompoundTag("display")?.getTagList("Lore", 8)?.let {
            val list = mutableListOf<String>()
            for (i in 0 until it.tagCount()) {
                list.add(it.getStringTagAt(i))
            }
            list
        } ?: emptyList()

    fun modMessage(message: String) = UChat.chat("$CHAT_PREFIX $message")
    fun rand(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1) + min
    }

    fun renderText(
        text: String,
        x: Int,
        y: Int,
        scale: Double = 1.0,
        color: Int = 0xFFFFFF,
    ) {
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.disableBlend()
        GlStateManager.scale(scale, scale, scale)
        var yOffset = y - mc.fontRendererObj.FONT_HEIGHT
        text.split("\n").forEach {
            yOffset += (mc.fontRendererObj.FONT_HEIGHT * scale).toInt()
            mc.fontRendererObj.drawString(
                it,
                round(x / scale).toFloat(),
                round(yOffset / scale).toFloat(),
                color,
                true
            )
        }
        GlStateManager.popMatrix()
    }

    fun rightClick() {
        val method = try {
            Minecraft::class.java.getDeclaredMethod("func_147121_ag")
        } catch (e: NoSuchMethodException) {
            Minecraft::class.java.getDeclaredMethod("rightClickMouse")
        }
        method.isAccessible = true
        method.invoke(Minecraft.getMinecraft())
    }

    fun leftClick() {
        val method = try {
            Minecraft::class.java.getDeclaredMethod("func_147116_af")
        } catch (e: NoSuchMethodException) {
            Minecraft::class.java.getDeclaredMethod("clickMouse")
        }
        method.isAccessible = true
        method.invoke(Minecraft.getMinecraft())
    }

    fun addMsg(msg: String){
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(msg))

    }

    fun isNpc(entity: Entity): Boolean {
        if (entity !is EntityOtherPlayerMP) {
            return false
        }
        return !TabListUtils.getTabListPlayersSkyblock().contains(entity.getName())
    }
}
