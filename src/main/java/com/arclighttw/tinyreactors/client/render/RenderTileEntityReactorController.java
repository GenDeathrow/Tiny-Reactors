package com.arclighttw.tinyreactors.client.render;

import org.lwjgl.opengl.GL11;

import com.arclighttw.tinyreactors.blocks.BlockTiny;
import com.arclighttw.tinyreactors.init.TRBlocks;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;
import com.arclighttw.tinyreactors.util.Util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEntityReactorController extends TileEntitySpecialRenderer<TileEntityReactorController>
{
	float ticks;
	
	@Override
	public void render(TileEntityReactorController tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		if(tile == null || !tile.hasWorld())
			return;
		
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		EnumFacing facing = state.getValue(BlockTiny.FACING);
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder wr = tess.getBuffer();
		
		RenderHelper.disableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		switch(facing)
		{
		case SOUTH:
			break;
		case NORTH:
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-1, 0, -1);
			break;
		case EAST:
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(-1, 0, 0);
			break;
		case WEST:
			GlStateManager.rotate(270, 0, 1, 0);
			GlStateManager.translate(0, 0, -1);
			break;
		default:
			break;
		}
		
		GlStateManager.pushMatrix();
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		renderPanel(tile, TRBlocks.REACTOR_CONTROLLER.getDefaultState(), wr);
		tess.draw();
		
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}
	
	void renderPanel(TileEntityReactorController tile, IBlockState state, BufferBuilder wr)
	{
		TextureAtlasSprite icon = Util.getTexture(state);
		
		double minU = (double)icon.getMinU();
        double maxU = (double)icon.getMaxU();
        double minV = (double)icon.getMinV();
        double maxV = (double)icon.getMaxV();
        
        int percentage = tile.isValid() ? tile.getReactorEfficiencyScaled(100) : -1;
        
        int xPos;
        int yPos = 12;
        
        if(percentage > 75)
        		xPos = 14;
        else if(percentage > 50)
        		xPos = 12;
        else if(percentage > 25)
        		xPos = 10;
        else if(percentage > 0)
        		xPos = 8;
        else
        {
        		xPos = 8;
        		yPos = 14;
        }
        
        double u = minU + (maxU - minU) * xPos / 16F;
        double v = minV + (maxV - minV) * yPos / 16F;
        
		wr.pos(0.44F, 0.33F, 1F).tex(u, v).normal(0, 1, 0).endVertex();
        wr.pos(0.93F, 0.33F, 1F).tex(u, v).normal(0, 1, 0).endVertex();
        wr.pos(0.93F, 0.80F, 1F).tex(u, v).normal(0, 1, 0).endVertex();
        wr.pos(0.44F, 0.80F, 1F).tex(u, v).normal(0, 1, 0).endVertex();
	}
}
