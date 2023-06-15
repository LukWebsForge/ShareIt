package de.lukweb.share;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
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
            return "The URL has to start with http:// or https://";
        }

        if (url.contains(" ")) {
            return "The URL may not contain spaces";
        }

        try {
            URL javaUrl = new URI(url).toURL();
            javaUrl.toURI();
        } catch (URISyntaxException | MalformedURLException ex) {
            return ex.getMessage();
        }

        return null;
    }
}
