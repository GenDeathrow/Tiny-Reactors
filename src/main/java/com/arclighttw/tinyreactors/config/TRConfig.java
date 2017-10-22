package com.arclighttw.tinyreactors.config;

public class TRConfig
{
	public static boolean REACTOR_MELTDOWN = true;
	public static String REACTOR_MELTDOWN_LABEL = "Should the Reactor go into meltdown when the Controller's internal energy buffer fills?";
	
	public static MeltdownType REACTOR_MELTDOWN_TYPE = MeltdownType.CONTROLLER_ONLY;
	public static String REACTOR_MELTDOWN_TYPE_LABEL =  "Which blocks should be destroyed by a Reactor meltdown?\n0. Controller Only\n1. Controller + Reactants.\n2. Controller + All Structure Blocks.\n3. Entire Reactor";
	
	public static String[] REACTANT_REGISTRY = new String[] {
			"minecraft:coal_ore:1",
			"minecraft:iron_ore:2",
			"minecraft:lapis_ore:4",
			"minecraft:redstone_ore:8",
			"minecraft:gold_ore:8",
			"minecraft:diamond_ore:16",
			"minecraft:emerald_ore:32",
			"minecraft:quartz_ore:8",
			
			"minecraft:coal_block:8",
			"minecraft:iron_block:16",
			"minecraft:lapis_block:32",
			"minecraft:redstone_block:64",
			"minecraft:gold_block:64",
			"minecraft:diamond_block:128",
			"minecraft:emerald_block:256",
			"minecraft:quartz_block:32"		
	};
	public static String REACTANT_REGISTRY_LABEL = "Entries should be input in the format (rate is specified per tick):\nmod_id:block_name:rate";
	
	public enum MeltdownType
	{
		CONTROLLER_ONLY,
		REACTANTS_ONLY,
		CASING_ONLY,
		ENTIRE_REACTOR
	}
}
