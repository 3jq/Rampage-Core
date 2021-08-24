package dev.rampage.rampagecore.commands;

import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import dev.rampage.rampagecore.api.utils.ExpGaining;
import dev.rampage.rampagecore.api.utils.LP;
import dev.rampage.rampagecore.api.utils.RestorePotionEffects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class C
        implements CommandExecutor {
    public static void removeEffects(Player player) {
        UUID id = player.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
        int lvl = playerInfo.getLvl();
        player.setHealthScale(20 + lvl / 20 * 2);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(0.0);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(4.0);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(0.0);
        player.setWalkSpeed(0.2f);
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.SLOW);
        LP.removePermission(id, "essentials.kits.healer");
        LP.removePermission(id, "essentials.kits.tank");
        LP.removePermission(id, "essentials.kits.warrior");
        LP.removePermission(id, "essentials.kits.archer");
        LP.removePermission(id, "essentials.kits.assassin");
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(false);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "\u0412\u044b \u043d\u0435 \u043d\u0430\u043f\u0438\u0441\u0430\u043b\u0438 \u043a\u043e\u043c\u0430\u043d\u0434\u0443. \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /c <command>");
            return true;
        }

        switch (args[0]) {
            case "stats": {
                if (sender instanceof Player && args.length == 1) {
                    Player player = (Player) sender;
                    String name = player.getName();
                    PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(name);
                    String selectedClass = playerInfo.getSelectedClass();
                    int lvl = playerInfo.getLvl();
                    int exp = playerInfo.getExp();
                    int new_lvl_exp = ExpGaining.calcNewLvl(lvl);
                    player.sendMessage(ChatColor.DARK_RED + "\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 " + ChatColor.AQUA + name);
                    player.sendMessage(ChatColor.YELLOW + "\u041a\u043b\u0430\u0441\u0441: " + ChatColor.GREEN + selectedClass);
                    player.sendMessage(ChatColor.YELLOW + "\u0423\u0440\u043e\u0432\u0435\u043d\u044c: " + ChatColor.WHITE + lvl);
                    player.sendMessage(ChatColor.YELLOW + "\u041e\u043f\u044b\u0442: " + ChatColor.WHITE + exp + '/' + new_lvl_exp);
                    break;
                }
                if (args.length != 2) break;
                String name = args[1];
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(name);
                if (!sender.hasPermission("cwc.stats")) {
                    sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432!");
                    return true;
                }
                if (playerInfo == null) {
                    sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d.");
                    return true;
                }
                String selectedClass = playerInfo.getSelectedClass();
                int lvl = playerInfo.getLvl();
                int exp = playerInfo.getExp();
                int new_lvl_exp = ExpGaining.calcNewLvl(lvl);
                sender.sendMessage(ChatColor.DARK_RED + "\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 " + ChatColor.AQUA + name);
                sender.sendMessage(ChatColor.YELLOW + "\u041a\u043b\u0430\u0441\u0441: " + ChatColor.GREEN + selectedClass);
                sender.sendMessage(ChatColor.YELLOW + "\u0423\u0440\u043e\u0432\u0435\u043d\u044c: " + ChatColor.WHITE + lvl);
                sender.sendMessage(ChatColor.YELLOW + "\u041e\u043f\u044b\u0442: " + ChatColor.WHITE + exp + '/' + new_lvl_exp);
                break;
            }
            case "skills": {
                if (!(sender instanceof Player)) break;
                Player player = (Player) sender;
                UUID id = player.getUniqueId();
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
                String selectedClass = playerInfo.getSelectedClass();
                if (args.length == 1) {
                    if (selectedClass.equals("none")) {
                        sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043a\u043b\u0430\u0441\u0441\u0430!");
                        sender.sendMessage(ChatColor.RED + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /c choose <class>");
                        return true;
                    }
                } else {
                    selectedClass = args[1];
                }
                Inventory gui = Bukkit.createInventory(player, 9, ChatColor.BLACK + "\u0421\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u0438");
                ItemStack[] menu_items = new ItemStack[]{};
                switch (selectedClass) {
                    case "archer": {
                        gui = Bukkit.createInventory(player, 18, ChatColor.BLACK + "\u0421\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u0438");
                        ItemStack arrowSpeed = new ItemStack(Material.BOW);
                        ItemMeta arrowSpeed_meta = arrowSpeed.getItemMeta();
                        arrowSpeed_meta.setDisplayName(ChatColor.DARK_RED + "\u0421\u0442\u0440\u0435\u043c\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c" + ChatColor.GRAY + " (0)");
                        ArrayList<String> arrowSpeed_lore = new ArrayList<String>();
                        arrowSpeed_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:" + ChatColor.WHITE + " \u0432\u044b\u043f\u0443\u0449\u0435\u043d\u043d\u044b\u0435 \u0432\u0430\u043c\u0438 \u0441\u0442\u0440\u0435\u043b\u044b");
                        arrowSpeed_lore.add(ChatColor.WHITE + "\u043b\u0435\u0442\u044f\u0442 \u0432 1.5 \u0440\u0430\u0437\u0430 \u0431\u044b\u0441\u0442\u0440\u0435\u0435");
                        arrowSpeed_meta.setLore(arrowSpeed_lore);
                        arrowSpeed.setItemMeta(arrowSpeed_meta);
                        ItemStack rebound = new ItemStack(Material.IRON_BOOTS);
                        ItemMeta rebound_meta = rebound.getItemMeta();
                        rebound_meta.setDisplayName(ChatColor.YELLOW + "\u041e\u0442\u0441\u043a\u043e\u043a" + ChatColor.GRAY + " (10)");
                        ArrayList<String> rebound_lore = new ArrayList<String>();
                        rebound_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u043d\u0430\u0436\u0430\u0442\u0438\u0438 \u043f\u0440\u043e\u0431\u0435\u043b\u0430 \u0442\u0440\u0438\u0436\u0434\u044b");
                        rebound_lore.add(ChatColor.WHITE + "\u0432\u044b \u043e\u0442\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u0435\u0442\u0435\u0441\u044c \u043d\u0430\u0437\u0430\u0434");
                        rebound_lore.add("");
                        rebound_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 5 \u0441\u0435\u043a\u0443\u043d\u0434");
                        rebound_meta.setLore(rebound_lore);
                        rebound_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        rebound.setItemMeta(rebound_meta);
                        ItemStack rabbitJump = new ItemStack(Material.RABBIT_FOOT);
                        ItemMeta rabbitJump_meta = rabbitJump.getItemMeta();
                        rabbitJump_meta.setDisplayName(ChatColor.GREEN + "\u041a\u0440\u043e\u043b\u0438\u0447\u0438\u0439 \u043f\u0440\u044b\u0436\u043e\u043a" + ChatColor.GRAY + " (20)");
                        ArrayList<String> rabbitJump_lore = new ArrayList<String>();
                        rabbitJump_lore.add(ChatColor.WHITE + "\u041f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u0430");
                        rabbitJump_lore.add("");
                        rabbitJump_lore.add(ChatColor.BLUE + "\u041f\u0440\u044b\u0433\u0443\u0447\u0435\u0441\u0442\u044c (00:07)");
                        rabbitJump_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 20 \u0441\u0435\u043a\u0443\u043d\u0434");
                        rabbitJump_meta.setLore(rabbitJump_lore);
                        rabbitJump.setItemMeta(rabbitJump_meta);
                        ItemStack speedPotion = this.getPotionItemStack(PotionType.SPEED, true, false);
                        ItemMeta speedPotion_meta = speedPotion.getItemMeta();
                        speedPotion_meta.setDisplayName(ChatColor.DARK_AQUA + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c" + ChatColor.GRAY + " (30)");
                        ArrayList<String> speedPotion_lore = new ArrayList<String>();
                        speedPotion_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        speedPotion_lore.add(ChatColor.BLUE + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c (**:**)");
                        speedPotion_meta.setLore(speedPotion_lore);
                        speedPotion_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        speedPotion.setItemMeta(speedPotion_meta);
                        ItemStack arrowPoison = new ItemStack(Material.TIPPED_ARROW);
                        PotionMeta arrowPoisonMetaP = (PotionMeta) arrowPoison.getItemMeta();
                        arrowPoisonMetaP.setBasePotionData(new PotionData(PotionType.POISON));
                        arrowPoison.setItemMeta(arrowPoisonMetaP);
                        ItemMeta arrowPoison_meta = arrowPoison.getItemMeta();
                        arrowPoison_meta.setDisplayName(ChatColor.AQUA + "\u0421\u0442\u0440\u0435\u043b\u0430 \u043e\u0442\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f" + ChatColor.GRAY + " (40)");
                        ArrayList<String> arrowPoison_lore = new ArrayList<String>();
                        arrowPoison_lore.add(ChatColor.WHITE + "\u0412\u044b\u043f\u0443\u0441\u043a\u0430\u0435\u0442 \u0441\u0442\u0440\u0435\u043b\u0443 \u043e\u0442\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f");
                        arrowPoison_lore.add(ChatColor.WHITE + "\u043f\u043e \u043d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044e \u0432\u0437\u0433\u043b\u044f\u0434\u0430");
                        arrowPoison_lore.add("");
                        arrowPoison_lore.add(ChatColor.RED + "\u041e\u0442\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 III (0:03)");
                        arrowPoison_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 10 \u0441\u0435\u043a\u0443\u043d\u0434");
                        arrowPoison_meta.setLore(arrowPoison_lore);
                        arrowPoison_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        arrowPoison.setItemMeta(arrowPoison_meta);
                        ItemStack arrowSlow = new ItemStack(Material.TIPPED_ARROW);
                        PotionMeta arrowPMetaS = (PotionMeta) arrowSlow.getItemMeta();
                        arrowPMetaS.setBasePotionData(new PotionData(PotionType.SLOWNESS));
                        arrowSlow.setItemMeta(arrowPMetaS);
                        ItemMeta arrowSlow_meta = arrowSlow.getItemMeta();
                        arrowSlow_meta.setDisplayName(ChatColor.AQUA + "\u0421\u0442\u0440\u0435\u043b\u0430 \u043c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438" + ChatColor.GRAY + " (40)");
                        ArrayList<String> arrowSlow_lore = new ArrayList<String>();
                        arrowSlow_lore.add(ChatColor.WHITE + "\u0412\u044b\u043f\u0443\u0441\u043a\u0430\u0435\u0442 \u0441\u0442\u0440\u0435\u043b\u0443 \u043c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438");
                        arrowSlow_lore.add(ChatColor.WHITE + "\u043f\u043e \u043d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044e \u0432\u0437\u0433\u043b\u044f\u0434\u0430");
                        arrowSlow_lore.add("");
                        arrowSlow_lore.add(ChatColor.RED + "\u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c III (0:05)");
                        arrowSlow_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 10 \u0441\u0435\u043a\u0443\u043d\u0434");
                        arrowSlow_meta.setLore(arrowSlow_lore);
                        arrowSlow_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        arrowSlow.setItemMeta(arrowSlow_meta);
                        ItemStack ascent = new ItemStack(Material.FEATHER);
                        ItemMeta ascent_meta = ascent.getItemMeta();
                        ascent_meta.setDisplayName(ChatColor.GOLD + "\u0412\u0437\u043b\u0451\u0442" + ChatColor.GRAY + " (50)");
                        ArrayList<String> ascent_lore = new ArrayList<String>();
                        ascent_lore.add(ChatColor.WHITE + "\u041f\u043e\u0434\u043d\u0438\u043c\u0430\u0435\u0442 \u0432 \u0432\u043e\u0437\u0434\u0443\u0445 \u043d\u0430 30 \u0431\u043b\u043e\u043a\u043e\u0432");
                        ascent_lore.add(ChatColor.WHITE + "\u0438 \u0437\u0430\u043c\u0435\u0434\u043b\u044f\u0435\u0442 \u043f\u0430\u0434\u0435\u043d\u0438\u0435");
                        ascent_lore.add("");
                        ascent_lore.add(ChatColor.BLUE + "\u041f\u043b\u0430\u0432\u043d\u043e\u0435 \u043f\u0430\u0434\u0435\u043d\u0438\u0435 (00:06)");
                        ascent_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 20 \u0441\u0435\u043a\u0443\u043d\u0434");
                        ascent_meta.setLore(ascent_lore);
                        ascent.setItemMeta(ascent_meta);
                        menu_items = new ItemStack[]{arrowSpeed, rebound, rabbitJump, speedPotion, arrowPoison, ascent, null, null, null, null, null, null, null, arrowSlow};
                        break;
                    }
                    case "assassin": {
                        ItemStack speedAssassin = this.getPotionItemStack(PotionType.SPEED, true, false);
                        ItemMeta speedAssassin_meta = speedAssassin.getItemMeta();
                        speedAssassin_meta.setDisplayName(ChatColor.DARK_RED + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c" + ChatColor.GRAY + " (0)");
                        ArrayList<String> speedAssassin_lore = new ArrayList<String>();
                        speedAssassin_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        speedAssassin_lore.add(ChatColor.BLUE + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c (**:**)");
                        speedAssassin_meta.setLore(speedAssassin_lore);
                        speedAssassin_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        speedAssassin.setItemMeta(speedAssassin_meta);
                        ItemStack loner = new ItemStack(Material.PLAYER_HEAD);
                        ItemMeta loner_meta = loner.getItemMeta();
                        loner_meta.setDisplayName(ChatColor.YELLOW + "\u041e\u0434\u0438\u043d\u043e\u0447\u043a\u0430" + ChatColor.GRAY + " (10)");
                        ArrayList<String> loner_lore = new ArrayList<String>();
                        loner_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        loner_lore.add(ChatColor.WHITE + "\u0423\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043d\u0430 \u043d\u0430 30% \u043f\u043e \u0438\u0433\u0440\u043e\u043a\u0443 \u0435\u0441\u043b\u0438");
                        loner_lore.add(ChatColor.WHITE + "\u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 16 \u0431\u043b\u043e\u043a\u043e\u0432 \u043d\u0435\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u0432,");
                        loner_lore.add(ChatColor.WHITE + "\u043a\u0440\u043e\u043c\u0435 \u0432\u0430\u0441, \u0441\u043e\u043a\u043b\u0430\u043d\u043e\u0432\u0446\u0435\u0432 \u0438 \u0436\u0435\u0440\u0442\u0432\u044b");
                        loner_meta.setLore(loner_lore);
                        loner_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        loner.setItemMeta(loner_meta);
                        ItemStack burrow = new ItemStack(Material.WOODEN_SHOVEL);
                        ItemMeta burrow_meta = burrow.getItemMeta();
                        burrow_meta.setDisplayName(ChatColor.DARK_GREEN + "\u0417\u0430\u0441\u0430\u0434\u0430" + ChatColor.GRAY + " (20)");
                        ArrayList<String> burrow_lore = new ArrayList<String>();
                        burrow_lore.add(ChatColor.WHITE + "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u041f\u041a\u041c \u043f\u043e \u0431\u043b\u043e\u043a\u0443");
                        burrow_lore.add(ChatColor.WHITE + "\u043d\u0430 \u043a\u043e\u0442\u043e\u0440\u043e\u043c \u0441\u0442\u043e\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b");
                        burrow_lore.add(ChatColor.WHITE + "'\u043f\u0440\u043e\u0432\u0430\u043b\u0438\u0442\u044c\u0441\u044f' \u0432 \u043d\u0435\u0433\u043e");
                        burrow_lore.add(ChatColor.WHITE + "\u0438 \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u044d\u0444\u0444\u0435\u043a\u0442");
                        burrow_lore.add("");
                        burrow_lore.add(ChatColor.BLUE + "\u041d\u0435\u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c (**:**)");
                        burrow_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 10 \u0441\u0435\u043a\u0443\u043d\u0434");
                        burrow_meta.setLore(burrow_lore);
                        burrow_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        burrow_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        burrow.setItemMeta(burrow_meta);
                        ItemStack nightVision = this.getPotionItemStack(PotionType.NIGHT_VISION, true, false);
                        ItemMeta nightVision_meta = nightVision.getItemMeta();
                        nightVision_meta.setDisplayName(ChatColor.DARK_AQUA + "\u041d\u043e\u0447\u043d\u043e\u0435 \u0437\u0440\u0435\u043d\u0438\u0435" + ChatColor.GRAY + " (30)");
                        ArrayList<String> nightVision_lore = new ArrayList<String>();
                        nightVision_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        nightVision_lore.add(ChatColor.BLUE + "\u041d\u043e\u0447\u043d\u043e\u0435 \u0437\u0440\u0435\u043d\u0438\u0435 (**:**)");
                        nightVision_meta.setLore(nightVision_lore);
                        nightVision_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        nightVision.setItemMeta(nightVision_meta);
                        ItemStack trauma = new ItemStack(Material.BONE);
                        ItemMeta trauma_meta = trauma.getItemMeta();
                        trauma_meta.setDisplayName(ChatColor.AQUA + "\u0422\u0440\u0430\u0432\u043c\u0430" + ChatColor.GRAY + " (40)");
                        ArrayList<String> trauma_lore = new ArrayList<String>();
                        trauma_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        trauma_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435 \u0448\u0430\u043d\u0441 15%");
                        trauma_lore.add(ChatColor.WHITE + "\u043d\u0430\u043b\u043e\u0436\u0438\u0442\u044c \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 \u044d\u0444\u0444\u0435\u043a\u0442");
                        trauma_lore.add("");
                        trauma_lore.add(ChatColor.RED + "\u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c III (0:04)");
                        trauma_meta.setLore(trauma_lore);
                        trauma_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        trauma.setItemMeta(trauma_meta);
                        ItemStack arrowBlind = new ItemStack(Material.TIPPED_ARROW);
                        PotionMeta arrowBlindMetaP = (PotionMeta) arrowBlind.getItemMeta();
                        arrowBlindMetaP.setBasePotionData(new PotionData(PotionType.WEAKNESS));
                        arrowBlind.setItemMeta(arrowBlindMetaP);
                        ItemMeta arrowBlind_meta = arrowBlind.getItemMeta();
                        arrowBlind_meta.setDisplayName(ChatColor.GOLD + "\u0421\u0442\u0440\u0435\u043b\u0430 \u0441\u043b\u0435\u043f\u043e\u0442\u044b" + ChatColor.GRAY + " (50)");
                        ArrayList<String> arrowBlind_lore = new ArrayList<String>();
                        arrowBlind_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435 \u0441\u0442\u0440\u0435\u043b\u043e\u0439 \u0441\u043b\u0430\u0431\u043e\u0441\u0442\u0438");
                        arrowBlind_lore.add(ChatColor.WHITE + "\u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 \u043d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u0442\u0441\u044f \u044d\u0444\u0444\u0435\u043a\u0442");
                        arrowBlind_lore.add("");
                        arrowBlind_lore.add(ChatColor.RED + "\u0421\u043b\u0435\u043f\u043e\u0442\u0430 II (0:05)");
                        arrowBlind_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 30 \u0441\u0435\u043a\u0443\u043d\u0434");
                        arrowBlind_meta.setLore(arrowBlind_lore);
                        arrowBlind_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        arrowBlind.setItemMeta(arrowBlind_meta);
                        menu_items = new ItemStack[]{speedAssassin, loner, burrow, nightVision, trauma, arrowBlind};
                        break;
                    }
                    case "healer": {
                        gui = Bukkit.createInventory(player, 18, ChatColor.BLACK + "\u0421\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u0438");
                        ItemStack smite = new ItemStack(Material.ZOMBIE_HEAD);
                        ItemMeta smite_meta = smite.getItemMeta();
                        smite_meta.setDisplayName(ChatColor.DARK_RED + "\u041d\u0435\u0431\u0435\u0441\u043d\u0430\u044f \u043a\u0430\u0440\u0430" + ChatColor.GRAY + " (0)");
                        ArrayList<String> smite_lore = new ArrayList<String>();
                        smite_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        smite_lore.add(ChatColor.WHITE + "\u0423\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043d\u0430 \u043d\u0430 30%");
                        smite_lore.add(ChatColor.WHITE + "\u043f\u043e \u043d\u0435\u0436\u0438\u0442\u0438");
                        smite_meta.setLore(smite_lore);
                        smite_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        smite.setItemMeta(smite_meta);
                        ItemStack massiveHeal = this.getPotionItemStack(PotionType.INSTANT_HEAL, false, true);
                        ItemMeta massiveHeal_meta = massiveHeal.getItemMeta();
                        massiveHeal_meta.setDisplayName(ChatColor.YELLOW + "\u041c\u0430\u0441\u0441\u043e\u0432\u043e\u0435 \u043b\u0435\u0447\u0435\u043d\u0438\u0435" + ChatColor.GRAY + " (10)");
                        ArrayList<String> massiveHeal_lore = new ArrayList<String>();
                        massiveHeal_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438 \u0437\u0435\u043b\u044c\u044f \u043b\u0435\u0447\u0435\u043d\u0438\u044f");
                        massiveHeal_lore.add(ChatColor.WHITE + "\u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 10 \u0431\u043b\u043e\u043a\u043e\u0432");
                        massiveHeal_lore.add(ChatColor.WHITE + "\u0441\u043e\u043a\u043b\u0430\u043d\u043e\u0432\u0446\u044b \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u044e\u0442 4 HP");
                        massiveHeal_lore.add("");
                        massiveHeal_lore.add(ChatColor.BLUE + "\u041c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e\u0435 \u043b\u0435\u0447\u0435\u043d\u0438\u0435");
                        massiveHeal_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 30 \u0441\u0435\u043a\u0443\u043d\u0434");
                        massiveHeal_meta.setLore(massiveHeal_lore);
                        massiveHeal_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        massiveHeal.setItemMeta(massiveHeal_meta);
                        ItemStack massiveRegen = this.getPotionItemStack(PotionType.REGEN, true, false);
                        ItemMeta massiveRegen_meta = massiveRegen.getItemMeta();
                        massiveRegen_meta.setDisplayName(ChatColor.YELLOW + "\u041c\u0430\u0441\u0441\u043e\u0432\u0430\u044f \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f" + ChatColor.GRAY + " (10)");
                        ArrayList<String> massiveRegen_lore = new ArrayList<String>();
                        massiveRegen_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438 \u0437\u0435\u043b\u044c\u044f \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438");
                        massiveRegen_lore.add(ChatColor.WHITE + "\u0441\u043e\u043a\u043b\u0430\u043d\u043e\u0432\u0446\u044b \u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 10 \u0431\u043b\u043e\u043a\u043e\u0432");
                        massiveRegen_lore.add(ChatColor.WHITE + "\u043f\u043e\u043b\u0443\u0447\u0430\u044e\u0442 \u044d\u0444\u0444\u0435\u043a\u0442");
                        massiveRegen_lore.add("");
                        massiveRegen_lore.add(ChatColor.BLUE + "\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f (00:10)");
                        massiveRegen_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 20 \u0441\u0435\u043a\u0443\u043d\u0434");
                        massiveRegen_meta.setLore(massiveRegen_lore);
                        massiveRegen_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        massiveRegen.setItemMeta(massiveRegen_meta);
                        ItemStack regainHealth = new ItemStack(Material.BREAD);
                        ItemMeta regainHealth_meta = regainHealth.getItemMeta();
                        regainHealth_meta.setDisplayName(ChatColor.DARK_GREEN + "\u0423\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u0438\u0435 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u044f" + ChatColor.GRAY + " (20)");
                        ArrayList<String> regainHealth_lore = new ArrayList<String>();
                        regainHealth_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        regainHealth_lore.add(ChatColor.WHITE + "\u041b\u044e\u0431\u043e\u0435 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f");
                        regainHealth_lore.add(ChatColor.WHITE + "\u0443\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u043e \u043d\u0430 20%");
                        regainHealth_meta.setLore(regainHealth_lore);
                        regainHealth_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        regainHealth.setItemMeta(regainHealth_meta);
                        ItemStack rejection = new ItemStack(Material.SUGAR);
                        ItemMeta rejection_meta = rejection.getItemMeta();
                        rejection_meta.setDisplayName(ChatColor.DARK_AQUA + "\u041e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u0435" + ChatColor.GRAY + " (30)");
                        ArrayList<String> rejection_lore = new ArrayList<String>();
                        rejection_lore.add(ChatColor.WHITE + "\u041e\u0442\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u0435\u0442 \u0432\u0441\u0435\u0445 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439");
                        rejection_lore.add(ChatColor.WHITE + "\u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 5 \u0431\u043b\u043e\u043a\u043e\u0432,");
                        rejection_lore.add(ChatColor.WHITE + "\u043a\u0440\u043e\u043c\u0435 \u0441\u043e\u043a\u043b\u0430\u043d\u043e\u0432\u0446\u0435\u0432");
                        massiveHeal_lore.add("");
                        massiveHeal_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 15 \u0441\u0435\u043a\u0443\u043d\u0434");
                        rejection_meta.setLore(rejection_lore);
                        rejection_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        rejection.setItemMeta(rejection_meta);
                        ItemStack battleCry = new ItemStack(Material.WOODEN_SWORD);
                        ItemMeta battleCry_meta = battleCry.getItemMeta();
                        battleCry_meta.setDisplayName(ChatColor.AQUA + "\u0411\u043e\u0435\u0432\u043e\u0439 \u043a\u043b\u0438\u0447" + ChatColor.GRAY + " (40)");
                        ArrayList<String> battleCry_lore = new ArrayList<String>();
                        battleCry_lore.add(ChatColor.WHITE + "\u041f\u041a\u041c \u0434\u0435\u0440\u0435\u0432\u044f\u043d\u043d\u044b\u043c \u043c\u0435\u0447\u043e\u043c \u043d\u0430 \u0431\u043b\u043e\u043a - ");
                        battleCry_lore.add(ChatColor.WHITE + "\u043d\u0430 \u0441\u043e\u043a\u043b\u0430\u043d\u043e\u0432\u0446\u0435\u0432 \u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 10 \u0431\u043b\u043e\u043a\u043e\u0432");
                        battleCry_lore.add(ChatColor.WHITE + "\u043d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u044e\u0442\u0441\u044f \u044d\u0444\u0444\u0435\u043a\u0442\u044b");
                        battleCry_lore.add("");
                        battleCry_lore.add(ChatColor.BLUE + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c (00:10)");
                        battleCry_lore.add(ChatColor.BLUE + "\u041e\u0433\u043d\u0435\u0441\u0442\u043e\u0439\u043a\u043e\u0441\u0442\u044c (00:10)");
                        battleCry_lore.add(ChatColor.BLUE + "\u0421\u0438\u043b\u0430 (00:10)");
                        battleCry_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 40 \u0441\u0435\u043a\u0443\u043d\u0434");
                        battleCry_meta.setLore(battleCry_lore);
                        battleCry_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        battleCry.setItemMeta(battleCry_meta);
                        ItemStack almostDead = new ItemStack(Material.TOTEM_OF_UNDYING);
                        ItemMeta almostDead_meta = almostDead.getItemMeta();
                        almostDead_meta.setDisplayName(ChatColor.GOLD + "\u0412\u043e\u043b\u044f \u043a \u0436\u0438\u0437\u043d\u0438" + ChatColor.GRAY + " (50)");
                        ArrayList<String> almostDead_lore = new ArrayList<String>();
                        almostDead_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        almostDead_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u0441\u043c\u0435\u0440\u0442\u0435\u043b\u044c\u043d\u043e\u0433\u043e \u0443\u0440\u043e\u043d\u0430");
                        almostDead_lore.add(ChatColor.WHITE + "\u0432\u044b \u043d\u0435 \u0443\u043c\u0438\u0440\u0430\u0435\u0442\u0435,");
                        almostDead_lore.add(ChatColor.WHITE + "\u0430 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442\u0435 50% HP");
                        almostDead_lore.add("");
                        almostDead_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 300 \u0441\u0435\u043a\u0443\u043d\u0434");
                        almostDead_meta.setLore(almostDead_lore);
                        almostDead_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        almostDead.setItemMeta(almostDead_meta);
                        menu_items = new ItemStack[]{smite, massiveHeal, regainHealth, rejection, battleCry, almostDead, null, null, null, null, massiveRegen};
                        break;
                    }
                    case "tank": {
                        gui = Bukkit.createInventory(player, 18, ChatColor.BLACK + "\u0421\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u0438");
                        ItemStack naturalArmor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                        ItemMeta naturalArmor_meta = naturalArmor.getItemMeta();
                        naturalArmor_meta.setDisplayName(ChatColor.DARK_RED + "\u0412\u0440\u043e\u0436\u0434\u0451\u043d\u043d\u0430\u044f \u0431\u0440\u043e\u043d\u044f" + ChatColor.GRAY + " (0)");
                        ArrayList<String> naturalArmor_lore = new ArrayList<String>();
                        naturalArmor_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        naturalArmor_lore.add(ChatColor.WHITE + "\u0412\u044b \u043e\u0431\u043b\u0430\u0434\u0430\u0435\u0442\u0435 \u0434\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u043c\u0438");
                        naturalArmor_lore.add(ChatColor.WHITE + "5 \u043e\u0447\u043a\u0430\u043c\u0438 \u0431\u0440\u043e\u043d\u0438");
                        naturalArmor_meta.setLore(naturalArmor_lore);
                        naturalArmor_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        naturalArmor.setItemMeta(naturalArmor_meta);
                        ItemStack slow = this.getPotionItemStack(PotionType.SLOWNESS, true, false);
                        ItemMeta slow_meta = slow.getItemMeta();
                        slow_meta.setDisplayName(ChatColor.DARK_RED + "\u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c" + ChatColor.GRAY + " (0)");
                        ArrayList<String> slow_lore = new ArrayList<String>();
                        slow_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        slow_lore.add(ChatColor.RED + "\u041c\u0435\u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c (**:**)");
                        slow_meta.setLore(slow_lore);
                        slow_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        slow.setItemMeta(slow_meta);
                        ItemStack extraHP = new ItemStack(Material.GOLDEN_APPLE);
                        ItemMeta extraHP_meta = extraHP.getItemMeta();
                        extraHP_meta.setDisplayName(ChatColor.YELLOW + "\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435" + ChatColor.GRAY + " (10)");
                        ArrayList<String> extraHP_lore = new ArrayList<String>();
                        extraHP_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        extraHP_lore.add(ChatColor.WHITE + "\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u0442\u0435 \u0434\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0435 \u0441\u0435\u0440\u0434\u0446\u0435");
                        extraHP_lore.add(ChatColor.WHITE + "\u043a \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044e \u043a\u0430\u0436\u0434\u044b\u0435 10 \u0443\u0440\u043e\u0432\u043d\u0435\u0439, \u0430 \u043d\u0435 20");
                        extraHP_meta.setLore(extraHP_lore);
                        extraHP_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        extraHP.setItemMeta(extraHP_meta);
                        ItemStack reduceKnockback = new ItemStack(Material.DIAMOND_BOOTS);
                        ItemMeta reduceKnockback_meta = reduceKnockback.getItemMeta();
                        reduceKnockback_meta.setDisplayName(ChatColor.DARK_GREEN + "\u0421\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u0435 \u043e\u0442\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u043d\u0438\u044e " + ChatColor.GRAY + " (20)");
                        ArrayList<String> reduceKnockback_lore = new ArrayList<String>();
                        reduceKnockback_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        reduceKnockback_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u0443\u0440\u043e\u043d\u0430");
                        reduceKnockback_lore.add(ChatColor.WHITE + "\u0432\u0430\u0441 \u043e\u0442\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u0435\u0442 \u0432 2 \u0440\u0430\u0437\u0430 \u0441\u043b\u0430\u0431\u0435\u0435");
                        reduceKnockback_meta.setLore(reduceKnockback_lore);
                        reduceKnockback_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        reduceKnockback.setItemMeta(reduceKnockback_meta);
                        ItemStack poisonedSkin = new ItemStack(Material.LEATHER);
                        ItemMeta poisonedSkin_meta = poisonedSkin.getItemMeta();
                        poisonedSkin_meta.setDisplayName(ChatColor.DARK_AQUA + "\u042f\u0434\u043e\u0432\u0438\u0442\u0430\u044f \u043a\u043e\u0436\u0430 " + ChatColor.GRAY + " (30)");
                        ArrayList<String> poisonedSkin_lore = new ArrayList<String>();
                        poisonedSkin_lore.add(ChatColor.WHITE + "\u0412 \u0442\u0435\u0447\u0435\u043d\u0438\u0435 8 \u0441\u0435\u043a\u0443\u043d\u0434");
                        poisonedSkin_lore.add(ChatColor.WHITE + "\u0430\u0442\u0430\u043a\u0443\u044e\u0449\u0438\u0435 \u0432\u0430\u0441 \u0438\u0433\u0440\u043e\u043a\u0438");
                        poisonedSkin_lore.add(ChatColor.WHITE + "\u043f\u043e\u043b\u0443\u0447\u0430\u044e\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u044b ");
                        poisonedSkin_lore.add("");
                        poisonedSkin_lore.add(ChatColor.RED + "\u041e\u0442\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 (00:06)");
                        poisonedSkin_lore.add(ChatColor.RED + "\u0421\u043b\u0430\u0431\u043e\u0441\u0442\u044c (00:06)");
                        poisonedSkin_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 25 \u0441\u0435\u043a\u0443\u043d\u0434");
                        poisonedSkin_meta.setLore(poisonedSkin_lore);
                        poisonedSkin_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        poisonedSkin.setItemMeta(poisonedSkin_meta);
                        ItemStack attraction = new ItemStack(Material.SUGAR);
                        ItemMeta attraction_meta = attraction.getItemMeta();
                        attraction_meta.setDisplayName(ChatColor.AQUA + "\u041f\u0440\u0438\u0442\u044f\u0436\u0435\u043d\u0438\u0435 " + ChatColor.GRAY + " (40)");
                        ArrayList<String> attraction_lore = new ArrayList<String>();
                        attraction_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438\u0442\u044f\u0433\u0438\u0432\u0430\u0435\u0442 \u0432\u0441\u0435\u0445 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439");
                        attraction_lore.add(ChatColor.WHITE + "\u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 10 \u0431\u043b\u043e\u043a\u043e\u0432,");
                        attraction_lore.add(ChatColor.WHITE + "\u043a\u0440\u043e\u043c\u0435 \u0441\u043e\u043a\u043b\u0430\u043d\u043e\u0432\u0446\u0435\u0432");
                        attraction_lore.add("");
                        attraction_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 10 \u0441\u0435\u043a\u0443\u043d\u0434");
                        attraction_meta.setLore(attraction_lore);
                        attraction_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        attraction.setItemMeta(attraction_meta);
                        ItemStack resetAndBuff = new ItemStack(Material.APPLE);
                        ItemMeta resetAndBuff_meta = resetAndBuff.getItemMeta();
                        resetAndBuff_meta.setDisplayName(ChatColor.GOLD + "\u041e\u0447\u0438\u0449\u0435\u043d\u0438\u0435 " + ChatColor.GRAY + " (50)");
                        ArrayList<String> resetAndBuff_lore = new ArrayList<String>();
                        resetAndBuff_lore.add(ChatColor.WHITE + "\u0421\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u0435\u0442 \u0441 \u0432\u0430\u0441 \u0432\u0441\u0435 \u0434\u0435\u0431\u0430\u0444\u0444\u044b");
                        resetAndBuff_lore.add(ChatColor.WHITE + "\u0438 \u043d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u044b");
                        resetAndBuff_lore.add("");
                        resetAndBuff_lore.add(ChatColor.BLUE + "\u041e\u0433\u043d\u0435\u0441\u0442\u043e\u0439\u043a\u043e\u0441\u0442\u044c (00:08)");
                        resetAndBuff_lore.add(ChatColor.BLUE + "\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f II (00:08)");
                        resetAndBuff_lore.add(ChatColor.BLUE + "\u0421\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u0435 II (00:08)");
                        resetAndBuff_lore.add(ChatColor.BLUE + "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c II (00:08)");
                        resetAndBuff_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 20 \u0441\u0435\u043a\u0443\u043d\u0434");
                        resetAndBuff_meta.setLore(resetAndBuff_lore);
                        resetAndBuff_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        resetAndBuff.setItemMeta(resetAndBuff_meta);
                        menu_items = new ItemStack[]{naturalArmor, extraHP, reduceKnockback, poisonedSkin, attraction, resetAndBuff, null, null, null, slow};
                        break;
                    }
                    case "warrior": {
                        ItemStack naturalStrength = new ItemStack(Material.WOODEN_SWORD);
                        ItemMeta naturalStrength_meta = naturalStrength.getItemMeta();
                        naturalStrength_meta.setDisplayName(ChatColor.DARK_RED + "\u0421\u0438\u043b\u0430 \u0443\u0434\u0430\u0440\u0430" + ChatColor.GRAY + " (0)");
                        ArrayList<String> naturalStrength_lore = new ArrayList<String>();
                        naturalStrength_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        naturalStrength_lore.add(ChatColor.WHITE + "\u0412\u0430\u0448 \u0443\u0440\u043e\u043d \u0443\u0432\u0435\u043b\u0438\u0447\u0435\u043d \u043d\u0430 1");
                        naturalStrength_meta.setLore(naturalStrength_lore);
                        naturalStrength_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        naturalStrength.setItemMeta(naturalStrength_meta);
                        ItemStack dash = new ItemStack(Material.IRON_BOOTS);
                        ItemMeta dash_meta = dash.getItemMeta();
                        dash_meta.setDisplayName(ChatColor.YELLOW + "\u0420\u044b\u0432\u043e\u043a" + ChatColor.GRAY + " (10)");
                        ArrayList<String> dash_lore = new ArrayList<String>();
                        dash_lore.add(ChatColor.WHITE + "\u041f\u0440\u0438 \u043d\u0430\u0436\u0430\u0442\u0438\u0438 \u043f\u0440\u043e\u0431\u0435\u043b\u0430 \u0442\u0440\u0438\u0436\u0434\u044b");
                        dash_lore.add(ChatColor.WHITE + "\u0432\u044b \u0443\u0441\u0442\u0440\u0435\u043c\u043b\u044f\u0435\u0442\u0435\u0441\u044c \u0432\u043f\u0435\u0440\u0451\u0434");
                        dash_lore.add("");
                        dash_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 7 \u0441\u0435\u043a\u0443\u043d\u0434");
                        dash_meta.setLore(dash_lore);
                        dash_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        dash.setItemMeta(dash_meta);
                        ItemStack shieldBreak = new ItemStack(Material.SHIELD);
                        ItemMeta shieldBreak_meta = shieldBreak.getItemMeta();
                        shieldBreak_meta.setDisplayName(ChatColor.DARK_GREEN + "\u041f\u0440\u043e\u0431\u0438\u0442\u0438\u0435 \u0431\u043b\u043e\u043a\u0430" + ChatColor.GRAY + " (20)");
                        ArrayList<String> shieldBreak_lore = new ArrayList<String>();
                        shieldBreak_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        shieldBreak_lore.add(ChatColor.WHITE + "\u0412\u0430\u0448\u0430 \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u0430\u0442\u0430\u043a\u0430");
                        shieldBreak_lore.add(ChatColor.WHITE + "\u0443\u0431\u0435\u0440\u0451\u0442 \u0449\u0438\u0442 \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430");
                        shieldBreak_lore.add(ChatColor.WHITE + "\u0438\u0437 \u043b\u0435\u0432\u043e\u0439 \u0440\u0443\u043a\u0438 \u043d\u0430 1.5 \u0441\u0435\u043a\u0443\u043d\u0434\u044b");
                        shieldBreak_lore.add("");
                        shieldBreak_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 7 \u0441\u0435\u043a\u0443\u043d\u0434");
                        shieldBreak_meta.setLore(shieldBreak_lore);
                        shieldBreak_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        shieldBreak.setItemMeta(shieldBreak_meta);
                        ItemStack powerUp = new ItemStack(Material.STONE_SWORD);
                        ItemMeta powerUp_meta = powerUp.getItemMeta();
                        powerUp_meta.setDisplayName(ChatColor.DARK_AQUA + "\u0423\u0441\u0438\u043b\u0435\u043d\u0438\u0435" + ChatColor.GRAY + " (30)");
                        ArrayList<String> powerUp_lore = new ArrayList<String>();
                        powerUp_lore.add(ChatColor.WHITE + "\u041f\u041a\u041c \u043b\u044e\u0431\u044b\u043c \u043c\u0435\u0447\u043e\u043c \u043d\u0430 \u0431\u043b\u043e\u043a - ");
                        powerUp_lore.add(ChatColor.WHITE + "\u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u0442\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b");
                        powerUp_lore.add("");
                        powerUp_lore.add(ChatColor.BLUE + "\u0421\u0438\u043b\u0430 (00:10)");
                        powerUp_lore.add(ChatColor.BLUE + "\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f  (00:10)");
                        powerUp_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 15 \u0441\u0435\u043a\u0443\u043d\u0434");
                        powerUp_meta.setLore(powerUp_lore);
                        powerUp_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        powerUp.setItemMeta(powerUp_meta);
                        ItemStack immunity = new ItemStack(Material.GLOWSTONE_DUST);
                        ItemMeta immunity_meta = immunity.getItemMeta();
                        immunity_meta.setDisplayName(ChatColor.AQUA + "\u0418\u043c\u043c\u0443\u043d\u0438\u0442\u0435\u0442" + ChatColor.GRAY + " (40)");
                        ArrayList<String> immunity_lore = new ArrayList<String>();
                        immunity_lore.add(ChatColor.WHITE + "\u0412 \u0442\u0435\u0447\u0435\u043d\u0438\u0435 10 \u0441\u0435\u043a\u0443\u043d\u0434");
                        immunity_lore.add(ChatColor.WHITE + "\u0432\u0441\u0435 \u043d\u0430\u043b\u043e\u0436\u0435\u043d\u043d\u044b\u0435 \u043d\u0430 \u0432\u0430\u0441 \u0434\u0435\u0431\u0430\u0444\u0444\u044b");
                        immunity_lore.add(ChatColor.WHITE + "\u0431\u0443\u0434\u0443\u0442 \u043c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e \u0441\u043d\u044f\u0442\u044b");
                        immunity_lore.add("");
                        immunity_lore.add(ChatColor.RED + "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 (00:10)");
                        immunity_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 60 \u0441\u0435\u043a\u0443\u043d\u0434");
                        immunity_meta.setLore(immunity_lore);
                        immunity_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        immunity.setItemMeta(immunity_meta);
                        ItemStack berserkMode = new ItemStack(Material.GOLDEN_AXE);
                        ItemMeta berserkMode_meta = berserkMode.getItemMeta();
                        berserkMode_meta.setDisplayName(ChatColor.GOLD + "\u0420\u0435\u0436\u0438\u043c \u0431\u0435\u0440\u0441\u0435\u0440\u043a\u0430" + ChatColor.GRAY + " (50)");
                        ArrayList<String> berserkMode_lore = new ArrayList<String>();
                        berserkMode_lore.add(ChatColor.ITALIC + "\u041f\u0430\u0441\u0441\u0438\u0432\u043d\u043e:");
                        berserkMode_lore.add(ChatColor.WHITE + "\u0415\u0441\u043b\u0438 \u043f\u043e\u0441\u043b\u0435 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438\u044f \u0443\u0440\u043e\u043d\u0430");
                        berserkMode_lore.add(ChatColor.WHITE + "\u0432\u0430\u0448\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0441\u0442\u0430\u043b\u043e \u043c\u0435\u043d\u044c\u0448\u0435 30%");
                        berserkMode_lore.add(ChatColor.WHITE + "\u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u0442\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b");
                        berserkMode_lore.add("");
                        berserkMode_lore.add(ChatColor.BLUE + "\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f (00:10)");
                        berserkMode_lore.add(ChatColor.BLUE + "\u0421\u0438\u043b\u0430 II (00:10)");
                        berserkMode_lore.add(ChatColor.BLUE + "\u041f\u043e\u0433\u043b\u043e\u0449\u0435\u043d\u0438\u0435 II (00:10)");
                        berserkMode_lore.add(ChatColor.GREEN + "\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430: 300 \u0441\u0435\u043a\u0443\u043d\u0434");
                        berserkMode_meta.setLore(berserkMode_lore);
                        berserkMode_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        berserkMode.setItemMeta(berserkMode_meta);
                        menu_items = new ItemStack[]{naturalStrength, dash, shieldBreak, powerUp, immunity, berserkMode};
                        break;
                    }
                    case "user": {
                        player.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043a\u043b\u0430\u0441\u0441\u0430! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /\u0441 choose <class>");
                        break;
                    }
                    default: {
                        player.sendMessage(ChatColor.RED + "\u041a\u043b\u0430\u0441\u0441\u0430 " + selectedClass + " \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442.");
                    }
                }
                gui.setContents(menu_items);
                player.openInventory(gui);
                break;
            }
            case "choose": {
                String Class2;
                if (!(sender instanceof Player)) break;
                if (args.length >= 2) {
                    Class2 = args[1];
                    if (!(Class2.equals("healer") || Class2.equals("tank") || Class2.equals("warrior") || Class2.equals("archer") || Class2.equals("assassin"))) {
                        sender.sendMessage(ChatColor.RED + "\u0422\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0448\u044c \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043a\u043b\u0430\u0441\u0441 " + Class2 + ": \u0435\u0433\u043e \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "\u0412\u044b \u043d\u0435 \u0432\u044b\u0431\u0440\u0430\u043b\u0438 \u043a\u043b\u0430\u0441\u0441! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /c choose <class>");
                    return true;
                }
                UUID id = ((Player) sender).getUniqueId();
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(sender.getName());
                String selectedClass = playerInfo.getSelectedClass();
                String nickname = sender.getName();
                int lvl = playerInfo.getLvl();
                int exp = playerInfo.getExp();
                if (playerInfo.getSelectedClass().equals("none")) {
                    Player player = ((Player) sender).getPlayer();
                    C.removeEffects(player);
                    RestorePotionEffects.calculateHP(player);
                    switch (Class2) {
                        case "healer": {
                            LP.addPermission(id, "essentials.kits.healer");
                            break;
                        }
                        case "tank": {
                            LP.addPermission(id, "essentials.kits.tank");
                            player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(5.0);
                            player.addPotionEffect(PotionEffectType.SLOW.createEffect(6000000, 0));
                            if (lvl < 20) break;
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
                            break;
                        }
                        case "warrior": {
                            LP.addPermission(id, "essentials.kits.warrior");
                            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0);
                            break;
                        }
                        case "archer": {
                            LP.addPermission(id, "essentials.kits.archer");
                            if (lvl < 30) break;
                            player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                            break;
                        }
                        case "assassin": {
                            LP.addPermission(id, "essentials.kits.assassin");
                            player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                            if (lvl < 40) break;
                            player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
                        }
                    }
                    JsonUtils.createPlayerInfo(nickname, Class2, lvl, exp);
                    sender.sendMessage(ChatColor.GREEN + "\u0422\u0435\u043f\u0435\u0440\u044c \u0442\u044b - " + Class2);
                    break;
                }
                sender.sendMessage(ChatColor.RED + "\u0422\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0448\u044c \u0441\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u043b\u0430\u0441\u0441, \u0442.\u043a. \u0443\u0436\u0435 \u0432\u044b\u0431\u0440\u0430\u043b " + selectedClass);
                break;
            }
            case "reset": {
                if (sender instanceof Player && args.length == 1) {
                    Player player = (Player) sender;
                    String name = player.getName();
                    UUID id = player.getUniqueId();
                    JsonUtils.createPlayerInfo(name, "none", 0, 0);
                    player.sendMessage(ChatColor.YELLOW + "\u0422\u0435\u043f\u0435\u0440\u044c \u0443 \u0432\u0430\u0441 0 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0438 \u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u043e\u0432\u044b\u0439 \u043a\u043b\u0430\u0441\u0441.");
                    C.removeEffects(player);
                    break;
                }
                if (args.length != 2) break;
                String name = args[1];
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(name);
                if (!sender.hasPermission("cwc.reset")) {
                    sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432!");
                    return true;
                }
                if (playerInfo == null) {
                    sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d.");
                    return true;
                }
                JsonUtils.createPlayerInfo(name, "none", 0, 0);
                Player player = Bukkit.getPlayer(name);
                player.sendMessage(ChatColor.YELLOW + "\u0422\u0435\u043f\u0435\u0440\u044c \u0443 \u0432\u0430\u0441 0 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0438 \u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u043e\u0432\u044b\u0439 \u043a\u043b\u0430\u0441\u0441.");
                sender.sendMessage(ChatColor.YELLOW + "\u0412\u044b \u0441\u0431\u0440\u043e\u0441\u0438\u043b\u0438 \u0438\u0433\u0440\u043e\u043a\u0443 " + name + " \u043a\u043b\u0430\u0441\u0441 \u0438 \u0443\u0440\u043e\u0432\u0435\u043d\u044c.");
                C.removeEffects(player);
                break;
            }
            case "remove": {
                if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (!sender.hasPermission("cwc.remove")) {
                        sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432!");
                        return true;
                    }
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d.");
                        return true;
                    }
                    UUID id = player.getUniqueId();
                    String nickname = player.getName();
                    String selectedClass = JsonUtils.getPlayerInfoName(player.getName()).getSelectedClass();
                    int lvl = JsonUtils.getPlayerInfoName(player.getName()).getLvl();
                    int exp = JsonUtils.getPlayerInfoName(player.getName()).getExp();
                    C.removeEffects(player);
                    JsonUtils.createPlayerInfo(nickname, "none", lvl, exp);
                    sender.sendMessage(ChatColor.YELLOW + "\u0412\u044b \u0443\u0431\u0440\u0430\u043b\u0438 \u043a\u043b\u0430\u0441\u0441 " + ChatColor.GREEN + selectedClass + ChatColor.YELLOW + " \u0443 \u0438\u0433\u0440\u043e\u043a\u0430 " + ChatColor.GREEN + nickname);
                    player.sendMessage(ChatColor.YELLOW + "\u0423 \u0432\u0430\u0441 \u0443\u0431\u0440\u0430\u043b\u0438 \u043a\u043b\u0430\u0441\u0441, \u0438 \u0442\u0435\u043f\u0435\u0440\u044c \u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u043e\u0432\u044b\u0439!");
                    break;
                }
                sender.sendMessage(ChatColor.RED + "\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e \u043d\u0430\u043f\u0438\u0441\u0430\u043d\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /c remove <player>");
                break;
            }
            case "givexp": {
                if (args.length == 3) {
                    int exp;
                    Player player = Bukkit.getPlayer(args[1]);
                    if (!sender.hasPermission("cwc.givexp")) {
                        sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432.");
                        return true;
                    }
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d.");
                        return true;
                    }
                    UUID id = player.getUniqueId();
                    int amount = Integer.parseInt(args[2]);
                    PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
                    String nickname = player.getName();
                    String selectedClass = playerInfo.getSelectedClass();
                    int lvl = playerInfo.getLvl();
                    int new_lvl_exp = ExpGaining.calcNewLvl(lvl);
                    player.sendMessage(ChatColor.YELLOW + "\u0418\u0433\u0440\u043e\u043a " + sender.getName() + " \u0432\u044b\u0434\u0430\u043b \u0432\u0430\u043c " + amount + " \u043e\u043f\u044b\u0442\u0430");
                    for (exp = playerInfo.getExp() + amount; exp >= new_lvl_exp; exp -= new_lvl_exp) {
                        new_lvl_exp = ExpGaining.calcNewLvl(++lvl);
                        player.sendMessage(ChatColor.YELLOW + "\u0412\u0430\u0448 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u043f\u043e\u0432\u044b\u0448\u0435\u043d \u0434\u043e " + lvl + '!');
                    }
                    JsonUtils.createPlayerInfo(nickname, selectedClass, lvl, exp);
                    RestorePotionEffects.calculateHP(player);
                    break;
                }
                sender.sendMessage(ChatColor.RED + "\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e \u043d\u0430\u043f\u0438\u0441\u0430\u043d\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /c givexp <player> <xp>");
                break;
            }
            case "setlvl": {
                if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (!sender.hasPermission("cwc.setlvl")) {
                        sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432!");
                        return true;
                    }
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d.");
                        return true;
                    }
                    int amount = Integer.parseInt(args[2]);
                    UUID id = player.getUniqueId();
                    PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
                    String nickname = player.getName();
                    String selectedClass = playerInfo.getSelectedClass();
                    int lvl = playerInfo.getLvl();
                    if (amount > lvl) {
                        player.sendMessage(ChatColor.YELLOW + sender.getName() + " \u043f\u043e\u0432\u044b\u0441\u0438\u043b \u0432\u0430\u0448 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0434\u043e " + amount + '!');
                    } else if (amount < lvl) {
                        player.sendMessage(ChatColor.YELLOW + sender.getName() + " \u043f\u043e\u043d\u0438\u0437\u0438\u043b \u0432\u0430\u0448 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0434\u043e " + amount + '!');
                    }
                    JsonUtils.createPlayerInfo(nickname, selectedClass, amount, 0);
                    RestorePotionEffects.calculateHP(player);
                    break;
                }
                sender.sendMessage(ChatColor.RED + "\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e \u043d\u0430\u043f\u0438\u0441\u0430\u043d\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /c setlvl <player> <lvl>");
                break;
            }
            case "buster": {
                if (args.length != 2) break;
                if (!sender.hasPermission("cwc.buster")) {
                    sender.sendMessage(ChatColor.RED + "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0440\u0430\u0432!");
                    return true;
                }
                double amount = Double.parseDouble(args[1]);
                amount = Math.max(amount, 500.0);
                ExpGaining.global_buster = amount /= 100.0;
                Bukkit.broadcastMessage(ChatColor.YELLOW + "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u043c\u043e\u0433\u043e \u043e\u043f\u044b\u0442\u0430 - " + ChatColor.GREEN + amount * 100.0 + "%");
            }
        }
        return true;
    }

    public ItemStack getPotionItemStack(PotionType type, boolean extend, boolean upgraded) {
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extend, upgraded));
        potion.setItemMeta(meta);
        return potion;
    }
}

