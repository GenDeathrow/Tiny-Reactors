package com.arclighttw.tinyreactors.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Util
{
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getTexture(IBlockState state)
	{
		if(state == null)
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
	}
}
