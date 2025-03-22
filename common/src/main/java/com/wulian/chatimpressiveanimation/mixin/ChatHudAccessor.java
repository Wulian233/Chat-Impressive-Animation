package com.wulian.chatimpressiveanimation.mixin;

import com.mojang.brigadier.Message;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
	@Accessor("visibleMessages")
	List<Message> getVisibleMessages();
}
