package com.arclighttw.tinyreactors.client.render;

import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
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
		
		GlStateManager.pushMatrix();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.translate(x + 0.5D, y + 0.1D, z + 0.5D);
		GlStateManager.scale(0.55D, 0.55D, 0.55D);
		GlStateManager.pushMatrix();
//		GlStateManager.rotate(ticks, 0F, 1F, 0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5D, 0D, 0.5D);
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.SEA_LANTERN.getDefaultState(), 1.0F * (tile.getReactorEfficiencyScaled(100) / 100F));
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}
}
