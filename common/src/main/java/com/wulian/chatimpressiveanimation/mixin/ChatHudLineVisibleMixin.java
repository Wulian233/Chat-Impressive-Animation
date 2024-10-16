package com.wulian.chatimpressiveanimation.mixin;

import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHudLine.Visible.class)
public class ChatHudLineVisibleMixin {
	@Inject(method = "indicator", at = @At("HEAD"), cancellable = true)
	private void injectIndicator(CallbackInfoReturnable<MessageIndicator> cir) {
		if (ConfigUtil.getConfig().removeMessageIndicator){
			cir.setReturnValue(null);
		}
	}
}
