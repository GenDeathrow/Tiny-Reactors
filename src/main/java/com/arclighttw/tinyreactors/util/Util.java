package com.arclighttw.tinyreactors.util;

import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.network.MessageIGuiSync;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	
	public static void syncServerToClient(World world, BlockPos pos, TileEntity tile)
	{
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.scheduleBlockUpdate(pos, tile.getBlockType(), 0, 0);
		tile.markDirty();
		
		if(!world.isRemote)
			TinyReactors.NETWORK.sendToAll(new MessageIGuiSync());
	}
}
