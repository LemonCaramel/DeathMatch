package org.light.dayz.game;

import moe.caramel.caramellibrarylegacy.api.API;
import moe.caramel.caramellibrarylegacy.api.FontAPI;
import moe.caramel.caramellibrarylegacy.user.CaramelUserData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.data.DayZData;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.team.TeamData;
import org.light.source.Game.GameManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.DataManager;
import org.light.source.Singleton.EconomyApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GameController {

    public static HashMap<UUID, DayZData> gameData = new HashMap<>();
    public static TeamData team = new TeamData();

    public static void addPlayer(Player p) {
        if (!contains(p.getUniqueId()) && canStart() && !GameManager.getInstance().contains(p.getUniqueId())) {
            addData(p.getUniqueId());
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0);
            p.setHealthScaled(true);
            p.setHealth(100.0);
            p.setFoodLevel(20);
            if (p.getInventory().getItem(8) != null && p.getInventory().getItem(8).getType() == Material.PLAYER_HEAD)
                p.getInventory().setItem(8, new ItemStack(Material.AIR));
            p.setGameMode(GameMode.ADVENTURE);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 5, true, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 3, true, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 140, 5, true, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 100, true, false));
            p.setLevel(0);
            p.setExp(0.0f);
            p.sendMessage(" ");
            FontAPI.sendCenteredMessage(p, "§f좀비와 다른 생존자들을 피해 아이템을 흭득하고");
            FontAPI.sendCenteredMessage(p, "§f대치하며 탈출구§7(신호기, 우클릭 시 탈출 가능)§f로 탈출하세요");
            FontAPI.sendCenteredMessage(p, "§b탈출 시 파밍 한 모든 아이템이 보존되며,");
            FontAPI.sendCenteredMessage(p, "§b그 이외의 경우 아이템이 전부 드랍됩니다.");
            FontAPI.sendCenteredMessage(p, "§c좀비나 생존자를 죽일 경우 포인트가 추가로 지급됩니다.");
            FontAPI.sendCenteredMessage(p, "§7(단, 탈출 시 보상 흭득 가능)");
            p.sendMessage(" ");
            team.addPlayer(p);
            p.teleport(getRandomLocation());
            if (CaramelUserData.getData().getUser(p.getUniqueId()) != null)
                CaramelUserData.getData().getUser(p.getUniqueId()).setInvincibility(false);
        }
        else
            p.sendMessage("§c조건에 충족하지 않아 참여할 수 없습니다.");
    }

    public static void removePlayer(Player p, boolean alive) {
        if (contains(p.getUniqueId())) {
            DayZData data = getData(p.getUniqueId());
            gameData.remove(p.getUniqueId());
            team.removePlayer(p);
            new ArrayList<>(p.getActivePotionEffects()).forEach(pos -> p.removePotionEffect(pos.getType()));
            p.setLevel(0);
            p.setExp(0.0f);
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.teleport(DataManager.getInstance().getLocations()[0]);
            p.setFireTicks(0);
            p.sendMessage(" ");
            FontAPI.sendCenteredMessage(p, "§f《 레이드 결과 》");
            p.sendMessage(" ");
            FontAPI.sendCenteredMessage(p, "§f플레이어 §4킬 §f총 " + data.getKill() + "§f회, 포인트 §6" + data.getAccumulateMoney() + "§f원");
            if (alive) {
                FontAPI.sendCenteredMessage(p, "§f탈출에 성공하여 §6포인트§f와 아이템을 흭득하였습니다.");
                p.sendMessage(" ");
                EconomyApi.getInstance().giveMoney(p, data.getAccumulateMoney());
                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 탈출하여 아이템과 " + data.getAccumulateMoney() + "원 흭득");
            }
            else {
                FontAPI.sendCenteredMessage(p, "§f탈출에 §c실패§f하여 보상 흭득에 실패하였습니다.");
                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 탈출 실패");
            }
            if (CaramelUserData.getData().getUser(p.getUniqueId()) != null)
                CaramelUserData.getData().getUser(p.getUniqueId()).setInvincibility(true);
            if (p.getInventory().getItem(8) == null || p.getInventory().getItem(8).getType() == Material.AIR)
                API.giveChannel(p, 8);
            else
                p.sendMessage("§6[ §f! §6] §f핫바 9번째 칸에 아이템이 있어 채널 이동기를 지급하지 못했습니다. " +
                        "§7(재접속 또는 채널 이동 명령어로 사용이 가능합니다)");
        }
    }

    public static boolean contains(UUID data) {
        return gameData.containsKey(data);
    }

    public static DayZData getData(UUID data) {
        return gameData.get(data);
    }

    public static void addData(UUID data) {
        gameData.put(data, new DayZData());
    }

    public static int getSize() {
        return gameData.size();
    }

    public static boolean canStart() {
        return YamlConfig.instance.getLocations().size() != 0;
    }

    public static Location getRandomLocation() {
        int random = ThreadLocalRandom.current().nextInt(0, YamlConfig.instance.getLocations().size());
        return YamlConfig.instance.getLocations().get(random);
    }
}
