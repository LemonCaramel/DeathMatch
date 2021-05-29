package org.light.source.Runnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ConnectionSchedular implements Runnable {

    private final Plugin plugin;

    public ConnectionSchedular(Plugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        if (Bukkit.getOnlinePlayers().size() != 0) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF("ALL");

            try {
                new ObjectArrayList<>(Bukkit.getOnlinePlayers()).get(0)
                        .sendPluginMessage(plugin, "caramel:redis", out.toByteArray());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
