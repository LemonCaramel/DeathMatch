package org.light.source.Listener;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import moe.caramel.caramellibrarylegacy.api.API;
import moe.caramel.caramellibrarylegacy.user.CaramelUserData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.light.source.Command.KillDeathCommand;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Game.KillDeathManager;
import org.light.source.Game.NoKnockbackObject;
import org.light.source.Game.UserMananger;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventManager implements Listener {

    private DeathMatch Plugin;

    public EventManager(DeathMatch Plugin) {
        this.Plugin = Plugin;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().contains("ChatCraft")) {
            event.setCancelled(true);
            Player target = event.getPlayer();
            if (!PhoneManager.getInstance().contains(target.getUniqueId())) {
                PhoneManager.getInstance().addObject(target.getUniqueId(), true);
                Bukkit.broadcastMessage("§6" + target.getName() + "§f님이 §b모바일로 접속하여 참여가 제한되었습니다.");
            }
            else
                target.sendMessage("§c고의로 치지마세요..");

        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (GameManager.getInstance().contains(p.getUniqueId()) && GameManager.getInstance().isGaming()) {
            if (event.getRawSlot() == -999)
                return;
            event.setCancelled(true);
            if (event.getClick() == ClickType.MIDDLE && event.getCurrentItem() != null) {
                CSDirector director = CrackShotApi.getPlugin();
                String[] node = director.itemParentNode(event.getCurrentItem(), null);
                if (node != null) {
                    if (director.getBoolean(node[0] + ".Extras.One_Time_Use"))
                        p.getInventory().setItem(0, CrackShotApi.generateRandomWeapon());
                    else {
                        for (UserMananger data : GameManager.getInstance().getUsers()) {
                            if (data.getUUID().equals(p.getUniqueId())) {
                                if (data.getReRoll() >= DataManager.getInstance().getMaxReroll()) {
                                    p.sendMessage("§4최대 리롤 횟수를 초과하셨습니다..");
                                }
                                else if (EconomyApi.getInstance().currentMoney(p) < DataManager.getInstance().getReRollMoney()) {
                                    p.sendMessage("§6돈이 부족합니다.");
                                }
                                else {
                                    data.setReRoll(data.getReRoll() + 1);
                                    EconomyApi.getInstance().subtractMoney(p, DataManager.getInstance().getReRollMoney());
                                    p.getInventory().setItem(0, CrackShotApi.generateNotOPWeapon());
                                    p.sendTitle("", "§c-" + DataManager.getInstance().getReRollMoney() + " §6포인트", 5, 40, 5);
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§f남은 리롤 횟수 §7: §b" + data.getReRoll() + " §7/ §6" + DataManager.getInstance().getMaxReroll()));
                                    p.closeInventory();
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (!GameManager.getInstance().contains(p.getUniqueId()) && event.getInventory().getTitle().contains("랭크")) {
            if (event.getRawSlot() == -999)
                return;
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getRawSlot() == 51) {
                //다음페이지 (서로 위치 바꾸기)
                ItemStack stack = event.getCurrentItem();
                if (stack.getItemMeta().getDisplayName() != null) {
                    int i = 0;
                    flushData(event.getInventory());
                    if (stack.getItemMeta().getDisplayName().contains("뎃")) {
                        //킬랭킹
                        for (ItemStack st : KillDeathCommand.createKillDeathRank())
                            event.getInventory().setItem(i++, st);
                    }
                    else {
                        //킬뎃 랭킹
                        for (ItemStack st : KillDeathCommand.createKillRank())
                            event.getInventory().setItem(i++, st);
                    }
                    swapItem(event.getInventory());
                }
            }
            //제발 돌아가라 흑흑
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (CrackShotApi.getCSID(event.getItemDrop().getItemStack()) != null || event.getItemDrop().getItemStack().getType() == Material.SKULL_ITEM)
            event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOffhandMove(PlayerSwapHandItemsEvent event) {
        Player target = event.getPlayer();
        if (GameManager.getInstance().isGaming() && GameManager.getInstance().contains(target.getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
        Vector velocity = event.getVelocity();
        if (!NoKnockbackObject.getInstance().getKnockbackState(event.getPlayer())) {
            event.setCancelled(true);
            NoKnockbackObject.getInstance().setKnockBackState(event.getPlayer(), true);
        }
        if (velocity.getY() > 0.5)
            event.setVelocity(velocity.setY(0.5));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();
        target.setGameMode(GameMode.ADVENTURE);
        ScoreboardObject.getInstance().setScoreboard(target);
        API api = new API();
        api.giveChannel(target, 8);
        TeamManager.getInstance().removePlayer(target);
        setNoDamageState(target, true);
        checkPhone(target);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (GameManager.getInstance().contains(p.getUniqueId()))
            GameManager.getInstance().removePlayer(p);
        PhoneManager.getInstance().getPhoneObjects().removeIf(phoneObject -> phoneObject.getUuid().equals(p.getUniqueId()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (GameManager.getInstance().isGaming() && GameManager.getInstance().contains(event.getEntity().getUniqueId())) {
            event.setDeathMessage("");
            event.getDrops().clear();
            addDeath(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent event) {
        if (event.getVictim() instanceof Player && GameManager.getInstance().isGaming()) {
            Player killer = event.getPlayer();
            Player victim = (Player) event.getVictim();
            if (GameManager.getInstance().contains(killer.getUniqueId()) && GameManager.getInstance().contains(victim.getUniqueId())) {
                if (victim.getHealth() - event.getDamage() <= 0) {
                    int killerData = 0;
                    ItemStack stack = CrackShotApi.getCSWeapon(event.getWeaponTitle());
                    if (stack == null)
                        stack = createDummyItem();
                    if (stack.getType() == Material.IRON_AXE || stack.getType() == Material.DIAMOND_AXE)
                        sendKillMsg(killer, victim, "§c" + killer.getName() + " §f(" + stack.getItemMeta().getDisplayName() + "§f)" + " §7メ §b" + victim.getName());
                    else
                        sendKillMsg(killer, victim, "§c" + killer.getName() + " §f(" + stack.getItemMeta().getDisplayName() + "§f)" + " §7➾ §b" + victim.getName());
                    for (UserMananger data : GameManager.getInstance().getUsers()) {
                        if (data.getUUID().equals(killer.getUniqueId())) {
                            data.setKills(data.getKills() + 1);
                            killerData = data.getKills();
                        }
                        else if (data.getUUID().equals(victim.getUniqueId())) {
                            victim.getInventory().clear();
                            event.setDamage(0.0);
                            victim.setHealth(80.0);
                            sendRespawn(victim, killer.getName(), stack.getItemMeta().getDisplayName(), false);
                            if (victim.getName().equalsIgnoreCase(RatingManager.getInstance().getFirst())) {
                                //1위 인경우
                                int back, to;
                                back = data.getKills() / DataManager.getInstance().getKilltolevel();
                                to = (data.getKills() - 1) / DataManager.getInstance().getKilltolevel();
                                if (to >= (DataManager.getInstance().getRounds() / 2) && back != to) {
                                    sendLevelDown(victim, back, to);
                                    data.setKills(data.getKills() - 1);
                                }
                            }
                        }
                    }
                    RatingManager.getInstance().updateRank();
                    addKill(killer.getUniqueId());
                    addDeath(victim.getUniqueId());
                    if (DataManager.getInstance().getRounds() * DataManager.getInstance().getKilltolevel() <= killerData) {
                        //승리!
                        Bukkit.broadcastMessage("§b" + killer.getName() + "§f님이 §6" + DataManager.getInstance().getRounds() + " §f레벨을 달성하여 게임을 승리하셨습니다!");
                        GameManager.getInstance().stop();
                    }
                    else {
                        //레벨업 및 경고
                        int back = (killerData - 1) / DataManager.getInstance().getKilltolevel();
                        int to = killerData / DataManager.getInstance().getKilltolevel();
                        if (killerData - 1 != 0 && back != to) {
                            if (killerData == DataManager.getInstance().getKilltolevel() * (DataManager.getInstance().getRounds() - 1))
                                Bukkit.broadcastMessage("§b" + killer.getName() + " §f님이 §6" + (DataManager.getInstance().getRounds() - 1) + "§f레벨에 도달하셨습니다!");
                            sendLevelUp(killer, back, to);
                        }
                    }
                }
                else if (me.DeeCaaD.CrackShotPlus.API.getB(event.getWeaponTitle() + ".Damage.No_Knockback.caramel_Affect_Players") != null && me.DeeCaaD.CrackShotPlus.API.getB(event.getWeaponTitle() + ".Damage.No_Knockback.caramel_Affect_Players"))
                    NoKnockbackObject.getInstance().setKnockBackState(victim, false);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (GameManager.getInstance().isGaming()) {
            Player target = event.getPlayer();
            if (GameManager.getInstance().contains(target.getUniqueId())) {
                for (UserMananger mananger : GameManager.getInstance().getUsers()) {
                    if (mananger.getUUID().equals(target.getUniqueId())) {
                        target.getInventory().setItem(0, CrackShotApi.generateRandomWeapon());
                        target.getInventory().clear();
                        sendRespawn(target, "MineCraft", "§c<none> §7<<x>>", false);
                    }
                }
            }
        }
        else {
            Player target = event.getPlayer();
            Bukkit.getScheduler().runTaskLater(Plugin, () -> target.teleport(DataManager.getInstance().getLocations()[0]), 1L);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework)
            event.setCancelled(true);
    }

    @EventHandler
    public void onAnyDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING || event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

    public void sendLevelUp(Player p, int back, int to) {
        p.sendTitle("§bLevel UP!", "§6" + back + " §f=> §b" + to, 5, 50, 5);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        p.setLevel(to);
        p.setExp(GameManager.getInstance().calcLevelProgress(to));
    }

    public void sendLevelDown(Player p, int back, int to) {
        p.sendTitle("§cLevel Down..", "§6" + back + " §f=> §b" + to, 5, 50, 5);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
        p.setLevel(to);
        p.setExp(GameManager.getInstance().calcLevelProgress(to));
    }

    public void sendKillMsg(Player killer, Player victim, String msg) {
        //킬당 참여보상의 1/10 지급, 연속킬시 킬보상의 1/10 * 반올림(연속킬수 / 2), 제압킬시 참여 보상의 1/5 * 연속킬 횟수지급, 전부다 합연산으로 지급
        UserMananger killManager = null, victimManager = null;
        int reward = 0;
        for (UserMananger mananger : GameManager.getInstance().getUsers()) {
            if (mananger.getUUID().equals(killer.getUniqueId())) {
                killManager = mananger;
            }
            else if (mananger.getUUID().equals(victim.getUniqueId())) {
                victimManager = mananger;
            }
        }
        if (killManager != null && victimManager != null) {
            //죽은사람이 연속킬중일 경우
            reward += DataManager.getInstance().getJoinMoney() / 10;
            killManager.setKillMaintain(killManager.getKillMaintain() + 1);
            if (victimManager.getKillMaintain() >= 2) {
                craeteKillLog("§4§oShutDown! " + msg);
                reward += DataManager.getInstance().getJoinMoney() / 5 * victimManager.getKillMaintain();

            }
            else {
                //아닌경우 그냥 출력
                if (killManager.calcKillStay()) {
                    //이어갈 수 있는경우
                    if (killManager.getKillMaintain() != 1) {
                        if (killManager.getKillMaintain() == 2)
                            craeteKillLog("§e§oDouble Kill! " + msg);
                        else if (killManager.getKillMaintain() == 3)
                            craeteKillLog("§b§oTriple Kill! " + msg);
                        else if (killManager.getKillMaintain() == 4)
                            craeteKillLog("§a§oQuadra Kill! " + msg);
                        else if (killManager.getKillMaintain() == 5)
                            craeteKillLog("§d§oPenta Kill! " + msg);
                        else if (killManager.getKillMaintain() == 6)
                            craeteKillLog("§4§oHexa Kill! " + msg);
                        else
                            craeteKillLog("§6§oLegendary! " + msg);
                        reward += DataManager.getInstance().getJoinMoney() / 10 * (int) (Math.floor((double) killManager.getKillMaintain() / 2));
                    }
                    else
                        craeteKillLog(msg);
                }
                else {
                    craeteKillLog(msg);
                    killManager.setKillMaintain(1);
                }
            }
            victimManager.setKillMaintain(0);
            killManager.setLastKillTime(System.currentTimeMillis());
            killer.sendTitle("", "+ " + reward + " §6포인트", 0, 20, 0);
            MinimizeLogger.getInstance().appendLog(killer.getName() + "님이 " + victim.getName() + "님을 죽여 " + reward + "원을 흭득");
            killManager.setCalcResultMoney(killManager.getCalcResultMoney() + reward);
        }

    }

    public void sendRespawn(Player victim, String killerName, String WeaponName, boolean melee) {
        //게임 끝날때 사망 리스폰 처리
        victim.setGameMode(GameMode.SPECTATOR);
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
        if (melee)
            victim.sendTitle("§c§oRespawn..", "§c" + killerName + " §7メ §6" + victim.getName(), 0, 40, 0);
        else {
            victim.sendTitle("§c§oRespawn..", "§c" + killerName + " §7➾ §6" + victim.getName(), 0, 40, 0);
            victim.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§oUsing §8: " + WeaponName));
        }
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            //2초후 리스폰
            if (GameManager.getInstance().isGaming()) {
                if (GameManager.getInstance().contains(victim.getUniqueId())) {
                    for (UserMananger victimgr : GameManager.getInstance().getUsers()) {
                        if (victimgr.getUUID().equals(victim.getUniqueId())) {
                            victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0);
                            victim.setHealthScaled(true);
                            victim.setHealth(80.0);
                            for (PotionEffectType type : PotionEffectType.values()) {
                                if (type == null)
                                    continue;
                                if (victim.hasPotionEffect(type))
                                    victim.removePotionEffect(type);
                            }
                            victim.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[GameManager.getInstance().getRandomNumber()], DataManager.getInstance().getLocations()[GameManager.getInstance().getRandomNumber() + 1]));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 5, true, false));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 100, true, false));
                            if (victim.getName().equalsIgnoreCase(RatingManager.getInstance().getFirst()))
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999, 5, true, true));
                            victim.getInventory().setItem(0, CrackShotApi.generateRandomWeapon());
                        }
                    }
                    victim.setGameMode(GameMode.ADVENTURE);
                }
                else {
                    victim.teleport(DataManager.getInstance().getLocations()[0]);
                    victim.setGameMode(GameMode.ADVENTURE);
                }
            }
            else {
                victim.teleport(DataManager.getInstance().getLocations()[0]);
                victim.setGameMode(GameMode.ADVENTURE);
            }
        }, 40L);
    }

    private void craeteKillLog(String message) {
        List<UUID> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        String stripMsg = ChatColor.stripColor(message);
        for (int i = 0; i < (105 - stripMsg.length() - (stripMsg.replaceAll("[^ㄱ-힣]", "").length() + (stripMsg.contains("▄") ? 4 : 0) + (stripMsg.contains("▬▬") ? 2 : 0))); i++)
            builder.append(" ");
        builder.append(message);
        BossBar bar = Bukkit.createBossBar(builder.toString(), BarColor.WHITE, BarStyle.SOLID);
        GameManager.getInstance().getUsers().forEach(p -> {
            bar.addPlayer(Bukkit.getPlayer(p.getUUID()));
            list.add(p.getUUID());
        });
        Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
            list.stream().filter(p -> Bukkit.getPlayer(p) != null).forEach(p -> bar.removePlayer(Bukkit.getPlayer(p)));
            bar.removeAll();
        }, 70L);
    }

    private ItemStack createDummyItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§cError §f<<X>>");
        stack.setItemMeta(meta);
        return stack;
    }

    private void setNoDamageState(Player target, boolean state) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
            if (CaramelUserData.getData().getUser(target.getUniqueId()) != null && !GameManager.getInstance().contains(target.getUniqueId()))
                CaramelUserData.getData().getUser(target.getUniqueId()).setInvincibility(state);
        }, 20L);
    }

    private void checkPhone(Player target) {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            if (!PhoneManager.getInstance().contains(target.getUniqueId()))
                PhoneManager.getInstance().addObject(target.getUniqueId(), false);
        }, 40L);
    }

    private void flushData(Inventory inv) {
        for (int i = 0; i < 45; i++)
            inv.setItem(i, new ItemStack(Material.AIR));
    }

    private void addKill(UUID uuid) {
        KillDeathObject object = KillDeathManager.getInstance().getValue(uuid);
        object.setKill(object.getKill() + 1);
    }

    private void addDeath(UUID uuid) {
        KillDeathObject object = KillDeathManager.getInstance().getValue(uuid);
        object.setDeath(object.getDeath() + 1);
    }

    private void swapItem(Inventory inv) {
        ItemStack previous, next;
        previous = inv.getItem(47).clone();
        next = inv.getItem(51);
        inv.setItem(47, next);
        inv.setItem(51, previous);
    }

}
