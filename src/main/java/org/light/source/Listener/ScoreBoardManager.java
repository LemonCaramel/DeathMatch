package org.light.source.Listener;

import moe.caramel.caramellibrarylegacy.api.score.common.EntryBuilder;
import moe.caramel.caramellibrarylegacy.api.score.type.Entry;
import moe.caramel.caramellibrarylegacy.api.score.type.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.light.dayz.game.GameController;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.RatingManager;

import java.util.List;
import java.util.UUID;

public class ScoreBoardManager implements ScoreboardHandler {

    private final Plugin plugin;

    public ScoreBoardManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getTitle(Player player) {
        return "§f§oDeath§7§oMatch";
    }

    @Override
    public List<Entry> getEntries(Player player) {
        UUID uuid = player.getUniqueId();
        GameManager manager = GameManager.getInstance(); // DeathMatch
        EntryBuilder builder = new EntryBuilder();
        int chnnelOnline = plugin.getServer().getOnlinePlayers().size();

        builder.blank().next("§8»  §a온라인:  §r" + chnnelOnline + "/" + ProxyMessage.playerCount + " 명")
                .next("§8»  §6데스매치: §r" + manager.getGameState().getState() + " (" + manager.getUserCount() + " 명)")
                .next("§8»  §c데이즈: §r" + GameController.getSize() + " 명");

        if (manager.contains(uuid)) { // DeathMatch Player
            if (manager.isGaming() && manager.getGameState() == GameManager.State.START) {
                builder.blank()
                        .next("§fLV. §6" + RatingManager.getLV(RatingManager.getInstance().getFirstKill()) + " §b" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getFirst())))
                        .next("§fLV. §6" + RatingManager.getLV(RatingManager.getInstance().getSecondKill()) + " §c" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getSecond())))
                        .next("§fLV. §6" + RatingManager.getLV(RatingManager.getInstance().getSecondKill()) + " §c" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getThird())));
            }
        } else if (GameController.contains(uuid)) { // DayZ Player
            builder.blank()
                    .next("§7  ============ §fDayZ §7============")
                    .next("§8»  §c체력§7: §f" + (int)player.getHealth() + " §4HP")
                    .next("§8»  §6배고픔§7: §f" + player.getFoodLevel()).blank()
                    .next("§8»  §7X: " + player.getLocation().getBlockX())
                    .next("§8»  §7Y: " + player.getLocation().getBlockY())
                    .next("§8»  §7Z: " + player.getLocation().getBlockZ());
        }

        return builder.blank().build();
    }

    public String checkLength(String value) {
        return (value.length() >= 40) ? "§cLOOOONG.." : value;
    }

}
