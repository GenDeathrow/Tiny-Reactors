package com.arclighttw.tinyreactors.network;

import com.arclighttw.tinyreactors.client.gui.GuiReactorController;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReactorStateClient implements IMessage
{
	public MessageReactorStateClient()
	{
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
	}
	
	public static class Handler implements IMessageHandler<MessageReactorStateClient, IMessage>
	{
		@Override
		public IMessage onMessage(MessageReactorStateClient message, MessageContext ctx)
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			
			if(gui != null && gui instanceof GuiReactorController)
				((GuiReactorController)gui).syncButton();
			
			return null;
		}
	}
}
