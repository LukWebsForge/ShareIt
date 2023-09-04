package de.lukweb.hasteit;

import de.lukweb.share.ShareSettingsState;

public class HasteSettingsState implements ShareSettingsState {

    private String customUrl = null;
    private String apiKey = null;

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getAPIKey() {
        return apiKey;
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
