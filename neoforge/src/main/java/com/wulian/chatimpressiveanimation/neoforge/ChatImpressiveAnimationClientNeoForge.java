package com.wulian.chatimpressiveanimation.neoforge;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimation;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import com.wulian.chatimpressiveanimation.config.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Function;

@Mod(value = ChatImpressiveAnimation.MOD_ID, dist = Dist.CLIENT)
public class ChatImpressiveAnimationClientNeoForge {
    public ChatImpressiveAnimationClientNeoForge() {
        if (FMLEnvironment.getDist().isClient()) {
			ConfigUtil.getConfig();

			registerConfigScreen(ChatImpressiveAnimation.MOD_ID, screen -> AutoConfig.getConfigScreen(ModConfigs.class, screen).get());

			ChatImpressiveAnimation.LOGGER.info("Chat Impressive Animation is loaded!");
        }
    }

	public static void registerConfigScreen(String modid, Function<Screen, Screen> screenFunction) {
		ModContainer modContainer = ModList.get().getModContainerById(modid).orElseThrow();
		modContainer.registerExtensionPoint(IConfigScreenFactory.class,
			(client, screen) -> screenFunction.apply(screen));
	}
}
