package de.lukweb.discordbeam;

import com.intellij.openapi.ui.DialogWrapper;
import de.lukweb.discordbeam.uploaders.LargeShareService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ShareAsFileDialog extends DialogWrapper {

    private JPanel centerPanel;
    private JLabel labelLongerThan;
    private JPanel panelRadio;
    private ButtonGroup shareGroup;
    private JRadioButton radioDisFile;
    private JRadioButton radioGist;
    private JCheckBox buttonAlways;
    private JRadioButton radioHaste;

    public ShareAsFileDialog(LargeShareService service) {
        super(true);

        setOKButtonText("Upload");
        setTitle("Upload as File or as Github Gist?");

        labelLongerThan.setText(
                "<html>The code you've selected is longer than " + DiscordMenu.MAX_TEXT_LENGTH + " characters." +
                        "<br> It can't be sent as a Discord message.</html>"
        );

        LargeShareService.applyGistNotAvailable(radioGist);
        LargeShareService.applyHasteStatus(radioHaste);

        if (service == LargeShareService.GITHUB_GIST && LargeShareService.GITHUB_GIST.isAvailable()) {
            shareGroup.setSelected(radioGist.getModel(), true);
        } else if (service == LargeShareService.HASTEBIN && LargeShareService.HASTEBIN.isAvailable()) {
            shareGroup.setSelected(radioHaste.getModel(), true);
        } else {
            shareGroup.setSelected(radioDisFile.getModel(), true);
        }

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return centerPanel;
    }

    public LargeShareService getShareService() {
        if (radioDisFile.isSelected()) {
            return LargeShareService.DISCORD_FILE;
        } else if (radioGist.isSelected()) {
            return LargeShareService.GITHUB_GIST;
        } else if (radioHaste.isSelected()) {
            return LargeShareService.HASTEBIN;
        } else {
            return LargeShareService.DISCORD_FILE;
        }
    }

    public boolean isNeverAskAgain() {
        return buttonAlways.isSelected();
    }

}
