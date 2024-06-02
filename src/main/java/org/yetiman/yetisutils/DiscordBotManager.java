package org.yetiman.yetisutils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import javax.security.auth.login.LoginException;

public class DiscordBotManager {
    private final String token;
    private final String channelId;
    private JDA jda;

    public DiscordBotManager(String token, String channelId) {
        this.token = token;
        this.channelId = channelId;
    }

    public void startBot() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(token)
                .build();
        jda.awaitReady();
    }

    public void sendMessage(String message) {
        if (jda != null) {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            }
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }
}
