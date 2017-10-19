package com.arclighttw.tinyreactors.client.gui;

import com.arclighttw.tinyreactors.container.ContainerReactorController;
import com.arclighttw.tinyreactors.main.Reference;
import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.network.MessageReactorController;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiReactorController extends GuiContainer
{
	static final ResourceLocation TEXTURE = new ResourceLocation(Reference.ID, "textures/gui/reactor_controller.png");
	
	final TileEntityReactorController controller;
	
	public GuiReactorController(InventoryPlayer player, TileEntityReactorController controller)
	{
		super(new ContainerReactorController(player, controller));
		this.controller = controller;
		
		xSize = 176;
		ySize = 163;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		TinyReactors.NETWORK.sendToServer(new MessageReactorController(Minecraft.getMinecraft().player, controller.getPos()));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(controller.isValid())
		{
			int scale = controller.getReactorEfficiencyScaled(60);
			drawTexturedModalRect(guiLeft + 82, guiTop + 70 - scale, 176, 0, 12, scale);
		}
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		drawString(fontRenderer, "Efficiency:", 12, 12, 0xFFFFFF);
		drawString(fontRenderer, controller.getReactorEfficiencyScaled(100) + "%", 12, 22, 0xFFFFFF);
		
		drawString(fontRenderer, "Output:", 12, 50, 0xFFFFFF);
		drawString(fontRenderer, controller.getReactorOutput() + " RF/t", 12, 60, 0xFFFFFF);
		
		drawString(fontRenderer, "Scale:", 108, 12, 0xFFFFFF);
		drawString(fontRenderer, controller.getReactorScale("x"), 108, 22, 0xFFFFFF);
		drawString(fontRenderer, controller.getReactorScale("y"), 108, 32, 0xFFFFFF);
		drawString(fontRenderer, controller.getReactorScale("z"), 108, 42, 0xFFFFFF);
		
		drawString(fontRenderer, controller.isValid() ? "Active" : "Inactive", 108, 60, 0xFFFFFF);
	}
}
