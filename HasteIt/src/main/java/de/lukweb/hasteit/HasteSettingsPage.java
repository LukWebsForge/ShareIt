package de.lukweb.hasteit;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTextField;
import de.lukweb.share.ShareWebTools;
import de.lukweb.share.UrlValidator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class HasteSettingsPage implements SearchableConfigurable {

    private final Disposable disposable;

    private JPanel panelMain;
    private JLabel labelHasteUrl;
    private JBTextField textHasteUrl;
    private JButton buttonReset;

    public HasteSettingsPage() {
        disposable = Disposer.newDisposable();
        Disposer.register(ApplicationManager.getApplication().getService(HasteSettings.class), disposable);
    }

    @Override
    public @NotNull
    @NonNls
    String getId() {
        return "hasteit";
    }

    @Override
    public String getDisplayName() {
        return "Haste It";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        labelHasteUrl.setLabelFor(textHasteUrl);
        UrlValidator.installOn(this.disposable, textHasteUrl);
        textHasteUrl.getEmptyText().setText(HasteSettings.DEFAULT_URL);
        textHasteUrl.setTextToTriggerEmptyTextStatus(HasteSettings.DEFAULT_URL);
        textHasteUrl.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateResetButton();
            }
        });

        buttonReset.addActionListener(l -> textHasteUrl.setText(HasteSettings.DEFAULT_URL));

        reset();
        updateResetButton();

        return panelMain;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return textHasteUrl;
    }

    private void updateResetButton() {
        boolean canReset = !textHasteUrl.getText().equals(textHasteUrl.getEmptyText().getText());
        buttonReset.setEnabled(canReset);
    }

    @Override
    public boolean isModified() {
        HasteSettings settingsState = HasteSettings.getInstance();

        if (isModified(textHasteUrl, settingsState.getBaseURL())) {
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        HasteSettings settings = HasteSettings.getInstance();
        textHasteUrl.setText(settings.getBaseURL());
    }

    @Override
    public void apply() throws ConfigurationException {
        String urlCheck = ShareWebTools.checkUrl(textHasteUrl.getText());

        if (!textHasteUrl.getText().isEmpty() && urlCheck != null) {
            textHasteUrl.requestFocusInWindow();
            throw new ConfigurationException(urlCheck, "Invalid Hastebin URL");
        }

        HasteSettings settingsState = HasteSettings.getInstance();
        settingsState.setBaseURL(textHasteUrl.getText());

        if (textHasteUrl.getText().isEmpty()) {
            settingsState.setBaseURL(HasteSettings.DEFAULT_URL);
            reset();
        }
    }

    @Override
    public void disposeUIResources() {
        disposable.dispose();
    }
}
