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
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

public class MenuHaste extends AnAction {

    private static final NotificationGroup notificationGroup = new NotificationGroup("HasteIt", NotificationDisplayType.BALLOON, false);

    @Override
    public void actionPerformed(AnActionEvent event) {

        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        if (psiFile == null) {
            err("No file is selected!");
            return;
        }

        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        if (editor == null) {
            err("There's no editor!");
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();

        if (!selectionModel.hasSelection()) {
            err("No text is selected!");
            return;
        }

        String text = selectionModel.getSelectedText();

        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(), "Uploading to Hastebin") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                String hasteCode = saveTextToHastebin(text);

                if (hasteCode == null) {
                    progressIndicator.cancel();
                    return;
                }

                String fullHastebinUrl = "http://hastebin.com/" + hasteCode + "." + psiFile.getFileType().getDefaultExtension();

                copyToClipboard(fullHastebinUrl);

                notificationGroup.createNotification(
                        "HasteIt",
                        "Upload successful! Copied to clipboard! <a href=\"" + fullHastebinUrl + "\">Open in Browser</a> ",
                        NotificationType.INFORMATION,
                        (notification, hyperlinkEvent) -> {
                            if (!hyperlinkEvent.getEventType().equals(EventType.ACTIVATED)) return;
                            openURL(hyperlinkEvent.getURL());
                        }

                ).notify(null);

                progressIndicator.cancel();
            }
        });

    }

    private void show(String text) {
        show(text, NotificationType.INFORMATION);
    }

    private void err(String text) {
        show(text, NotificationType.ERROR);
    }

    private void show(String text, NotificationType type) {
        notificationGroup.createNotification(text, type).notify(null);
    }

    private String saveTextToHastebin(String text) {
        try {
            String url = "http://www.hastebin.com/documents";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(text);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result

            String[] split = response.toString().split(":");
            return split[1].replaceAll("}", "").replace("{", "").replaceAll("\"", "");

        } catch (IOException e) {
            err("Error while uploading: " + e.getLocalizedMessage());
        }
        return null;
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
