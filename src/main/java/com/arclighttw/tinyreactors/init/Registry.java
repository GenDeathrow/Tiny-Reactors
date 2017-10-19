package com.arclighttw.tinyreactors.init;

import java.util.Map;

import com.arclighttw.tinyreactors.main.Reference;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Reference.ID)
public class Registry
{
	static Map<ResourceLocation, Block> BLOCKS = Maps.newHashMap();
	static Map<ResourceLocation, Item> ITEMS = Maps.newHashMap();
	static Map<ResourceLocation, Class<? extends TileEntity>> TILES = Maps.newHashMap();
	
	public static void registerBlock(Block block, String name)
	{
		block.setRegistryName(Reference.ID, name).setUnlocalizedName(Reference.ID + ":" + name);
		BLOCKS.put(block.getRegistryName(), block);
		
		registerItem(new ItemBlock(block), name);
		
		if(!(block instanceof ITileEntityProvider))
			return;
		
		TileEntity tile = ((ITileEntityProvider)block).createNewTileEntity(null, -1);
		
		if(tile == null || TILES.containsValue(tile.getClass()))
			return;
		
		TILES.put(block.getRegistryName(), tile.getClass());
	}
	
	public static void registerItem(Item item, String name)
	{
		item.setRegistryName(Reference.ID, name).setUnlocalizedName(Reference.ID + ":" + name);
		ITEMS.put(item.getRegistryName(), item);
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event)
	{
		for(Map.Entry<ResourceLocation, Block> block : BLOCKS.entrySet())
			event.getRegistry().register(block.getValue());
		
		for(Map.Entry<ResourceLocation, Class<? extends TileEntity>> tile : TILES.entrySet())
			GameRegistry.registerTileEntity(tile.getValue(), tile.getKey().toString());
	}
	
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		for(Map.Entry<ResourceLocation, Item> item : ITEMS.entrySet())
			event.getRegistry().register(item.getValue());
	}
	
	@SubscribeEvent
	public static void onMModelRegister(ModelRegistryEvent event)
	{
		for(Map.Entry<ResourceLocation, Block> block : BLOCKS.entrySet())
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block.getValue()), 0, new ModelResourceLocation(Reference.ID + ":" + block.getKey().getResourcePath(), "inventory"));
	}
}
