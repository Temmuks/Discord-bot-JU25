package me.tnkcodes.listeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class onJoin extends ListenerAdapter {
    private Dotenv config;

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        config = Dotenv.configure().load();
        User user = (User) event.getMember().getUser();
        String botchannel = config.get("BOTCHANNEL");
        String channel = event.getChannelJoined().getAsMention();
        String joinMessage = user.getAsMention() + " Joined " + channel + "!";
        ((MessageChannel) event.getGuild().getTextChannelById(botchannel)).sendMessage(joinMessage).queue();
    }
}
