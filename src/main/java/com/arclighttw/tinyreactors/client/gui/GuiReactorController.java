package com.arclighttw.tinyreactors.client.gui;

import java.io.IOException;
import java.util.Arrays;

import com.arclighttw.tinyreactors.container.ContainerReactorController;
import com.arclighttw.tinyreactors.main.Reference;
import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.network.MessageReactorStateServer;
import com.arclighttw.tinyreactors.proxy.ClientProxy;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiReactorController extends GuiContainer
{
	static final ResourceLocation TEXTURE = new ResourceLocation(Reference.ID, "textures/gui/reactor_controller.png");
	
	public final TileEntityReactorController controller;
	
	GuiButton controlButton;
	
	public GuiReactorController(InventoryPlayer player, TileEntityReactorController controller)
	{
		super(new ContainerReactorController(player, controller));
		this.controller = controller;
		
		xSize = 177;
		ySize = 163;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		controlButton = new GuiButton(0, guiLeft + 82, guiTop + 52, 59, 16, "");
		syncButton();
		
		addButton(controlButton);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch(button.id)
		{
		case 0:
			TinyReactors.NETWORK.sendToServer(new MessageReactorStateServer(controller.getPos(), controller.isActive()));
			break;
		}
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		syncButton();
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int scale = controller.getEnergyStoredScaled(60);
		drawTexturedModalRect(guiLeft + 156, guiTop + 68 - scale, 177, 0, 8, scale);
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		if(controller.isActive())
		{
			ClientProxy.fontRenderer.drawString("Producing:", 12, 12, 0xFFFFFF);
			ClientProxy.fontRenderer.drawString(String.format("%,d", controller.getReactorTotalYield()) + " RF/t", 12, 20, 0xFFFFFF);
		}
		else
			ClientProxy.fontRenderer.drawString("Inactive", 12, 12, 0xFFFFFF);
		
		ClientProxy.fontRenderer.drawString("Scale:", 84, 12, 0xFFFFFF);
		
		int[] bounds = controller.getReactorBounds();
		ClientProxy.fontRenderer.drawString("X: " + bounds[0], 84, 22, 0xFFFFFF);
		ClientProxy.fontRenderer.drawString("Y: " + bounds[1], 84, 32, 0xFFFFFF);
		ClientProxy.fontRenderer.drawString("Z: " + bounds[2], 84, 42, 0xFFFFFF);
		
		if(mouseX > guiLeft + 152 && mouseX < guiLeft + 168 && mouseY > guiTop + 8 && mouseY < guiTop + 72)
			drawHoveringText(Arrays.asList(String.format("Energy: %,d RF", controller.getEnergyStored()), String.format("Filled: %d", controller.getEnergyStoredScaled(100)) + "%"), mouseX - guiLeft, mouseY - guiTop);
	}
	
	public void syncButton()
	{
		if(controlButton == null)
			return;
		
		if(controller.isActive())
		{
			controlButton.displayString = "Disable";
			controlButton.enabled = true;
		}
		else	if(controller.isStructureValid())
		{
			controlButton.displayString = "Enable";
			controlButton.enabled = true;
		}
		else
		{
			controlButton.displayString = "Invalid";
			controlButton.enabled = false;
		}
	}
}
