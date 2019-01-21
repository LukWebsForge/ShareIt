package de.lukweb.hasteit;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class HasteSettingsPage implements Configurable {

    private HasteSettings settings;

    private JPanel panelMain;
    private JCheckBox checkUseCustomHaste;
    private JLabel labelHasteUrl;
    private JTextField textHasteUrl;
    private JLabel labelUrlError;

    public HasteSettingsPage(HasteSettings settings) {
        this.settings = settings;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "HasteIt";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadFromSettings();

        checkUseCustomHaste.addActionListener(e -> checkEnableInput());
        textHasteUrl.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            private void onChange() {
                checkInputUrlCorrect();
            }
        });
        return panelMain;
    }

    @Override
    public boolean isModified() {
        String customBefore = settings.getCustomUrl();
        String customNow = checkUseCustomHaste.isSelected() ? textHasteUrl.getText() : null;

        if (customBefore == null && customNow == null) {
            return false;
        } else if (customNow == null) {
            return true;
        }

        if (customNow.equals(customBefore)) {
            return false;
        }

        if (customNow.isEmpty()) {
            return false;
        } else if (labelUrlError.isVisible()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void apply() {
        if (checkUseCustomHaste.isSelected()) {
            String url = textHasteUrl.getText();
            settings.setBaseUrl(url);
        } else {
            settings.setBaseUrl(null);
        }
    }

    @Override
    public void reset() {
        loadFromSettings();
    }

    private void loadFromSettings() {
        if (settings.getCustomUrl() != null) {
            checkUseCustomHaste.setSelected(true);
        } else {
            checkUseCustomHaste.setSelected(false);
        }
        labelUrlError.setVisible(false);
        textHasteUrl.setText(settings.getBaseUrl());
        checkEnableInput();
    }

    private void checkEnableInput() {
        if (checkUseCustomHaste.isSelected()) {
            labelHasteUrl.setEnabled(true);
            textHasteUrl.setEnabled(true);
        } else {
            labelHasteUrl.setEnabled(false);
            textHasteUrl.setEnabled(false);
        }
    }

    private void checkInputUrlCorrect() {
        String url = textHasteUrl.getText();
        String checkResult = settings.checkUrl(url);
        if (url.isEmpty() || checkResult == null || !checkUseCustomHaste.isSelected()) {
            labelUrlError.setVisible(false);
        } else {
            if (!labelUrlError.isVisible()) {
                labelUrlError.setVisible(true);
            }
            if (!labelUrlError.getText().equals(checkResult)) {
                labelUrlError.setText(checkResult);
            }
        }
    }

}
