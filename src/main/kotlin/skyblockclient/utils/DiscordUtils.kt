package skyblockclient.utils
import me.binarywriter.discordwebhooks.data.*

class DiscordUtils {

    fun sendWebhook(username:String,content:String) {
        val webhook = Webhook {
            this.username = "Username"
            this.content = "Content"
            embed {
                this.title = "Embed Title"
                this.description = "Embed Description"

                author {
                    this.name = "Author Name"
                }

                field {
                    this.name = "Field Name"
                    this.value = "Field Value"
                }

            }
        }

        webhook.send("URL HERE")

    }
}
