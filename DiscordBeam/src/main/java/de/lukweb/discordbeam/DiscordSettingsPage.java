package de.lukweb.discordbeam;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;
import de.lukweb.discordbeam.ui.JTextFieldLimit;
import de.lukweb.discordbeam.ui.WebhookValidator;
import de.lukweb.discordbeam.uploaders.LargeShareService;
import de.lukweb.share.ShareWebTools;
import de.lukweb.share.UrlValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class DiscordSettingsPage implements SearchableConfigurable {

    private final Disposable disposable;

    private JPanel panelSettings;
    private JPanel panelDiscord;
    private JPanel panelLongCode;
    private JTextField editWebHookUrl;
    private JBTextField editUserName;
    private JLabel labelUserNameCount;
    private JBTextField editUserIcon;
    private JRadioButton radioDiscordFile;
    private JRadioButton radioGist;
    private JCheckBox checkDontAskForService;
    private JLabel labelUserName;
    private JLabel labelUserIcon;
    private JRadioButton radioHaste;
    private ButtonGroup shareVia;

    public DiscordSettingsPage() {
        this.disposable = Disposer.newDisposable();
        Disposer.register(ApplicationManager.getApplication().getService(DiscordSettings.class), disposable);
    }

    @NotNull
    @Override
    public String getId() {
        return "discordbeam";
    }

    @Override
    public String getDisplayName() {
        return "Discord Beam";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        panelDiscord.setBorder(IdeBorderFactory.createTitledBorder("Discord Settings"));
        panelLongCode.setBorder(IdeBorderFactory.createTitledBorder("Share Long Code via..."));

        WebhookValidator.installOn(this.disposable, editWebHookUrl);

        labelUserName.setLabelFor(editUserName);
        editUserName.getEmptyText().setText(DiscordSettingsState.DEFAULT_USER_NAME);
        editUserName.setTextToTriggerEmptyTextStatus(DiscordSettingsState.DEFAULT_USER_NAME);
        editUserName.setDocument(new JTextFieldLimit(80));
        editUserName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateCustomNameCounter();
            }
        });

        labelUserIcon.setLabelFor(editUserIcon);
        UrlValidator.installOn(this.disposable, editUserIcon);
        editUserIcon.getEmptyText().setText(DiscordSettingsState.DEFAULT_USER_ICON);
        editUserIcon.setTextToTriggerEmptyTextStatus(DiscordSettingsState.DEFAULT_USER_ICON);

        LargeShareService.applyGistNotAvailable(radioGist);
        LargeShareService.applyHasteStatus(radioHaste);

        reset();
        updateCustomNameCounter();

        return panelSettings;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return editWebHookUrl;
    }

    private void updateCustomNameCounter() {
        int length = editUserName.getText().length();
        String lengthString = Integer.toString(length);

        if (lengthString.length() < 2) {
            lengthString = "0" + lengthString;
        }

        labelUserNameCount.setText(lengthString + " / 80");
    }

    private LargeShareService getSelectedShareService() {
        if (radioGist.isSelected() && LargeShareService.GITHUB_GIST.isAvailable()) {
            return LargeShareService.GITHUB_GIST;
        } else if (radioHaste.isSelected() && LargeShareService.HASTEBIN.isAvailable()) {
            return LargeShareService.HASTEBIN;
        } else {
            return LargeShareService.DISCORD_FILE;
        }
    }

    @Override
    public boolean isModified() {
        DiscordSettingsState settingsState = DiscordSettings.getInstance().getState();

        if (isModified(checkDontAskForService, settingsState.isDontAskForService())) {
            return true;
        }

        if (getSelectedShareService() != settingsState.getShareService()) {
            return true;
        }

        if (isModified(editWebHookUrl, settingsState.getWebhookUrl())) {
            return true;
        }

        if (isModified(editUserName, settingsState.getUserName())) {
            return true;
        }

        if (isModified(editUserIcon, settingsState.getUserIcon())) {
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        DiscordSettingsState settingsState = DiscordSettings.getInstance().getState();

        editWebHookUrl.setText(settingsState.getWebhookUrl());
        editUserName.setText(settingsState.getUserName());
        editUserIcon.setText(settingsState.getUserIcon());
        checkDontAskForService.setSelected(settingsState.isDontAskForService());
        if (settingsState.getShareService() == LargeShareService.GITHUB_GIST && LargeShareService.GITHUB_GIST.isAvailable()) {
            shareVia.setSelected(radioGist.getModel(), true);
        } else if (settingsState.getShareService() == LargeShareService.HASTEBIN && LargeShareService.HASTEBIN.isAvailable()) {
            shareVia.setSelected(radioHaste.getModel(), true);
        } else {
            shareVia.setSelected(radioDiscordFile.getModel(), true);
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        String webHookCheck = ShareWebTools.checkUrl(editWebHookUrl.getText());
        String userIconCheck = ShareWebTools.checkUrl(editUserIcon.getText());

        if (!editWebHookUrl.getText().isEmpty() && webHookCheck != null) {
            editWebHookUrl.requestFocusInWindow();
            throw new ConfigurationException(webHookCheck, "Invalid WebHook URL");
        }
        if (!editUserIcon.getText().isEmpty() && userIconCheck != null) {
            editUserIcon.requestFocusInWindow();
            throw new ConfigurationException(userIconCheck, "Invalid User Icon URL");
        }
        if (editUserName.getText().trim().length() > 80) {
            editUserName.requestFocusInWindow();
            throw new ConfigurationException("Your custom name is too long. A maximum of 80 characters is allowed.", "Custom Name");
        }

        DiscordSettingsState settingsState = DiscordSettings.getInstance().getState();

        settingsState.setShareService(getSelectedShareService());
        settingsState.setDontAskForService(checkDontAskForService.isSelected());
        settingsState.setWebhookUrl(editWebHookUrl.getText());
        settingsState.setUserName(editUserName.getText());
        settingsState.setUserIcon(editUserIcon.getText());

        if (settingsState.getUserName().isEmpty()) {
            settingsState.setUserName(DiscordSettingsState.DEFAULT_USER_NAME);
            reset();
        }
        if (settingsState.getUserIcon().isEmpty()) {
            settingsState.setUserIcon(DiscordSettingsState.DEFAULT_USER_ICON);
            reset();
        }
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(disposable);
    }
}
