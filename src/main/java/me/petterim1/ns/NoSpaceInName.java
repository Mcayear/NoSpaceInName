package me.petterim1.ns;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.plugin.PluginBase;

/* renamed from: me.petterim1.ns.NoSpaceInName */
public class NoSpaceInName extends PluginBase implements Listener {
    private String replaceWith;

    public void onEnable() {
        saveDefaultConfig();
        this.replaceWith = getConfig().getString("replaceWith");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void DataPacketReceiveEvent(DataPacketReceiveEvent e) {
        if (e.getPacket() instanceof LoginPacket) {
            if (((LoginPacket) e.getPacket()).username == null) return;
            ((LoginPacket) e.getPacket()).username = ((LoginPacket) e.getPacket()).username.replace(" ", this.replaceWith);
        }
    }
}
