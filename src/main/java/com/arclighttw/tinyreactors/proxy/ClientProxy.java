package com.arclighttw.tinyreactors.proxy;

import com.arclighttw.tinyreactors.client.render.RenderTileEntityReactorController;
import com.arclighttw.tinyreactors.client.util.SmallFontRenderer;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	public static SmallFontRenderer fontRenderer;
	
	@Override
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
	}
	
	@Override
	public void onInitialization(FMLInitializationEvent event)
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReactorController.class, new RenderTileEntityReactorController());
	}
	
	@Override
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		fontRenderer = new SmallFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("minecraft:textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);	
	}
}
