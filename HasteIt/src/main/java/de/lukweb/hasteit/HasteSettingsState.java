package de.lukweb.hasteit;

import de.lukweb.share.ShareSettingsState;

public class HasteSettingsState implements ShareSettingsState {

    private String customUrl = null;

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }
}
