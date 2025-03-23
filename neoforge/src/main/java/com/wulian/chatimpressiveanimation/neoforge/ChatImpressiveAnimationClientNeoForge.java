package com.wulian.chatimpressiveanimation.neoforge;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimation;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import com.wulian.chatimpressiveanimation.config.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(ChatImpressiveAnimation.MOD_ID)
public class ChatImpressiveAnimationClientNeoForge {
    public ChatImpressiveAnimationClientNeoForge() {
        if (FMLLoader.getDist().isClient()) {
			ConfigUtil.getConfig();
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((client, screen) -> AutoConfig.getConfigScreen(ModConfigs.class, screen).get())
            );
			ChatImpressiveAnimation.LOGGER.info("Chat Impressive Animation is loaded!");
        }
    }
}
