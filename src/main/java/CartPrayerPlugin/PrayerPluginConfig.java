package CartPrayerPlugin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("prayeralert")
public interface PrayerPluginConfig extends Config
{
	@ConfigItem(
			keyName = "prayerThreshold",
			name = "Prayer Threshold",
			description = "Play a sound when prayer drops to or below this value"
	)
	default int prayerThreshold()
	{
		return 10;
	}
}