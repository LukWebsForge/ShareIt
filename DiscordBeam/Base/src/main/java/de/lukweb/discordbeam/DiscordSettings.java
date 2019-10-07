package de.lukweb.discordbeam;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import de.lukweb.share.ShareSettings;

@State(
        name = "DiscordBeamSettings",
        storages = @Storage(
                value = "discordBeamSettings.xml"
        )
)
public class DiscordSettings extends ShareSettings<DiscordSettingsState> {

    public static DiscordSettings getInstance() {
        return ServiceManager.getService(DiscordSettings.class);
    }

    @Override
    protected DiscordSettingsState newState() {
        return new DiscordSettingsState();
    }

    public boolean isWebhookSet() {
        String webhookUrl = getState().getWebhookUrl();
        return webhookUrl != null && !webhookUrl.isEmpty();
    }

}
