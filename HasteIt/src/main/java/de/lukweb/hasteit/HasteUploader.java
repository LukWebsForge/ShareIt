package de.lukweb.hasteit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.intellij.openapi.application.ApplicationManager;
import de.lukweb.share.ShareResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HasteUploader {

    public static HasteUploader getInstance() {
        return ApplicationManager.getApplication().getService(HasteUploader.class);
    }

    public void upload(String content, String extension, HasteResult result) {
        HasteSettings settings = HasteSettings.getInstance();

        try {
            URL obj = new URI(settings.computeUploadURL()).toURL();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Add a request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            if (settings.getAPIKey() != null) {
                con.setRequestProperty("Authorization", "Bearer " + settings.getAPIKey());
            }

            // Send the post request
            con.setDoOutput(true);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
            wr.write(content);
            wr.flush();
            wr.close();

            if (con.getResponseCode() == 401) {
                result.onAuthorizationRequired();
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            // Get the id of the haste
            JsonElement json = JsonParser.parseString(response.toString());
            if (!json.isJsonObject()) {
                throw new IOException("Can't parse JSON");
            }

            String hasteCode = json.getAsJsonObject().get("key").getAsString();
            result.onHaste(settings.computeFileURL(hasteCode, extension));
            result.onSuccess();
        } catch (IOException | JsonParseException | URISyntaxException e) {
            result.onFailure(e);
        }
    }

    public interface HasteResult extends ShareResult {

        void onAuthorizationRequired();

        void onHaste(String hasteUrl);

    }

}
