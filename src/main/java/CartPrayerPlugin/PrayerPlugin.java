package CartPrayerPlugin;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

@Slf4j
@PluginDescriptor(
		name = "Prayer Alert"
)
public class PrayerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PrayerPluginConfig config;

	private boolean alertFired = false;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("Prayer Alert started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		alertFired = false;
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() != Skill.PRAYER) return;

		int currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);

		if (currentPrayer <= config.prayerThreshold() && !alertFired)
		{
			alertFired = true;
			playSound();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Prayer is low!", null);
		}

		// Reset when prayer is restored OR hits 0
		if (currentPrayer > config.prayerThreshold() || currentPrayer == 0)
		{
			alertFired = false;
		}
	}

	private void playSound()
	{
		try
		{
			InputStream soundStream = PrayerPlugin.class.getResourceAsStream("/alert.wav");
			if (soundStream == null)
			{
				log.warn("Sound file not found!");
				return;
			}

			AudioInputStream audioStream = AudioSystem.getAudioInputStream(
					new BufferedInputStream(soundStream)
			);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start();
		}
		catch (Exception e)
		{
			log.warn("Failed to play sound: {}", e.getMessage());
		}
	}

	@Provides
	PrayerPluginConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PrayerPluginConfig.class);
	}
}