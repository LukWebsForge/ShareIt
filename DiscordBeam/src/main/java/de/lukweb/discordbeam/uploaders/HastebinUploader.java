package de.lukweb.discordbeam.uploaders;

import java.io.IOException;
import java.lang.reflect.Modifier;

public interface HastebinUploader {

    String shareHaste(String content, String extension) throws IOException;

    static HastebinServiceStatus works() {
        try {
            Class<?> resultClass = Class.forName("de.lukweb.hasteit.HasteUploader$HasteResult");
            int mods = resultClass.getModifiers();
            if (Modifier.isPublic(mods)) {
                return HastebinServiceStatus.OK;
            } else {
                return HastebinServiceStatus.OUTDATED;
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return HastebinServiceStatus.NOT_INSTALLED;
        }
    }

    enum HastebinServiceStatus {
        OK(""),
        OUTDATED("Please update HasteIt to the latest version to share via hastebin"),
        NOT_INSTALLED("Please install the plugin HasteIt to share via hastebin"),
        ;

        private String infoText;

        HastebinServiceStatus(String infoText) {
            this.infoText = infoText;
        }

        public String getInfoText() {
            return infoText;
        }
    }

}
