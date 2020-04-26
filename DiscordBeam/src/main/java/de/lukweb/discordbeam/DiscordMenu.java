package de.lukweb.discordbeam;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.lukweb.discordbeam.uploaders.*;
import de.lukweb.share.ShareMenu;
import de.lukweb.share.ShareResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DiscordMenu extends ShareMenu {

    public static final int MAX_TEXT_LENGTH = 1950;
    public static final String TASK_TITLE = "Beaming to Discord";

    private final DiscordUploader uploader;
    private final DiscordSettings settings;

    public DiscordMenu() {
        super("DiscordBeam", true);
        this.uploader = DiscordUploader.getInstance();
        this.settings = DiscordSettings.getInstance();
    }

    @Override
    protected void uploadText(String text, VirtualFile file, AnActionEvent event) {

        if (!settings.isWebhookSet()) {
            EnterWebhookDialog dialog = new EnterWebhookDialog(settings.getState().getWebhookUrl());
            if (dialog.showAndGet()) {
                settings.getState().setWebhookUrl(dialog.getWebhookUrl());
            } else {
                return;
            }
        }

        LargeShareReason reason;
        DiscordSettingsState settingsState = settings.getState();

        // Discord doesn't allow to escape three backticks properly, so we have to use a file sharing service
        boolean isTextTooLong = text.length() > MAX_TEXT_LENGTH;
        boolean containsUnwantedChars = text.contains("```");
        if (isTextTooLong || containsUnwantedChars) {

            if (isTextTooLong) {
                reason = LargeShareReason.TEXT_TOO_LONG;
            } else {
                reason = LargeShareReason.FORBIDDEN_CHARACTERS;
            }

            if (!settingsState.isDontAskForService()) {
                ShareAsFileDialog dialog = new ShareAsFileDialog(settingsState.getShareService(), reason);

                if (!dialog.showAndGet()) {
                    return;
                }

                settingsState.setDontAskForService(dialog.isNeverAskAgain());
                settingsState.setShareService(dialog.getShareService());
            }
        } else {
            reason = null;
        }

        String fileName = file.getName();
        String fileExtension = file.getExtension();
        long timestamp = file.getTimeStamp() / 1000;
        startUploadTask(TASK_TITLE, event.getProject(), (indicator, backgroundable) -> {
            if (reason != null) {
                uploadLongText(text, fileName, fileExtension, timestamp,
                        settingsState.getShareService(), backgroundable.getProject());
            } else {
                uploader.uploadCode(text, fileName, fileExtension, timestamp, handleUploadResult());
            }
        });
    }

    private void uploadLongText(String text, String fileName, String fileExtension, long timestamp,
                                LargeShareService service, Project project) {
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

            uploader.uploadGistEmbed(fileName, gistUrl, timestamp, handleUploadResult());
        } else if (service == LargeShareService.HASTEBIN && service.isAvailable()) {
            HastebinUploader hasteUploader = ServiceManager.getService(HastebinUploader.class);

            String hasteUrl;
            try {
                hasteUrl = hasteUploader.shareHaste(text, fileExtension);
            } catch (IOException ex) {
                errorNotification(ex.getClass().getName() + " while uploading to hastebin: " + ex.getMessage());
                return;
            }

            uploader.uploadHasteEmbed(fileName, hasteUrl, timestamp, handleUploadResult());
        } else {
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            if (!checkFileSizeLimit(textBytes.length)) {
                return;
            }

            uploader.uploadFile(textBytes, fileName, timestamp, handleUploadResult());
        }
    }

    @Override
    protected void uploadFile(final VirtualFile file, final AnActionEvent event) {
        String fileName = file.getName();
        long fileLength = file.getLength();
        long timestamp = file.getTimeStamp();

        startUploadTask("Beaming to Discord", event.getProject(), (indicator, backgroundable) -> {
            if (!checkFileSizeLimit(fileLength)) {
                return;
            }

            try {
                byte[] fileContent = ReadAction.compute(file::contentsToByteArray);
                uploader.uploadFile(fileContent, fileName, timestamp, handleUploadResult());
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
                ex.printStackTrace();
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
