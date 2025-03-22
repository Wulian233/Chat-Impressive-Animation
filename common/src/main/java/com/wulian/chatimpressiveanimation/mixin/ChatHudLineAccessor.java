package com.wulian.chatimpressiveanimation.mixin;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatHudLine.class)
public interface ChatHudLineAccessor {
	@Accessor("creationTick")
	int getCreationTick();
}
