package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class PlayerPlaceholderRequest
{
    // {player:[PlayerName]:[Contents]}
    public static void playerPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 3);
        if (content.length < 3) return;
        Player player = Bukkit.getPlayer(content[1]);
        if (player == null) return;
        try {
            String result = noParameterRequest(player, content[2]);
            if (result == null) {
                result = parameterRequest(player, content[2]);
            }
            if (result != null) {
                placeholders.put(placeholder, result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // %lce_player:[PlayerName]:[Contents]%
    public static String playerPlaceholderAPIRequest(String placeholder) {
        String[] content = placeholder.split(":", 3);
        if (content.length < 3) return null;
        Player player = Bukkit.getPlayer(content[1]);
        if (player == null) return null;
        try {
            String result = noParameterRequest(player, content[2]);
            if (result == null) {
                result = parameterRequest(player, content[2]);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    // %lce_me:[Contents]%
    public static String playerPlaceholderAPIRequest(Player player, String placeholder) {
        String[] content = placeholder.split(":", 2);
        if (content.length < 2) return null;
        if (player == null) return null;
        try {
            String result = noParameterRequest(player, content[1]);
            if (result == null) {
                result = parameterRequest(player, content[1]);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static String noParameterRequest(Player player, String content) {
        String result = null;
        switch (content.toLowerCase()) {
            case "name": {
                result = player.getName();
                break;
            }
            case "uuid": {
                result = player.getUniqueId().toString();
                break;
            }
            case "custom_name": {
                result = player.getCustomName();
                break;
            }
            case "display_name": {
                result = player.getDisplayName();
                break;
            }
            case "locale": {
                result = player.getLocale();
                break;
            }
            case "player_list_footer": {
                result = player.getPlayerListFooter();
                break;
            }
            case "player_list_header": {
                result = player.getPlayerListHeader();
                break;
            }
            case "player_list_name": {
                result = player.getPlayerListName();
                break;
            }
            case "absorption_amount": {
                result = String.valueOf(player.getAbsorptionAmount());
                break;
            }
            case "address": {
                result = player.getAddress().toString();
                break;
            }
            case "allow_flight": {
                result = String.valueOf(player.getAllowFlight());
                break;
            }
            case "arrow_cooldown": {
                result = String.valueOf(player.getArrowCooldown());
                break;
            }
            case "arrows_in_body": {
                result = String.valueOf(player.getArrowsInBody());
                break;
            }
            case "attack_cooldown": {
                result = String.valueOf(player.getAttackCooldown());
                break;
            }
            case "can_pickup_items": {
                result = String.valueOf(player.getCanPickupItems());
                break;
            }
            case "client_view_distance": {
                result = String.valueOf(player.getClientViewDistance());
                break;
            }
            case "exhaustion": {
                result = String.valueOf(player.getExhaustion());
                break;
            }
            case "exp": {
                result = String.valueOf(player.getExp());
                break;
            }
            case "exp_to_level": {
                result = String.valueOf(player.getExpToLevel());
                break;
            }
            case "fall_distance": {
                result = String.valueOf(player.getFallDistance());
                break;
            }
            case "fire_ticks": {
                result = String.valueOf(player.getFireTicks());
                break;
            }
            case "first_played": {
                result = String.valueOf(player.getFirstPlayed());
                break;
            }
            case "fly_speed": {
                result = String.valueOf(player.getFlySpeed());
                break;
            }
            case "food_level": {
                result = String.valueOf(player.getFoodLevel());
                break;
            }
            case "gamemode": {
                result = player.getGameMode().name();
                break;
            }
            case "health": {
                result = String.valueOf(player.getHealth());
                break;
            }
            case "health_scale": {
                result = String.valueOf(player.getHealthScale());
                break;
            }
            case "last_damage": {
                result = String.valueOf(player.getLastDamage());
                break;
            }
            case "last_played": {
                result = String.valueOf(player.getLastPlayed());
                break;
            }
            case "level": {
                result = String.valueOf(player.getLevel());
                break;
            }
            case "main_hand": {
                result = player.getMainHand().name();
                break;
            }
            case "max_fire_ticks": {
                result = String.valueOf(player.getMaxFireTicks());
                break;
            }
            case "max_health": {
                result = String.valueOf(player.getMaxHealth());
                break;
            }
            case "maximum_air": {
                result = String.valueOf(player.getMaximumAir());
                break;
            }
            case "maximum_no_damage_ticks": {
                result = String.valueOf(player.getMaximumNoDamageTicks());
                break;
            }
            case "no_damage_ticks": {
                result = String.valueOf(player.getNoDamageTicks());
                break;
            }
            case "ping": {
                result = String.valueOf(player.getPing());
                break;
            }
            case "player_time": {
                result = String.valueOf(player.getPlayerTime());
                break;
            }
            case "player_time_offset": {
                result = String.valueOf(player.getPlayerTimeOffset());
                break;
            }
            case "player_weather": {
                result = player.getPlayerWeather().name();
                break;
            }
            case "portal_cooldown": {
                result = String.valueOf(player.getPortalCooldown());
                break;
            }
            case "remaining_air": {
                result = String.valueOf(player.getRemainingAir());
                break;
            }
            case "saturated_regen_rate": {
                result = String.valueOf(player.getSaturatedRegenRate());
                break;
            }
            case "saturation": {
                result = String.valueOf(player.getSaturation());
                break;
            }
            case "sleep_ticks": {
                result = String.valueOf(player.getSleepTicks());
                break;
            }
            case "starvation_rate": {
                result = String.valueOf(player.getStarvationRate());
                break;
            }
            case "ticks_lived": {
                result = String.valueOf(player.getTicksLived());
                break;
            }
            case "total_experience": {
                result = String.valueOf(player.getTotalExperience());
                break;
            }
            case "unsaturated_regen_rate": {
                result = String.valueOf(player.getUnsaturatedRegenRate());
                break;
            }
            case "walk_speed": {
                result = String.valueOf(player.getWalkSpeed());
                break;
            }
            case "has_gravity": {
                result = String.valueOf(player.hasGravity());
                break;
            }
            case "has_played_before": {
                result = String.valueOf(player.hasPlayedBefore());
                break;
            }
            case "blocking": {
                result = String.valueOf(player.isBlocking());
                break;
            }
            case "collidable": {
                result = String.valueOf(player.isCollidable());
                break;
            }
            case "conversing": {
                result = String.valueOf(player.isConversing());
                break;
            }
            case "custom_name_visible": {
                result = String.valueOf(player.isCustomNameVisible());
                break;
            }
            case "dead": {
                result = String.valueOf(player.isDead());
                break;
            }
            case "flying": {
                result = String.valueOf(player.isFlying());
                break;
            }
            case "gilding": {
                result = String.valueOf(player.isGliding());
                break;
            }
            case "glowing": {
                result = String.valueOf(player.isGlowing());
                break;
            }
            case "hand_raised": {
                result = String.valueOf(player.isHandRaised());
                break;
            }
            case "health_scaled": {
                result = String.valueOf(player.isHealthScaled());
                break;
            }
            case "in_water": {
                result = String.valueOf(player.isInWater());
                break;
            }
            case "inside_vehicle": {
                result = String.valueOf(player.isInsideVehicle());
                break;
            }
            case "invisible": {
                result = String.valueOf(player.isInvisible());
                break;
            }
            case "invulnerable": {
                result = String.valueOf(player.isInvulnerable());
                break;
            }
            case "is_op": {
                result = String.valueOf(player.isOp());
                break;
            }
            case "player_time_relative": {
                result = String.valueOf(player.isPlayerTimeRelative());
                break;
            }
            case "riptiding": {
                result = String.valueOf(player.isRiptiding());
                break;
            }
            case "sleeping": {
                result = String.valueOf(player.isSleeping());
                break;
            }
            case "silent": {
                result = String.valueOf(player.isSilent());
                break;
            }
            case "sleeping_ignored": {
                result = String.valueOf(player.isSleepingIgnored());
                break;
            }
            case "sneaking": {
                result = String.valueOf(player.isSneaking());
                break;
            }
            case "sprinting": {
                result = String.valueOf(player.isSprinting());
                break;
            }
            case "swimming": {
                result = String.valueOf(player.isSwimming());
                break;
            }
            case "whitelisted": {
                result = String.valueOf(player.isWhitelisted());
                break;
            }
        }
        return result;
    }
    
    private static String parameterRequest(Player player, String content) {
        if (content.toLowerCase().startsWith("can_see_")) {
            String[] details = content.split("_", 3);
            if (details.length == 3) {
                Player target = Bukkit.getPlayer(details[2]);
                if (target != null) {
                    return String.valueOf(player.canSee(target));
                }
            }
        }
        // {player:[Name]:active_potion_effects_[Number]_[PotionInfo]}
        if (content.toLowerCase().startsWith("active_potion_effects_")) {
            String[] details = content.split("_", 5);
            if (details.length == 5 && LiteCommandEditorUtils.isInteger(details[3])) {
                PotionEffect[] effects = player.getActivePotionEffects().toArray(new PotionEffect[0]);
                int number = Integer.valueOf(details[3]) - 1;
                if (effects.length > number) {
                    return getPotionEffectDetail(effects[number], details[4]);
                }
            }
        }
        // {player:[Name]:potion_effect_[PotionID]_[PotionInfo]}
        if (content.toLowerCase().startsWith("potion_effect_")) {
            String[] details = content.split("_", 4);
            if (details.length == 4) {
                PotionEffect effect;
                if (LiteCommandEditorUtils.isInteger(details[2])) {
                    effect = player.getPotionEffect(PotionEffectType.getById(Integer.valueOf(details[2])));
                } else {
                    effect = player.getPotionEffect(PotionEffectType.getByName(details[2]));
                }
                if (effect != null) {
                    return getPotionEffectDetail(effect, details[3]);
                }
            }
        }
        // {player:[Name]:bed_location_[Detail]}
        if (content.toLowerCase().startsWith("bed_location_")) {
            String[] details = content.split("_", 3);
            if (details.length == 3) {
                return getLocationDetail(player.getBedLocation(), details[2]);
            }
        }
        // {player:[Name]:bed_spawn_location_[Detail]}
        if (content.toLowerCase().startsWith("bed_spawn_location_")) {
            String[] details = content.split("_", 4);
            if (details.length == 4) {
                return getLocationDetail(player.getBedSpawnLocation(), details[3]);
            }
        }
        // {player:[Name]:compass_target_[Detail]}
        if (content.toLowerCase().startsWith("compass_target_")) {
            String[] details = content.split("_", 3);
            if (details.length == 3) {
                return getLocationDetail(player.getCompassTarget(), details[2]);
            }
        }
        // {player:[Name]:location_[Detail]}
        if (content.toLowerCase().startsWith("location_")) {
            String[] details = content.split("_", 2);
            if (details.length == 2) {
                return getLocationDetail(player.getLocation(), details[1]);
            }
        }
        // {player:[Name]:inventory_[Details]}
        if (content.toLowerCase().startsWith("inventory_")) {
            String[] details = content.split("_", 3);
            if (details.length == 3 && LiteCommandEditorUtils.isInteger(details[1])) {
                ItemStack[] items = player.getInventory().getContents();
                int number = Integer.valueOf(details[1]) - 1;
                if (items.length > number && items[number] != null) {
                    return getItemStackDetail(items[number], details[2]);
                }
            }
        }
        // {player:[Name]:ender_chest_[Details]}
        if (content.toLowerCase().startsWith("ender_chest_")) {
            String[] details = content.split("_", 4);
            if (details.length == 4 && LiteCommandEditorUtils.isInteger(details[2])) {
                ItemStack[] items = player.getEnderChest().getContents();
                int number = Integer.valueOf(details[2]) - 1;
                if (items.length > number && items[number] != null) {
                    return getItemStackDetail(items[number], details[3]);
                }
            }
        }
        return null;
    }
    
    private static String getLocationDetail(Location location, String detail) {
        switch (detail.toLowerCase()) {
            case "x": {
                return String.valueOf(location.getX());
            }
            case "y": {
                return String.valueOf(location.getY());
            }
            case "z": {
                return String.valueOf(location.getZ());
            }
            case "block_x": {
                return String.valueOf(location.getBlockX());
            }
            case "block_y": {
                return String.valueOf(location.getBlockY());
            }
            case "block_z": {
                return String.valueOf(location.getBlockZ());
            }
            case "pitch": {
                return String.valueOf(location.getPitch());
            }
            case "yaw": {
                return String.valueOf(location.getYaw());
            }
            case "world": {
                return location.getWorld().getName();
            }
        }
        return null;
    }
    
    private static String getItemStackDetail(ItemStack item, String detail) {
        switch (detail.toLowerCase()) {
            case "amount": {
                return String.valueOf(item.getAmount());
            }
            case "durability": {
                return String.valueOf(item.getDurability());
            }
            case "max_stack_size": {
                return String.valueOf(item.getMaxStackSize());
            }
            case "type": {
                return item.getType().name();
            }
            case "data": {
                return String.valueOf(item.getData().getData());
            }
            case "custom_model_data": {
                return String.valueOf(item.getItemMeta().getCustomModelData());
            }
            case "display_name": {
                return item.getItemMeta().getDisplayName();
            }
            case "name": {
                return item.getItemMeta().getLocalizedName();
            }
            case "has_item_meta": {
                return String.valueOf(item.hasItemMeta());
            }
            case "has_custom_model_data": {
                return String.valueOf(item.getItemMeta().hasCustomModelData());
            }
            case "has_display_name": {
                return String.valueOf(item.getItemMeta().hasDisplayName());
            }
            case "has_enchants": {
                return String.valueOf(item.getItemMeta().hasEnchants());
            }
            case "has_name": {
                return String.valueOf(item.getItemMeta().hasLocalizedName());
            }
            case "has_lore": {
                return String.valueOf(item.getItemMeta().hasLore());
            }
        }
        if (detail.toLowerCase().startsWith("enchantment_level_")) {
            String[] detailsInDetail = detail.split("_", 3);
            if (detailsInDetail.length == 3) {
                String[] itemID = detailsInDetail[2].split(":");
                if (itemID.length == 2) {
                    return String.valueOf(item.getItemMeta().getEnchantLevel(Enchantment.getByKey(NamespacedKey.fromString(detailsInDetail[2]))));
                } else {
                    return String.valueOf(item.getItemMeta().getEnchantLevel(Enchantment.getByName(detailsInDetail[2])));
                }
            }
        }
        if (detail.toLowerCase().startsWith("lore_")) {
            String[] detailsInDetail = detail.split("_", 2);
            if (detailsInDetail.length == 2 && LiteCommandEditorUtils.isInteger(detailsInDetail[1])) {
                return item.getItemMeta().getLore().get(Integer.valueOf(detailsInDetail[1]) - 1);
            }
        }
        if (detail.toLowerCase().startsWith("has_enchant_")) {
            String[] detailsInDetail = detail.split("_", 3);
            if (detailsInDetail.length == 3) {
                String[] itemID = detailsInDetail[2].split(":");
                if (itemID.length == 2) {
                    return String.valueOf(item.getItemMeta().hasEnchant(Enchantment.getByKey(NamespacedKey.fromString(detailsInDetail[2]))));
                } else {
                    return String.valueOf(item.getItemMeta().hasEnchant(Enchantment.getByName(detailsInDetail[2])));
                }
            }
        }
        return null;
    }
    
    private static String getPotionEffectDetail(PotionEffect effect, String detail) {
        switch (detail.toLowerCase()) {
            case "amplifier": {
                return String.valueOf(effect.getAmplifier());
            }
            case "duration": {
                return String.valueOf(effect.getDuration());
            }
            case "name": {
                return effect.getType().getName();
            }
        }
        return null;
    }
}
