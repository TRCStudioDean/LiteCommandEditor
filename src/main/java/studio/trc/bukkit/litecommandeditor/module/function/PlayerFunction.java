package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemInfo;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.NMSUtils;

public class PlayerFunction
    implements CommandFunctionTask
{
    @Getter
    private final String expression;
    @Getter
    private final String configPath;
    @Getter
    private final CommandFunction function;
    @Getter
    private final String identifier = "PlayerFunction";
    
    public PlayerFunction(CommandFunction function, String expression, String configPath) {
        this.expression = expression;
        this.function = function;
        this.configPath = configPath;
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, expression, placeholders), ':');
        try {
            if (parameters.length > 1) {
                Player player = Bukkit.getPlayer(parameters[0]);
                if (player != null) {
                    boolean incorrect = true;
                    switch (parameters[1].toLowerCase()) {
                        case "attack": {
                            if (parameters.length > 2) {
                                if (Bukkit.getPlayer(parameters[2]) != null) {
                                    player.attack(Bukkit.getPlayer(parameters[2]));
                                    incorrect = false;
                                } else if (LiteCommandEditorUtils.isUUID(parameters[2])) {
                                    player.attack(Bukkit.getEntity(UUID.fromString(parameters[2])));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "addscoreboardtag": {
                            if (parameters.length > 2) {
                                player.addScoreboardTag(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "chat": {
                            if (parameters.length > 2) {
                                player.chat(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "damage": {
                            if (parameters.length == 3) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    player.damage(Double.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            } else if (parameters.length > 3) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    if (Bukkit.getPlayer(parameters[3]) != null) {
                                        player.damage(Double.valueOf(parameters[2]), Bukkit.getPlayer(parameters[3]));
                                        incorrect = false;
                                    } else if (LiteCommandEditorUtils.isUUID(parameters[3])) {
                                        player.damage(Double.valueOf(parameters[2]), Bukkit.getEntity(UUID.fromString(parameters[3])));
                                        incorrect = false;
                                    }
                                }
                            }
                            break;
                        }
                        case "giveexp": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.giveExp(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "giveexplevels": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.giveExpLevels(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "hideplayer": {
                            if (parameters.length > 2) {
                                if (Bukkit.getPlayer(parameters[2]) != null) {
                                    try {
                                        player.hidePlayer(Main.getInstance(), Bukkit.getPlayer(parameters[2]));
                                        incorrect = false;
                                    } catch (Exception ex) {
                                        player.hidePlayer(Bukkit.getPlayer(parameters[2]));
                                        incorrect = false;
                                    }
                                }
                            }
                            break;
                        }
                        case "kickplayer": {
                            if (parameters.length > 2) {
                                player.kickPlayer(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "loaddata": {
                            player.loadData();
                            incorrect = false;
                            break;
                        }
                        case "performcommand": {
                            if (parameters.length > 2) {
                                player.performCommand(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "playsound": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                float volume = Float.valueOf(parameters[3]);
                                float pitch = Float.valueOf(parameters[4]);
                                player.playSound(player.getLocation(), parameters[2], volume, pitch);
                                incorrect = false;
                            }
                            break;
                        }
                        case "recalculatepermissions": {
                            player.recalculatePermissions();
                            incorrect = false;
                            break;
                        }
                        case "remove": {
                            player.remove();
                            incorrect = false;
                            break;
                        }
                        case "removescoreboardtag": {
                            if (parameters.length > 2) {
                                player.removeScoreboardTag(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "resetmaxhealth": {
                            player.resetMaxHealth();
                            incorrect = false;
                            break;
                        }
                        case "resetplayertime": {
                            player.resetPlayerTime();
                            incorrect = false;
                            break;
                        }
                        case "resetplayerwether": {
                            player.resetPlayerWeather();
                            incorrect = false;
                            break;
                        }
                        case "savedata": {
                            player.saveData();
                            incorrect = false;
                            break;
                        }
                        case "sendmessage": {
                            if (parameters.length > 2) {
                                player.sendMessage(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "sendrawmessage": {
                            if (parameters.length > 2) {
                                player.sendRawMessage(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "sendsignchange": {
                            if (parameters.length > 6) {
                                World world = Bukkit.getWorld(parameters[2]);
                                if (world != null && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5])) {
                                    List<String> lines = new ArrayList<>();
                                    for (int i = 6;i < parameters.length;i++) {
                                        lines.add(parameters[i]);
                                    }
                                    player.sendSignChange(new Location(world, Double.valueOf(parameters[3]), Double.valueOf(parameters[4]), Double.valueOf(parameters[5])), lines.toArray(new String[0]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setabsorptionamount": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    player.setAbsorptionAmount(Double.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setallowflight": {
                            if (parameters.length > 2) {
                                player.setAllowFlight(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setarrowsinbody": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setArrowsInBody(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setbedspawnlocation": {
                            if (parameters.length > 5) {
                                World world = Bukkit.getWorld(parameters[2]);
                                if (world != null && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5])) {
                                    player.setBedSpawnLocation(new Location(world, Double.valueOf(parameters[3]), Double.valueOf(parameters[4]), Double.valueOf(parameters[5])));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setcanpickupitems": {
                            if (parameters.length > 2) {
                                player.setCanPickupItems(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setcollidable": {
                            if (parameters.length > 2) {
                                player.setCollidable(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setcompasstarget": {
                            if (parameters.length > 5) {
                                World world = Bukkit.getWorld(parameters[2]);
                                if (world != null && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5])) {
                                    player.setCompassTarget(new Location(world, Double.valueOf(parameters[3]), Double.valueOf(parameters[4]), Double.valueOf(parameters[5])));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setcooldown": {
                            if (parameters.length > 3) {
                                ItemInfo item = function.getCommandConfig().getItemCollection().getItem(parameters[2]);
                                if (item != null && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                    player.setCooldown(item.getItem(sender, placeholders).getType(), Integer.valueOf(parameters[3]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setcustomnamevisible": {
                            if (parameters.length > 2) {
                                player.setCustomNameVisible(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setdisplayname": {
                            if (parameters.length > 2) {
                                player.setDisplayName(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setexhaustion": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isFloat(parameters[2])) {
                                    player.setExhaustion(Float.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setexp": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setExp(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setfalldistance": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isFloat(parameters[2])) {
                                    player.setFallDistance(Float.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setfireticks": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setFireTicks(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setflyspeed": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isFloat(parameters[2])) {
                                    player.setFlySpeed(Float.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setflying": {
                            if (parameters.length > 2) {
                                player.setFlying(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setfoodlevel": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setFoodLevel(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setgamemode": {
                            if (parameters.length > 2) {
                                try {
                                    player.setGameMode(GameMode.valueOf(parameters[2].toUpperCase()));
                                    incorrect = false;
                                } catch (Exception ex) {}
                            }
                            break;
                        }
                        case "setgliding": {
                            if (parameters.length > 2) {
                                player.setGliding(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setglowing": {
                            if (parameters.length > 2) {
                                player.setGlowing(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setgravity": {
                            if (parameters.length > 2) {
                                player.setGravity(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "sethealth": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    player.setHealth(Double.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "sethealthscale": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    player.setHealthScale(Double.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "sethealthscaled": {
                            if (parameters.length > 2) {
                                player.setHealthScaled(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setinvisible": {
                            if (parameters.length > 2) {
                                player.setInvisible(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setinvulnerable": {
                            if (parameters.length > 2) {
                                player.setInvulnerable(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setiteminhand": {
                            if (parameters.length > 2) {
                                ItemInfo item = function.getCommandConfig().getItemCollection().getItem(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                if (item != null) {
                                    player.setItemInHand(item.getItem(sender, placeholders));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setitemoncursor": {
                            if (parameters.length > 2) {
                                ItemInfo item = function.getCommandConfig().getItemCollection().getItem(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                if (item != null) {
                                    player.setItemOnCursor(item.getItem(sender, placeholders));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setlastdamage": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    player.setLastDamage(Double.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setlevel": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setLevel(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setmaxhealth": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2])) {
                                    player.setMaxHealth(Double.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setmaximumair": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setMaximumAir(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setmaximumnodamageticks": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setMaximumNoDamageTicks(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setnodamageticks": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setNoDamageTicks(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setop": {
                            if (parameters.length > 2) {
                                player.setOp(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setplayerlistfooter": {
                            if (parameters.length > 2) {
                                player.setPlayerListFooter(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setplayerlistheader": {
                            if (parameters.length > 2) {
                                player.setPlayerListHeader(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setplayerlistfooterheader": {
                            if (parameters.length > 3) {
                                player.setPlayerListHeaderFooter(parameters[2], parameters[3]);
                                incorrect = false;
                            }
                            break;
                        }
                        case "setplayerlistname": {
                            if (parameters.length > 2) {
                                player.setPlayerListName(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setplayertime": {
                            if (parameters.length > 3 && LiteCommandEditorUtils.isLong(parameters[2])) {
                                player.setPlayerTime(Long.valueOf(parameters[2]), Boolean.valueOf(parameters[3]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setplayerweather": {
                            if (parameters.length > 2) {
                                try {
                                    player.setPlayerWeather(WeatherType.valueOf(parameters[2].toUpperCase()));
                                    incorrect = false;
                                } catch (Exception ex) {}
                            }
                            break;
                        }
                        case "setportalcooldown": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setPortalCooldown(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setremainingair": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setRemainingAir(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setremovewhenfaraway": {
                            if (parameters.length > 2) {
                                player.setRemoveWhenFarAway(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setresourcepack": {
                            if (parameters.length > 2) {
                                player.setResourcePack(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setrotation": {
                            if (parameters.length > 3) {
                                if (LiteCommandEditorUtils.isFloat(parameters[2]) && LiteCommandEditorUtils.isFloat(parameters[3])) {
                                    player.setRotation(Float.valueOf(parameters[2]), Float.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setsaturatedregenrate": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setSaturatedRegenRate(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setsaturation": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isFloat(parameters[2])) {
                                    player.setSaturation(Float.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setsilent": {
                            if (parameters.length > 2) {
                                player.setSilent(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setsleepingignored": {
                            if (parameters.length > 2) {
                                player.setSleepingIgnored(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setsneaking": {
                            if (parameters.length > 2) {
                                player.setSneaking(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setsprinting": {
                            if (parameters.length > 2) {
                                player.setSprinting(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setstarvationrate": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setStarvationRate(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setswimming": {
                            if (parameters.length > 2) {
                                player.setSwimming(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settexturepack": {
                            if (parameters.length > 2) {
                                player.setTexturePack(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settickslived": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setTicksLived(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "settotalexperience": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setTotalExperience(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setunsaturatedregenrate": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isInteger(parameters[2])) {
                                    player.setUnsaturatedRegenRate(Integer.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setvelocity": {
                            if (parameters.length > 4) {
                                if (LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                    player.setVelocity(new Vector(Double.valueOf(parameters[2]), Double.valueOf(parameters[3]), Double.valueOf(parameters[4])));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setwalkspeed": {
                            if (parameters.length > 2) {
                                if (LiteCommandEditorUtils.isFloat(parameters[2])) {
                                    player.setWalkSpeed(Float.valueOf(parameters[2]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setwhitelisted": {
                            if (parameters.length > 2) {
                                player.setWhitelisted(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "sleep": {
                            if (parameters.length > 6) {
                                World world = Bukkit.getWorld(parameters[2]);
                                if (world != null && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5])) {
                                    player.sleep(new Location(world, Double.valueOf(parameters[3]), Double.valueOf(parameters[4]), Double.valueOf(parameters[5])), Boolean.valueOf(parameters[6]));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "spawnparticle": {
                            if (parameters.length > 7 && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5]) && LiteCommandEditorUtils.isDouble(parameters[6]) && LiteCommandEditorUtils.isInteger(parameters[7])) {
                                World world = Bukkit.getWorld(parameters[3]);
                                double x = Double.valueOf(parameters[4]);
                                double y = Double.valueOf(parameters[5]);
                                double z = Double.valueOf(parameters[6]);
                                int count = Integer.valueOf(parameters[7]);
                                if (parameters.length == 8) {
                                    NMSUtils.ParticleUtil.spawnParticle(world, parameters[2], x, y, z, count);
                                    incorrect = false;
                                } else if (parameters.length > 10 && LiteCommandEditorUtils.isDouble(parameters[8]) && LiteCommandEditorUtils.isDouble(parameters[9]) && LiteCommandEditorUtils.isDouble(parameters[10])) {
                                    double offsetX = Double.valueOf(parameters[8]);
                                    double offsetY = Double.valueOf(parameters[9]);
                                    double offsetZ = Double.valueOf(parameters[10]);
                                    if (parameters.length == 11) {
                                        NMSUtils.ParticleUtil.spawnParticle(world, parameters[2], x, y, z, count, offsetX, offsetY, offsetZ);
                                        incorrect = false;
                                    } else if (LiteCommandEditorUtils.isDouble(parameters[11])) {
                                        NMSUtils.ParticleUtil.spawnParticle(world, parameters[2], x, y, z, count, offsetX, offsetY, offsetZ, Double.valueOf(parameters[11]));
                                        incorrect = false;
                                    }
                                }
                            }
                            break;
                        }
                        case "stopsound": {
                            if (parameters.length > 2) {
                                try {
                                    player.stopSound(Sound.valueOf(parameters[2].toUpperCase()));
                                    incorrect = false;
                                } catch (Exception ex) {}
                            }
                            break;
                        }
                        case "swingmainhand": {
                            player.swingMainHand();
                            incorrect = false;
                            break;
                        }
                        case "swingoffhand": {
                            player.swingOffHand();
                            incorrect = false;
                            break;
                        }
                        case "teleport": {
                            if (parameters.length >= 6 && parameters.length <= 7) {
                                World world = Bukkit.getWorld(parameters[2]);
                                if (world != null && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5])) {
                                    player.teleport(new Location(world, Double.valueOf(parameters[3]), Double.valueOf(parameters[4]), Double.valueOf(parameters[5])));
                                    incorrect = false;
                                }
                            } else if (parameters.length > 7) {
                                World world = Bukkit.getWorld(parameters[2]);
                                if (world != null && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5]) && LiteCommandEditorUtils.isFloat(parameters[6]) && LiteCommandEditorUtils.isFloat(parameters[7])) {
                                    player.teleport(new Location(world, Double.valueOf(parameters[3]), Double.valueOf(parameters[4]), Double.valueOf(parameters[5]), Float.valueOf(parameters[6]), Float.valueOf(parameters[7])));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "updatecommands": {
                            player.updateCommands();
                            incorrect = false;
                            break;
                        }
                        default: {
                            if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                                unknownFunction(parameters[1]);
                            }
                            incorrect = false;
                            break;
                        }
                    }
                    if (incorrect) {
                        if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                            incorrectParameters(parameters[1], String.join(":", Arrays.asList(parameters).stream().skip(2).toArray(String[]::new)));
                        }
                    }
                } else {
                    if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                        unknownPlayer(parameters[0]);
                    }
                }
            } else {
                if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                    unknownFunction(expression);
                }
            }
        } catch (NoSuchMethodError t) {}
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Expression=" + expression;
    }
    
    private void incorrectParameters(String functionName, String parameters) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{parameters}", parameters);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.Player"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Incorrect-Parameters", placeholders);
    }
    
    private void unknownPlayer(String playerName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{playerName}", playerName);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-Player-Name", placeholders);
    }
    
    private void unknownFunction(String functionName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.Player"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-Function", placeholders);
    }
    
    public static PlayerFunction build(CommandFunction function, Map map, String configPath) {
        if (map.get("Player-Function") != null) {
            return new PlayerFunction(function, map.get("Player-Function").toString(), configPath);
        }
        return null;
    }
    
    public static List<PlayerFunction> build(CommandFunction function, List<String> functions, String configPath) {
        return functions.stream().map(syntax -> new PlayerFunction(function, syntax, configPath)).collect(Collectors.toList());
    }
}
