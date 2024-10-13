package de.sebaty5.penandpaperbot.commandModule;

import net.dv8tion.jda.api.interactions.commands.OptionType;
public record CommandOption(OptionType type, String name, String description, boolean required) {
    public CommandOption(OptionType type, String name, String description) {
        this(type, name, description, true);
    }
}