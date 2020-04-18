package de.lukweb.discordbeam;

import de.lukweb.discordbeam.uploaders.LargeShareService;
import de.lukweb.share.ShareSettingsState;
import org.jetbrains.annotations.NotNull;

public class DiscordSettingsState implements ShareSettingsState {

    public static final String DEFAULT_USER_NAME = "Mystic Creature";
    public static final String DEFAULT_USER_ICON = "https://cdn.discordapp.com/embed/avatars/0.png";

    private String webhookUrl;
    private String userName;
    private String userIcon;
    private LargeShareService shareService;
    private boolean dontAskForService;

    public DiscordSettingsState() {
        webhookUrl = "";
        userName = DEFAULT_USER_NAME;
        userIcon = DEFAULT_USER_ICON;
        shareService = LargeShareService.DISCORD_FILE;
        dontAskForService = false;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(@NotNull String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(@NotNull String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(@NotNull String userIcon) {
        this.userIcon = userIcon;
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
