package studio.trc.bukkit.litecommandeditor.itemmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.Material;

import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

/**
 * Used for managing materials in versions 1.7.10 - 1.12.2
 * @author Mercy
 */
public class LegacyItemUtil 
{
    static Map<String, String> updateLegacyItemNames() {
        Map<String, String> materials = new HashMap<>();
        Properties languageProperties = ItemUtil.getLanguageProperties();
        for (Material material : Material.values()) {
            try {
                String itemID = material.name();
                //Multiples
                if (itemID.equals("STONE")) putStones(materials);
                else if (itemID.equals("DIRT")) putDirts(materials);
                else if (itemID.equals("SAND")) putSands(materials);
                else if (itemID.equals("STEP")) putSlabs(materials);
                else if (itemID.equals("QUARTZ_BLOCK")) putQuartzBlocks(materials);
                else if (itemID.equals("COBBLE_WALL")) putCobbleWalls(materials);
                else if (itemID.equals("SPONGE")) putSponge(materials);
                else if (itemID.equals("SAPLING")) putSaplings(materials);
                else if (itemID.equals("WOOD")) putWoods(materials);
                else if (itemID.equals("RED_ROSE")) putFlower2(materials);
                else if (itemID.equals("DOUBLE_PLANT")) putDoublePlant(materials);
                else if (itemID.equals("LONG_GRASS")) putTallGrass(materials);
                else if (itemID.equals("LOG")) putLog(materials);
                else if (itemID.equals("LOG_2")) putLog2(materials);
                else if (itemID.equals("LEAVES")) putLeaves(materials);
                else if (itemID.equals("LEAVES_2")) putLeaves2(materials);
                else if (itemID.equals("PRISMARINE")) putPrismarines(materials);
                else if (itemID.equals("SANDSTONE")) putSandStones(materials);
                else if (itemID.equals("RED_SANDSTONE")) putRedSandStones(materials);
                else if (itemID.equals("SKULL_ITEM") || itemID.equals("SKULL")) putSkulls(materials);
                else if (itemID.startsWith("RECORD_") || itemID.endsWith("_RECORD")) putRecord(itemID, materials);
                else if (itemID.endsWith("_GLAZED_TERRACOTTA") || itemID.equals("STAINED_CLAY")) putTerracotta(itemID, materials);
                else if (itemID.endsWith("_SHULKER_BOX")) putShulkerBox(itemID, materials);
                else if (itemID.equals("BOAT") || itemID.startsWith("BOAT_")) putBoat(itemID, materials);
                else if (itemID.endsWith("_MINECART")) putMinecart(itemID, materials);
                else if (itemID.endsWith("_DOOR_ITEM")) putDoor(itemID, materials);
                else if (itemID.endsWith("_BARDING")) putHorseArmor(itemID, materials);

                //Special named and multiple items
                else if (itemID.endsWith("_AXE") && languageProperties.containsKey("item." + toInvertedCamelCase(itemID.replace("_AXE", "_PICKAXE")) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toInvertedCamelCase(itemID.replace("AXE", "PICKAXE")) + ".name"));
                else if (itemID.endsWith("_SPADE") && languageProperties.containsKey("item." + toInvertedCamelCase(itemID.replace("_SPADE", "_SHOVEL")) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toInvertedCamelCase(itemID.replace("_SPADE", "_SHOVEL")) + ".name"));
                else if (itemID.startsWith("LEATHER_") && languageProperties.containsKey("item." + toInvertedCamelCase(itemID.replace("LEATHER_", "CLOTH_")) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toInvertedCamelCase(itemID.replace("LEATHER_", "CLOTH_")) + ".name"));
                else if (itemID.startsWith("CHAINMAIL_") && languageProperties.containsKey("item." + toInvertedCamelCase(itemID.replace("CHAINMAIL_", "CHAIN_")) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toInvertedCamelCase(itemID.replace("CHAINMAIL_", "CHAIN_")) + ".name"));

                //Colorables
                else if (itemID.equals("WOOL")) putItemColors(itemID, materials, "tile.cloth");
                else if (itemID.equals("CONCRETE")) putItemColors(itemID, materials, "tile.concrete");
                else if (itemID.equals("CONCRETE_POWDER")) putItemColors(itemID, materials, "tile.concretePowder");
                else if (itemID.equals("INK_SACK")) putItemColors(itemID, materials, "item.dyePowder");
                else if (itemID.equals("BED") && !languageProperties.containsKey("item.bed.name")) putItemColors(itemID, materials, "item.bed");
                else if (itemID.equals("BANNER")) putItemColors(itemID, materials, "item.banner");
                else if (itemID.equals("SHIELD")) putItemColors(itemID, materials, "item.shield");
                else if (itemID.equals("STAINED_GLASS")) putItemColors(itemID, materials, "tile.stainedGlass");
                else if (itemID.equals("THIN_GLASS")) putItemColors(itemID, materials, "tile.thinStainedGlass");
                else if (itemID.equals("CARPET")) putItemColors(itemID, materials, "tile.woolCarpet");

                //Special named
                else if ((itemID.equals("WRITTEN_BOOK") || itemID.equals("BOOK_AND_QUILL")) && languageProperties.containsKey("item.writingBook.name")) materials.put(itemID, languageProperties.getProperty("item.writingBook.name"));
                else if (itemID.equals("COBBLESTONE") && languageProperties.containsKey("tile.stonebrick.name")) materials.put(itemID, languageProperties.getProperty("tile.stonebrick.name"));
                else if (itemID.equals("YELLOW_FLOWER") && languageProperties.containsKey("tile.flower1.dandelion.name")) materials.put(itemID, languageProperties.getProperty("tile.flower1.dandelion.name"));
                else if (itemID.equals("WATCH") && languageProperties.containsKey("item.clock.name")) materials.put(itemID, languageProperties.getProperty("item.clock.name"));
                else if (itemID.equals("ITEM_FRAME") && languageProperties.containsKey("item.frame.name")) materials.put(itemID, languageProperties.getProperty("item.frame.name"));
                else if (itemID.equals("COMMAND") && languageProperties.containsKey("tile.commandBlock.name")) materials.put(itemID, languageProperties.getProperty("tile.commandBlock.name"));
                else if (itemID.equals("COMMAND_REPEATING") && languageProperties.containsKey("tile.repeatingCommandBlock.name")) materials.put(itemID, languageProperties.getProperty("tile.repeatingCommandBlock.name"));
                else if (itemID.equals("COMMAND_CHAIN") && languageProperties.containsKey("tile.chainCommandBlock.name")) materials.put(itemID, languageProperties.getProperty("tile.chainCommandBlock.name"));
                else if ((itemID.equals("RED_MUSHROOM") || itemID.equals("BROWN_MUSHROOM")) && languageProperties.containsKey("tile.mushroom.name")) materials.put(itemID, languageProperties.getProperty("tile.mushroom.name"));
                else if (itemID.equals("GOLDEN_APPLE") && languageProperties.containsKey("item.appleGold.name")) {
                    materials.put(itemID, languageProperties.getProperty("item.appleGold.name"));
                    materials.put(itemID + ":1", languageProperties.getProperty("item.appleGold.name"));
                }
                else if (itemID.equals("WOODEN_DOOR") && languageProperties.containsKey("item.doorOak.name")) materials.put(itemID, languageProperties.getProperty("item.doorOak.name"));
                else if (itemID.equals("ENDER_STONE") && languageProperties.containsKey("tile.whiteStone.name")) materials.put(itemID, languageProperties.getProperty("tile.whiteStone.name"));
                else if (itemID.equals("ENDER_PORTAL") && languageProperties.containsKey("tile.portal.name")) materials.put(itemID, languageProperties.getProperty("tile.portal.name"));
                else if (itemID.equals("BREWING_STAND_ITEM") && languageProperties.containsKey("item.brewingStand.name")) materials.put(itemID, languageProperties.getProperty("item.brewingStand.name"));
                else if (itemID.equals("CAULDRON_ITEM") && languageProperties.containsKey("tile.cauldron.name")) materials.put(itemID, languageProperties.getProperty("tile.cauldron.name"));
                else if (itemID.equals("FLOWER_POT_ITEM") && languageProperties.containsKey("item.flowerPot.name")) materials.put(itemID, languageProperties.getProperty("item.flowerPot.name"));
                else if ((itemID.equals("CARROT") || itemID.equals("CARROT_ITEM")) && languageProperties.containsKey("item.carrots.name")) materials.put(itemID, languageProperties.getProperty("item.carrots.name"));
                else if (itemID.equals("POTATO_ITEM") && languageProperties.containsKey("item.potato.name")) materials.put(itemID, languageProperties.getProperty("item.potato.name"));
                else if (itemID.equals("DRAGONS_BREATH") && languageProperties.containsKey("item.dragon_breath.name")) materials.put(itemID, languageProperties.getProperty("item.dragon_breath.name"));
                else if (itemID.equals("MUTTON") && languageProperties.containsKey("item.muttonRaw.name")) materials.put(itemID, languageProperties.getProperty("item.muttonRaw.name"));
                else if (itemID.equals("COOKED_MUTTON") && languageProperties.containsKey("item.muttonCooked.name")) materials.put(itemID, languageProperties.getProperty("item.muttonCooked.name"));
                else if (itemID.equals("RABBIT") && languageProperties.containsKey("item.rabbitRaw.name")) materials.put(itemID, languageProperties.getProperty("item.rabbitRaw.name"));
                else if (itemID.equals("COOKED_RABBIT") && languageProperties.containsKey("item.rabbitCooked.name")) materials.put(itemID, languageProperties.getProperty("item.rabbitCooked.name"));
                else if (itemID.equals("QUARTZ") && languageProperties.containsKey("item.netherquartz.name")) materials.put(itemID, languageProperties.getProperty("item.netherquartz.name"));
                else if ((itemID.equals("FIREWORK") || itemID.equals("FIREWORK_CHARGE")) && languageProperties.containsKey("item.fireworks.name")) materials.put(itemID, languageProperties.getProperty("item.fireworks.name"));
                else if (itemID.equals("CARROT_STICK") && languageProperties.containsKey("item.carrotOnAStick.name")) materials.put(itemID, languageProperties.getProperty("item.carrotOnAStick.name"));
                else if ((itemID.equals("MONSTER_EGG") || itemID.equals("MONSTER_EGGS")) && languageProperties.containsKey("item.monsterPlacer.name")) materials.put(itemID, languageProperties.getProperty("item.monsterPlacer.name"));
                else if (itemID.equals("MELON_SEEDS") && languageProperties.containsKey("item.seeds_melon.name")) materials.put(itemID, languageProperties.getProperty("item.seeds_melon.name"));
                else if (itemID.equals("PUMPKIN_SEEDS") && languageProperties.containsKey("item.seeds_pumpkin.name")) materials.put(itemID, languageProperties.getProperty("item.seeds_pumpkin.name"));
                else if (itemID.equals("COOKED_FISH") && languageProperties.containsKey("item.fish.cod.cooked.name")) materials.put(itemID, languageProperties.getProperty("item.fish.cod.cooked.name"));
                else if (itemID.equals("RAW_FISH") && languageProperties.containsKey("item.fish.cod.raw.name")) materials.put(itemID, languageProperties.getProperty("item.fish.cod.raw.name"));
                else if (itemID.equals("GLOWSTONE_DUST") && languageProperties.containsKey("item.yellowDust.name")) materials.put(itemID, languageProperties.getProperty("item.yellowDust.name"));
                else if ((itemID.equals("SUGAR_CANE") || itemID.equals("SUGAR_CANE_BLOCK")) && languageProperties.containsKey("tile.reeds.name")) materials.put(itemID, languageProperties.getProperty("tile.reeds.name"));
                else if (itemID.equals("CLAY_BALL") && languageProperties.containsKey("item.clay.name")) materials.put(itemID, languageProperties.getProperty("item.clay.name"));
                else if (itemID.equals("CLAY_BRICK") && languageProperties.containsKey("item.brick.name")) materials.put(itemID, languageProperties.getProperty("item.brick.name"));
                else if (itemID.equals("MILK_BUCKET") && languageProperties.containsKey("item.milk.name")) materials.put(itemID, languageProperties.getProperty("item.milk.name"));
                else if (itemID.equals("GRILLED_PORK") && languageProperties.containsKey("item.porkchopCooked.name")) materials.put(itemID, languageProperties.getProperty("item.porkchopCooked.name"));
                else if (itemID.equals("PORK") && languageProperties.containsKey("item.porkchopRaw.name")) materials.put(itemID, languageProperties.getProperty("item.porkchopRaw.name"));
                else if (itemID.equals("MUSHROOM_SOUP") && languageProperties.containsKey("item.mushroomStew.name")) materials.put(itemID, languageProperties.getProperty("item.mushroomStew.name"));
                else if (itemID.equals("END_GATEWAY") && languageProperties.containsKey("tile.portal.name")) materials.put(itemID, languageProperties.getProperty("tile.portal.name"));
                else if (itemID.equals("BEETROOT_BLOCK") && languageProperties.containsKey("item.beetroot.name")) materials.put(itemID, languageProperties.getProperty("item.beetroot.name"));
                else if (itemID.equals("PURPUR_DOUBLE_SLAB") && languageProperties.containsKey("tile.purpurSlab.name")) materials.put(itemID, languageProperties.getProperty("tile.purpurSlab.name"));
                else if ((itemID.equals("STONE_SLAB2") || itemID.equals("DOUBLE_STONE_SLAB2")) && languageProperties.containsKey("tile.stoneSlab2.red_sandstone.name")) materials.put(itemID, languageProperties.getProperty("tile.stoneSlab2.red_sandstone.name"));
                else if (itemID.equals("DAYLIGHT_DETECTOR_INVERTED") && languageProperties.containsKey("tile.daylightDetector.name")) materials.put(itemID, languageProperties.getProperty("tile.daylightDetector.name"));
                else if ((itemID.equals("WALL_BANNER") || itemID.equals("STANDING_BANNER")) && languageProperties.containsKey("item.banner.white.name")) materials.put(itemID, languageProperties.getProperty("item.banner.white.name"));
                else if (itemID.equals("HARD_CLAY") && languageProperties.containsKey("tile.clay.name")) materials.put(itemID, languageProperties.getProperty("tile.clay.name"));
                else if (itemID.equals("SLIME_BLOCK") && languageProperties.containsKey("tile.slime.name")) materials.put(itemID, languageProperties.getProperty("tile.slime.name"));
                else if (itemID.equals("STATIONARY_WATER") && languageProperties.containsKey("tile.water.name")) materials.put(itemID, languageProperties.getProperty("tile.water.name"));
                else if (itemID.equals("STATIONARY_LAVA") && languageProperties.containsKey("tile.lava.name")) materials.put(itemID, languageProperties.getProperty("tile.lava.name"));
                else if (itemID.equals("BED_BLOCK") && languageProperties.containsKey("item.bed.name")) materials.put(itemID, languageProperties.getProperty("item.bed.name"));
                else if (itemID.equals("BED_BLOCK") && languageProperties.containsKey("item.bed.white.name")) materials.put(itemID, languageProperties.getProperty("item.bed.white.name"));
                else if (itemID.equals("SANDSTONE_STAIRS") && languageProperties.containsKey("tile.stairsSandStone.name")) materials.put(itemID, languageProperties.getProperty("tile.stairsSandStone.name"));
                else if (itemID.equals("GLOWSTONE") && languageProperties.containsKey("tile.lightgem.name")) materials.put(itemID, languageProperties.getProperty("tile.lightgem.name"));
                else if (itemID.equals("RED_SANDSTONE_STAIRS") && languageProperties.containsKey("tile.stairsRedSandStone.name")) materials.put(itemID, languageProperties.getProperty("tile.stairsRedSandStone.name"));
                else if (itemID.equals("DARK_OAK_STAIRS") && languageProperties.containsKey("tile.stairsWoodDarkOak.name")) materials.put(itemID, languageProperties.getProperty("tile.stairsWoodDarkOak.name"));
                else if (itemID.equals("ACACIA_STAIRS") && languageProperties.containsKey("tile.stairsWoodAcacia.name")) materials.put(itemID, languageProperties.getProperty("tile.stairsWoodAcacia.name"));
                else if (itemID.equals("STAINED_GLASS_PANE") && languageProperties.containsKey("tile.thinStainedGlass.name")) materials.put(itemID, languageProperties.getProperty("tile.thinStainedGlass.name"));
                else if (itemID.equals("QUARTZ_ORE") && languageProperties.containsKey("tile.netherquartz.name")) materials.put(itemID, languageProperties.getProperty("tile.netherquartz.name"));
                else if (itemID.equals("IRON_PLATE") && languageProperties.containsKey("tile.weightedPlate_light.name")) materials.put(itemID, languageProperties.getProperty("tile.weightedPlate_light.name"));
                else if (itemID.equals("GOLD_PLATE") && languageProperties.containsKey("tile.weightedPlate_heavy.name")) materials.put(itemID, languageProperties.getProperty("tile.weightedPlate_heavy.name"));
                else if (itemID.equals("TRAPPED_CHEST") && languageProperties.containsKey("tile.chestTrap.name")) materials.put(itemID, languageProperties.getProperty("tile.chestTrap.name"));
                else if ((itemID.equals("WOOD_BUTTON") || itemID.equals("STONE_BUTTON")) && languageProperties.containsKey("tile.button.name")) materials.put(itemID, languageProperties.getProperty("tile.button.name"));
                else if (itemID.equals("TRIPWIRE") && languageProperties.containsKey("tile.tripWire.name")) materials.put(itemID, languageProperties.getProperty("tile.tripWire.name"));
                else if (itemID.equals("TRIPWIRE_HOOK") && languageProperties.containsKey("tile.tripWireSource.name")) materials.put(itemID, languageProperties.getProperty("tile.tripWireSource.name"));
                else if ((itemID.equals("WOOD_STEP") || itemID.equals("WOOD_DOUBLE_STEP")) && languageProperties.containsKey("tile.stoneSlab.wood.name")) materials.put(itemID, languageProperties.getProperty("tile.stoneSlab.wood.name"));
                else if (itemID.equals("NETHER_WARTS") && languageProperties.containsKey("item.netherStalkSeeds.name")) materials.put(itemID, languageProperties.getProperty("item.netherStalkSeeds.name"));
                else if (itemID.equals("SMOOTH_STAIRS") && languageProperties.containsKey("tile.stairsStoneBrickSmooth.name")) materials.put(itemID, languageProperties.getProperty("tile.stairsStoneBrickSmooth.name"));
                else if ((itemID.equals("MELON_STEM") || itemID.equals("MELON_BLOCK")) && languageProperties.containsKey("tile.melon.name")) materials.put(itemID, languageProperties.getProperty("tile.melon.name"));
                else if ((itemID.equals("PUMPKIN_STEM")) && languageProperties.containsKey("tile.pumpkin.name")) materials.put(itemID, languageProperties.getProperty("tile.pumpkin.name"));
                else if ((itemID.equals("HUGE_MUSHROOM_1") || itemID.equals("HUGE_MUSHROOM_2")) && languageProperties.containsKey("tile.mushroom.name")) materials.put(itemID, languageProperties.getProperty("tile.mushroom.name"));
                else if (itemID.equals("SMOOTH_BRICK") && languageProperties.containsKey("tile.stonebricksmooth.name")) materials.put(itemID, languageProperties.getProperty("tile.stonebricksmooth.name"));
                else if (itemID.equals("CAKE_BLOCK") && languageProperties.containsKey("item.cake.name")) materials.put(itemID, languageProperties.getProperty("item.cake.name"));
                else if (itemID.equals("JACK_O_LANTERN") && languageProperties.containsKey("tile.litpumpkin.name")) materials.put(itemID, languageProperties.getProperty("tile.litpumpkin.name"));
                else if (itemID.equals("SOUL_SAND") && languageProperties.containsKey("tile.hellsand.name")) materials.put(itemID, languageProperties.getProperty("tile.hellsand.name"));
                else if (itemID.equals("NETHERRACK") && languageProperties.containsKey("tile.hellrock.name")) materials.put(itemID, languageProperties.getProperty("tile.hellrock.name"));
                else if (itemID.equals("SNOW_BLOCK") && languageProperties.containsKey("tile.snow.name")) materials.put(itemID, languageProperties.getProperty("tile.snow.name"));
                else if (itemID.equals("GLOWING_REDSTONE_ORE") && languageProperties.containsKey("tile.oreRedstone.name")) materials.put(itemID, languageProperties.getProperty("tile.oreRedstone.name"));
                else if (itemID.equals("WOOD_PLATE") && languageProperties.containsKey("tile.pressurePlateWood.name")) materials.put(itemID, languageProperties.getProperty("tile.pressurePlateWood.name")); //1.12.2
                else if (itemID.equals("STONE_PLATE") && languageProperties.containsKey("tile.pressurePlateStone.name")) materials.put(itemID, languageProperties.getProperty("tile.pressurePlateStone.name")); //1.12.2
                else if (itemID.equals("WOOD_PLATE") && languageProperties.containsKey("tile.pressurePlate.name")) materials.put(itemID, languageProperties.getProperty("tile.pressurePlate.name")); //1.7.10
                else if (itemID.equals("STONE_PLATE") && languageProperties.containsKey("tile.pressurePlate.name")) materials.put(itemID, languageProperties.getProperty("tile.pressurePlate.name")); //1.7.10
                else if (itemID.equals("WOODEN_DOOR") && languageProperties.containsKey("item.doorWood.name")) materials.put(itemID, languageProperties.getProperty("item.doorWood.name")); //1.7.10
                else if (itemID.equals("IRON_DOOR_BLOCK") && languageProperties.containsKey("tile.doorIron.name")) materials.put(itemID, languageProperties.getProperty("tile.doorIron.name"));
                else if ((itemID.equals("WALL_SIGN") || itemID.equals("SIGN_POST"))&& languageProperties.containsKey("item.sign.name")) materials.put(itemID, languageProperties.getProperty("item.sign.name"));
                else if (itemID.equals("COBBLESTONE_STAIRS") && languageProperties.containsKey("tile.stairsStone.name")) materials.put(itemID, languageProperties.getProperty("tile.stairsStone.name"));
                else if (itemID.equals("RAILS") && languageProperties.containsKey("tile.rail.name")) materials.put(itemID, languageProperties.getProperty("tile.rail.name"));
                else if (itemID.equals("POWERED_RAIL") && languageProperties.containsKey("tile.goldenRail.name")) materials.put(itemID, languageProperties.getProperty("tile.goldenRail.name"));
                else if (itemID.equals("BURNING_FURNACE") && languageProperties.containsKey("tile.furnace.name")) materials.put(itemID, languageProperties.getProperty("tile.furnace.name"));
                else if (itemID.equals("SOIL") && languageProperties.containsKey("tile.farmland.name")) materials.put(itemID, languageProperties.getProperty("tile.farmland.name"));
                else if (itemID.equals("REDSTONE_WIRE") && languageProperties.containsKey("item.redstone.name")) materials.put(itemID, languageProperties.getProperty("item.redstone.name"));
                else if (itemID.equals("MOSSY_COBBLESTONE") && languageProperties.containsKey("tile.stoneMoss.name")) materials.put(itemID, languageProperties.getProperty("tile.stoneMoss.name"));
                else if (itemID.equals("DOUBLE_STEP") && languageProperties.containsKey("tile.stoneSlab.stone.name")) materials.put(itemID, languageProperties.getProperty("tile.stoneSlab.stone.name"));
                else if ((itemID.equals("PISTON_EXTENSION") || itemID.equals("PISTON_MOVING_PIECE")) && languageProperties.containsKey("tile.pistonStickyBase.name")) materials.put(itemID, languageProperties.getProperty("tile.pistonStickyBase.name"));
                else if (itemID.equals("NOTE_BLOCK") && languageProperties.containsKey("tile.musicBlock.name")) materials.put(itemID, languageProperties.getProperty("tile.musicBlock.name"));
                else if (itemID.equals("NETHER_BRICK_ITEM") && languageProperties.containsKey("item.netherbrick.name")) materials.put(itemID, languageProperties.getProperty("item.netherbrick.name"));
                else if (itemID.equals("ENDER_PORTAL_FRAME") && languageProperties.containsKey("tile.endPortalFrame.name")) materials.put(itemID, languageProperties.getProperty("tile.endPortalFrame.name"));
                else if ((itemID.equals("REDSTONE_LAMP_OFF") || itemID.equals("REDSTONE_LAMP_ON")) && languageProperties.containsKey("tile.redstoneLight.name")) materials.put(itemID, languageProperties.getProperty("tile.redstoneLight.name"));
                else if ((itemID.equals("REDSTONE_COMPARATOR") || itemID.equals("REDSTONE_COMPARATOR_ON") || itemID.equals("REDSTONE_COMPARATOR_OFF")) && languageProperties.containsKey("item.comparator.name")) materials.put(itemID, languageProperties.getProperty("item.comparator.name"));
                else if ((itemID.equals("REDSTONE_TORCH_ON") || itemID.equals("REDSTONE_TORCH_OFF")) && languageProperties.containsKey("tile.notGate.name")) materials.put(itemID, languageProperties.getProperty("tile.notGate.name"));
                else if ((itemID.equals("DIODE_BLOCK_ON") || itemID.equals("DIODE_BLOCK_OFF")) && languageProperties.containsKey("item.diode.name")) materials.put(itemID, languageProperties.getProperty("item.diode.name"));
                else if (itemID.equals("SPLASH_POTION") && !languageProperties.containsKey("item.splash_potion.name")) materials.put(itemID, languageProperties.getProperty("item.potion.name"));
                else if (itemID.equals("LINGERING_POTION") && !languageProperties.containsKey("item.lingering_potion.name")) materials.put(itemID, languageProperties.getProperty("item.potion.name"));

                //Generals (Attempting to get key from properties by deforming the naming)
                else if (languageProperties.containsKey("item." + itemID.toLowerCase() + ".name")) materials.put(itemID, languageProperties.getProperty("item." + itemID.toLowerCase() + ".name"));
                else if (languageProperties.containsKey("tile." + itemID.toLowerCase() + ".name")) materials.put(itemID, languageProperties.getProperty("tile." + itemID.toLowerCase() + ".name"));
                else if (languageProperties.containsKey("item." + itemID.toLowerCase().replace("_", "") + ".name")) materials.put(itemID, languageProperties.getProperty("item." + itemID.toLowerCase().replace("_", "") + ".name"));
                else if (languageProperties.containsKey("tile." + itemID.toLowerCase().replace("_", "") + ".name")) materials.put(itemID, languageProperties.getProperty("tile." + itemID.toLowerCase().replace("_", "") + ".name"));
                else if (languageProperties.containsKey("item." + toCamelCase(itemID) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toCamelCase(itemID) + ".name"));
                else if (languageProperties.containsKey("tile." + toCamelCase(itemID) + ".name")) materials.put(itemID, languageProperties.getProperty("tile." + toCamelCase(itemID) + ".name"));
                else if (languageProperties.containsKey("item." + toInvertedCamelCase(itemID) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toInvertedCamelCase(itemID) + ".name"));
                else if (languageProperties.containsKey("tile." + toInvertedCamelCase(itemID) + ".name")) materials.put(itemID, languageProperties.getProperty("tile." + toInvertedCamelCase(itemID) + ".name"));
                else if (languageProperties.containsKey("item." + toLastWordReversedCamelCase(itemID) + ".name")) materials.put(itemID, languageProperties.getProperty("item." + toLastWordReversedCamelCase(itemID) + ".name"));
                else if (languageProperties.containsKey("tile." + toLastWordReversedCamelCase(itemID) + ".name")) materials.put(itemID, languageProperties.getProperty("tile." + toLastWordReversedCamelCase(itemID) + ".name"));

                //1.7.10
                else if (itemID.equals("AIR")) materials.put(itemID, "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return materials;
    }
    
    /**
     * Convert to camel naming format
     * @param text input
     * @return 
     */
    private static String toCamelCase(String text) {
        StringBuilder builder = new StringBuilder();
        String[] paragraphs = text.split("_");
        for (int i = 0;i < paragraphs.length;i++) {
            if (i > 0 && !paragraphs[i].isEmpty()) {
                builder.append(paragraphs[i].substring(0, 1).toUpperCase()).append(paragraphs[i].substring(1).toLowerCase());
            } else {
                builder.append(paragraphs[i].toLowerCase());
            }
        }
        return builder.toString();
    }
    
    /**
     * Convert to inverted camel naming format
     * @param text input
     * @return 
     */
    private static String toInvertedCamelCase(String text) {
        StringBuilder builder = new StringBuilder();
        String[] paragraphs = text.split("_");
        for (int i = paragraphs.length - 1;i > -1;i--) {
            if (i < paragraphs.length - 1 && !paragraphs[i].isEmpty()) {
                builder.append(paragraphs[i].substring(0, 1).toUpperCase()).append(paragraphs[i].substring(1).toLowerCase());
            } else {
                builder.append(paragraphs[i].toLowerCase());
            }
        }
        return builder.toString();
    }
    
    /**
     * Convert to camel naming format, but place the last word at the beginning
     * @param text input
     * @return 
     */
    private static String toLastWordReversedCamelCase(String text) {
        StringBuilder builder = new StringBuilder();
        String[] paragraphs = text.split("_");
        for (int i = 0;i < paragraphs.length;i++) {
            if (i == 0) {
                builder.append(paragraphs[paragraphs.length - 1].toLowerCase());
            } else {
                builder.append(paragraphs[i - 1].substring(0, 1).toUpperCase()).append(paragraphs[i - 1].substring(1).toLowerCase());
            }
        }
        return builder.toString();
    }
    
    private static void putItemColors(String itemID, Map<String, String> materials, String prefixPath) {
        String[] colorNames = {"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red" ,"black"};
        for (int i = 0;i < colorNames.length;i++) {
            String color = colorNames[i];
            if (ItemUtil.languageProperties.containsKey(prefixPath + "." + color + ".name")) {
                materials.put(i == 0 ? itemID : itemID + ":" + i, ItemUtil.languageProperties.getProperty(prefixPath + "." + color + ".name"));
            }
        }
    }
    
    private static void putDirts(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.dirt.name")) {
            materials.put("DIRT", ItemUtil.languageProperties.getProperty("tile.dirt.name"));
        }
        String[] dirts = {"default", "coarse", "podzol"};
        for (int i = 0;i < dirts.length;i++) {
            String dirt = dirts[i];
            if (ItemUtil.languageProperties.containsKey("tile.dirt." + dirt + ".name")) {
                materials.put(i == 0 ? "DIRT" : "DIRT:" + i, ItemUtil.languageProperties.getProperty("tile.dirt." + dirt + ".name"));
            }
        }
    }
    
    private static void putSands(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.sand.name")) {
            materials.put("SAND", ItemUtil.languageProperties.getProperty("tile.sand.name"));
        }
        String[] sands = {"default", "red"};
        for (int i = 0;i < sands.length;i++) {
            String sand = sands[i];
            if (ItemUtil.languageProperties.containsKey("tile.sand." + sand + ".name")) {
                materials.put(i == 0 ? "SAND" : "SAND:" + i, ItemUtil.languageProperties.getProperty("tile.sand." + sand + ".name"));
            }
        }
    }
    
    private static void putSlabs(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.stoneSlab.name")) {
            materials.put("STEP", ItemUtil.languageProperties.getProperty("tile.stoneSlab.name"));
        }
        String[] slabs = {"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
        for (int i = 0;i < slabs.length;i++) {
            String slab = slabs[i];
            if (ItemUtil.languageProperties.containsKey("tile.stoneSlab." + slab + ".name")) {
                materials.put(i == 0 ? "STEP" : "STEP:" + i, ItemUtil.languageProperties.getProperty("tile.stoneSlab." + slab + ".name"));
            }
        }
    }
    
    private static void putQuartzBlocks(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.quartzBlock.name")) {
            materials.put("QUARTZ_BLOCK", ItemUtil.languageProperties.getProperty("tile.quartzBlock.name"));
        }
        String[] quartzBlocks = {"default", "chiseled", "lines"};
        for (int i = 0;i < quartzBlocks.length;i++) {
            String quartz = quartzBlocks[i];
            if (ItemUtil.languageProperties.containsKey("tile.quartzBlock." + quartz + ".name")) {
                materials.put(i == 0 ? "QUARTZ_BLOCK" : "QUARTZ_BLOCK:" + i, ItemUtil.languageProperties.getProperty("tile.quartzBlock." + quartz + ".name"));
            }
        }
    }
    
    private static void putStones(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.stone.stone.name")) {
            String[] stoneNames = {"stone", "granite", "graniteSmooth", "diorite", "dioriteSmooth", "andesite", "andesiteSmooth"};
            for (int i = 0;i < stoneNames.length;i++) {
                String stone = stoneNames[i];
                if (ItemUtil.languageProperties.containsKey("tile.stone." + stone + ".name")) {
                    materials.put(i == 0 ? "STONE" : "STONE:" + i, ItemUtil.languageProperties.getProperty("tile.stone." + stone + ".name"));
                }
            }
        } else {
            materials.put("STONE", ItemUtil.languageProperties.getProperty("tile.stone.name"));
        }
    }
    
    private static void putSaplings(Map<String, String> materials) {
        String[] types = {"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};
        for (int i = 0;i < types.length;i++) {
            String spaling = types[i];
            if (ItemUtil.languageProperties.containsKey("tile.sapling." + spaling + ".name")) {
                materials.put(i == 0 ? "SAPLING" : "SAPLING:" + i, ItemUtil.languageProperties.getProperty("tile.sapling." + spaling + ".name"));
            }
        }
    }
    
    private static void putWoods(Map<String, String> materials) {
        String[] types = {"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};
        for (int i = 0;i < types.length;i++) {
            String wood = types[i];
            if (ItemUtil.getLanguageProperties().containsKey("tile.wood." + wood + ".name")) {
                materials.put(i == 0 ? "WOOD" : "WOOD:" + i, ItemUtil.getLanguageProperties().getProperty("tile.wood." + wood + ".name"));
            }
        }
    }
    
    private static void putRecord(String itemID, Map<String, String> materials) {
        if (itemID.equals("GOLD_RECORD")) {
            materials.put("GOLD_RECORD", ItemUtil.languageProperties.getProperty("item.record.13.desc"));
        } else if (itemID.equals("GREEN_RECORD")) {
            materials.put("GREEN_RECORD", ItemUtil.languageProperties.getProperty("item.record.cat.desc"));
        } else if (itemID.startsWith("RECORD_")) {
            String[] recordNumbers = {"blocks", "chirp", "far", "mall", "mellohi", "stal", "strad", "ward", "11", "wait"};
            String number = itemID.replace("RECORD_", "");
            if (LiteCommandEditorUtils.isInteger(number)) {
                int i = Integer.valueOf(number) - 2 /*Two specially named record*/ - 1;
                if (i < recordNumbers.length) {
                    materials.put(itemID, ItemUtil.languageProperties.getProperty("item.record." + recordNumbers[i] + ".desc"));
                }
            }
        }
    }
    
    private static void putTerracotta(String itemID, Map<String, String> materials) {
        if (itemID.equals("STAINED_CLAY")) {
            if (ItemUtil.languageProperties.containsKey("tile.clayHardened.name")) {
                materials.put(itemID, ItemUtil.languageProperties.getProperty("tile.clayHardened.name"));
            }
            putItemColors(itemID, materials, "tile.clayHardenedStained");
        } else {
            String color = toCamelCase(itemID.replace("_GLAZED_TERRACOTTA", ""));
            if (ItemUtil.languageProperties.containsKey("tile.clayHardenedStained." + color + ".name")) {
                materials.put(itemID, ItemUtil.languageProperties.getProperty("tile.clayHardenedStained." + color + ".name"));
            }
        }
    }
    
    private static void putShulkerBox(String itemID, Map<String, String> materials) {
        String color = toCamelCase("_" + itemID.replace("_SHULKER_BOX", ""));
        if (ItemUtil.languageProperties.containsKey("tile.shulkerBox" + color + ".name")) {
            materials.put(itemID, ItemUtil.languageProperties.getProperty("tile.shulkerBox" + color + ".name"));
        }
    }
    
    private static void putBoat(String itemID, Map<String, String> materials) {
        if (itemID.equals("BOAT")) {
            materials.put(itemID, ItemUtil.languageProperties.containsKey("item.boat.oak.name") ? ItemUtil.languageProperties.getProperty("item.boat.oak.name") : ItemUtil.languageProperties.getProperty("item.boat.name"));
        } else {
            String type = itemID.replace("BOAT_", "").toLowerCase();
            if (ItemUtil.languageProperties.containsKey("item.boat." + type + ".name")) {
                materials.put(itemID, ItemUtil.languageProperties.getProperty("item.boat." + type + ".name"));
            }
        }
    }
    
    private static void putMinecart(String itemID, Map<String, String> materials) {
        String newID = itemID.replace("EXPLOSIVE_", "TNT_").replace("COMMAND_", "BLOCK_COMMAND_").replace("POWERED_", "FURNACE_").replace("STORAGE_", "CHEST_");
        if (ItemUtil.languageProperties.containsKey("item." + toInvertedCamelCase(newID) + ".name")) {
            materials.put(itemID, ItemUtil.languageProperties.getProperty("item." + toInvertedCamelCase(newID) + ".name"));
        }
    }
    
    private static void putSkulls(Map<String, String> materials) {
        String[] skulls = {"skeleton", "wither", "zombie", "char", "creeper", "dragon"};
        if (ItemUtil.languageProperties.containsKey("item.skull." + skulls[0] + ".name")) {
            materials.put("SKULL", ItemUtil.languageProperties.getProperty("item.skull." + skulls[0] + ".name"));
        }
        for (int i = 0;i < skulls.length;i++) {
            String skull = skulls[i];
            if (ItemUtil.languageProperties.containsKey("item.skull." + skull + ".name")) {
                materials.put(i == 0 ? "SKULL_ITEM" : "SKULL_ITEM:" + i, ItemUtil.languageProperties.getProperty("item.skull." + skull + ".name"));
            }
        }
    }
    
    private static void putDoor(String itemID, Map<String, String> materials) {
        String newID = toCamelCase(itemID.replace("_DOOR_ITEM", ""));
        if (ItemUtil.languageProperties.containsKey("item.door" + newID.substring(0, 1).toUpperCase() + newID.substring(1) + ".name")) {
            materials.put(itemID, ItemUtil.languageProperties.getProperty("item.door" + newID.substring(0, 1).toUpperCase() + newID.substring(1) + ".name"));
        }
    }
    
    private static void putHorseArmor(String itemID, Map<String, String> materials) {
        String newID = itemID.replace("IRON_BARDING", "horsearmormetal").replace("GOLD_BARDING", "horsearmorgold").replace("DIAMOND_BARDING", "horsearmordiamond");
        if (ItemUtil.languageProperties.containsKey("item." + newID + ".name")) {
            materials.put(itemID, ItemUtil.languageProperties.getProperty("item." + newID + ".name"));
        }
    }
    
    private static void putLeaves(Map<String, String> materials) {
        String[] leaves = {"oak", "spruce", "birch", "jungle"};
        for (int i = 0;i < leaves.length;i++) {
            String leavesName = leaves[i];
            if (ItemUtil.languageProperties.containsKey("tile.leaves." + leavesName + ".name")) {
                materials.put(i == 0 ? "LEAVES" : "LEAVES:" + i, ItemUtil.languageProperties.getProperty("tile.leaves." + leavesName + ".name"));
            }
        }
    }
    
    private static void putLeaves2(Map<String, String> materials) {
        String[] leaves = {"acacia", "big_oak"};
        for (int i = 0;i < leaves.length;i++) {
            String leavesName = leaves[i];
            if (ItemUtil.languageProperties.containsKey("tile.leaves." + leavesName + ".name")) {
                materials.put(i == 0 ? "LEAVES_2" : "LEAVES_2:" + i, ItemUtil.languageProperties.getProperty("tile.leaves." + leavesName + ".name"));
            }
        }
    }
    
    private static void putTallGrass(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.tallgrass.grass.name")) {
            String[] grass = {"shrub", "grass", "fern"};
            for (int i = 0;i < grass.length;i++) {
                String grassName = grass[i];
                if (ItemUtil.languageProperties.containsKey("tile.tallgrass." + grassName + ".name")) {
                    materials.put(i == 0 ? "LONG_GRASS" : "LONG_GRASS:" + i, ItemUtil.languageProperties.getProperty("tile.tallgrass." + grassName + ".name"));
                }
            }
        } else {
            materials.put("LONG_GRASS", ItemUtil.languageProperties.getProperty("tile.tallgrass.name"));
        }
    }
    
    private static void putFlower2(Map<String, String> materials) {
        String[] flowers = {"poppy", "blueOrchid", "allium", "houstonia", "tulipRed", "tulipOrange", "tulipWhite", "tulipPink", "oxeyeDaisy"};
        for (int i = 0;i < flowers.length;i++) {
            String flower = flowers[i];
            if (ItemUtil.languageProperties.containsKey("tile.flower2." + flower + ".name")) {
                materials.put(i == 0 ? "RED_ROSE" : "RED_ROSE:" + i, ItemUtil.languageProperties.getProperty("tile.flower2." + flower + ".name"));
            }
        }
    }
    
    private static void putDoublePlant(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.doublePlant.name")) {
            materials.put("DOUBLE_PLANT", ItemUtil.languageProperties.getProperty("tile.doublePlant.name"));
        }
        String[] plants = {"sunflower", "syringa", "grass", "fern", "rose", "paeonia"};
        for (int i = 0;i < plants.length;i++) {
            String plant = plants[i];
            if (ItemUtil.languageProperties.containsKey("tile.doublePlant." + plant + ".name")) {
                materials.put(i == 0 ? "DOUBLE_PLANT" : "DOUBLE_PLANT:" + i, ItemUtil.languageProperties.getProperty("tile.doublePlant." + plant + ".name"));
            }
        }
    }
    
    private static void putLog(Map<String, String> materials) {
        String[] logs = {"oak", "spruce", "birch", "jungle"};
        for (int i = 0;i < logs.length;i++) {
            String log = logs[i];
            if (ItemUtil.languageProperties.containsKey("tile.log." + log + ".name")) {
                materials.put(i == 0 ? "LOG" : "LOG:" + i, ItemUtil.languageProperties.getProperty("tile.log." + log + ".name"));
            }
        }
    }
    
    private static void putLog2(Map<String, String> materials) {
        String[] logs = {"acacia", "big_oak"};
        for (int i = 0;i < logs.length;i++) {
            String log = logs[i];
            if (ItemUtil.languageProperties.containsKey("tile.log." + log + ".name")) {
                materials.put(i == 0 ? "LOG_2" : "LOG_2:" + i, ItemUtil.languageProperties.getProperty("tile.log." + log + ".name"));
            }
        }
    }
    
    private static void putPrismarines(Map<String, String> materials) {
        String[] prismaines = {"rough", "bricks", "dark"};
        for (int i = 0;i < prismaines.length;i++) {
            String prismaine = prismaines[i];
            if (ItemUtil.languageProperties.containsKey("tile.prismarine." + prismaine + ".name")) {
                materials.put(i == 0 ? "PRISMARINE" : "PRISMARINE:" + i, ItemUtil.languageProperties.getProperty("tile.prismarine." + prismaine + ".name"));
            }
        }
    }
    
    private static void putSandStones(Map<String, String> materials) {
        String[] sandstones = {"default", "chiseled", "smooth"};
        for (int i = 0;i < sandstones.length;i++) {
            String sandstone = sandstones[i];
            if (ItemUtil.languageProperties.containsKey("tile.sandStone." + sandstone + ".name")) {
                materials.put(i == 0 ? "SANDSTONE" : "SANDSTONE:" + i, ItemUtil.languageProperties.getProperty("tile.sandStone." + sandstone + ".name"));
            }
        }
    }
    
    private static void putRedSandStones(Map<String, String> materials) {
        String[] sandstones = {"default", "chiseled", "smooth"};
        for (int i = 0;i < sandstones.length;i++) {
            String sandstone = sandstones[i];
            if (ItemUtil.languageProperties.containsKey("tile.redSandStone." + sandstone + ".name")) {
                materials.put(i == 0 ? "RED_SANDSTONE" : "RED_SANDSTONE:" + i, ItemUtil.languageProperties.getProperty("tile.redSandStone." + sandstone + ".name"));
            }
        }
    }
    
    private static void putCobbleWalls(Map<String, String> materials) {
        String[] walls = {"normal", "mossy"};
        for (int i = 0;i < walls.length;i++) {
            String wall = walls[i];
            if (ItemUtil.languageProperties.containsKey("tile.cobbleWall." + wall + ".name")) {
                materials.put(i == 0 ? "COBBLE_WALL" : "COBBLE_WALL:" + i, ItemUtil.languageProperties.getProperty("tile.cobbleWall." + wall + ".name"));
            }
        }
    }
    
    private static void putSponge(Map<String, String> materials) {
        if (ItemUtil.languageProperties.containsKey("tile.sponge.dry.name")) {
            String[] types = {"dry", "wet"};
            for (int i = 0;i < types.length;i++) {
                String type = types[i];
                if (ItemUtil.languageProperties.containsKey("tile.sponge." + type + ".name")) {
                    materials.put(i == 0 ? "SPONGE" : "SPONGE:" + i, ItemUtil.languageProperties.getProperty("tile.sponge." + type + ".name"));
                }
            }
        } else {
            if (ItemUtil.languageProperties.containsKey("tile.sponge.name")) {
                materials.put("SPONGE", ItemUtil.languageProperties.getProperty("tile.sponge.name"));
            }
        }
    }
}
