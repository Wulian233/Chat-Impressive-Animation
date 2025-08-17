package com.wulian.chatimpressiveanimation.forge;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimation;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import com.wulian.chatimpressiveanimation.config.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(ChatImpressiveAnimation.MOD_ID)
public class ChatImpressiveAnimationClientForge {
    @SuppressWarnings("removal")
    public ChatImpressiveAnimationClientForge() {
        ModContainer container = ModList.get().getModContainerById(ChatImpressiveAnimation.MOD_ID).orElseThrow();
        container.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> DisplayTest.IGNORESERVERONLY, (a, b) -> true));

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
