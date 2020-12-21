package de.lukweb.discordbeam.uploaders;

import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;


public enum LargeShareService {

    DISCORD_FILE("Discord File"),
    HASTEBIN("Hastebin"),
    GITHUB_GIST("Github Gist"),
    ;

    private final String name;

    LargeShareService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        if (this == DISCORD_FILE) {
            return true;
        } else if (this == HASTEBIN) {
            return HastebinUploader.works() == HastebinUploader.HastebinServiceStatus.OK;
        } else if (this == GITHUB_GIST) {
            return ServiceManager.getService(GistUploader.class) != null;
        }
        return false;
    }

    public static void applyGistNotAvailable(JRadioButton radioButton) {
        if (!GITHUB_GIST.isAvailable()) {
            radioButton.setEnabled(false);
            radioButton.setToolTipText("Enable the GitHub plugin to share your code using Gists");
        }
    }

    public static void applyHasteStatus(JRadioButton radioButton) {
        HastebinUploader.HastebinServiceStatus works = HastebinUploader.works();
        if (works != HastebinUploader.HastebinServiceStatus.OK) {
            radioButton.setEnabled(false);
            radioButton.setToolTipText(works.getInfoText());
        }
    }
}
