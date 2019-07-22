package de.lukweb.discordbeam;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;
import de.lukweb.discordbeam.ui.JTextFieldLimit;
import de.lukweb.share.ShareWebTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class DiscordSettingsPage implements SearchableConfigurable {

    private DiscordSettings settings;

    private JPanel panelSettings;
    private JPanel panelDiscord;
    private JPanel panelLongCode;
    private JTextField editWebHookUrl;
    private JBTextField editCustomName;
    private JLabel labelCustomNameCount;
    private JRadioButton radioDiscordFile;
    private JRadioButton radioGist;
    private JCheckBox checkDontAskForService;
    private JLabel labelCustomName;
    private ButtonGroup shareVia;

    public DiscordSettingsPage(DiscordSettings settings) {
        this.settings = settings;
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
        panelLongCode.setBorder(IdeBorderFactory.createTitledBorder("Share long code via..."));

        labelCustomName.setLabelFor(editCustomName);

        editCustomName.getEmptyText().setText(DiscordSettingsState.DEFAULT_CUSTOM_NAME);
        editCustomName.setTextToTriggerEmptyTextStatus(DiscordSettingsState.DEFAULT_CUSTOM_NAME);
        editCustomName.setDocument(new JTextFieldLimit(80));
        editCustomName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateCustomNameCounter();
            }
        });

        LargeShareService.applyGistNotAvailable(radioGist);

        reset();
        updateCustomNameCounter();

        return panelSettings;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return editWebHookUrl;
    }

    private void updateCustomNameCounter() {
        int length = editCustomName.getText().length();
        String lengthString = Integer.toString(length);

        if (lengthString.length() < 2) {
            lengthString = "0" + lengthString;
        }

        labelCustomNameCount.setText(lengthString + " / 80");
    }

    private LargeShareService getSelectedShareService() {
        if (radioGist.isSelected() && LargeShareService.GITHUB_GIST.isAvailable()) {
            return LargeShareService.GITHUB_GIST;
        } else {
            return LargeShareService.DISCORD_FILE;
        }
    }

    @Override
    public boolean isModified() {
        DiscordSettingsState settingsState = settings.getState();

        if (isModified(checkDontAskForService, settingsState.isDontAskForService())) {
            return true;
        }

        if (getSelectedShareService() != settingsState.getShareService()) {
            return true;
        }

        if (isModified(editWebHookUrl, settingsState.getWebhookUrl())) {
            return true;
        }

        if (isModified(editCustomName, settingsState.getCustomName())) {
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        DiscordSettingsState settingsState = settings.getState();

        editWebHookUrl.setText(settingsState.getWebhookUrl());
        editCustomName.setText(settingsState.getCustomName());
        checkDontAskForService.setSelected(settingsState.isDontAskForService());
        if (settingsState.getShareService() == LargeShareService.GITHUB_GIST && LargeShareService.GITHUB_GIST.isAvailable()) {
            shareVia.setSelected(radioGist.getModel(), true);
        } else {
            shareVia.setSelected(radioDiscordFile.getModel(), true);
        }
    }

    @Override
    public void apply() throws ConfigurationException {

        String urlCheck = ShareWebTools.checkUrl(editWebHookUrl.getText());
        if (urlCheck != null) {
            editWebHookUrl.requestFocusInWindow();
            throw new ConfigurationException(urlCheck, "Invalid Webhhok Url");
        }
        if (editCustomName.getText().trim().length() > 80) {
            editCustomName.requestFocusInWindow();
            throw new ConfigurationException("Your custom name is too long. A maximum of 80 characters is allowed.", "Custom Name");
        }

        DiscordSettingsState settingsState = settings.getState();

        settingsState.setShareService(getSelectedShareService());
        settingsState.setDontAskForService(checkDontAskForService.isSelected());
        settingsState.setWebhookUrl(editWebHookUrl.getText());
        settingsState.setCustomName(editCustomName.getText());

        if (settingsState.getCustomName().isEmpty()) {
            settingsState.setCustomName(DiscordSettingsState.DEFAULT_CUSTOM_NAME);
            reset();
        }
    }
}
