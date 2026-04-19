package com.wulian.chatimpressiveanimation.mixin;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimationExpectPlatform;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
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

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	@Shadow private int chatScrollbarPos;
	@Shadow @Final private List<GuiMessage.Line> trimmedMessages;
	@Shadow private int getLineHeight() { return 0; }
	@Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();
	@Unique private int chatDisplacementY = 0;

	@Unique
	private void calculateYOffset() {
		// Calculate current required offset to achieve slide in from bottom effect
		try {
			int lineHeight = this.getLineHeight();
			int chatSendingAnimationFadeTime = Math.max(1, ConfigUtil.getConfig().chatSendingAnimationFadeTime);
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

	@Inject(
			method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
			at = @At("HEAD")
	)
	private void onRenderStart(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode displayMode, boolean changeCursorOnInsertions, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatSendingAnimation) return;
		calculateYOffset();

		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}

		graphics.pose().translate(0, chatDisplacementY + raisedOffset);
	}

	@Inject(
			method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
			at = @At("TAIL")
	)
	private void onRenderEnd(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode displayMode, boolean changeCursorOnInsertions, CallbackInfo ci) {
		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}

		graphics.pose().translate(0, -(chatDisplacementY + raisedOffset));
	}

	@Inject(
			method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
			at = @At("TAIL")
	)
	private void addMessage(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
	messageTimestamps.addFirst(System.currentTimeMillis());
		while (this.messageTimestamps.size() > this.trimmedMessages.size()) {
			this.messageTimestamps.removeLast();
		}
	}
}
