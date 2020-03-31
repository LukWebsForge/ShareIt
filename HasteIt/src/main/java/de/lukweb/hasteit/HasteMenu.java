package de.lukweb.hasteit;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import de.lukweb.share.ShareMenu;

import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class HasteMenu extends ShareMenu {

    private HasteUploader uploader;

    public HasteMenu() {
        super("HasteIt", false);
        uploader = HasteUploader.getInstance();
    }

    @Override
    protected void uploadText(String text, VirtualFile file, AnActionEvent event) {
        startUploadTask("Uploading to Hastebin", event.getProject(), (indicator, backgroundable) -> {
            uploader.upload(text, file.getExtension(), new HasteUploader.HasteResult() {
                @Override
                public void onHaste(String hasteUrl) {
                    copyToClipboard(hasteUrl);
                    notifySuccess(hasteUrl);
                }

                @Override
                public void onFailure(Throwable ex) {
                    errorNotification(ex.getClass().getName() + " while uploading: " + ex.getMessage());
                }
            });
        });
    }

    @Override
    protected void uploadFile(VirtualFile file, AnActionEvent event) {
        // This won't be called, because we've set allowAllFiles in the constructor to false
    }

    private void notifySuccess(String url) {
        getNotificationGroup().createNotification(
                "HasteIt",
                "Upload successful, copied to clipboard<br/> <a href=\"" + url + "\">Open in Browser</a> ",
                NotificationType.INFORMATION,
                (notification, hyperlinkEvent) -> {
                    if (!hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) return;
                    openURL(hyperlinkEvent.getURL());
                }

        ).notify(null);
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
