package de.sebaty5.penandpaperbot.commandModule;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
public interface Command {
    SlashCommandData getData();
    void onEvent(SlashCommandInteractionEvent event);
}