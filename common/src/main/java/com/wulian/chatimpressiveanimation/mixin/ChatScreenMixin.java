package com.wulian.chatimpressiveanimation.mixin;

import com.mojang.brigadier.Message;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	@Unique private boolean wasOpenedLastFrame = false;
	@Unique private boolean isClosing = false;
	@Unique private long animationStartTime = 0;
	@Unique private float offsetY = 0;

	private static final int FADE_TIME = ConfigUtil.getConfig().chatBarAnimationFadeTime;
	private static final float FADE_OFFSET = 10;
	private static final float EASE_IN_OUT_FACTOR = 1.70158f;
	private static final float EASE_OUT_FACTOR = EASE_IN_OUT_FACTOR + 1;

	public final Minecraft client = Minecraft.getInstance();

	@Inject(method = "render", at = @At("HEAD"))
	private void renderStart(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatBarAnimation) return;

		if (client.player != null && !wasOpenedLastFrame && !client.player.isSleeping()) {
			wasOpenedLastFrame = true;
			animationStartTime = System.currentTimeMillis();
			isClosing = false;
		}

		float screenFactor = (float) client.getWindow().getScreenHeight() / 1080;
		float elapsedTime = (float) (System.currentTimeMillis() - animationStartTime);
		float alpha = isClosing ? elapsedTime / FADE_TIME : 1 - (elapsedTime / FADE_TIME);
		alpha = Math.min(1, Math.max(0, alpha));

		float easedAlpha = EASE_OUT_FACTOR * alpha * alpha * alpha - EASE_IN_OUT_FACTOR * alpha * alpha;
		offsetY = easedAlpha * FADE_OFFSET * screenFactor;

		context.pose().pushMatrix();
		context.pose().translate(0, offsetY);
	}

	@Unique
	private boolean hasActiveChatMessages() {
		List<Message> messages = ((ChatHudAccessor) client.gui.getChat()).getVisibleMessages();

		int ticks = client.gui.getGuiTicks();
		final int fadeTicks = 200;

		for (Object msg : messages) {
			if (msg instanceof GuiMessage line) {
				int creationTick = ((ChatHudLineAccessor) (Object) line).getCreationTick();
				if (ticks - creationTick < fadeTicks) {
					return true;
				}
			}
		}
		return false;
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true, require = 0)
	private void onKeyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
		if (input.key() == 256) { // ESC
			if (ConfigUtil.getConfig().enableChatBarAnimation && !hasActiveChatMessages()) {
				isClosing = true;
				animationStartTime = System.currentTimeMillis();
			} else {
				client.setScreen(null);
			}
			cir.cancel();
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void renderEnd(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatBarAnimation) return;

		context.pose().popMatrix();

		if (isClosing && (System.currentTimeMillis() - animationStartTime) >= FADE_TIME) {
			client.setScreen(null);
		}
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void onClosed(CallbackInfo ci) {
		wasOpenedLastFrame = false;
	}
}
