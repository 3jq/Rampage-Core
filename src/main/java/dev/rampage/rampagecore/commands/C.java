package dev.rampage.rampagecore.commands;

import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import dev.rampage.rampagecore.api.listeners.ExpGainingListener;
import dev.rampage.rampagecore.api.utils.LuckPermsUtils;
import dev.rampage.rampagecore.api.listeners.PotionListener;
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
        LuckPermsUtils.removePermission(id, "essentials.kits.healer");
        LuckPermsUtils.removePermission(id, "essentials.kits.tank");
        LuckPermsUtils.removePermission(id, "essentials.kits.warrior");
        LuckPermsUtils.removePermission(id, "essentials.kits.archer");
        LuckPermsUtils.removePermission(id, "essentials.kits.assassin");
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(false);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Вы не написали команду. Используйте /c <command>");
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
                    int newLvlExp = ExpGainingListener.calcNewLvl(lvl);
                    player.sendMessage(ChatColor.DARK_RED + "Статистика игрока " + ChatColor.AQUA + name);
                    player.sendMessage(ChatColor.YELLOW + "Класс: " + ChatColor.GREEN + selectedClass);
                    player.sendMessage(ChatColor.YELLOW + "Уровень: " + ChatColor.WHITE + lvl);
                    player.sendMessage(ChatColor.YELLOW + "Опыт: " + ChatColor.WHITE + exp + '/' + newLvlExp);
                    break;
                }

                if (args.length != 2) break;
                String name = args[1];
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(name);
                if (!sender.hasPermission("cwc.stats")) {
                    sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
                    return true;
                }

                if (playerInfo == null) {
                    sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                    return true;
                }

                String selectedClass = playerInfo.getSelectedClass();
                int lvl = playerInfo.getLvl();
                int exp = playerInfo.getExp();
                int newLvlExp = ExpGainingListener.calcNewLvl(lvl);
                sender.sendMessage(ChatColor.DARK_RED + "Статистика игрока " + ChatColor.AQUA + name);
                sender.sendMessage(ChatColor.YELLOW + "Класс: " + ChatColor.GREEN + selectedClass);
                sender.sendMessage(ChatColor.YELLOW + "Уровень: " + ChatColor.WHITE + lvl);
                sender.sendMessage(ChatColor.YELLOW + "Опыт: " + ChatColor.WHITE + exp + '/' + newLvlExp);
                break;
            }

            case "skills": {
                if (!(sender instanceof Player)) break;
                Player player = (Player) sender;
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
                String selectedClass = playerInfo.getSelectedClass();
                if (args.length == 1) {
                    if (selectedClass.equalsIgnoreCase("none")) {
                        sender.sendMessage(ChatColor.RED + "У вас нет класса!");
                        sender.sendMessage(ChatColor.RED + "Используйте /c choose <class>");
                        return true;
                    }
                } else {
                    selectedClass = args[1];
                }

                Inventory gui = Bukkit.createInventory(player, 9, ChatColor.BLACK + "Способности");
                ItemStack[] menuItems = new ItemStack[]{};

                switch (selectedClass) {
                    case "archer":
                        gui = Bukkit.createInventory(player, 18, ChatColor.BLACK + "Способности");

                        ItemStack arrowSpeed = new ItemStack(Material.BOW);
                        ItemMeta arrowSpeedMeta = arrowSpeed.getItemMeta();

                        arrowSpeedMeta.setDisplayName(ChatColor.DARK_RED + "Стремительность" + ChatColor.GRAY + " (0)");

                        ArrayList<String> arrowSpeedLore = new ArrayList<>();

                        arrowSpeedLore.add(ChatColor.ITALIC + "Пассивно:" + ChatColor.WHITE + " выпущенные вами стрелы");
                        arrowSpeedLore.add(ChatColor.WHITE + "летят в 1.5 раза быстрее");
                        arrowSpeedMeta.setLore(arrowSpeedLore);
                        arrowSpeed.setItemMeta(arrowSpeedMeta);

                        ItemStack rebound = new ItemStack(Material.IRON_BOOTS);
                        ItemMeta reboundMeta = rebound.getItemMeta();

                        reboundMeta.setDisplayName(ChatColor.YELLOW + "Отскок" + ChatColor.GRAY + " (10)");

                        ArrayList<String> reboundLore = new ArrayList<>();

                        reboundLore.add(ChatColor.WHITE + "При нажатии пробела трижды");
                        reboundLore.add(ChatColor.WHITE + "вы отбрасываетесь назад");
                        reboundLore.add("");
                        reboundLore.add(ChatColor.GREEN + "Перезарядка: 5 секунд");
                        reboundMeta.setLore(reboundLore);
                        reboundMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        rebound.setItemMeta(reboundMeta);

                        ItemStack rabbitJump = new ItemStack(Material.RABBIT_FOOT);
                        ItemMeta rabbitJumpMeta = rabbitJump.getItemMeta();

                        rabbitJumpMeta.setDisplayName(ChatColor.GREEN + "Кроличий прыжок" + ChatColor.GRAY + " (20)");
                        ArrayList<String> rabbitJumpLore = new ArrayList<>();
                        rabbitJumpLore.add(ChatColor.WHITE + "Получение эффекта");
                        rabbitJumpLore.add("");
                        rabbitJumpLore.add(ChatColor.BLUE + "Прыгучесть (00:07)");
                        rabbitJumpLore.add(ChatColor.GREEN + "Перезарядка: 20 секунд");
                        rabbitJumpMeta.setLore(rabbitJumpLore);
                        rabbitJump.setItemMeta(rabbitJumpMeta);

                        ItemStack speedPotion = this.getPotionItemStack(PotionType.SPEED, true, false);
                        ItemMeta speedPotionMeta = speedPotion.getItemMeta();

                        speedPotionMeta.setDisplayName(ChatColor.DARK_AQUA + "Скорость" + ChatColor.GRAY + " (30)");

                        ArrayList<String> speedPotionLore = new ArrayList<>();

                        speedPotionLore.add(ChatColor.ITALIC + "Пассивно:");
                        speedPotionLore.add(ChatColor.BLUE + "Скорость (**:**)");
                        speedPotionMeta.setLore(speedPotionLore);
                        speedPotionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        speedPotion.setItemMeta(speedPotionMeta);

                        ItemStack arrowPoison = new ItemStack(Material.TIPPED_ARROW);
                        PotionMeta arrowPoisonMetaP = (PotionMeta) arrowPoison.getItemMeta();

                        arrowPoisonMetaP.setBasePotionData(new PotionData(PotionType.POISON));
                        arrowPoison.setItemMeta(arrowPoisonMetaP);

                        ItemMeta arrowPoisonMeta = arrowPoison.getItemMeta();

                        arrowPoisonMeta.setDisplayName(ChatColor.AQUA + "Стрела отравления" + ChatColor.GRAY + " (40)");

                        ArrayList<String> arrowPosionLore = new ArrayList<>();

                        arrowPosionLore.add(ChatColor.WHITE + "Выпускает стрелу отравления");
                        arrowPosionLore.add(ChatColor.WHITE + "по направлению взгляда");
                        arrowPosionLore.add("");
                        arrowPosionLore.add(ChatColor.RED + "Отравление III (0:03)");
                        arrowPosionLore.add(ChatColor.GREEN + "Перезарядка: 10 секунд");
                        arrowPoisonMeta.setLore(arrowPosionLore);
                        arrowPoisonMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        arrowPoison.setItemMeta(arrowPoisonMeta);

                        ItemStack arrowSlow = new ItemStack(Material.TIPPED_ARROW);
                        PotionMeta arrowPMetaS = (PotionMeta) arrowSlow.getItemMeta();

                        arrowPMetaS.setBasePotionData(new PotionData(PotionType.SLOWNESS));
                        arrowSlow.setItemMeta(arrowPMetaS);

                        ItemMeta arrowSlowMeta = arrowSlow.getItemMeta();

                        arrowSlowMeta.setDisplayName(ChatColor.AQUA + "Стрела медлительности" + ChatColor.GRAY + " (40)");

                        ArrayList<String> arrowSlowLore = new ArrayList<>();

                        arrowSlowLore.add(ChatColor.WHITE + "Выпускает стрелу медлительности");
                        arrowSlowLore.add(ChatColor.WHITE + "по направлению взгляда");
                        arrowSlowLore.add("");
                        arrowSlowLore.add(ChatColor.RED + "Медлительность III (0:05)");
                        arrowSlowLore.add(ChatColor.GREEN + "Перезарядка: 10 секунд");
                        arrowSlowMeta.setLore(arrowSlowLore);
                        arrowSlowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        arrowSlow.setItemMeta(arrowSlowMeta);

                        ItemStack ascent = new ItemStack(Material.FEATHER);
                        ItemMeta ascentMeta = ascent.getItemMeta();

                        ascentMeta.setDisplayName(ChatColor.GOLD + "Взлёт" + ChatColor.GRAY + " (50)");

                        ArrayList<String> ascentLore = new ArrayList<>();

                        ascentLore.add(ChatColor.WHITE + "Поднимает в воздух на 30 блоков");
                        ascentLore.add(ChatColor.WHITE + "и замедляет падение");
                        ascentLore.add("");
                        ascentLore.add(ChatColor.BLUE + "Плавное падение (00:06)");
                        ascentLore.add(ChatColor.GREEN + "Перезарядка: 20 секунд");
                        ascentMeta.setLore(ascentLore);
                        ascent.setItemMeta(ascentMeta);

                        menuItems = new ItemStack[]{arrowSpeed, rebound, rabbitJump, speedPotion, arrowPoison, ascent, null, null, null, null, null, null, null, arrowSlow};

                        break;

                    case "assassin":
                        ItemStack speedAssassin = this.getPotionItemStack(PotionType.SPEED, true, false);
                        ItemMeta speedAssasinMeta = speedAssassin.getItemMeta();

                        speedAssasinMeta.setDisplayName(ChatColor.DARK_RED + "Скорость" + ChatColor.GRAY + " (0)");

                        ArrayList<String> speedAssassinLore = new ArrayList<>();

                        speedAssassinLore.add(ChatColor.ITALIC + "Пассивно:");
                        speedAssassinLore.add(ChatColor.BLUE + "Скорость (**:**)");
                        speedAssasinMeta.setLore(speedAssassinLore);
                        speedAssasinMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        speedAssassin.setItemMeta(speedAssasinMeta);

                        ItemStack loner = new ItemStack(Material.PLAYER_HEAD);
                        ItemMeta lonerMeta = loner.getItemMeta();

                        lonerMeta.setDisplayName(ChatColor.YELLOW + "Одиночка" + ChatColor.GRAY + " (10)");

                        ArrayList<String> lonerLore = new ArrayList<>();

                        lonerLore.add(ChatColor.ITALIC + "Пассивно:");
                        lonerLore.add(ChatColor.WHITE + "Увеличение урона на 30% по игроку если");
                        lonerLore.add(ChatColor.WHITE + "в радиусе 16 блоков нет игроков,");
                        lonerLore.add(ChatColor.WHITE + "кроме вас, соклановцев и жертвы");
                        lonerMeta.setLore(lonerLore);
                        lonerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        loner.setItemMeta(lonerMeta);

                        ItemStack burrow = new ItemStack(Material.WOODEN_SHOVEL);
                        ItemMeta burrowMeta = burrow.getItemMeta();

                        burrowMeta.setDisplayName(ChatColor.DARK_GREEN + "Засада" + ChatColor.GRAY + " (20)");

                        ArrayList<String> burrowLore = new ArrayList<>();

                        burrowLore.add(ChatColor.WHITE + "Нажмите ПКМ по блоку");
                        burrowLore.add(ChatColor.WHITE + "на котором стоите, чтобы");
                        burrowLore.add(ChatColor.WHITE + "'провалиться' в него");
                        burrowLore.add(ChatColor.WHITE + "и получить эффект");
                        burrowLore.add("");
                        burrowLore.add(ChatColor.BLUE + "Невидимость (**:**)");
                        burrowLore.add(ChatColor.GREEN + "Перезарядка: 10 секунд");
                        burrowMeta.setLore(burrowLore);
                        burrowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        burrowMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        burrow.setItemMeta(burrowMeta);

                        ItemStack nightVision = this.getPotionItemStack(PotionType.NIGHT_VISION, true, false);
                        ItemMeta nightVisionMeta = nightVision.getItemMeta();

                        nightVisionMeta.setDisplayName(ChatColor.DARK_AQUA + "Ночное зрение" + ChatColor.GRAY + " (30)");

                        ArrayList<String> nightVisionLore = new ArrayList<>();

                        nightVisionLore.add(ChatColor.ITALIC + "Пассивно:");
                        nightVisionLore.add(ChatColor.BLUE + "Ночное зрение (**:**)");
                        nightVisionMeta.setLore(nightVisionLore);
                        nightVisionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        nightVision.setItemMeta(nightVisionMeta);

                        ItemStack trauma = new ItemStack(Material.BONE);
                        ItemMeta traumaMeta = trauma.getItemMeta();

                        traumaMeta.setDisplayName(ChatColor.AQUA + "Травма" + ChatColor.GRAY + " (40)");

                        ArrayList<String> traumaLore = new ArrayList<>();

                        traumaLore.add(ChatColor.ITALIC + "Пассивно:");
                        traumaLore.add(ChatColor.WHITE + "При ударе шанс 15%");
                        traumaLore.add(ChatColor.WHITE + "наложить на игрока эффект");
                        traumaLore.add("");
                        traumaLore.add(ChatColor.RED + "Медлительность III (0:04)");
                        traumaMeta.setLore(traumaLore);
                        traumaMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        trauma.setItemMeta(traumaMeta);

                        ItemStack arrowBlind = new ItemStack(Material.TIPPED_ARROW);
                        PotionMeta arrowBlindMetaP = (PotionMeta) arrowBlind.getItemMeta();

                        arrowBlindMetaP.setBasePotionData(new PotionData(PotionType.WEAKNESS));
                        arrowBlind.setItemMeta(arrowBlindMetaP);

                        ItemMeta arrowBlindMeta = arrowBlind.getItemMeta();

                        arrowBlindMeta.setDisplayName(ChatColor.GOLD + "Стрела слепоты" + ChatColor.GRAY + " (50)");

                        ArrayList<String> arrowBlindLore = new ArrayList<>();

                        arrowBlindLore.add(ChatColor.WHITE + "При ударе стрелой слабости");
                        arrowBlindLore.add(ChatColor.WHITE + "на игрока накладывается эффект");
                        arrowBlindLore.add("");
                        arrowBlindLore.add(ChatColor.RED + "Слепота II (0:05)");
                        arrowBlindLore.add(ChatColor.GREEN + "Перезарядка: 30 секунд");
                        arrowBlindMeta.setLore(arrowBlindLore);
                        arrowBlindMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        arrowBlind.setItemMeta(arrowBlindMeta);

                        menuItems = new ItemStack[]{speedAssassin, loner, burrow, nightVision, trauma, arrowBlind};

                        break;

                    case "healer":
                        gui = Bukkit.createInventory(player, 18, ChatColor.BLACK + "Способности");

                        ItemStack smite = new ItemStack(Material.ZOMBIE_HEAD);
                        ItemMeta smiteMeta = smite.getItemMeta();

                        smiteMeta.setDisplayName(ChatColor.DARK_RED + "Небесная кара" + ChatColor.GRAY + " (0)");

                        ArrayList<String> smiteLore = new ArrayList<>();

                        smiteLore.add(ChatColor.ITALIC + "Пассивно:");
                        smiteLore.add(ChatColor.WHITE + "Увеличение урона на 30%");
                        smiteLore.add(ChatColor.WHITE + "по нежити");
                        smiteMeta.setLore(smiteLore);
                        smiteMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        smite.setItemMeta(smiteMeta);
                        ItemStack massiveHeal = this.getPotionItemStack(PotionType.INSTANT_HEAL, false, true);
                        ItemMeta massiveHealMeta = massiveHeal.getItemMeta();

                        massiveHealMeta.setDisplayName(ChatColor.YELLOW + "Массовое лечение" + ChatColor.GRAY + " (10)");

                        ArrayList<String> massiveHealLore = new ArrayList<>();

                        massiveHealLore.add(ChatColor.WHITE + "При использовании зелья лечения");
                        massiveHealLore.add(ChatColor.WHITE + "в радиусе 10 блоков");
                        massiveHealLore.add(ChatColor.WHITE + "соклановцы восстанавливают 4 HP");
                        massiveHealLore.add("");
                        massiveHealLore.add(ChatColor.BLUE + "Мгновенное лечение");
                        massiveHealLore.add(ChatColor.GREEN + "Перезарядка: 30 секунд");
                        massiveHealMeta.setLore(massiveHealLore);
                        massiveHealMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        massiveHeal.setItemMeta(massiveHealMeta);

                        ItemStack massiveRegen = this.getPotionItemStack(PotionType.REGEN, true, false);
                        ItemMeta massiveRegenMeta = massiveRegen.getItemMeta();

                        massiveRegenMeta.setDisplayName(ChatColor.YELLOW + "Массовая регенерация" + ChatColor.GRAY + " (10)");

                        ArrayList<String> massiveRegenLore = new ArrayList<>();

                        massiveRegenLore.add(ChatColor.WHITE + "При использовании зелья регенерации");
                        massiveRegenLore.add(ChatColor.WHITE + "соклановцы в радиусе 10 блоков");
                        massiveRegenLore.add(ChatColor.WHITE + "получают эффект");
                        massiveRegenLore.add("");
                        massiveRegenLore.add(ChatColor.BLUE + "Регенерация (00:10)");
                        massiveRegenLore.add(ChatColor.GREEN + "Перезарядка: 20 секунд");
                        massiveRegenMeta.setLore(massiveRegenLore);
                        massiveRegenMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        massiveRegen.setItemMeta(massiveRegenMeta);

                        ItemStack regainHealth = new ItemStack(Material.BREAD);
                        ItemMeta regainHealthMeta = regainHealth.getItemMeta();
                        regainHealthMeta.setDisplayName(ChatColor.DARK_GREEN + "Увеличение восстановления" + ChatColor.GRAY + " (20)");

                        ArrayList<String> regainHealthLore = new ArrayList<>();

                        regainHealthLore.add(ChatColor.ITALIC + "Пассивно:");
                        regainHealthLore.add(ChatColor.WHITE + "Любое восстановление здоровья");
                        regainHealthLore.add(ChatColor.WHITE + "увеличено на 20%");
                        regainHealthMeta.setLore(regainHealthLore);
                        regainHealthMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        regainHealth.setItemMeta(regainHealthMeta);

                        ItemStack rejection = new ItemStack(Material.SUGAR);
                        ItemMeta rejectionMeta = rejection.getItemMeta();

                        rejectionMeta.setDisplayName(ChatColor.DARK_AQUA + "Отталкивание" + ChatColor.GRAY + " (30)");

                        ArrayList<String> rejectionLore = new ArrayList<>();

                        rejectionLore.add(ChatColor.WHITE + "Отбрасывает всех сущностей");
                        rejectionLore.add(ChatColor.WHITE + "в радиусе 5 блоков,");
                        rejectionLore.add(ChatColor.WHITE + "кроме соклановцев");
                        massiveHealLore.add("");
                        massiveHealLore.add(ChatColor.GREEN + "Перезарядка: 15 секунд");
                        rejectionMeta.setLore(rejectionLore);
                        rejectionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        rejection.setItemMeta(rejectionMeta);

                        ItemStack battleCry = new ItemStack(Material.WOODEN_SWORD);
                        ItemMeta battleCryMeta = battleCry.getItemMeta();

                        battleCryMeta.setDisplayName(ChatColor.AQUA + "Боевой клич" + ChatColor.GRAY + " (40)");

                        ArrayList<String> battleCryLore = new ArrayList<>();

                        battleCryLore.add(ChatColor.WHITE + "ПКМ деревянным мечом на блок - ");
                        battleCryLore.add(ChatColor.WHITE + "на соклановцев в радиусе 10 блоков");
                        battleCryLore.add(ChatColor.WHITE + "накладываются эффекты");
                        battleCryLore.add("");
                        battleCryLore.add(ChatColor.BLUE + "Скорость (00:10)");
                        battleCryLore.add(ChatColor.BLUE + "Огнестойкость (00:10)");
                        battleCryLore.add(ChatColor.BLUE + "Сила (00:10)");
                        battleCryLore.add(ChatColor.GREEN + "Перезарядка: 40 секунд");
                        battleCryMeta.setLore(battleCryLore);
                        battleCryMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        battleCry.setItemMeta(battleCryMeta);

                        ItemStack almostDead = new ItemStack(Material.TOTEM_OF_UNDYING);
                        ItemMeta almostDeadMeta = almostDead.getItemMeta();

                        almostDeadMeta.setDisplayName(ChatColor.GOLD + "Воля к жизни" + ChatColor.GRAY + " (50)");

                        ArrayList<String> almostDeadLore = new ArrayList<>();

                        almostDeadLore.add(ChatColor.ITALIC + "Пассивно:");
                        almostDeadLore.add(ChatColor.WHITE + "При получении смертельного урона");
                        almostDeadLore.add(ChatColor.WHITE + "вы не умираете,");
                        almostDeadLore.add(ChatColor.WHITE + "а восстанавливаете 50% HP");
                        almostDeadLore.add("");
                        almostDeadLore.add(ChatColor.GREEN + "Перезарядка: 300 секунд");
                        almostDeadMeta.setLore(almostDeadLore);
                        almostDeadMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        almostDead.setItemMeta(almostDeadMeta);

                        menuItems = new ItemStack[]{smite, massiveHeal, regainHealth, rejection, battleCry, almostDead, null, null, null, null, massiveRegen};

                        break;

                    case "tank":
                        gui = Bukkit.createInventory(player, 18, ChatColor.BLACK + "Способности");

                        ItemStack naturalArmor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                        ItemMeta naturalArmorMeta = naturalArmor.getItemMeta();

                        naturalArmorMeta.setDisplayName(ChatColor.DARK_RED + "Врождённая броня" + ChatColor.GRAY + " (0)");

                        ArrayList<String> naturalArmorLore = new ArrayList<>();

                        naturalArmorLore.add(ChatColor.ITALIC + "Пассивно:");
                        naturalArmorLore.add(ChatColor.WHITE + "Вы обладаете дополнительными");
                        naturalArmorLore.add(ChatColor.WHITE + "5 очками брони");
                        naturalArmorMeta.setLore(naturalArmorLore);
                        naturalArmorMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        naturalArmor.setItemMeta(naturalArmorMeta);

                        ItemStack slow = this.getPotionItemStack(PotionType.SLOWNESS, true, false);
                        ItemMeta slowMeta = slow.getItemMeta();

                        slowMeta.setDisplayName(ChatColor.DARK_RED + "Медлительность" + ChatColor.GRAY + " (0)");

                        ArrayList<String> slowLore = new ArrayList<>();

                        slowLore.add(ChatColor.ITALIC + "Пассивно:");
                        slowLore.add(ChatColor.RED + "Медлительность (**:**)");
                        slowMeta.setLore(slowLore);
                        slowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        slow.setItemMeta(slowMeta);

                        ItemStack extraHP = new ItemStack(Material.GOLDEN_APPLE);
                        ItemMeta extraHPMeta = extraHP.getItemMeta();

                        extraHPMeta.setDisplayName(ChatColor.YELLOW + "Дополнительное здоровье" + ChatColor.GRAY + " (10)");

                        ArrayList<String> extraHPLore = new ArrayList<>();

                        extraHPLore.add(ChatColor.ITALIC + "Пассивно:");
                        extraHPLore.add(ChatColor.WHITE + "Вы получаете дополнительное сердце");
                        extraHPLore.add(ChatColor.WHITE + "к здоровью каждые 10 уровней, а не 20");
                        extraHPMeta.setLore(extraHPLore);
                        extraHPMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        extraHP.setItemMeta(extraHPMeta);

                        ItemStack reduceKnockback = new ItemStack(Material.DIAMOND_BOOTS);
                        ItemMeta reduceKnockbackMeta = reduceKnockback.getItemMeta();

                        reduceKnockbackMeta.setDisplayName(ChatColor.DARK_GREEN + "Сопротивление отбрасыванию " + ChatColor.GRAY + " (20)");

                        ArrayList<String> reduceKnockbackLore = new ArrayList<>();

                        reduceKnockbackLore.add(ChatColor.ITALIC + "Пассивно:");
                        reduceKnockbackLore.add(ChatColor.WHITE + "При получении урона");
                        reduceKnockbackLore.add(ChatColor.WHITE + "вас отбрасывает в 2 раза слабее");
                        reduceKnockbackMeta.setLore(reduceKnockbackLore);
                        reduceKnockbackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        reduceKnockback.setItemMeta(reduceKnockbackMeta);

                        ItemStack poisonedSkin = new ItemStack(Material.LEATHER);
                        ItemMeta poisonedSkinMeta = poisonedSkin.getItemMeta();

                        poisonedSkinMeta.setDisplayName(ChatColor.DARK_AQUA + "Ядовитая кожа " + ChatColor.GRAY + " (30)");

                        ArrayList<String> poisonedSkinLore = new ArrayList<>();

                        poisonedSkinLore.add(ChatColor.WHITE + "В течение 8 секунд");
                        poisonedSkinLore.add(ChatColor.WHITE + "атакующие вас игроки");
                        poisonedSkinLore.add(ChatColor.WHITE + "получают эффекты ");
                        poisonedSkinLore.add("");
                        poisonedSkinLore.add(ChatColor.RED + "Отравление (00:06)");
                        poisonedSkinLore.add(ChatColor.RED + "Слабость (00:06)");
                        poisonedSkinLore.add(ChatColor.GREEN + "Перезарядка: 25 секунд");
                        poisonedSkinMeta.setLore(poisonedSkinLore);
                        poisonedSkinMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        poisonedSkin.setItemMeta(poisonedSkinMeta);

                        ItemStack attraction = new ItemStack(Material.SUGAR);
                        ItemMeta attractionMeta = attraction.getItemMeta();

                        attractionMeta.setDisplayName(ChatColor.AQUA + "Притяжение " + ChatColor.GRAY + " (40)");

                        ArrayList<String> attractionLore = new ArrayList<>();

                        attractionLore.add(ChatColor.WHITE + "Притягивает всех сущностей");
                        attractionLore.add(ChatColor.WHITE + "в радиусе 10 блоков,");
                        attractionLore.add(ChatColor.WHITE + "кроме соклановцев");
                        attractionLore.add("");
                        attractionLore.add(ChatColor.GREEN + "Перезарядка: 10 секунд");
                        attractionMeta.setLore(attractionLore);
                        attractionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        attraction.setItemMeta(attractionMeta);

                        ItemStack resetAndBuff = new ItemStack(Material.APPLE);
                        ItemMeta resetAndBuffMeta = resetAndBuff.getItemMeta();

                        resetAndBuffMeta.setDisplayName(ChatColor.GOLD + "Очищение " + ChatColor.GRAY + " (50)");

                        ArrayList<String> resetAndBuffLore = new ArrayList<>();

                        resetAndBuffLore.add(ChatColor.WHITE + "Сбрасывает с вас все дебаффы");
                        resetAndBuffLore.add(ChatColor.WHITE + "и накладывает эффекты");
                        resetAndBuffLore.add("");
                        resetAndBuffLore.add(ChatColor.BLUE + "Огнестойкость (00:08)");
                        resetAndBuffLore.add(ChatColor.BLUE + "Регенерация II (00:08)");
                        resetAndBuffLore.add(ChatColor.BLUE + "Сопротивление II (00:08)");
                        resetAndBuffLore.add(ChatColor.BLUE + "Скорость II (00:08)");
                        resetAndBuffLore.add(ChatColor.GREEN + "Перезарядка: 20 секунд");
                        resetAndBuffMeta.setLore(resetAndBuffLore);
                        resetAndBuffMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        resetAndBuff.setItemMeta(resetAndBuffMeta);

                        menuItems = new ItemStack[]{naturalArmor, extraHP, reduceKnockback, poisonedSkin, attraction, resetAndBuff, null, null, null, slow};

                        break;

                    case "warrior":
                        ItemStack naturalStrength = new ItemStack(Material.WOODEN_SWORD);
                        ItemMeta naturalStrengthMeta = naturalStrength.getItemMeta();

                        naturalStrengthMeta.setDisplayName(ChatColor.DARK_RED + "Сила удара" + ChatColor.GRAY + " (0)");

                        ArrayList<String> naturalStrengthLore = new ArrayList<>();

                        naturalStrengthLore.add(ChatColor.ITALIC + "Пассивно:");
                        naturalStrengthLore.add(ChatColor.WHITE + "При атаке ваш урон увеличен на 1 единицу");
                        naturalStrengthMeta.setLore(naturalStrengthLore);
                        naturalStrengthMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        naturalStrength.setItemMeta(naturalStrengthMeta);

                        ItemStack dash = new ItemStack(Material.IRON_BOOTS);
                        ItemMeta dashMeta = dash.getItemMeta();

                        dashMeta.setDisplayName(ChatColor.YELLOW + "Рывок" + ChatColor.GRAY + " (10)");

                        ArrayList<String> dashLore = new ArrayList<>();

                        dashLore.add(ChatColor.WHITE + "При нажатии пробела трижды");
                        dashLore.add(ChatColor.WHITE + "вы устремляетесь вперёд");
                        dashLore.add("");
                        dashLore.add(ChatColor.GREEN + "Перезарядка: 7 секунд");
                        dashMeta.setLore(dashLore);
                        dashMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        dash.setItemMeta(dashMeta);

                        ItemStack shieldBreak = new ItemStack(Material.SHIELD);
                        ItemMeta shieldBreakMeta = shieldBreak.getItemMeta();

                        shieldBreakMeta.setDisplayName(ChatColor.DARK_GREEN + "Пробитие блока" + ChatColor.GRAY + " (20)");

                        ArrayList<String> shieldBreakLore = new ArrayList<>();

                        shieldBreakLore.add(ChatColor.ITALIC + "Пассивно:");
                        shieldBreakLore.add(ChatColor.WHITE + "Ваша следующая атака");
                        shieldBreakLore.add(ChatColor.WHITE + "уберёт щит противника");
                        shieldBreakLore.add(ChatColor.WHITE + "из левой руки на 1.5 секунды");
                        shieldBreakLore.add("");
                        shieldBreakLore.add(ChatColor.GREEN + "Перезарядка: 7 секунд");
                        shieldBreakMeta.setLore(shieldBreakLore);
                        shieldBreakMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        shieldBreak.setItemMeta(shieldBreakMeta);

                        ItemStack powerUp = new ItemStack(Material.STONE_SWORD);
                        ItemMeta powerUpMeta = powerUp.getItemMeta();

                        powerUpMeta.setDisplayName(ChatColor.DARK_AQUA + "Усиление" + ChatColor.GRAY + " (30)");

                        ArrayList<String> powerUpLore = new ArrayList<>();

                        powerUpLore.add(ChatColor.WHITE + "ПКМ любым мечом на блок - ");
                        powerUpLore.add(ChatColor.WHITE + "вы получаете эффекты");
                        powerUpLore.add("");
                        powerUpLore.add(ChatColor.BLUE + "Сила (00:10)");
                        powerUpLore.add(ChatColor.BLUE + "Регенерация (00:10)");
                        powerUpLore.add(ChatColor.GREEN + "Перезарядка: 15 секунд");
                        powerUpMeta.setLore(powerUpLore);
                        powerUpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        powerUp.setItemMeta(powerUpMeta);

                        ItemStack immunity = new ItemStack(Material.GLOWSTONE_DUST);
                        ItemMeta immunityMeta = immunity.getItemMeta();

                        immunityMeta.setDisplayName(ChatColor.AQUA + "Иммунитет" + ChatColor.GRAY + " (40)");

                        ArrayList<String> immunityLore = new ArrayList<>();

                        immunityLore.add(ChatColor.WHITE + "В течение 10 секунд");
                        immunityLore.add(ChatColor.WHITE + "все наложенные на вас дебаффы");
                        immunityLore.add(ChatColor.WHITE + "будут мгновенно сняты");
                        immunityLore.add("");
                        immunityLore.add(ChatColor.RED + "Свечение (00:10)");
                        immunityLore.add(ChatColor.GREEN + "Перезарядка: 60 секунд");
                        immunityMeta.setLore(immunityLore);
                        immunityMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        immunity.setItemMeta(immunityMeta);

                        ItemStack berserkMode = new ItemStack(Material.GOLDEN_AXE);
                        ItemMeta berserkModeMeta = berserkMode.getItemMeta();

                        berserkModeMeta.setDisplayName(ChatColor.GOLD + "Режим берсерка" + ChatColor.GRAY + " (50)");

                        ArrayList<String> berserkModeLore = new ArrayList<>();

                        berserkModeLore.add(ChatColor.ITALIC + "Пассивно:");
                        berserkModeLore.add(ChatColor.WHITE + "Если после получения урона");
                        berserkModeLore.add(ChatColor.WHITE + "ваше здоровье стало меньше 30%");
                        berserkModeLore.add(ChatColor.WHITE + "вы получаете эффекты");
                        berserkModeLore.add("");
                        berserkModeLore.add(ChatColor.BLUE + "Регенерация (00:10)");
                        berserkModeLore.add(ChatColor.BLUE + "Сила II (00:10)");
                        berserkModeLore.add(ChatColor.BLUE + "Поглощение II (00:10)");
                        berserkModeLore.add(ChatColor.GREEN + "Перезарядка: 300 секунд");
                        berserkModeMeta.setLore(berserkModeLore);
                        berserkModeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        berserkMode.setItemMeta(berserkModeMeta);

                        menuItems = new ItemStack[]{naturalStrength, dash, shieldBreak, powerUp, immunity, berserkMode};

                        break;

                    case "user":
                        player.sendMessage(ChatColor.RED + "У вас нет класса! Используйте /с choose <class>");
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "Класса " + selectedClass + " не существует.");
                        break;
                }

                gui.setContents(menuItems);
                player.openInventory(gui);
                break;
            }

            case "choose": {
                String secondSelectedClass;
                if (!(sender instanceof Player)) break;
                if (args.length >= 2) {
                    secondSelectedClass = args[1];
                    if (!(secondSelectedClass.equalsIgnoreCase("healer")
                            || secondSelectedClass.equalsIgnoreCase("tank")
                            || secondSelectedClass.equalsIgnoreCase("warrior")
                            || secondSelectedClass.equalsIgnoreCase("archer")
                            || secondSelectedClass.equalsIgnoreCase("assassin"))) {
                        sender.sendMessage(ChatColor.RED + "Ты не можешь выбрать класс " + secondSelectedClass + ": его не существует");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Вы не выбрали класс! Используйте /c choose <class>");
                    return true;
                }

                UUID id = ((Player) sender).getUniqueId();
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(sender.getName());
                String selectedClass = playerInfo.getSelectedClass();
                String nickname = sender.getName();
                int lvl = playerInfo.getLvl();
                int exp = playerInfo.getExp();

                if (playerInfo.getSelectedClass().equalsIgnoreCase("none")) {
                    Player player = ((Player) sender).getPlayer();
                    C.removeEffects(player);
                    PotionListener.calculateHP(player);
                    switch (secondSelectedClass) {
                        case "healer":
                            LuckPermsUtils.addPermission(id, "essentials.kits.healer");
                            break;

                        case "tank":
                            LuckPermsUtils.addPermission(id, "essentials.kits.tank");
                            player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(5.0);
                            player.addPotionEffect(PotionEffectType.SLOW.createEffect(6000000, 0));
                            if (lvl < 20) break;
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
                            break;

                        case "warrior":
                            LuckPermsUtils.addPermission(id, "essentials.kits.warrior");
                            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0);
                            break;

                        case "archer":
                            LuckPermsUtils.addPermission(id, "essentials.kits.archer");
                            if (lvl < 30) break;
                            player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                            break;

                        case "assassin":
                            LuckPermsUtils.addPermission(id, "essentials.kits.assassin");
                            player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                            if (lvl < 40) break;
                            player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
                            break;
                    }

                    JsonUtils.createPlayerInfo(nickname, secondSelectedClass, lvl, exp);
                    sender.sendMessage(ChatColor.GREEN + "Теперь ты - " + secondSelectedClass);
                    break;
                }

                sender.sendMessage(ChatColor.RED + "Ты не можешь сменить класс, так как ты уже выбрал " + selectedClass);
                break;
            }

            case "reset": {
                if (sender instanceof Player && args.length == 1) {
                    Player player = (Player) sender;
                    String name = player.getName();
                    JsonUtils.createPlayerInfo(name, "none", 0, 0);
                    player.sendMessage(ChatColor.YELLOW + "Теперь у вас 0 уровень и вы можете выбрать новый класс.");
                    C.removeEffects(player);
                    break;
                }

                if (args.length != 2) break;
                String name = args[1];
                PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(name);
                if (!sender.hasPermission("cwc.reset")) {
                    sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
                    return true;
                }

                if (playerInfo == null) {
                    sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                    return true;
                }

                JsonUtils.createPlayerInfo(name, "none", 0, 0);
                Player player = Bukkit.getPlayer(name);
                player.sendMessage(ChatColor.YELLOW + "Теперь у вас 0 уровень и вы можете выбрать новый класс.");
                sender.sendMessage(ChatColor.YELLOW + "Вы сбросили игроку " + name + " класс и уровень.");
                C.removeEffects(player);

                break;
            }

            case "remove": {
                if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (!sender.hasPermission("cwc.remove")) {
                        sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
                        return true;
                    }

                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                        return true;
                    }

                    String nickname = player.getName();
                    String selectedClass = JsonUtils.getPlayerInfoName(player.getName()).getSelectedClass();
                    int lvl = JsonUtils.getPlayerInfoName(player.getName()).getLvl();
                    int exp = JsonUtils.getPlayerInfoName(player.getName()).getExp();
                    C.removeEffects(player);
                    JsonUtils.createPlayerInfo(nickname, "none", lvl, exp);
                    sender.sendMessage(ChatColor.YELLOW + "Вы убрали класс " + ChatColor.GREEN + selectedClass + ChatColor.YELLOW + " у игрока " + ChatColor.GREEN + nickname);
                    player.sendMessage(ChatColor.YELLOW + "У вас убрали класс, и теперь вы можете выбрать новый!");
                    break;
                }

                sender.sendMessage(ChatColor.RED + "Неправильно написана команда! Используйте /c remove <player>");
                break;
            }

            case "givexp": {
                if (args.length == 3) {
                    int exp;
                    Player player = Bukkit.getPlayer(args[1]);
                    if (!sender.hasPermission("cwc.givexp")) {
                        sender.sendMessage(ChatColor.RED + "У вас недостаточно прав.");
                        return true;
                    }

                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                        return true;
                    }

                    int amount = Integer.parseInt(args[2]);
                    PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
                    String nickname = player.getName();
                    String selectedClass = playerInfo.getSelectedClass();
                    int lvl = playerInfo.getLvl();
                    int newLvlExp = ExpGainingListener.calcNewLvl(lvl);
                    player.sendMessage(ChatColor.YELLOW + "Игрок " + sender.getName() + " выдал вам " + amount + " опыта.");
                    for (exp = playerInfo.getExp() + amount; exp >= newLvlExp; exp -= newLvlExp) {
                        newLvlExp = ExpGainingListener.calcNewLvl(++lvl);
                        player.sendMessage(ChatColor.YELLOW + "Ваш уровень повышен до " + lvl + '!');
                    }

                    JsonUtils.createPlayerInfo(nickname, selectedClass, lvl, exp);
                    PotionListener.calculateHP(player);
                    break;
                }

                sender.sendMessage(ChatColor.RED + "Неправильно написана команда! Используйте /c givexp <player> <xp>");
                break;
            }

            case "setlvl": {
                if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (!sender.hasPermission("cwc.setlvl")) {
                        sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
                        return true;
                    }
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                        return true;
                    }
                    int amount = Integer.parseInt(args[2]);
                    UUID id = player.getUniqueId();
                    PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
                    String nickname = player.getName();
                    String selectedClass = playerInfo.getSelectedClass();
                    int lvl = playerInfo.getLvl();
                    if (amount > lvl) {
                        player.sendMessage(ChatColor.YELLOW + sender.getName() + " повысил ваш уровень до " + amount + '!');
                    } else if (amount < lvl) {
                        player.sendMessage(ChatColor.YELLOW + sender.getName() + " понизил ваш уровень до " + amount + '!');
                    }
                    JsonUtils.createPlayerInfo(nickname, selectedClass, amount, 0);
                    PotionListener.calculateHP(player);
                    break;
                }

                sender.sendMessage(ChatColor.RED + "Неправильно написана команда! Используйте /c setlvl <player> <lvl>");
                break;
            }

            case "buster": {
                if (args.length != 2) break;
                if (!sender.hasPermission("cwc.buster")) {
                    sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
                    return true;
                }
                double amount = Double.parseDouble(args[1]);
                amount = Math.max(amount, 500.0);
                ExpGainingListener.globalBuster = amount /= 100.0;
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Установленное значение получаемого опыта - " + ChatColor.GREEN + amount * 100.0 + "%");
                break;
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

