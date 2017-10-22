package com.arclighttw.tinyreactors.config;

import java.io.File;

import com.arclighttw.tinyreactors.config.TRConfig.MeltdownType;
import com.arclighttw.tinyreactors.main.Reference;
import com.arclighttw.tinyreactors.managers.ReactorManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfig
{
	public static Configuration config;
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(!event.getModID().equals(Reference.ID))
			return;
		
		syncConfig();
	}
	
	public static void initialize(File file)
	{
		config = new Configuration(file);
		syncConfig();
		
		MinecraftForge.EVENT_BUS.register(new ModConfig());
	}
	
	public static void syncConfig()
	{
		String category;
		
		category = "General";
		config.addCustomCategoryComment(category, "General Tiny Reactor settings.");
		
		TRConfig.REACTOR_MELTDOWN = config.getBoolean("Reactor Meltdown", category, TRConfig.REACTOR_MELTDOWN, TRConfig.REACTOR_MELTDOWN_LABEL);
		TRConfig.REACTOR_MELTDOWN_TYPE = MeltdownType.values()[config.getInt("Meltdown Type", category, TRConfig.REACTOR_MELTDOWN_TYPE.ordinal(), 0, MeltdownType.values().length - 1, TRConfig.REACTOR_MELTDOWN_TYPE_LABEL)];
		
		category = "Reactants";
		config.addCustomCategoryComment(category, "Specify the rate per tick of each Block when placed inside an active reactor.");
		
		TRConfig.REACTANT_REGISTRY = config.getStringList("Entries", category, TRConfig.REACTANT_REGISTRY, TRConfig.REACTANT_REGISTRY_LABEL);
		
		ReactorManager.populate();
		
		if(config.hasChanged())
			config.save();
	}
}
