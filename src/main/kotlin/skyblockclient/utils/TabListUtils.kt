package skyblockclient.utils


import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Comparator;


object TabListUtils {

    val playerOrdering: Ordering<NetworkPlayerInfo> = Ordering.from(PlayerComparator())

    @SideOnly(Side.CLIENT)
    class PlayerComparator internal constructor() : Comparator<NetworkPlayerInfo> {
        override fun compare(o1: NetworkPlayerInfo, o2: NetworkPlayerInfo): Int {
            val team1 = o1.playerTeam
            val team2 = o2.playerTeam
            return ComparisonChain.start().compareTrueFirst(
                o1.gameType != WorldSettings.GameType.SPECTATOR,
                o2.gameType != WorldSettings.GameType.SPECTATOR
            )
                .compare(
                    if (team1 != null) team1.registeredName else "",
                    if (team2 != null) team2.registeredName else ""
                )
                .compare(o1.gameProfile.name, o2.gameProfile.name).result()
        }
    }

    fun getTabList(): List<String> {
        val players: List<NetworkPlayerInfo> =
            playerOrdering.sortedCopy(Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoMap)

        val result: MutableList<String> = ArrayList()

        for (info in players) {
            val name = Minecraft.getMinecraft().ingameGUI.tabList.getPlayerName(info)
            result.add(name)
        }
        return result
    }

    fun getTabListPlayersUnprocessed(): MutableList<String> {
        val players: List<NetworkPlayerInfo> =
            playerOrdering.sortedCopy(Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoMap)

        val result: MutableList<String> = ArrayList()

        for (info in players) {
            val name = Minecraft.getMinecraft().ingameGUI.tabList.getPlayerName(info)
            result.add(name)
        }
        return result
    }

    fun getTabListPlayersSkyblock(): List<String> {
        val tabListPlayersFormatted = getTabListPlayersUnprocessed()
        val playerList: MutableList<String> = ArrayList()
        tabListPlayersFormatted.removeAt(0) // remove "Players (x)"
        var firstPlayer: String? = null
        for (s in tabListPlayersFormatted) {
            var s = s
            val a = s.indexOf("]")
            if (a == -1) continue
            if (s.length < a + 2) continue

            s = s.substring(a + 2).replace("§([0-9]|[a-z])".toRegex(), "").replace("♲", "").trim { it <= ' ' }
            if (firstPlayer == null) firstPlayer = s
            else if (s == firstPlayer)  // it returns two copy of the player list for some reason
                break


            playerList.add(s.split(" ").first())
        }
        return playerList.toMutableSet().toMutableList()
    }




}
