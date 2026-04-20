package me.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandManager extends ListenerAdapter {

    private Dotenv config;
    private final Set<String> group_id = new HashSet<>();

    // "/grp" command to create channels
    @Override
public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    config = Dotenv.configure().load();
    String command = event.getName();

    OptionMapping groupName = event.getOption("channelname");
    if (groupName == null) {
        event.reply("❌ You must provide a channel name.").setEphemeral(true).queue();
        return;
    }

    String nameChannel = groupName.getAsString();

    // Check if command /grp was called
    if (command.equals("grp")) {
        // Create the category first
        event.getGuild().createCategory(nameChannel + " kanaler").queue(category -> {

            // Create text channel inside category
            event.getGuild().createTextChannel(nameChannel)
                .setParent(category)
                .queue(newChannel -> {
                    group_id.add(newChannel.getId());
                });

            // Create voice channel inside category
            event.getGuild().createVoiceChannel(nameChannel + " voice")
                .setParent(category)
                .queue();

            // Confirm after everything queued
            event.reply("✅ Created category and channels for **" + nameChannel + "**").queue();
        });
    }
}


    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        //Create command "grp"
        commandData.add(Commands.slash("grp", "Creates a voice and text channel").addOption(OptionType.STRING, "channelname",
                "The name of your channel", true));
        // add commands from the list "commandData" to the serverbot
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
