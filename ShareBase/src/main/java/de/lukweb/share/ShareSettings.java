package de.lukweb.share;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.NotNull;

public abstract class ShareSettings<T extends ShareSettingsState> implements PersistentStateComponent<T> {

    private T settingsState;

    public ShareSettings() {
    }

    protected abstract T newState();

    @Override
    @NotNull
    public T getState() {
        if (settingsState == null) {
            this.settingsState = newState();
        }
        return settingsState;
    }

    @Override
    public void loadState(@NotNull T state) {
        this.settingsState = state;
    }

    @Override
    public void noStateLoaded() {
        settingsState = newState();
    }
}
