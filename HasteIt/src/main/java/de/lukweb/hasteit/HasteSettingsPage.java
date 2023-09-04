package de.lukweb.hasteit;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HasteSettingsPage implements SearchableConfigurable {

    private final Disposable disposable;

    private JPanel panelMain;
    private JLabel labelHasteUrl;
    private JBTextField textHasteUrl;
    private JButton buttonReset;
    private JLabel labelApiKey;
    private JBTextField textApiKey;
    private JButton buttonGetApiKey;

    public HasteSettingsPage() {
        disposable = Disposer.newDisposable();
        Disposer.register(HasteSettings.getInstance(), disposable);
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

        labelApiKey.setLabelFor(textApiKey);
        textApiKey.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateResetButton();
            }
        });
        buttonGetApiKey.addActionListener(l -> {
            try {
                URL docUrl = new URI("https://www.toptal.com/developers/hastebin/documentation").toURL();
                ShareWebTools.openURL(docUrl);
            } catch (URISyntaxException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        reset();
        updateResetButton();

        return panelMain;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return textHasteUrl;
    }

    private void updateResetButton() {
        HasteSettings settings = HasteSettings.getInstance();

        boolean canReset =
                !textHasteUrl.getText().equals(textHasteUrl.getEmptyText().getText()) ||
                !textApiKey.getText().equals(settings.getAPIKeyOrEmpty());

        buttonReset.setEnabled(canReset);
    }

    @Override
    public boolean isModified() {
        HasteSettings settingsState = HasteSettings.getInstance();

        if (Configurable.isFieldModified(textHasteUrl, settingsState.getBaseURL())) {
            return true;
        }

        if (Configurable.isFieldModified(textApiKey, settingsState.getAPIKeyOrEmpty())) {
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        HasteSettings settings = HasteSettings.getInstance();
        textHasteUrl.setText(settings.getBaseURL());
        textApiKey.setText(settings.getAPIKeyOrEmpty());
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

        if (textApiKey.getText().isEmpty()) {
            settingsState.getState().setAPIKey(null);
        } else {
            settingsState.getState().setAPIKey(textApiKey.getText());
        }
    }

    @Override
    public void disposeUIResources() {
        disposable.dispose();
    }
}
