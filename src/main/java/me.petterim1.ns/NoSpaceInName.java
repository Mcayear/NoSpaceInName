package me.petterim1.ns;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.plugin.PluginBase;

import java.lang.reflect.Field;
import java.util.Locale;

public class NoSpaceInName extends PluginBase implements Listener {

    private String replaceWith;
    private Field usernameField;
    private Field iusernameField;
    private Field displayNameField;

    public void onEnable() {
        saveDefaultConfig();
        replaceWith = getConfig().getString("replaceWith");
        try {
            usernameField = Player.class.getDeclaredField("username");
            iusernameField = Player.class.getDeclaredField("iusername");
            displayNameField = Player.class.getDeclaredField("displayName");
            usernameField.setAccessible(true);
            iusernameField.setAccessible(true);
            displayNameField.setAccessible(true);
        } catch (ReflectiveOperationException exception) {
            getLogger().error("Unable to access player name fields", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        if (event.getPacket() instanceof LoginPacket) {
            LoginPacket packet = (LoginPacket) event.getPacket();
            if (packet.username == null) {
                getLogger().debug("Unable to check and replace: username == null");
            } else {
                packet.username = replaceSpaces(packet.username);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        if (username == null) {
            getLogger().debug("Unable to check and replace: player name == null");
            return;
        }

        String replacedName = replaceSpaces(username);
        if (replacedName.equals(username)) {
            return;
        }

        try {
            usernameField.set(player, replacedName);
            iusernameField.set(player, replacedName.toLowerCase(Locale.ROOT));
            displayNameField.set(player, replacedName);
            player.setNameTag(replacedName);
        } catch (ReflectiveOperationException exception) {
            getLogger().error("Unable to replace spaces in player name", exception);
        }
    }

    private String replaceSpaces(String value) {
        return value.replace(" ", replaceWith);
    }
}
