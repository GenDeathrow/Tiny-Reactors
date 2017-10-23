package com.arclighttw.tinyreactors.init;

import java.lang.reflect.Field;
import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class TRRecipes
{
	public static IRecipe REACTOR_CONTROLLER;
	public static IRecipe REACTOR_ENERGY_PORT;
	public static IRecipe REACTOR_CASING;
	public static IRecipe REACTOR_GLASS;
	
	public static IRecipe CAPACITOR;
	
	static void onInitialize()
	{
		REACTOR_CONTROLLER = new ShapedRecipe(TRBlocks.REACTOR_CONTROLLER, new Object[] {
			"CTC",
			"LGL",
			"CRC",
				Character.valueOf('C'), TRBlocks.REACTOR_CASING,
				Character.valueOf('T'), Blocks.REDSTONE_TORCH,
				Character.valueOf('L'), Blocks.LEVER,
				Character.valueOf('G'), Blocks.GLASS,
				Character.valueOf('R'), Blocks.REDSTONE_BLOCK
		});
		
		REACTOR_ENERGY_PORT = new ShapedRecipe(TRBlocks.REACTOR_ENERGY_PORT, new Object[] {
			"CBC",
			"BBB",
			"CBC",
				Character.valueOf('C'), TRBlocks.REACTOR_CASING,
				Character.valueOf('B'), Blocks.IRON_BARS
		});
		
		REACTOR_CASING = new ShapedRecipe(new ItemStack(TRBlocks.REACTOR_CASING, 4), new Object[] {
			"III",
			"IRI",
			"III",
				Character.valueOf('I'), Items.IRON_INGOT,
				Character.valueOf('R'), Items.REDSTONE
		});
		
		REACTOR_GLASS = new ShapelessRecipe(new ItemStack(TRBlocks.REACTOR_GLASS, 7), new Object[] {
			Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, Items.IRON_INGOT, Items.REDSTONE	
		});
		
		CAPACITOR = new ShapedRecipe(TRBlocks.CAPACITOR, new Object[] {
			"IRI",
			"IrI",
			"IRI",
				Character.valueOf('I'), Items.IRON_INGOT,
				Character.valueOf('R'), Items.REDSTONE,
				Character.valueOf('r'), Blocks.REDSTONE_BLOCK
		});
	}
	
	public static void onRegister()
	{
		onInitialize();
		
		try
		{
			for(Field field : TRRecipes.class.getDeclaredFields())
			{
				Object obj = field.get(null);
				
				if(!(obj instanceof IRecipe))
					continue;
				
				IRecipe recipe = (IRecipe)obj;
				String name = field.getName().toLowerCase(Locale.ENGLISH);
				Registry.registerRecipe(recipe, name);
			}
		}
		catch(IllegalAccessException e)
		{
		}
	}
	
	static class ShapedRecipe extends ShapedOreRecipe
	{
		public ShapedRecipe(Block     result, Object... recipe){ this(new ItemStack(result), recipe); }
	    public ShapedRecipe(Item      result, Object... recipe){ this(new ItemStack(result), recipe); }
	    public ShapedRecipe(@Nonnull ItemStack result, Object... recipe) { this(result.getItem().getRegistryName(), result, CraftingHelper.parseShaped(recipe)); }
	    
	    ShapedRecipe(ResourceLocation group, @Nonnull ItemStack result, ShapedPrimer primer)
	    {
	    		super(group, result, primer);
	    }
	}
	
	static class ShapelessRecipe extends ShapelessOreRecipe
	{
		public ShapelessRecipe(Block result, Object... recipe){ this(new ItemStack(result), recipe); }
	    public ShapelessRecipe(Item  result, Object... recipe){ this(new ItemStack(result), recipe); }
	   
	    public ShapelessRecipe(@Nonnull ItemStack result, NonNullList<Ingredient> input)
	    {
	        super(result.getItem().getRegistryName(), input, result);
	    }
	    
	    public ShapelessRecipe(@Nonnull ItemStack result, Object... recipe)
	    {
	        super(result.getItem().getRegistryName(), result, recipe);
	    }
	}
}
