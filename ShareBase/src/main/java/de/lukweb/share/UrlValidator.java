package de.lukweb.share;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

public class UrlValidator {

    protected final JTextComponent textComponent;

    public UrlValidator(JTextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public ValidationInfo validate() {
        return validateUrl(textComponent.getText().trim());
    }

    protected ValidationInfo validateUrl(String url) {
        if (url.isEmpty()) {
            return null;
        }

        String urlCheck = ShareWebTools.checkUrl(url);

        if (urlCheck != null) {
            return new ValidationInfo(urlCheck, textComponent);
        }

        return null;
    }

    public static void installOn(Disposable disposable, JTextComponent textComponent) {
        installOn(disposable, new UrlValidator(textComponent), textComponent);
    }

    protected static void installOn(Disposable disposable, UrlValidator validator, JTextComponent textComponent) {
        new ComponentValidator(disposable)
                .withValidator(validator::validate)
                .installOn(textComponent);

        DocumentAdapter listener = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ComponentValidator.getInstance(textComponent).ifPresent(ComponentValidator::revalidate);
            }
        };

        textComponent.getDocument().addDocumentListener(listener);
        Disposer.register(disposable, () -> {
            if (textComponent.getDocument() != null) {
                textComponent.getDocument().removeDocumentListener(listener);
            }
        });
    }
}
