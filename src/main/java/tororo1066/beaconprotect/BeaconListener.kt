package tororo1066.beaconprotect

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Beacon
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object BeaconListener : Listener {

    @EventHandler
    fun onPlace(e : BlockPlaceEvent){
        if (!BeaconProtect.pluginEnable)return
        if (e.blockPlaced.type != Material.BEACON)return
        if (!BeaconProtect.protectLocations.containsKey(e.player.uniqueId)){
            BeaconProtect.protectLocations[e.player.uniqueId] = arrayListOf()
        }

        val protectLoc = ProtectLoc(e.block.world,Triple(e.blockPlaced.location.blockX,e.blockPlaced.location.blockY,e.blockPlaced.location.blockZ))
        if (!protectLoc.isSuccess())return
        BeaconProtect.protectLocations[e.player.uniqueId]!!.add(protectLoc)
        e.player.sendMessage(BeaconProtect.prefix + "§bビーコンが保護されました")


    }

    @EventHandler
    fun onBreak(e : BlockBreakEvent){
        if (!BeaconProtect.pluginEnable)return
        val type = e.block.type
        if (type != Material.IRON_BLOCK && type != Material.GOLD_BLOCK && type != Material.DIAMOND_BLOCK && type != Material.EMERALD_BLOCK && type != Material.NETHERITE_BLOCK && type != Material.BEACON)return
        for (list in BeaconProtect.protectLocations){
            for (loc in list.value){
                if (loc.isProtected(e.block.location)){
                    if (loc.isAllowPlayer(e.player.uniqueId) || e.player.uniqueId == list.key || e.player.isOp){
                        BeaconProtect.protectLocations[list.key]!!.remove(loc)
                        e.player.sendMessage(BeaconProtect.prefix + "§a保護を削除しました")
                        return

                    }
                    e.isCancelled = true
                    e.player.sendMessage(BeaconProtect.prefix + "§4${Bukkit.getOfflinePlayer(list.key).name}によってこのビーコンは守られています")
                    return

                }
            }
        }
    }

    @EventHandler
    fun onOpenBeaconMenu(e : PlayerInteractEvent){
        if (!BeaconProtect.pluginEnable)return
        if (e.hand == EquipmentSlot.OFF_HAND)return
        if (e.action != Action.RIGHT_CLICK_BLOCK)return
        if (!e.hasBlock())return
        if (e.clickedBlock!!.type != Material.BEACON)return
        for (list in BeaconProtect.protectLocations){
            for (loc in list.value){
                if (loc.isProtected(e.clickedBlock!!.location)){
                    if (!loc.isAllowPlayer(e.player.uniqueId) && e.player.uniqueId != list.key && !e.player.isOp){
                        e.isCancelled = true
                        e.player.sendMessage(BeaconProtect.prefix + "§4${Bukkit.getOfflinePlayer(list.key).name}によってこのビーコンは守られています")
                        return
                    }
                }
            }
        }
    }

    //保留
    //@EventHandler
    //    fun onExplosive(e : EntityExplodeEvent){
    //        if (!BeaconProtect.pluginEnable)return
    //        for (list in BeaconProtect.protectLocations){
    //            for (loc in list.value){
    //                for (block in e.blockList()){
    //                    if (loc.isProtected(block.location)){
    //                        e.isCancelled = true
    //                        return
    //                    }
    //                }
    //            }
    //        }
    //
    //    }
}