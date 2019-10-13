package de.lukweb.discordbeam;

import de.lukweb.discordbeam.uploaders.LargeShareService;
import de.lukweb.share.ShareSettingsState;
import org.jetbrains.annotations.NotNull;

public class DiscordSettingsState implements ShareSettingsState {

    public static final String DEFAULT_CUSTOM_NAME = "DiscordBeam";

    private String webhookUrl;
    private String customName;
    private LargeShareService shareService;
    private boolean dontAskForService;

    public DiscordSettingsState() {
        webhookUrl = "";
        customName = DEFAULT_CUSTOM_NAME;
        shareService = LargeShareService.DISCORD_FILE;
        dontAskForService = false;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(@NotNull String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(@NotNull String customName) {
        this.customName = customName;
    }

    public LargeShareService getShareService() {
        return shareService;
    }

    public void setShareService(@NotNull LargeShareService shareService) {
        this.shareService = shareService;
    }

    public boolean isDontAskForService() {
        return dontAskForService;
    }

    public void setDontAskForService(boolean dontAskForService) {
        this.dontAskForService = dontAskForService;
    }
}
