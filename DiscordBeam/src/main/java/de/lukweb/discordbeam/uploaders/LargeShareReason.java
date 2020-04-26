package de.lukweb.discordbeam.uploaders;

import de.lukweb.discordbeam.DiscordMenu;

public enum LargeShareReason {

    TEXT_TOO_LONG,
    FORBIDDEN_CHARACTERS;

    private String getText() {
        switch (this) {
            case TEXT_TOO_LONG:
                return "The code you've selected is longer than " + DiscordMenu.MAX_TEXT_LENGTH + " characters." +
                        "<br> It can't be sent as a Discord message.";
            case FORBIDDEN_CHARACTERS:
                return "The code you've selected contains characters which can't be escaped.";
            default:
                return "The code you've selected, can't be sent as a Discord message";
        }
    }

    public String getHtmlText() {
        return "<html>" + getText() + "</html>";
    }
}
