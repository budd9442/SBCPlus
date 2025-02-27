package skyblockclient.utils
import net.minecraft.client.Minecraft
import skyblockclient.utils.Utils.rand
import java.util.*
import kotlin.math.*

object MouseUtils {


    fun look(yawOffset: Float, pitchOffset: Float, durationMs: Int) {
        val mc = Minecraft.getMinecraft()
        val timer = Timer()
        val totalYaw = floatArrayOf(0f)
        val totalPitch = floatArrayOf(0f)
        val steps = rand(30, 50)  // Randomized steps for realism

        val task = object : TimerTask() {
            var t = 0

            override fun run() {
                if (t >= steps) {
                    timer.cancel()
                    return
                }

                // Smooth easing function (sinusoidal easing in-out)
                val progress = t.toFloat() / steps
                val easedYaw = yawOffset * sin((progress * Math.PI / 2).toFloat()) / steps
                val easedPitch = pitchOffset * sin((progress * Math.PI / 2).toFloat()) / steps

                mc.thePlayer.rotationYaw += easedYaw
                mc.thePlayer.rotationPitch += easedPitch
                totalYaw[0] += easedYaw
                totalPitch[0] += easedPitch
                t++

                // Stop condition with small tolerance
                if (abs(totalYaw[0]) >= abs(yawOffset) - 0.1 && abs(totalPitch[0]) >= abs(pitchOffset) - 0.1) {
                    timer.cancel()
                }
            }
        }

        timer.schedule(task, 0L, (durationMs / steps).toLong()) // Adaptive speed per frame
    }







}