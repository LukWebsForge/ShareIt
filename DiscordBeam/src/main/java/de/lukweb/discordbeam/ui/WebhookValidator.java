package de.lukweb.discordbeam.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ValidationInfo;
import de.lukweb.share.UrlValidator;

import javax.swing.text.JTextComponent;

public class WebhookValidator extends UrlValidator {

    private static final String WEBHOOK_URL_START = "https://discord.com/api/webhooks/";

    public WebhookValidator(JTextComponent textComponent) {
        super(textComponent);
    }

    @Override
    protected ValidationInfo validateUrl(String url) {
        ValidationInfo superValidation = super.validateUrl(url);
        if (superValidation != null) {
            return superValidation;
        }

        if (!url.startsWith(WEBHOOK_URL_START)) {
            String message = "A Discord webhook should start with '" + WEBHOOK_URL_START + "'";
            return new ValidationInfo(message, textComponent).asWarning();
        }

        return null;
    }

    public static void installOn(Disposable disposable, JTextComponent textComponent) {
        UrlValidator.installOn(disposable, new WebhookValidator(textComponent), textComponent);
    }
}
