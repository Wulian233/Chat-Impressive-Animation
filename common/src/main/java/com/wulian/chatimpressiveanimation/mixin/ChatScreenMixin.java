package com.wulian.chatimpressiveanimation.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.brigadier.Message;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.ColorHelper;
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

	public final MinecraftClient client = MinecraftClient.getInstance();

	@Inject(method = "render", at = @At("HEAD"))
	private void renderStart(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatBarAnimation) return;

		if (client.player != null && !wasOpenedLastFrame && !client.player.isSleeping()) {
			wasOpenedLastFrame = true;
			animationStartTime = System.currentTimeMillis();
			isClosing = false;
		}

		float screenFactor = (float) client.getWindow().getHeight() / 1080;
		float elapsedTime = (float) (System.currentTimeMillis() - animationStartTime);
		float alpha = isClosing ? elapsedTime / FADE_TIME : 1 - (elapsedTime / FADE_TIME);
		alpha = Math.min(1, Math.max(0, alpha));

		float easedAlpha = EASE_OUT_FACTOR * alpha * alpha * alpha - EASE_IN_OUT_FACTOR * alpha * alpha;
		offsetY = easedAlpha * FADE_OFFSET * screenFactor;

		if (isClosing) {
			GlStateManager._enableBlend();
		}

		context.getMatrices().pushMatrix();
		context.getMatrices().translate(0, offsetY);
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
			shift = At.Shift.BEFORE
		)
	)
	private void renderScreenStart(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		context.getMatrices().translate(0, offsetY);
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
			shift = At.Shift.AFTER
		)
	)
	private void renderScreenEnd(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		context.getMatrices().translate(0, -offsetY);
	}

	@Unique
	private boolean hasActiveChatMessages() {
		if (client.inGameHud == null || client.inGameHud.getChatHud() == null) return false;

		List<Message> messages = ((ChatHudAccessor) client.inGameHud.getChatHud()).getVisibleMessages();

		int ticks = client.inGameHud.getTicks();
		final int fadeTicks = 200;

		for (Object msg : messages) {
			if (msg instanceof ChatHudLine line) {
				int creationTick = ((ChatHudLineAccessor) (Object) line).getCreationTick();
				if (ticks - creationTick < fadeTicks) {
					return true;
				}
			}
		}
		return false;
	}

	// Don't remove cancellable attribute!
	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (keyCode == 256) { // ESC
			if (ConfigUtil.getConfig().enableChatBarAnimation && !hasActiveChatMessages()) {
				isClosing = true;
				animationStartTime = System.currentTimeMillis();
			} else {
				client.setScreen(null);
			}
			cir.cancel();
		}
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
			shift = At.Shift.AFTER
		)
	)
	private void renderEnd(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatBarAnimation) return;
		context.getMatrices().popMatrix();
		if (isClosing) {
			ColorHelper.fromFloats(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager._disableBlend();
		}
		if (isClosing && (System.currentTimeMillis() - animationStartTime) >= FADE_TIME) {
			client.setScreen(null);
		}
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void onClosed(CallbackInfo ci) {
		wasOpenedLastFrame = false;
	}
}
