package skyblockclient.command

import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import skyblockclient.SkyblockClient.Companion.config
import skyblockclient.SkyblockClient.Companion.display
import skyblockclient.config.Config.mimicMessage
import skyblockclient.features.CapeManager
import skyblockclient.utils.DiscordUtils
import skyblockclient.utils.DiscordUtils.player

import skyblockclient.utils.Utils.modMessage
import java.awt.Color

class SkyblockClientCommands : CommandBase() {
    override fun getCommandName(): String {
        return "sbclient"
    }

    override fun getCommandAliases(): List<String> {
        return listOf(
            "skyblockclient",
            "sbc"
        )
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/$commandName"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            display = config.gui()
            return
        }
        when (args[0].lowercase()) {
            "mimicmessage" -> {
                args[0] = ""
                val message = args.joinToString(" ").trim()
                mimicMessage = message
                modMessage("§aMimic message changed to §f$message")
            }
            "test" -> {
                DiscordUtils.sendWebhook(
                    "Alert", player.name + " Logged in! <@" + config.userID + ">", Color.GREEN
                )
            }
            "af" -> {
                if(args.size <2 ) config.autoFishing = !config.autoFishing
                config.autoFishing = args[1] == "on"
            }
        }
    }
}
