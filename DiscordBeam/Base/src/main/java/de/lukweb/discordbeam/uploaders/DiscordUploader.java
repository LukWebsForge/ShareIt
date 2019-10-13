package de.lukweb.discordbeam.uploaders;

import com.intellij.openapi.components.ServiceManager;
import de.lukweb.discordbeam.DiscordSettings;
import de.lukweb.share.ShareResult;
import okhttp3.*;

import java.io.IOException;
import java.net.URLConnection;
import java.util.function.Function;

public class DiscordUploader {

    // https://square.github.io/okhttp/recipes/#posting-a-multipart-request
    // https://discordapp.com/developers/docs/resources/webhook#execute-webhook

    private static final String AVATAR_URL = "https://raw.githubusercontent.com/LukWebsForge/HasteIt/master/img/discordbeam%4030x.png";

    public static DiscordUploader getInstance() {
        return ServiceManager.getService(DiscordUploader.class);
    }

    private final OkHttpClient client = new OkHttpClient();
    private DiscordSettings settings;

    public DiscordUploader(DiscordSettings settings) {
        this.settings = settings;
    }

    public void uploadText(String text, ShareResult result) {
        post((builder -> builder.addFormDataPart("content", text)), result);
    }

    public void uploadCode(String text, String extension, ShareResult result) {
        uploadText("```" + extension + "\n" + text + "\n```", result);
    }

    public void uploadFile(byte[] content, String fileName, ShareResult result) {
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        MediaType mediaType = MediaType.parse(contentType);

        post((builder -> {
            return builder.addFormDataPart("file", fileName, RequestBody.create(content, mediaType));
        }), result);
    }

    private void post(Function<MultipartBody.Builder, MultipartBody.Builder> postEditor, ShareResult result) {
        MultipartBody.Builder postBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", settings.getState().getCustomName())
                .addFormDataPart("avatar_url", AVATAR_URL);

        postBodyBuilder = postEditor.apply(postBodyBuilder);
        MultipartBody postBody = postBodyBuilder.build();

        Request request = new Request.Builder()
                .url(settings.getState().getWebhookUrl())
                .post(postBody)
                .build();

        send(request, result);
    }

    private void send(Request request, ShareResult result) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            result.onSuccess();
        } catch (IOException ex) {
            result.onFailure(ex);
        }
    }

}
