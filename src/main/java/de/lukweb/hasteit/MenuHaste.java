package de.lukweb.hasteit;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent.EventType;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MenuHaste extends AnAction {

    private static final NotificationGroup notificationGroup =
            new NotificationGroup("HasteIt", NotificationDisplayType.BALLOON, false);

    private HasteUploader uploader;

    public MenuHaste() {
        super("Uploads your current selection to hastebin");
        this.uploader = HasteUploader.getInstance();
    }

    @Override
    public void actionPerformed(AnActionEvent event) {

        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        if (psiFile == null) {
            errorNotification("No file is selected!");
            return;
        }

        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        if (editor == null) {
            errorNotification("There's no editor!");
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();

        if (!selectionModel.hasSelection()) {
            errorNotification("No text is selected!");
            return;
        }

        String text = selectionModel.getSelectedText();

        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(), "Uploading to Hastebin") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                uploader.upload(text, psiFile.getFileType().getDefaultExtension(), new HasteUploader.Result() {
                    @Override
                    public void onSuccess(String hasteUrl) {
                        copyToClipboard(hasteUrl);
                        notifySuccess(hasteUrl);
                        indicator.cancel();
                    }

                    @Override
                    public void onFailture(IOException ex) {
                        errorNotification(ex.getClass().getName() + " while uploading: " + ex.getMessage());
                        indicator.cancel();
                    }
                });
            }
        });

    }

    private void notifySuccess(String url) {
        notificationGroup.createNotification(
                "HasteIt",
                "Upload successful! Copied to clipboard! <a href=\"" + url + "\">Open in Browser</a> ",
                NotificationType.INFORMATION,
                (notification, hyperlinkEvent) -> {
                    if (!hyperlinkEvent.getEventType().equals(EventType.ACTIVATED)) return;
                    openURL(hyperlinkEvent.getURL());
                }

        ).notify(null);
    }

    private void showNotification(String text) {
        showNotification(text, NotificationType.INFORMATION);
    }

    private void errorNotification(String text) {
        showNotification(text, NotificationType.ERROR);
    }

    private void showNotification(String text, NotificationType type) {
        notificationGroup.createNotification(text, type).notify(null);
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private void openURL(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

}
