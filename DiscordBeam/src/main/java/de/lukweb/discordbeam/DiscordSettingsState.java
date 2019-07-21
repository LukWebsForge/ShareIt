package de.lukweb.discordbeam;

import de.lukweb.share.ShareSettingsState;

public class DiscordSettingsState implements ShareSettingsState {

    private String webhookUrl = null;
    private String customName = null;
    private LargeShareService shareService;
    private boolean dontAskForService;

    public DiscordSettingsState() {
        shareService = LargeShareService.DISCORD_FILE;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public LargeShareService getShareService() {
        return shareService;
    }

    public void setShareService(LargeShareService shareService) {
        this.shareService = shareService;
    }

    public boolean isDontAskForService() {
        return dontAskForService;
    }

    public void setDontAskForService(boolean dontAskForService) {
        this.dontAskForService = dontAskForService;
    }
}
