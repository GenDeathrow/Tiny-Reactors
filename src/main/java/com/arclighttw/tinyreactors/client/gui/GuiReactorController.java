package com.arclighttw.tinyreactors.client.gui;

import java.io.IOException;

import com.arclighttw.tinyreactors.container.ContainerReactorController;
import com.arclighttw.tinyreactors.main.Reference;
import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.network.MessageReactorStateServer;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
		
		this.inventorySlots.addListener(new IContainerListener() {
			@Override
			public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
			{
				syncButton();
			}
			
			@Override
			public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
			{
				syncButton();
			}
			
			@Override
			public void sendAllWindowProperties(Container containerIn, IInventory inventory)
			{
				syncButton();
			}
			
			@Override
			public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
			{
				syncButton();
			}
		});
		
		xSize = 176;
		ySize = 163;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		controlButton = new GuiButton(0, guiLeft + 106, guiTop + 52, 59, 16, "");
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
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(controller.isActive())
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
		drawString(fontRenderer, String.format("%,d", controller.getReactorTotalYield()) + " RF/t", 12, 60, 0xFFFFFF);
		
		drawString(fontRenderer, "Scale:", 108, 12, 0xFFFFFF);
		
		int[] bounds = controller.getReactorBounds();
		drawString(fontRenderer, "X:    " + bounds[0], 108, 22, 0xFFFFFF);
		drawString(fontRenderer, "Y:    " + bounds[1], 108, 32, 0xFFFFFF);
		drawString(fontRenderer, "Z:    " + bounds[2], 108, 42, 0xFFFFFF);
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
