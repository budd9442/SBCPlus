package skyblockclient.utils
import me.binarywriter.discordwebhooks.data.*
import net.minecraft.client.Minecraft
import skyblockclient.SkyblockClient.Companion.config
import java.awt.Color

object DiscordUtils {
    var player = Minecraft.getMinecraft().thePlayer

    fun sendWebhook(title : String, desc : String, color:Color) {
        if(player==null) return
        val webhook = Webhook {
            this.username = player.name + "'s Slave"

            this.avatarUrl = "https://mc-heads.net/avatar/" + player.name
            embed {
                this.description = desc
                this.title = title


                this.color = color



            }
        }

        webhook.send(config.webHookURL)

    }
    fun sendWebhookText(text:String) {
        if(player==null) return
        val webhook = Webhook {
            this.username = player.name + "'s Slave"

            this.avatarUrl = "https://mc-heads.net/avatar/" + player.name
            this.content = text
//            embed {
//                this.description = desc
//                this.title = title
//
//
//                this.color = color
//
//
//
//            }
        }

        webhook.send(config.webHookURL)

    }
}
