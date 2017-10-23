package com.arclighttw.tinyreactors.network;

import com.arclighttw.tinyreactors.client.gui.IGui;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIGuiSync implements IMessage
{
	public MessageIGuiSync()
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
	
	public static class Handler implements IMessageHandler<MessageIGuiSync, IMessage>
	{
		@Override
		public IMessage onMessage(MessageIGuiSync message, MessageContext ctx)
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			
			if(gui != null && gui instanceof IGui)
				((IGui)gui).sync();
			
			return null;
		}
	}
}
