package tororo1066.beaconprotect

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.annotations.Nullable

object BeaconCommands : CommandExecutor, @Nullable TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNullOrEmpty()){
            sender.sendMessage("§b=========================BeaconProtect========================")

            sender.sendMessage("§eビーコンは置くことで土台ごと保護できます")
            sender.sendMessage("§e保護はビーコン、土台を壊すことで消せます")

            sender.sendMessage("§d/beacon list §a保護しているリストを表示します")
            sender.sendMessage("§d/beacon allow <x> <y> <z> <mcid>")
            sender.sendMessage("§a指定したプレイヤーが保護したビーコンにアクセスできるようにします")
            sender.sendMessage("§d/beacon deny <x> <y> <z> <mcid>")
            sender.sendMessage("§a指定したプレイヤーが保護したビーコンにアクセスできないようにします")
            sender.sendMessage("§d/beacon allowlist <x> <y> <z>")
            sender.sendMessage("§a保護されたビーコンに誰がアクセスできるか確認します")

            if (sender.isOp){
                sender.sendMessage("§1/beacon on §aモードをオンにします")
                sender.sendMessage("§1/beacon off §aモードをオフにします §c保護がすべて消えます！")
                sender.sendMessage("§1/beacon clear §a保護を全削除します")
            }

            sender.sendMessage("§b==Author:tororo_1066======BeaconProtect=============Version:1.0==")
            return true
        }

        when(args[0]){
            "on"->{
                if (!sender.isOp){
                    sender.sendMessage(BeaconProtect.prefix + "§4権限がありません")
                    return true
                }

                if (BeaconProtect.pluginEnable){
                    sender.sendMessage(BeaconProtect.prefix + "§4すでにモードがオンです")
                    return true
                }

                BeaconProtect.pluginEnable = true
                BeaconProtect.plugin.config.set("defaultMode",true)
                BeaconProtect.plugin.saveConfig()
                sender.sendMessage(BeaconProtect.prefix + "§aモードがオンになりました")
                return true
            }

            "off"->{
                if (!sender.isOp){
                    sender.sendMessage(BeaconProtect.prefix + "§4権限がありません")
                    return true
                }

                if (!BeaconProtect.pluginEnable){
                    sender.sendMessage(BeaconProtect.prefix + "§4すでにモードがオフです")
                    return true
                }

                BeaconProtect.pluginEnable = false
                BeaconProtect.plugin.config.set("defaultMode",false)
                BeaconProtect.plugin.saveConfig()
                BeaconProtect.protectLocations.clear()
                sender.sendMessage(BeaconProtect.prefix + "§aモードがオフになり、保護データが全削除されました")
                return true
            }

            "clear"->{
                if (!sender.isOp){
                    sender.sendMessage(BeaconProtect.prefix + "§4権限がありません")
                    return true
                }

                BeaconProtect.protectLocations.clear()
                sender.sendMessage(BeaconProtect.prefix + "§a保護データをすべて削除しました")
                return true
            }

            "list"->{
                if (sender !is Player){
                    sender.sendMessage(BeaconProtect.prefix + "§4このコマンドはプレイヤーのみ実行できます")
                    return true
                }

                if (!BeaconProtect.pluginEnable){
                    sender.sendMessage(BeaconProtect.prefix + "§4現在停止中です")
                    return true
                }

                if (!BeaconProtect.protectLocations.containsKey(sender.uniqueId) || BeaconProtect.protectLocations[sender.uniqueId]?.isEmpty() == true){
                    sender.sendMessage(BeaconProtect.prefix + "§4保護しているビーコンがありません")
                    return true
                }

                sender.sendMessage(BeaconProtect.prefix + "§a保護リスト§f(§eクリックでコピー§f)")
                for (list in BeaconProtect.protectLocations[sender.uniqueId]!!){
                    sender.sendMessage(Component.text("${list.beaconLoc.first}、${list.beaconLoc.second}、${list.beaconLoc.third}").clickEvent(
                        ClickEvent.copyToClipboard("${list.beaconLoc.first} ${list.beaconLoc.second} ${list.beaconLoc.third}")).hoverEvent(HoverEvent.showText(
                        Component.text("§6ここをクリックでコピー！"))))
                }

                return true
            }

            "allow"->{
                if (sender !is Player){
                    sender.sendMessage(BeaconProtect.prefix + "§4このコマンドはプレイヤーのみ実行できます")
                    return true
                }

                if (!BeaconProtect.pluginEnable){
                    sender.sendMessage(BeaconProtect.prefix + "§4現在停止中です")
                    return true
                }
                if (args.size != 5){
                    sender.sendMessage(BeaconProtect.prefix + "§d/beacon allow <x> <y> <z> <mcid>")
                    return true
                }
                val x = args[1].toIntOrNull()
                val y = args[2].toIntOrNull()
                val z = args[3].toIntOrNull()
                val player = Bukkit.getPlayer(args[4])

                if (x == null || y == null || z == null){
                    sender.sendMessage(BeaconProtect.prefix + "§d/beacon allow <x> <y> <z> <mcid>")
                    return true
                }

                if (player == null){
                    sender.sendMessage(BeaconProtect.prefix + "§4オンラインのプレイヤーを選択してください")
                    return true
                }

                if (!BeaconProtect.protectLocations.containsKey(sender.uniqueId) || BeaconProtect.protectLocations[sender.uniqueId]?.isEmpty() == true){
                    sender.sendMessage(BeaconProtect.prefix + "§4保護しているビーコンがありません")
                    return true
                }

                val protectLoc = BeaconProtect.protectLocations[sender.uniqueId]!!.find { it.beaconLoc == Triple(x,y,z) }
                if (protectLoc == null){
                    sender.sendMessage(BeaconProtect.prefix + "§4そこは保護されていません")
                    return true
                }

                if (protectLoc.allowPlayers.contains(player.uniqueId)){
                    sender.sendMessage(BeaconProtect.prefix + "§4既に追加されています")
                    return true
                }

                protectLoc.allowPlayers.add(player.uniqueId)

                sender.sendMessage(BeaconProtect.prefix + "§b${player.name}§aを追加しました")
                return true
            }

            "deny"->{
                if (sender !is Player){
                    sender.sendMessage(BeaconProtect.prefix + "§4このコマンドはプレイヤーのみ実行できます")
                    return true
                }

                if (!BeaconProtect.pluginEnable){
                    sender.sendMessage(BeaconProtect.prefix + "§4現在停止中です")
                    return true
                }
                if (args.size != 5){
                    sender.sendMessage(BeaconProtect.prefix + "§d/beacon deny <x> <y> <z> <mcid>")
                    return true
                }
                val x = args[1].toIntOrNull()
                val y = args[2].toIntOrNull()
                val z = args[3].toIntOrNull()
                val player = Bukkit.getOfflinePlayer(args[4])

                if (x == null || y == null || z == null){
                    sender.sendMessage(BeaconProtect.prefix + "§d/beacon deny <x> <y> <z> <mcid>")
                    return true
                }


                if (!BeaconProtect.protectLocations.containsKey(sender.uniqueId) || BeaconProtect.protectLocations[sender.uniqueId]?.isEmpty() == true){
                    sender.sendMessage(BeaconProtect.prefix + "§4保護しているビーコンがありません")
                    return true
                }

                val protectLoc = BeaconProtect.protectLocations[sender.uniqueId]!!.find { it.beaconLoc == Triple(x,y,z) }
                if (protectLoc == null){
                    sender.sendMessage(BeaconProtect.prefix + "§4そこは保護されていません")
                    return true
                }

                if (!protectLoc.allowPlayers.remove(player.uniqueId)){
                    sender.sendMessage(BeaconProtect.prefix + "§4このプレイヤーは追加されていません")
                    return true
                }

                sender.sendMessage(BeaconProtect.prefix + "§b${player.name}§aを削除しました")
                return true
            }

            "allowlist"->{
                if (sender !is Player){
                    sender.sendMessage(BeaconProtect.prefix + "§4このコマンドはプレイヤーのみ実行できます")
                    return true
                }

                if (!BeaconProtect.pluginEnable){
                    sender.sendMessage(BeaconProtect.prefix + "§4現在停止中です")
                    return true
                }
                if (args.size != 4){
                    sender.sendMessage(BeaconProtect.prefix + "§d/beacon allowlist <x> <y> <z> ")
                    return true
                }
                val x = args[1].toIntOrNull()
                val y = args[2].toIntOrNull()
                val z = args[3].toIntOrNull()

                if (x == null || y == null || z == null){
                    sender.sendMessage(BeaconProtect.prefix + "§d/beacon allowlist <x> <y> <z>")
                    return true
                }

                if (!BeaconProtect.protectLocations.containsKey(sender.uniqueId) || BeaconProtect.protectLocations[sender.uniqueId]?.isEmpty() == true){
                    sender.sendMessage(BeaconProtect.prefix + "§4保護しているビーコンがありません")
                    return true
                }

                val protectLoc = BeaconProtect.protectLocations[sender.uniqueId]!!.find { it.beaconLoc == Triple(x,y,z) }
                if (protectLoc == null){
                    sender.sendMessage(BeaconProtect.prefix + "§4そこは保護されていません")
                    return true
                }

                sender.sendMessage("§a${x}、${y}、${z}で許可されているプレイヤー")
                for (list in protectLoc.allowPlayers){
                    Bukkit.getOfflinePlayer(list).name?.let { sender.sendMessage(it) }
                }
                return true
            }

        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.size == 1){
            if (sender.isOp)return mutableListOf("allow","deny","list","allowlist","on","off","clear")
            return mutableListOf("allow","deny","list","allowlist")
        }

        if (args.size == 2 && (args[0] == "allow" || args[1] == "deny" || args[1] == "allowlist")){
            return mutableListOf("<x>")
        }

        if (args.size == 3 && (args[0] == "allow" || args[1] == "deny" || args[1] == "allowlist")){
            return mutableListOf("<y>")
        }

        if (args.size == 4 && (args[0] == "allow" || args[1] == "deny" || args[1] == "allowlist")){
            return mutableListOf("<z>")
        }

        if (args.size == 5 && (args[0] == "allow" || args[1] == "deny")){
            val online = mutableListOf<String>()
            for (player in Bukkit.getOnlinePlayers()){
                if (player == sender)continue
                online.add(player.name)
            }
            return online
        }
        return null
    }


}