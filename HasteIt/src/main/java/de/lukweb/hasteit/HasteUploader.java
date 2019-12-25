package de.lukweb.hasteit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.ServiceManager;
import de.lukweb.share.ShareResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HasteUploader {

    public static HasteUploader getInstance() {
        return ServiceManager.getService(HasteUploader.class);
    }

    public void upload(String content, String extension, HasteResult result) {
        HasteSettings settings = HasteSettings.getInstance();

        try {
            URL obj = new URL(settings.getUploadUrl());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Add a reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send the post request
            con.setDoOutput(true);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
            wr.write(content);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            // Get the id of the haste
            JsonElement json = new JsonParser().parse(response.toString());
            if (!json.isJsonObject()) {
                throw new IOException("Can't parse JSON");
            }

            String hasteCode = json.getAsJsonObject().get("key").getAsString();
            result.onHaste(settings.getFileUrl(hasteCode, extension));
            result.onSuccess();
        } catch (IOException e) {
            result.onFailure(e);
        }
    }

    public interface HasteResult extends ShareResult {

        void onHaste(String hasteUrl);

    }

}
