package de.lukweb.discordbeam;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import de.lukweb.discordbeam.ui.UrlValidator;
import de.lukweb.share.ShareWebTools;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

public class EnterWebhookDialog extends DialogWrapper {

    private URL supportUrl;
    private final UrlValidator validator;

    private JPanel centerPanel;
    private JTextField urlTextField;
    private JLabel infoLabel;

    public EnterWebhookDialog(String oldWebhookUrl) {
        super(true);

        try {
            supportUrl = new URL("https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        urlTextField.setText(oldWebhookUrl);
        validator = new UrlValidator(urlTextField, true);

        setTitle("Enter a Webhook Url");
        init();

        initValidation();
    }

    @Nullable
    @Override
    protected String getHelpId() {
        // The value is irrelevant, because we just want to show the help button
        return "discord-bla-bla";
    }

    @Override
    protected void doHelpAction() {
        ShareWebTools.openURL(supportUrl);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return centerPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return urlTextField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return validator.validate();
    }

    public String getWebhookUrl() {
        return urlTextField.getText();
    }

}
