package studio.trc.bukkit.litecommandeditor.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import net.kyori.adventure.text.event.HoverEventSource;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Item;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;

public class NMSUtils
{
    private static boolean nmsLoaded = false;
    
    private static Class<?> craftPlayer;
    public static Method sendPacket = null;
    
    public static String getPackagePath() {
        try {
            return "." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
        } catch (ArrayIndexOutOfBoundsException ex) {
            return ".";
        }
    }
    
    public static void initialize() {
        nmsLoaded = true;
        TitleUtil.initialize();
        ActionBarUtil.initialize();
        JSONItem.initialize();
        ParticleUtil.initialize();
        try {
            craftPlayer = Class.forName("org.bukkit.craftbukkit" + getPackagePath() + "entity.CraftPlayer");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (nmsLoaded) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{nms}", Bukkit.getBukkitVersion());
            LiteCommandEditorProperties.sendOperationMessage("NMSSuccessfullyInitialized", placeholders);
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object entityPlayer = craftPlayer.getMethod("getHandle").invoke(craftPlayer.cast(player));
            Object connection;
            Field playerConnection = Arrays.stream(entityPlayer.getClass().getFields())
                    .filter(field -> 
                            field.getType().getSimpleName().equals("PlayerConnection"))
                    .findFirst().orElse(null);
            if (playerConnection != null) {
                connection = playerConnection.get(entityPlayer);
            } else {
                return;
            }
            if (sendPacket == null) {
                sendPacket = Arrays.stream(connection.getClass().getMethods())
                        .filter(method -> 
                                method.getParameterTypes().length == 1 && method.getParameterTypes()[0].getSimpleName().equals("Packet") && method.getReturnType().getSimpleName().equals("void"))
                        .findFirst().orElse(null);
            }
            if (sendPacket != null) {
                sendPacket.invoke(connection, packet);
            }
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    
    public static class ParticleUtil {
        private static Class<?> packetPlayOutWorldParticles;
        private static Class<?> enumParticle;
        public static Object particleClass;
        
        public static void initialize() {
            try {
                if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8")) {
                    packetPlayOutWorldParticles = Class.forName("net.minecraft.server" + getPackagePath() + "PacketPlayOutWorldParticles");
                    if (Bukkit.getBukkitVersion().startsWith("1.7")) {
                        enumParticle = packetPlayOutWorldParticles.getDeclaredClasses()[0];
                    } else {
                        enumParticle = Class.forName("net.minecraft.server" + getPackagePath() + "EnumParticle");
                    }
                }
            } catch (Exception exception) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{type}", "ParticleUtil");
                placeholders.put("{error}", exception.getLocalizedMessage() != null ? exception.getLocalizedMessage() : "null");
                LiteCommandEditorProperties.sendOperationMessage("NMSInitializeFailed", placeholders);
                nmsLoaded = false;
            }
        }
        
        public static Stream<String> values() {
            try {
                return Arrays.stream(Particle.values()).map(particle -> particle.name());
            } catch (NoClassDefFoundError ex) {
                return nmsValues();
            }
        }
        
        public static Stream<String> nmsValues() {
            try {
                Object[] particles;
                if (Bukkit.getBukkitVersion().startsWith("1.8")) {
                    particles = (Object[]) enumParticle.getMethod("values").invoke(null);
                } else {
                    Method method = enumParticle.getDeclaredMethod("values");
                    method.setAccessible(true);
                    particles = (Object[]) method.invoke(null);
                    method.setAccessible(false);
                }
                return Arrays.stream(particles).map(particle -> {
                    try {
                        return enumParticle.getMethod("name").invoke(particle).toString();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        public static void spawnParticle(World world, String particle, double x, double y, double z, int count) {
            try {
                world.spawnParticle(Particle.valueOf(particle), x, y, z, count);
            } catch (NoClassDefFoundError ex) {
                spawnNMSParticle(world, particle, x, y, z, count, 0.0, 0.0, 0.0, 1.0);
            }
        }
        
        public static void spawnParticle(World world, String particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
            try {
                world.spawnParticle(Particle.valueOf(particle), x, y, z, count, offsetX, offsetY, offsetZ);
            } catch (NoClassDefFoundError ex) {
                spawnNMSParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, 1.0);
            }
        }
        
        public static void spawnParticle(World world, String particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
            try {
                world.spawnParticle(Particle.valueOf(particle), x, y, z, count, offsetX, offsetY, offsetZ, extra);
            } catch (NoClassDefFoundError ex) {
                spawnNMSParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
            }
        }
        
        public static void spawnNMSParticle(World world, String particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
            try {
                Object packet;
                if (Bukkit.getBukkitVersion().startsWith("1.8")) {
                    packet = packetPlayOutWorldParticles.getConstructor(enumParticle, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class)
                                   .newInstance(enumParticle.getMethod("valueOf", String.class).invoke(null, particle), true, (float) x, (float) y, (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, new int[0]);
                } else {
                    packet = packetPlayOutWorldParticles.getConstructor(String.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class)
                                   .newInstance(particle, (float) x, (float) y, (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);
                }
                world.getPlayers().forEach(player -> sendPacket(player, packet));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static class TitleUtil {
        private static Class<?> enumTitleAction;
        private static Class<?> craftChatMessage;
        private static Class<?> interfaceChatBaseComponent;
        private static Class<?> packetPlayOutTitle;

        public static void initialize() {
            try {
                Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            } catch (Exception ex) {
                try {
                    if (Bukkit.getBukkitVersion().startsWith("1.7")) return;
                    if (Bukkit.getBukkitVersion().startsWith("1.8-R0.1")) {
                        enumTitleAction = Class.forName("net.minecraft.server" + getPackagePath() + "EnumTitleAction");
                    } else if (Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10") || Bukkit.getBukkitVersion().startsWith("1.11") || Bukkit.getBukkitVersion().startsWith("1.12") || Bukkit.getBukkitVersion().startsWith("1.13") || Bukkit.getBukkitVersion().startsWith("1.14") || Bukkit.getBukkitVersion().startsWith("1.15") || Bukkit.getBukkitVersion().startsWith("1.16")) {
                        enumTitleAction = Class.forName("net.minecraft.server" + getPackagePath() + "PacketPlayOutTitle$EnumTitleAction");
                    }
                    if (Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10")
                     || Bukkit.getBukkitVersion().startsWith("1.11") || Bukkit.getBukkitVersion().startsWith("1.12") || Bukkit.getBukkitVersion().startsWith("1.13") || Bukkit.getBukkitVersion().startsWith("1.14")
                     || Bukkit.getBukkitVersion().startsWith("1.15") || Bukkit.getBukkitVersion().startsWith("1.16")) {
                        interfaceChatBaseComponent = Class.forName("net.minecraft.server" + getPackagePath() + "IChatBaseComponent");
                        packetPlayOutTitle = Class.forName("net.minecraft.server" + getPackagePath() + "PacketPlayOutTitle");
                        craftChatMessage = Class.forName("org.bukkit.craftbukkit" + getPackagePath() + "util.CraftChatMessage");
                    } else {
                        interfaceChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
                        craftChatMessage = Class.forName("org.bukkit.craftbukkit" + getPackagePath() + "util.CraftChatMessage");
                    }
                } catch (Exception exception) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{type}", "TitleUtil");
                    placeholders.put("{error}", exception.getLocalizedMessage() != null ? exception.getLocalizedMessage() : "null");
                    LiteCommandEditorProperties.sendOperationMessage("NMSInitializeFailed", placeholders);
                    nmsLoaded = false;
                }
            }
        }
        
        public static void sendTitle(Player player, String title, String subTitle, double fadein, double stay, double fadeout) {
            sendTitle(player, title, subTitle, fadein, stay, fadeout, MessageUtil.getDefaultPlaceholders());
        }

        public static void sendTitle(Player player, String title, String subTitle, double fadein, double stay, double fadeout, Map<String, String> placeholders) {
            if (Bukkit.getBukkitVersion().startsWith("1.7")) return;
            title = MessageUtil.replacePlaceholders(player, title, placeholders);
            subTitle = MessageUtil.replacePlaceholders(player, subTitle, placeholders);
            try {
                player.sendTitle(title, subTitle, (int) (fadein * 20), (int) (stay * 20), (int) (fadeout * 20));
            } catch (NoSuchMethodError ex) {
                try {
                    Object titleEnumPacket = enumTitleAction.getMethod("valueOf", String.class).invoke(enumTitleAction, "TITLE");
                    Object subTitleEnumPacket = enumTitleAction.getMethod("valueOf", String.class).invoke(enumTitleAction, "SUBTITLE");
                    Object animationPacket = packetPlayOutTitle.getConstructor(int.class, int.class, int.class).newInstance((int) (fadein * 20), (int) (stay * 20), (int) (fadeout * 20));
                    sendPacket(player, animationPacket);
                    if (title != null) {
                        Object titleMessagePacket = packetPlayOutTitle.getConstructor(enumTitleAction, interfaceChatBaseComponent).newInstance(titleEnumPacket, Array.get(craftChatMessage.getMethod("fromString", String.class).invoke(null, title), 0));
                        sendPacket(player, titleMessagePacket);
                    }
                    if (subTitle != null) {
                        Object subTitleMessagePacket = packetPlayOutTitle.getConstructor(enumTitleAction, interfaceChatBaseComponent).newInstance(subTitleEnumPacket, Array.get(craftChatMessage.getMethod("fromString", String.class).invoke(null, subTitle), 0));
                        sendPacket(player, subTitleMessagePacket);
                    }
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }
            }
        }
    }
    
    public static class ActionBarUtil {
        private static Class<?> chatComponentText;
        private static Class<?> packetPlayOutChat;
        private static Class<?> interfaceChatBaseComponent = null;
        private static Class<?> component; // 1.21.9+ added
        private static Class<?> chatMessageType;
        private static Class<?> clientboundSetActionBarTextPacket;
        private static Class<?> craftChatMessage;

        public static void initialize() {
            try {
                Player.class.getMethod("sendActionBar", String.class);
            } catch (Exception ex) {
                try {
                    if (Bukkit.getBukkitVersion().startsWith("1.7")) return;
                    if (Bukkit.getBukkitVersion().startsWith("1.17") || Bukkit.getBukkitVersion().startsWith("1.18") || Bukkit.getBukkitVersion().startsWith("1.19") || Bukkit.getBukkitVersion().startsWith("1.20") || Bukkit.getBukkitVersion().startsWith("1.21")) {
                        chatMessageType = Class.forName("net.minecraft.network.chat.ChatMessageType");
                        clientboundSetActionBarTextPacket = Class.forName("net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket");
                        craftChatMessage = Class.forName("org.bukkit.craftbukkit" + getPackagePath() + "util.CraftChatMessage");
                        try {
                            interfaceChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
                        } catch (Exception ex1) {
                            component = Class.forName("net.minecraft.network.chat.Component");
                        }
                    } else {
                        interfaceChatBaseComponent = Class.forName("net.minecraft.server" + getPackagePath() + "IChatBaseComponent");
                        packetPlayOutChat = Class.forName("net.minecraft.server" + getPackagePath() + "PacketPlayOutChat");
                        chatComponentText = Class.forName("net.minecraft.server" + getPackagePath() + "ChatComponentText"); 
                    }
                    if (Bukkit.getBukkitVersion().startsWith("1.12") || Bukkit.getBukkitVersion().startsWith("1.13") || Bukkit.getBukkitVersion().startsWith("1.14") || Bukkit.getBukkitVersion().startsWith("1.15") || Bukkit.getBukkitVersion().startsWith("1.16")) {
                        chatMessageType = Class.forName("net.minecraft.server" + getPackagePath() + "ChatMessageType");
                    }
                    craftPlayer = Class.forName("org.bukkit.craftbukkit" + getPackagePath() + "entity.CraftPlayer");
                } catch (Exception exception) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{type}", "ActionBarUtil");
                    placeholders.put("{error}", exception.getLocalizedMessage() != null ? exception.getLocalizedMessage() : "null");
                    LiteCommandEditorProperties.sendOperationMessage("NMSInitializeFailed", placeholders);
                    nmsLoaded = false;
                }
            }
        }
        
        public static void sendActionBar(Player player, String text) {
            sendActionBar(player, text, MessageUtil.getDefaultPlaceholders());
        }

        public static void sendActionBar(Player player, String text, Map<String, String> placeholders) {
            if (Bukkit.getBukkitVersion().startsWith("1.7")) return;
            if (text == null) return;
            text = MessageUtil.replacePlaceholders(player, text, placeholders);
            try {
                player.getClass().getMethod("sendActionBar", String.class).invoke(player, text);
            } catch (NoSuchMethodError ex) {
                try {
                    Object actionbar;
                    // 1.8 - 1.11.2
                    if (Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10") || Bukkit.getBukkitVersion().startsWith("1.11")) {
                        actionbar = packetPlayOutChat.getConstructor(interfaceChatBaseComponent, byte.class).newInstance(
                                chatComponentText.getConstructor(String.class).newInstance(text),
                                (byte) 2);
                    // 1.12 - 1.15.2
                    } else if (Bukkit.getBukkitVersion().startsWith("1.12") || Bukkit.getBukkitVersion().startsWith("1.13") || Bukkit.getBukkitVersion().startsWith("1.14") || Bukkit.getBukkitVersion().startsWith("1.15")) {
                        actionbar = packetPlayOutChat.getConstructor(interfaceChatBaseComponent, chatMessageType).newInstance(
                                chatComponentText.getConstructor(String.class).newInstance(text),
                                chatMessageType.getMethod("a", byte.class).invoke(chatMessageType, (byte) 2));
                    // 1.16 - 1.16.5
                    } else if (Bukkit.getBukkitVersion().startsWith("1.16")) {
                        actionbar = packetPlayOutChat.getConstructor(interfaceChatBaseComponent, chatMessageType, UUID.class).newInstance(
                                chatComponentText.getConstructor(String.class).newInstance(text),
                                chatMessageType.getMethod("a", byte.class).invoke(chatMessageType, (byte) 2),
                                UUID.randomUUID());
                    // 1.17 - 1.18
                    } else if (Bukkit.getBukkitVersion().startsWith("1.17") || Bukkit.getBukkitVersion().startsWith("1.18")) {
                        actionbar = clientboundSetActionBarTextPacket.getConstructor(interfaceChatBaseComponent).newInstance(craftChatMessage.getMethod("fromStringOrNull", String.class).invoke(null, text));
                    // 1.19 +
                    } else {
                        // 1.19 - 1.21.8
                        if (interfaceChatBaseComponent != null) {
                            actionbar = clientboundSetActionBarTextPacket.getConstructor(interfaceChatBaseComponent).newInstance(Array.get(craftChatMessage.getMethod("fromString", String.class).invoke(null, text), 0));
                        } else { // 1.21.9 +
                            actionbar = clientboundSetActionBarTextPacket.getConstructor(component).newInstance(Array.get(craftChatMessage.getMethod("fromString", String.class).invoke(null, text), 0));
                        }
                    }
                    sendPacket(player, actionbar);
                } catch (Exception ex1) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static class JSONItem {
        private static Class<?> craftItemStack;
        private static Class<?> nbtTagCompound = null;
        private static Class<?> itemStack = null;

        public static void initialize() {
            try {
                craftItemStack = Class.forName("org.bukkit.craftbukkit" + getPackagePath() + "inventory.CraftItemStack");
                if (!Bukkit.getBukkitVersion().startsWith("1.7") && !Bukkit.getBukkitVersion().startsWith("1.8") && !Bukkit.getBukkitVersion().startsWith("1.9") && !Bukkit.getBukkitVersion().startsWith("1.10")
                    && !Bukkit.getBukkitVersion().startsWith("1.11") && !Bukkit.getBukkitVersion().startsWith("1.12") && !Bukkit.getBukkitVersion().startsWith("1.13") && !Bukkit.getBukkitVersion().startsWith("1.14")
                    && !Bukkit.getBukkitVersion().startsWith("1.15") && !Bukkit.getBukkitVersion().startsWith("1.16")) {
                    try {
                        nbtTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
                        itemStack = Class.forName("net.minecraft.world.item.ItemStack");
                    } catch (ClassNotFoundException ex) {
                        // 1.21.9+ No longer needed.
                    }
                } else {
                    nbtTagCompound = Class.forName("net.minecraft.server" + getPackagePath() + "NBTTagCompound"); 
                    itemStack = Class.forName("net.minecraft.server" + getPackagePath() + "ItemStack");
                }
            } catch (ClassNotFoundException exception) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{type}", "JSONItem");
                placeholders.put("{error}", exception.getLocalizedMessage() != null ? exception.getLocalizedMessage() : "null");
                LiteCommandEditorProperties.sendOperationMessage("NMSInitializeFailed", placeholders);
            }
        }

        public static void setItemHover(ItemStack item, BaseComponent component) {
            try {
                Item hoverItem = new Item(
                    item.getType().getKey().toString(),
                    item.getAmount(),
                    ItemTag.ofNbt(item.getItemMeta() != null ? (String) ItemMeta.class.getMethod("getAsString").invoke(item.getItemMeta()) : "")
                );
                component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM, hoverItem));
            } catch (Throwable t) {
                try {
                    if (nbtTagCompound == null) return;
                    Object mcStack = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, item);
                    Object NBTTagCompound = nbtTagCompound.newInstance();
                    Method saveMethod = Arrays.stream(itemStack.getDeclaredMethods()).filter(method -> method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(nbtTagCompound) && method.getReturnType().equals(nbtTagCompound)).findFirst().orElse(null);
                    if (saveMethod != null) {
                        if (saveMethod.isAccessible()) {
                            saveMethod.invoke(mcStack, NBTTagCompound);
                        } else {
                            saveMethod.setAccessible(true);
                            saveMethod.invoke(mcStack, NBTTagCompound);
                            saveMethod.setAccessible(false);
                        }
                    } else {
                        nbtTagCompound.getMethod("putString", String.class, String.class).invoke(NBTTagCompound, "id", item.getType().getKey().toString());
                        nbtTagCompound.getMethod("putByte", String.class, byte.class).invoke(NBTTagCompound, "Count", (byte) item.getAmount());
                    }
                    component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(NBTTagCompound.toString()).create()));
                } catch (Throwable t1) {
                    t1.printStackTrace();
                }
            }
        }
        
        public static Object setItemHover(ItemStack item, Object component) {
            return AdventureUtils.toComponent(component).hoverEvent(HoverEventSource.class.cast(item));
        }
    }
}