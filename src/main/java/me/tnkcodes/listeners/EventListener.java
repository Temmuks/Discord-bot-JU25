package me.tnkcodes.listeners;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {

    private Dotenv config;

    // @Override
    // public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
    // config = Dotenv.configure().load();
    // User user = event.getUser();

    // String emoji = event.getReaction().getEmoji().getAsReactionCode();
    // String channelMention = event.getChannel().getAsMention();
    // String jumpLink = event.getJumpUrl();
    // String botchannel = config.get("BOTCHANNEL");
    // String message = user.getAsMention() + " reacted to a message with " + emoji
    // + "in the " + channelMention
    // + " channel!";
    // // Sends message in bot-spam
    // ((MessageChannel)
    // event.getGuild().getTextChannelById(botchannel)).sendMessage(message).queue();
    // }

    private final Set<String> tempChannels = new HashSet<>();

    @Override // Eventlistener to create a channel if "HUB" is joined!
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        config = Dotenv.configure().load();
        String HUB_CHANNEL_ID = config.get("HUB_CHANNEL_ID");
        String CATEGORY_ID = config.get("CATEGORY_ID");

        Member member = event.getMember();

        // Check if user joined a channel
        AudioChannel joinedChannel = event.getChannelJoined();
        AudioChannel leftChannel = event.getChannelLeft();

        // --- Case 1: User joins the hub channel ---
        if (joinedChannel != null && joinedChannel.getId().equals(HUB_CHANNEL_ID)) {
            event.getGuild().createVoiceChannel(member.getEffectiveName() + "'s Channel")
                    .setParent(event.getGuild().getCategoryById(CATEGORY_ID))
                    .queue(newChannel -> {
                        tempChannels.add(newChannel.getId());
                        event.getGuild().moveVoiceMember(member, newChannel).queue();
                    });
        }

        // --- Case 2: User leaves a channel (check if it’s a temp one) ---
        if (leftChannel != null && tempChannels.contains(leftChannel.getId())) {
            // If channel is now empty, delete it
            if (leftChannel.getMembers().isEmpty()) {
                leftChannel.delete().queue(success -> {
                    tempChannels.remove(leftChannel.getId());
                });
            }
        }
    }

}
