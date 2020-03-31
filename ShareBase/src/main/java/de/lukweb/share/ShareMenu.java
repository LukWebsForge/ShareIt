package de.lukweb.share;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public abstract class ShareMenu extends AnAction {

    private NotificationGroup notificationGroup;
    private boolean allowAllFiles;

    public ShareMenu(String notificationId, boolean allowAllFiles) {
        this.notificationGroup =
                new NotificationGroup(notificationId, NotificationDisplayType.BALLOON, false);
        this.allowAllFiles = allowAllFiles;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {

        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        if (virtualFile == null) {
            event.getPresentation().setEnabledAndVisible(false);
            return;
        }

        if (!allowAllFiles && virtualFile.getFileType().isBinary()) {
            event.getPresentation().setEnabledAndVisible(false);
            return;
        }

        event.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        if (virtualFile == null) {
            // This should never occur, because the menu entry is hidden @update
            errorNotification("Please select a file");
            return;
        }

        if (virtualFile.isDirectory()) {
            errorNotification("Sorry, but you can't upload directories");
            return;
        }

        if (virtualFile.getFileType().isBinary() && allowAllFiles) {
            uploadFile(virtualFile, event);
            return;
        }

        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        //System.out.println("Editor is " + editor + " virtual file is " + virtualFile);
        String text = null;

        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();

            if (selectionModel.hasSelection()) {
                text = selectionModel.getSelectedText();
            } else {
                text = editor.getDocument().getText();
            }

        } else {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                text = document.getText();
            }
        }

        if (text == null || text.equalsIgnoreCase("")) {
            errorNotification("Please select text to upload");
            return;
        }

        uploadText(text, virtualFile, event);
    }

    protected abstract void uploadText(String text, VirtualFile file, AnActionEvent event);

    protected abstract void uploadFile(VirtualFile file, AnActionEvent event);

    protected void startUploadTask(String taskTitle, Project project, BiConsumer<ProgressIndicator, Task.Backgroundable> task) {
        new Task.Backgroundable(project, taskTitle) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                task.accept(indicator, this);
            }
        }.queue();
    }

    protected NotificationGroup getNotificationGroup() {
        return notificationGroup;
    }

    protected void infoNotification(String text) {
        showNotification(text, NotificationType.INFORMATION);
    }

    protected void warningNotification(String text) {
        showNotification(text, NotificationType.WARNING);
    }

    protected void errorNotification(String text) {
        showNotification(text, NotificationType.ERROR);
    }

    private void showNotification(String text, NotificationType type) {
        notificationGroup.createNotification(text, type).notify(null);
    }

}
