package de.lukweb.hasteit;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@State(
        name = "HasteSettings",
        storages = @Storage(
                value = "hasteSettings.xml"
        )
)
public class HasteSettings implements PersistentStateComponent<HasteSettingsState> {

    public static HasteSettings getInstance() {
        return ServiceManager.getService(HasteSettings.class);
    }

    private HasteSettingsState settingsState;

    public HasteSettings() {
        settingsState = new HasteSettingsState();
    }

    @Nullable
    @Override
    public HasteSettingsState getState() {
        return settingsState;
    }

    @Override
    public void loadState(@NotNull HasteSettingsState state) {
        this.settingsState = state;
    }

    @Override
    public void noStateLoaded() {
        settingsState = new HasteSettingsState();
    }

    public String getCustomUrl() {
        return settingsState.getCustomUrl();
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
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        settingsState.setCustomUrl(url);
    }

    public String checkUrl(String url) {
        url = url.trim().toLowerCase();
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            return "Your url must start with http:// or https://";
        }

        if (url.contains(" ")) {
            return "Your url may not contain spaces";
        }

        try {
            URL javaUrl = new URL(url);
            javaUrl.toURI();
        } catch (URISyntaxException | MalformedURLException ex) {
            return ex.getMessage();
        }

        return null;
    }
}
