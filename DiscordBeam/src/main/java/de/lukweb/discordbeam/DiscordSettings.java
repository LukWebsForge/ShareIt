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

    public DiscordSettings() {
        super(DiscordSettingsState.class);
    }

    public boolean isWebhookSet() {
        String webhookUrl = getState().getWebhookUrl();
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return false;
        }

        return true;
    }

    public String getCustomOrDefaultName() {
        if (getState().getCustomName() != null) {
            return getState().getCustomName();
        } else {
            return getDefaultName();
        }
    }

    public String getDefaultName() {
        return "DiscordBeam";
    }

}
