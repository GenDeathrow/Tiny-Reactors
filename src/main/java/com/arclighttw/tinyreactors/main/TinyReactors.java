package com.arclighttw.tinyreactors.main;

import java.io.File;

import com.arclighttw.tinyreactors.config.ModConfig;
import com.arclighttw.tinyreactors.init.TRBlocks;
import com.arclighttw.tinyreactors.managers.GuiManager;
import com.arclighttw.tinyreactors.network.MessageReactorController;
import com.arclighttw.tinyreactors.proxy.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES, acceptedMinecraftVersions = Reference.MINECRAFT, guiFactory = Reference.GUI_FACTORY)
public class TinyReactors
{
	@Mod.Instance(value = Reference.ID)
	public static TinyReactors instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy proxy;
	
	public static final CreativeTabs TAB = new TinyTab();
	public static SimpleNetworkWrapper NETWORK;
	
	@Mod.EventHandler
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
		proxy.onPreInitialization(event);
		ModConfig.initialize(new File(event.getModConfigurationDirectory(), "TinyReactors.cfg"));
		
		TRBlocks.onRegister();
	}
	
	@Mod.EventHandler
	public void onInitialization(FMLInitializationEvent event)
	{
		proxy.onInitialization(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiManager());
		
		NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.ID);
		NETWORK.registerMessage(MessageReactorController.Handler.class, MessageReactorController.class, 0, Side.SERVER);
	}
	
	@Mod.EventHandler
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		proxy.onPostInitialization(event);
	}
}
