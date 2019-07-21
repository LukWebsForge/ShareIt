package de.lukweb.share;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public abstract class ShareSettings<T extends ShareSettingsState> implements PersistentStateComponent<T> {

    private Class<T> stateClass;
    private T settingsState;

    public ShareSettings(Class<T> stateClass) {
        this.stateClass = stateClass;
        this.settingsState = newState();
    }

    @Override
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

    private T newState() {
        try {
            return stateClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
