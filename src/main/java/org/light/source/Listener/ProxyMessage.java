package org.light.source.Listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ProxyMessage implements PluginMessageListener {

    public static int playerCount = 0;

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subChannel = input.readUTF();

        if (channel.equals("caramel:redis")) {
            if (subChannel.equals("PlayerCount")) {
                input.readUTF(); // ignored
                playerCount = input.readInt();
            }
        }
    }

}
