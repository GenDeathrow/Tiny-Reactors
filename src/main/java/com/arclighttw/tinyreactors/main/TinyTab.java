package com.arclighttw.tinyreactors.main;

import com.arclighttw.tinyreactors.init.TRBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TinyTab extends CreativeTabs
{
	public TinyTab()
	{
		super(Reference.ID);
	}
	
	@Override
	public ItemStack getTabIconItem()
	{
		return new ItemStack(TRBlocks.REACTOR_CONTROLLER);
	}
}
