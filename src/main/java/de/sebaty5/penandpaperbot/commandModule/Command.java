package de.sebaty5.penandpaperbot.commandModule;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
public interface Command {
    public String getName();
    public SlashCommandData getData();
    public void onEvent(SlashCommandInteractionEvent event);
}