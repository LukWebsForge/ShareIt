package de.lukweb.discordbeam;

import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

public enum LargeShareService {

    DISCORD_FILE("Discord File"),
    GITHUB_GIST("Github Gist"),
    ;

    private String name;

    LargeShareService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        if (this == DISCORD_FILE) {
            return true;
        } else if (this == GITHUB_GIST) {
            return ServiceManager.getService(GistUploader.class) != null;
        }
        return false;
    }

    public static void applyGistNotAvailable(JRadioButton radioButton) {
        if (!GITHUB_GIST.isAvailable()) {
            radioButton.setEnabled(false);
            radioButton.setToolTipText("You can't share via GitHub, because the GitHub plugin is disabled");
        }
    }
}
