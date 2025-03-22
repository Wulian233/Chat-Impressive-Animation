package com.wulian.chatimpressiveanimation.fabric;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimation;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.fabricmc.api.ClientModInitializer;

public class ChatImpressiveAnimationClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
		ConfigUtil.getConfig();
		ChatImpressiveAnimation.LOGGER.info("Chat Impressive Animation is loaded!");
    }
}
