package de.lukweb.hasteit;

import com.intellij.openapi.components.ServiceManager;
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

    public static final String DEFAULT_URL = "https://hastebin.com";

    public static HasteSettings getInstance() {
        return ServiceManager.getService(HasteSettings.class);
    }

    @Override
    protected HasteSettingsState newState() {
        return new HasteSettingsState();
    }

    public String getBaseURL() {
        String customUrl = getState().getCustomUrl();

        // A migration for the older versions of this plugin which store the default URL as null
        if (customUrl == null) {
            setBaseURL(DEFAULT_URL);
            return DEFAULT_URL;
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
        return getBaseURL() + "/" + hasteCode + "." + extension;
    }

}
