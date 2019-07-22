package de.lukweb.hasteit;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import de.lukweb.share.ShareSettings;

@State(
        name = "HasteSettings",
        storages = @Storage(
                value = "hasteSettings.xml"
        )
)
public class HasteSettings extends ShareSettings<HasteSettingsState> {

    public static HasteSettings getInstance() {
        return ServiceManager.getService(HasteSettings.class);
    }

    @Override
    protected HasteSettingsState newState() {
        return new HasteSettingsState();
    }

    public String getCustomUrl() {
        return getState().getCustomUrl();
    }

    public String getBaseUrl() {
        if (getCustomUrl() != null) {
            return getCustomUrl();
        }
        return "https://hastebin.com";
    }

    public String getUploadUrl() {
        return getBaseUrl() + "/documents";
    }

    public String getFileUrl(String hasteCode, String extension) {
        return getBaseUrl() + "/" + hasteCode + "." + extension;
    }

    public void setBaseUrl(String url) {
        // Trims the last slash:
        // A url without a slash at the end is needed, because we append a path to the url.
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        getState().setCustomUrl(url);
    }

}
