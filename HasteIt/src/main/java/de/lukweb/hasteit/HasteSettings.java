package de.lukweb.hasteit;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import de.lukweb.share.ShareSettings;
import org.jetbrains.annotations.NotNull;

@State(
        name = "HasteSettings",
        storages = @Storage(
                value = "hasteSettings.xml"
        )
)
public class HasteSettings extends ShareSettings<HasteSettingsState> {

    public static final String OLD_DEFAULT_URL = "https://www.toptal.com/developers/hastebin";
    public static final String DEFAULT_URL = "https://hastebin.com";

    public static HasteSettings getInstance() {
        return ApplicationManager.getApplication().getService(HasteSettings.class);
    }

    @Override
    protected HasteSettingsState newState() {
        return new HasteSettingsState();
    }

    public String getBaseURL() {
        String customUrl = getState().getCustomUrl();

        // A migration for the older versions of this plugin which store the default URL as null
        // and a migration for older versions of this plugin which use hastebin.com as the default URL
        if (customUrl == null || customUrl.equalsIgnoreCase(OLD_DEFAULT_URL)) {
            setBaseURL(DEFAULT_URL);
            customUrl = DEFAULT_URL;
        }

        return customUrl;
    }

    public void setBaseURL(@NotNull String url) {
        // Trims the last slash of the url, because we append one if we need it.
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        getState().setCustomUrl(url);
    }

    public String computeUploadURL() {
        return getBaseURL() + "/documents";
    }

    public String computeFileURL(String hasteCode, String extension) {
        if (getBaseURL().equals(DEFAULT_URL)) {
            return getBaseURL() + "/share/" + hasteCode + "." + extension;
        }

        return getBaseURL() + "/" + hasteCode + "." + extension;
    }

    public String getAPIKey() {
        return getState().getAPIKey();
    }

    public String getAPIKeyOrEmpty() {
        if (getAPIKey() == null) {
            return "";
        } else {
            return getAPIKey();
        }
    }

}
