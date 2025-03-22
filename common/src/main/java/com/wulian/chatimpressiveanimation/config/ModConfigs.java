package com.wulian.chatimpressiveanimation.config;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimation;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = ChatImpressiveAnimation.MOD_ID)
public class ModConfigs implements ConfigData {
	public boolean enableChatSendingAnimation = true;
	@ConfigEntry.BoundedDiscrete(min = 100, max = 900)
	public int chatSendingAnimationFadeTime = 170;

	public boolean enableChatBarAnimation = true;
	@ConfigEntry.BoundedDiscrete(min = 100, max = 900)
	public int chatBarAnimationFadeTime = 170;

	public boolean removeMessageIndicator = true;
}
