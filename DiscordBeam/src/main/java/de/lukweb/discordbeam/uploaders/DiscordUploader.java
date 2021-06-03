package de.lukweb.discordbeam.uploaders;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ApplicationManager;
import de.lukweb.discordbeam.DiscordSettings;
import de.lukweb.discordbeam.DiscordSettingsState;
import de.lukweb.discordbeam.FileExtensionMarkdownLanguage;
import de.lukweb.share.ShareResult;
import okhttp3.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class DiscordUploader {

    // https://square.github.io/okhttp/recipes/#posting-a-multipart-request
    // https://discord.com/developers/docs/resources/webhook#execute-webhook

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    // https://github.githubassets.com/pinned-octocat.svg
    private static final String ICON_DISCORDBEAM = "https://raw.githubusercontent.com/LukWebsForge/ShareIt/master/img/discordbeam%4030x.png";
    private static final String ICON_HASTEIT = "https://raw.githubusercontent.com/LukWebsForge/ShareIt/master/img/hasteit%4075.png";
    private static final String IMAGE_GITHUB_GIST = "https://github.githubassets.com/images/modules/gists/gist-og-image.png";
    private static final int COLOR_BLUE_CRAYOLA = 0x1F75FE;
    private static final int COLOR_BLACK_PEARL = 0x1E2327;

    public static DiscordUploader getInstance() {
        return ApplicationManager.getApplication().getService(DiscordUploader.class);
    }

    private final OkHttpClient client;
    private final Gson gson;

    public DiscordUploader() {
        client = new OkHttpClient();
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private void applyAuthorAndFooter(DiscordEmbed embed) {
        DiscordSettings settings = DiscordSettings.getInstance();

        DiscordSettingsState state = settings.getState();
        embed.setAuthor(new DiscordEmbedAuthor(state.getUserName(), null, state.getUserIcon()));
        embed.setFooter(new DiscordEmbedFooter("DiscordBeam for IDEA-based IDEs", ICON_DISCORDBEAM));
    }

    public void uploadGistEmbed(String fileName, String url, long timestamp, ShareResult result) {
        // GitHub already provides an embed for the gist.
        // We're creating a similar one with more information

        DiscordEmbed embed = new DiscordEmbed();
        embed.setTitle(fileName);
        embed.setDescription("View the Gist of " + fileName + " on GitHub");
        embed.setUrl(url);
        embed.setTimestamp(timestamp);
        embed.setColor(COLOR_BLACK_PEARL);
        embed.setImage(new DiscordEmbedImage(IMAGE_GITHUB_GIST, null, null));
        applyAuthorAndFooter(embed);

        postJson(embed, result);
    }

    public void uploadHasteEmbed(String fileName, String url, long timestamp, ShareResult result) {
        DiscordEmbed embed = new DiscordEmbed();
        embed.setTitle(fileName);
        embed.setDescription("View " + fileName + " on hastebin");
        embed.setUrl(url);
        embed.setTimestamp(timestamp);
        embed.setColor(COLOR_BLUE_CRAYOLA);
        embed.setThumbnail(new DiscordEmbedImage(ICON_HASTEIT, null, null));
        applyAuthorAndFooter(embed);

        postJson(embed, result);
    }

    public String buildEmbedCode(String text, String extension) {
        String markdownLanguage = extension;
        FileExtensionMarkdownLanguage language = FileExtensionMarkdownLanguage.getByExtension(extension);
        if (language != null) {
            markdownLanguage = language.getLanguage();
        }

        return "```" + markdownLanguage + "\n" + text + "\n```";
    }

    public void uploadCode(String text, String fileName, String extension, long timestamp, ShareResult result) {
        String codeInMd = buildEmbedCode(text, extension);

        DiscordEmbed embed = new DiscordEmbed();
        embed.setColor(COLOR_BLUE_CRAYOLA);
        embed.setTimestamp(timestamp);
        embed.setFields(Collections.singletonList(new DiscordEmbedField(fileName, codeInMd, false)));
        applyAuthorAndFooter(embed);

        postJson(embed, result);
    }

    public void uploadFile(byte[] content, String fileName, long timestamp, ShareResult result) {
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody requestBody = RequestBody.create(content, mediaType);

        DiscordEmbed embed = new DiscordEmbed();
        embed.setColor(COLOR_BLUE_CRAYOLA);
        embed.setTitle(fileName);
        embed.setTimestamp(timestamp);
        embed.setDescription("View the file " + fileName + " on Discord");
        applyAuthorAndFooter(embed);

        DiscordWebhook webhook = new DiscordWebhook();
        webhook.setEmbeds(Collections.singletonList(embed));

        postForm((builder -> builder
                .addFormDataPart("file", fileName, requestBody)
                .addFormDataPart("payload_json", gson.toJson(webhook))
        ), result);
    }

    private void postForm(Function<MultipartBody.Builder, MultipartBody.Builder> postEditor, ShareResult result) {
        DiscordSettings settings = DiscordSettings.getInstance();

        MultipartBody.Builder postBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        MultipartBody postBody = postEditor.apply(postBodyBuilder).build();

        Request request = new Request.Builder()
                .url(settings.getState().getWebhookUrl())
                .post(postBody)
                .build();

        send(request, result);
    }

    private void postJson(DiscordEmbed embed, ShareResult result) {
        DiscordWebhook webhook = new DiscordWebhook();
        webhook.setEmbeds(Collections.singletonList(embed));

        postJson(webhook, result);
    }

    private void postJson(DiscordWebhook webhook, ShareResult result) {
        Request request = new Request.Builder()
                .url(DiscordSettings.getInstance().getState().getWebhookUrl())
                .post(RequestBody.create(gson.toJson(webhook), JSON_MEDIA_TYPE))
                .build();

        send(request, result);
    }

    private void send(Request request, ShareResult result) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    LoggerFactory.getLogger(getClass()).warn(body.string());
                }
                throw new IOException("Unexpected code " + response);
            }

            result.onSuccess();
        } catch (IOException ex) {
            result.onFailure(ex);
        }
    }

    public static class DiscordWebhook {
        private String content;
        private String username;
        private String avatarUrl;
        private List<DiscordEmbed> embeds;

        public DiscordWebhook() {

        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setEmbeds(List<DiscordEmbed> embeds) {
            this.embeds = embeds;
        }
    }

    public static class DiscordEmbed {
        private String title;
        private String type;
        private String description;
        private String url;
        private String timestamp; // ISO8601
        private int color; // RGB value as int
        private DiscordEmbedAuthor author;
        private DiscordEmbedImage image;
        private DiscordEmbedImage thumbnail;
        private List<DiscordEmbedField> fields;
        private DiscordEmbedFooter footer;

        public DiscordEmbed() {
            this.type = "rich";
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = Instant.ofEpochSecond(timestamp)
                    .atZone(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT);
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setAuthor(DiscordEmbedAuthor author) {
            this.author = author;
        }

        public void setImage(DiscordEmbedImage image) {
            this.image = image;
        }

        public void setThumbnail(DiscordEmbedImage thumbnail) {
            this.thumbnail = thumbnail;
        }

        public void setFields(List<DiscordEmbedField> fields) {
            this.fields = fields;
        }

        public void setFooter(DiscordEmbedFooter footer) {
            this.footer = footer;
        }
    }

    public static class DiscordEmbedAuthor {
        private String name;
        private String url;
        private String iconUrl;

        DiscordEmbedAuthor() {

        }

        public DiscordEmbedAuthor(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }
    }

    public static class DiscordEmbedImage {
        private String url;
        private Integer height;
        private Integer width;

        DiscordEmbedImage() {

        }

        public DiscordEmbedImage(String url, Integer height, Integer width) {
            this.url = url;
            this.height = height;
            this.width = width;
        }
    }

    public static class DiscordEmbedField {
        private String name;
        private String value;
        private boolean inline;

        DiscordEmbedField() {

        }

        public DiscordEmbedField(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }

    public static class DiscordEmbedFooter {
        private String text;
        private String iconUrl;

        DiscordEmbedFooter() {

        }

        public DiscordEmbedFooter(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }
    }

}
