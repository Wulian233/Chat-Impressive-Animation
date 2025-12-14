package com.wulian.chatimpressiveanimation.fabric.mixin;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimationExpectPlatform;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatComponent.class)
public class ChatHudMixin {
	@Shadow private int chatScrollbarPos;
	@Shadow @Final private List<GuiMessage.Line> trimmedMessages;
	@Shadow private int getLineHeight() { return 0; }
	@Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();

	@Unique private final int chatSendingAnimationFadeTime = ConfigUtil.getConfig().chatSendingAnimationFadeTime;
	@Unique private int chatDisplacementY = 0;

	@Unique
	private void calculateYOffset() {
		// Calculate current required offset to achieve slide in from bottom effect
		try {
			int lineHeight = this.getLineHeight();
			// scale * lineHeight
			float fadeOffsetYScale = 0.8f;
			float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
			long timestamp = messageTimestamps.getFirst();
			long timeAlive = System.currentTimeMillis() - timestamp;
			if (timeAlive < chatSendingAnimationFadeTime && this.chatScrollbarPos == 0) {
				chatDisplacementY = (int)(maxDisplacement - (((float) timeAlive / chatSendingAnimationFadeTime) * maxDisplacement));
			} else {
				chatDisplacementY = 0;
			}
		} catch (Exception ignored) {
			chatDisplacementY = 0;
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRenderStart(GuiGraphics context, Font font, int currentTick, int mouseX, int mouseY, boolean focused, boolean open, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatSendingAnimation) return;
		calculateYOffset();

		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}

		context.pose().translate(0, chatDisplacementY + raisedOffset);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderEnd(GuiGraphics context, Font font, int currentTick, int mouseX, int mouseY, boolean focused, boolean open, CallbackInfo ci) {
		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}

		context.pose().translate(0, -(chatDisplacementY + raisedOffset));
	}

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("TAIL"))
	private void addMessage(Component message, MessageSignature signatureData, GuiMessageTag indicator, CallbackInfo ci) {
		messageTimestamps.addFirst(System.currentTimeMillis());
		while (this.messageTimestamps.size() > this.trimmedMessages.size()) {
			this.messageTimestamps.removeLast();
		}
	}
}
