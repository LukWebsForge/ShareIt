package de.lukweb.share;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class ShareWebTools {

    public static void openURL(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static String checkUrl(String url) {
        url = url.trim().toLowerCase();
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            return "Your url must start with http:// or https://";
        }

        if (url.contains(" ")) {
            return "Your url may not contain spaces";
        }

        try {
            URL javaUrl = new URL(url);
            javaUrl.toURI();
        } catch (URISyntaxException | MalformedURLException ex) {
            return ex.getMessage();
        }

        return null;
    }
}
