package skyblockclient.utils
import me.binarywriter.discordwebhooks.data.*
import net.minecraft.client.Minecraft
import skyblockclient.SkyblockClient.Companion.config
import skyblockclient.utils.Utils.addMsg
import java.awt.Color

object DiscordUtils {


    fun sendWebhook(title : String, desc : String, color:Color, ping : Boolean) {
        if(Minecraft.getMinecraft().thePlayer==null) return
        val webhook = Webhook {
            this.username = Minecraft.getMinecraft().thePlayer.name + "'s Slave"

            if(ping) this.content = "<@${config.userID}>"
            this.avatarUrl = "https://mc-heads.net/avatar/" + Minecraft.getMinecraft().thePlayer.name
            embed {
                this.description = desc
                this.title = title


                this.color = color



            }
        }

        webhook.send(config.webHookURL)

    }
    fun sendWebhookText(text:String,ping: Boolean) {
        if(Minecraft.getMinecraft().thePlayer==null) return
        val webhook = Webhook {
            this.username = Minecraft.getMinecraft().thePlayer.name + "'s Slave"

            this.avatarUrl = "https://mc-heads.net/avatar/" + Minecraft.getMinecraft().thePlayer.name
            this.content = text + if(ping)" <@${config.userID}>" else ""

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

        try {
            webhook.send(config.webHookURL)
        }catch (e:Exception){
            addMsg("Webhook fail")
            e.message?.let { addMsg(it) }
        }


    }
}
