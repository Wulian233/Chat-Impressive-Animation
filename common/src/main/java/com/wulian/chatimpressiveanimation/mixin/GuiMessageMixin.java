package com.wulian.chatimpressiveanimation.mixin;

import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiMessage.Line.class)
public class GuiMessageMixin {
	@Inject(method = "tag", at = @At("HEAD"), cancellable = true)
	private void injectIndicator(CallbackInfoReturnable<GuiMessageTag> cir) {
		if (ConfigUtil.getConfig().removeMessageIndicator){
			cir.setReturnValue(null);
		}
	}
}
