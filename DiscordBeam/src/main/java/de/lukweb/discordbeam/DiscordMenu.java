package de.lukweb.discordbeam;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.lukweb.share.ShareMenu;
import de.lukweb.share.ShareResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordMenu extends ShareMenu {

    public static final int MAX_TEXT_LENGTH = 1950;
    public static final String TASK_TITLE = "Beaming to Discord";

    private DiscordUploader uploader;
    private DiscordSettings settings;

    public DiscordMenu() {
        super("DiscordBeam", true);
        this.uploader = DiscordUploader.getInstance();
        this.settings = DiscordSettings.getInstance();
    }

    @Override
    protected void uploadText(String text, VirtualFile file, AnActionEvent event) {

        if (!settings.isWebhookSet()) {
            if (!new EnterWebhookDialog(settings).showAndGet()) {
                return;
            }
        }

        AtomicBoolean tooBigForDiscord = new AtomicBoolean(false);
        DiscordSettingsState settingsState = settings.getState();

        if (text.length() > MAX_TEXT_LENGTH) {
            tooBigForDiscord.set(true);

            if (!settingsState.isDontAskForService()) {
                ShareAsFileDialog dialog = new ShareAsFileDialog(settingsState.getShareService());

                if (!dialog.showAndGet()) {
                    return;
                }

                settingsState.setDontAskForService(dialog.isNeverAskAgain());
                settingsState.setShareService(dialog.getShareService());
            }
        }

        startUploadTask(TASK_TITLE, event.getProject(), (indicator) -> {
            if (tooBigForDiscord.get()) {
                uploadLongText(text, file.getName(), settingsState.getShareService(), event.getProject());
            } else {
                uploader.uploadCode(text, file.getExtension(), handleUploadResult());
            }
        });
    }

    private void uploadLongText(String text, String fileName, LargeShareService service, Project project) {
        if (service == LargeShareService.GITHUB_GIST && service.isAvailable()) {
            GistUploader gistUploader = ServiceManager.getService(GistUploader.class);

            if (!gistUploader.isGithubConfigured()) {
                warningNotification("Please setup a GitHub account, go to Settings -> Version Control -> GitHub -> +");
                return;
            }

            String gistUrl;
            try {
                gistUrl = gistUploader.shareGist(project, fileName, text);
            } catch (IOException ex) {
                errorNotification(ex.getClass().getName() + " while uploading a Gist: " + ex.getMessage());
                return;
            }

            uploader.uploadText(gistUrl, handleUploadResult());
        } else {
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            if (!checkFileSizeLimit(textBytes.length)) {
                return;
            }

            uploader.uploadFile(textBytes, fileName, handleUploadResult());
        }
    }

    @Override
    protected void uploadFile(VirtualFile file, AnActionEvent event) {
        startUploadTask("Beaming to Discord", event.getProject(), (indicator) -> {
            if (!checkFileSizeLimit(file.getLength())) {
                return;
            }

            try {
                uploader.uploadFile(file.contentsToByteArray(), file.getName(), handleUploadResult());
            } catch (IOException ex) {
                handleUploadResult().onFailure(ex);
            }
        });
    }

    private ShareResult handleUploadResult() {
        return new ShareResult() {
            @Override
            public void onFailure(Throwable ex) {
                errorNotification(ex.getClass().getName() + " while uploading: " + ex.getMessage());
            }
        };
    }

    private boolean checkFileSizeLimit(long fileLength) {
        boolean tooBig = fileLength >= 1024 * 1024 * 8;
        if (tooBig) {
            errorNotification("You can only upload files up to 8MB");
            return false;
        }

        return true;
    }
}
