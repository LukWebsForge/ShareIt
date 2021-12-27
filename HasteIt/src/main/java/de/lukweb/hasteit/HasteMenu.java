package de.lukweb.hasteit;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import de.lukweb.share.ShareMenu;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class HasteMenu extends ShareMenu {

    private static final String NOTIFICATION_ID = "Haste It";

    public HasteMenu() {
        super(NOTIFICATION_ID, false);
    }

    @Override
    protected void uploadText(String text, VirtualFile file, AnActionEvent event) {
        String fileExtension = file.getExtension();
        startUploadTask("Uploading to Hastebin", event.getProject(), (indicator, backgroundable) -> {
            HasteUploader.getInstance().upload(text, fileExtension, new HasteUploader.HasteResult() {
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
        Notification notification = getNotificationGroup().createNotification(
                "Upload successful, link copied to clipboard<br/> <a href=\"" + url + "\">Open in Browser</a> ",
                NotificationType.INFORMATION
        );
        notification.setListener(NotificationListener.URL_OPENING_LISTENER);
        notification.notify(null);
    }


    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}
