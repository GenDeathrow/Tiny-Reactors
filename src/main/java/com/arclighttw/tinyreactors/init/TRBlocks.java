package com.arclighttw.tinyreactors.init;

import java.lang.reflect.Field;
import java.util.Locale;

import com.arclighttw.tinyreactors.blocks.BlockCapacitor;
import com.arclighttw.tinyreactors.blocks.BlockReactorComponent;
import com.arclighttw.tinyreactors.blocks.BlockReactorController;
import com.arclighttw.tinyreactors.blocks.BlockReactorEnergyPort;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TRBlocks
{
	public static Block REACTOR_CONTROLLER = new BlockReactorController();
	public static Block REACTOR_ENERGY_PORT = new BlockReactorEnergyPort();
	
	public static Block REACTOR_CASING = new BlockReactorComponent(Material.IRON);
	public static Block REACTOR_GLASS = new BlockReactorComponent(Material.GLASS);
	
	public static Block CAPACITOR = new BlockCapacitor();
	
	public static void onRegister()
	{
		try
		{
			for(Field field : TRBlocks.class.getDeclaredFields())
			{
				Object obj = field.get(null);
				
				if(!(obj instanceof Block))
					continue;
				
				Block block = (Block)obj;
				String name = field.getName().toLowerCase(Locale.ENGLISH);
				Registry.registerBlock(block, name);
			}
		}
		catch(IllegalAccessException e)
		{
		}
	}
}
