package com.arclighttw.tinyreactors.client.gui;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.arclighttw.tinyreactors.container.ContainerReactorEnergyPort;
import com.arclighttw.tinyreactors.main.Reference;
import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.network.MessageEnergyPortServer;
import com.arclighttw.tinyreactors.network.MessageEnergyPortServer.EnergyPortAction;
import com.arclighttw.tinyreactors.network.MessageEnergyPortServer.EnergyPortMode;
import com.arclighttw.tinyreactors.proxy.ClientProxy;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorEnergyPort;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiReactorEnergyPort extends GuiContainer implements IGui
{
	static final ResourceLocation TEXTURE = new ResourceLocation(Reference.ID, "textures/gui/reactor_energy_port.png");
	
	final TileEntityReactorEnergyPort energyPort;
	
	GuiButtonDrawable inputDecrease, inputIncrease;
	GuiButtonDrawable outputDecrease, outputIncrease;
	
	public GuiReactorEnergyPort(InventoryPlayer player, TileEntityReactorEnergyPort energyPort)
	{
		super(new ContainerReactorEnergyPort(player, energyPort));
		this.energyPort = energyPort;

		xSize = 177;
		ySize = 163;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		addButton(inputDecrease = new GuiButtonDrawable(0, guiLeft + 25, guiTop + 40, TEXTURE, 177, 56, 18, 18).setHoverTextureLocation(177, 74).setDisabledTextureLocation(177, 92));
		addButton(inputIncrease = new GuiButtonDrawable(1, guiLeft + 45, guiTop + 40, TEXTURE, 195, 56, 18, 18).setHoverTextureLocation(195, 74).setDisabledTextureLocation(195, 92));
		
		addButton(outputDecrease = new GuiButtonDrawable(2, guiLeft + 115, guiTop + 40, TEXTURE, 177, 56, 18, 18).setHoverTextureLocation(177, 74).setDisabledTextureLocation(177, 92));
		addButton(outputIncrease = new GuiButtonDrawable(3, guiLeft + 135, guiTop + 40, TEXTURE, 195, 56, 18, 18).setHoverTextureLocation(195, 74).setDisabledTextureLocation(195, 92));
		
		sync();
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException
	{
		int amount = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? (Mouse.isButtonDown(0) ? 1000 : 100) : (Mouse.isButtonDown(0) ? 50 : 10);
		
		switch(button.id)
		{
		case 0:
			TinyReactors.NETWORK.sendToServer(new MessageEnergyPortServer(energyPort.getPos(), EnergyPortMode.Input, EnergyPortAction.Decrease, amount));
			break;
		case 1:
			TinyReactors.NETWORK.sendToServer(new MessageEnergyPortServer(energyPort.getPos(), EnergyPortMode.Input, EnergyPortAction.Increase, amount));
			break;
		case 2:
			TinyReactors.NETWORK.sendToServer(new MessageEnergyPortServer(energyPort.getPos(), EnergyPortMode.Output, EnergyPortAction.Decrease, amount));
			break;
		case 3:
			TinyReactors.NETWORK.sendToServer(new MessageEnergyPortServer(energyPort.getPos(), EnergyPortMode.Output, EnergyPortAction.Increase, amount));
			break;
		}
		
		sync();
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		sync();
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) 
	{
		drawDefaultBackground();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int scale = energyPort.getEnergyStoredScaled(60);
		drawTexturedModalRect(guiLeft + 84, guiTop + 68 - scale, 177, 0, 8, scale);
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		drawCenteredString("Max Energy Input", 44, 20, 0x00E7F7);
		drawCenteredString(String.format("%,d RF", energyPort.currentInput), 44, 29, 0xFFFFFF);
		
		drawCenteredString("Max Energy Output", 134, 20, 0xF77300);
		drawCenteredString(String.format("%,d RF", energyPort.currentOutput), 134, 29, 0xFFFFFF);
		
		if(mouseX > guiLeft + 80 && mouseX < guiLeft + 96 && mouseY > guiTop + 8 && mouseY < guiTop + 72)
			drawHoveringText(Arrays.asList(String.format("Energy: %,d RF", energyPort.getEnergyStored(EnumFacing.NORTH)), String.format("Filled: %d", energyPort.getEnergyStoredScaled(100)) + "%"), mouseX - guiLeft, mouseY - guiTop);
		
		if(inputDecrease.isMouseOver())
		{
			String message = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? "Decrease by 1000" /* (100) */ : "Decrease by 50" /* (10) */;
			drawHoveringText(message, mouseX - guiLeft, mouseY - guiTop);
		}
		
		if(inputIncrease.isMouseOver())
		{
			String message = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? "Increase by 1000" : "Increase by 50";
			drawHoveringText(message, mouseX - guiLeft, mouseY - guiTop);
		}
		
		if(outputDecrease.isMouseOver())
		{
			String message = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? "Decrease by 1000" : "Decrease by 50";
			drawHoveringText(message, mouseX - guiLeft, mouseY - guiTop);
		}
		
		if(outputIncrease.isMouseOver())
		{
			String message = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? "Increase by 1000" : "Increase by 50";
			drawHoveringText(message, mouseX - guiLeft, mouseY - guiTop);
		}
	}
	
	void drawCenteredString(String text, int x, int y, int color)
	{
		ClientProxy.fontRenderer.drawString(text, x - (ClientProxy.fontRenderer.getStringWidth(text) / 2), y, color);
	}
	
	@Override
	public void sync()
	{
		if(energyPort == null)
			return;
		
		if(inputIncrease == null)
			return;
		
		inputIncrease.enabled = energyPort.currentInput < energyPort.maxInput;
		inputDecrease.enabled = energyPort.currentInput > 1;
		
		outputIncrease.enabled = energyPort.currentOutput < energyPort.maxOutput;
		outputDecrease.enabled = energyPort.currentOutput > 1;
	}
}
