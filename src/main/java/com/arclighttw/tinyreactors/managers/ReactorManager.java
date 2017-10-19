package com.arclighttw.tinyreactors.managers;

import java.util.List;
import java.util.Map;

import com.arclighttw.tinyreactors.config.TRConfig;
import com.arclighttw.tinyreactors.init.TRBlocks;
import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.network.MessageReactorController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;

public class ReactorManager
{
	static final Map<BlockPos, List<String>> PLAYERS = Maps.newHashMap();
	static Map<ResourceLocation, Integer> REACTANTS = Maps.newHashMap();
	
	static int MAXIMUM_YIELD = -1;
	
	public static void populate()
	{
		REACTANTS = Maps.newHashMap();
		
		for(String reactant : TRConfig.REACTANT_REGISTRY)
		{
			String[] parts = reactant.split(":");
			
			if(parts.length < 3)
			{
				System.out.println("Invalid Custom Reactor Rate: [" + reactant + "]");
				continue;
			}
			
			if(!Loader.isModLoaded(parts[0]))
				continue;
			
			ResourceLocation registryName = new ResourceLocation(parts[0], parts[1]);
			Block block = Block.REGISTRY.getObject(registryName);
			
			if(block == null)
			{
				System.out.println("Invalid Block in Custom Reactor Rate: [" + registryName.toString() + "]");
				continue;
			}
			
			int rate = Integer.parseInt(parts[2]);
			REACTANTS.put(block.getRegistryName(), rate);
			
			if(rate > MAXIMUM_YIELD)
				MAXIMUM_YIELD = rate;
		}
	}
	
	public static boolean isReactant(Block block)
	{
		return getReactantRate(block) > 0;
	}
	
	public static boolean isValidCasing(Block block)
	{
		return block == TRBlocks.REACTOR_CONTROLLER ||
				block == TRBlocks.REACTOR_ENERGY_PORT ||
				block == TRBlocks.REACTOR_CASING ||
				block == TRBlocks.REACTOR_GLASS;
	}
	
	public static boolean isValidStructure(Block block)
	{
		return block == TRBlocks.REACTOR_CONTROLLER ||
				block == TRBlocks.REACTOR_ENERGY_PORT ||
				block == TRBlocks.REACTOR_CASING;
	}
	
	public static int getReactantRate(Block block)
	{
		return REACTANTS.containsKey(block.getRegistryName()) ? REACTANTS.get(block.getRegistryName()) : 0;
	}
	
	public static int getMaximumReactantRate()
	{
		return MAXIMUM_YIELD;
	}
	
	public static void onControllerOpened(MessageReactorController message)
	{
		BlockPos pos = new BlockPos(message.x, message.y, message.z);
		List<String> players;
		
		if(PLAYERS.containsKey(pos))
			players = PLAYERS.get(pos);
		else
			players = Lists.newArrayList();
		
		players.add(message.playerName);
		PLAYERS.put(pos, players);
	}
	
	public static void onControllerInvalidated(WorldServer world, BlockPos pos)
	{
		if(!PLAYERS.containsKey(pos))
			return;
		
		List<String> players = PLAYERS.get(pos);
		
		for(String playerName : players)
		{
			EntityPlayer player = world.getPlayerEntityByName(playerName);
			
			if(player == null)
			{
				System.err.println("Unable to find a Player with the name " + playerName);
				continue;
			}
			
			player.openGui(TinyReactors.instance, -1, world, pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
