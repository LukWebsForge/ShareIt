package de.lukweb.hasteit;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.lukweb.share.ShareMenu;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HasteMenu extends ShareMenu {

    private static final String NOTIFICATION_ID = "Haste It";

    public HasteMenu() {
        super(NOTIFICATION_ID, false);
    }

    @Override
    protected void uploadText(String text, VirtualFile file, AnActionEvent event) {
        String fileName = file.getName();
        String fileExtension = file.getExtension();
        Project project = getEventProject(event);
        startUploadTask("Uploading to Hastebin", event.getProject(), (indicator, backgroundable) -> {
            HasteUploader.getInstance().upload(text, fileExtension, new HasteUploader.HasteResult() {
                @Override
                public void onHaste(String hasteUrl) {
                    copyToClipboard(hasteUrl);
                    uploadSuccessNotification(hasteUrl, fileName, project);
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

    private void uploadSuccessNotification(String url, String filename, Project project) {
        Notification notification = getNotificationGroup().createNotification(
                filename + " successfully uploaded to hastebin. The link has been copied to your clipboard.",
                NotificationType.INFORMATION
        );
        notification.addAction(NotificationAction.createSimple("Open in Browser", () -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                errorNotification("Unable to open the URL: " + e.getClass().getName() + " " + e.getMessage());
            }
        }));
        notification.notify(project);
    }


    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}
