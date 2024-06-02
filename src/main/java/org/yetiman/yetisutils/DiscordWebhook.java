import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
    private final String webhookUrl;
    private String content;

    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void execute() throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String jsonPayload = "{\"content\": \"" + content + "\"}";
        byte[] output = jsonPayload.getBytes(StandardCharsets.UTF_8);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(output, 0, output.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 204) {
            throw new IOException("Failed to send Discord notification, response code: " + responseCode);
        }
    }
}
