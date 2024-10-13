package de.sebaty5.penandpaperbot.commandModule.Commands;

import de.sebaty5.penandpaperbot.commandModule.Command;
import de.sebaty5.penandpaperbot.configModule.Localization;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PingCommand implements Command {
    @Override
    public SlashCommandData getData() {
        return Commands.slash("ping", Localization.translate("cmd.ping.desc"));
    }
    @Override
    public void onEvent(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(false).flatMap(v -> event.getHook().editOriginalFormat(
                "Pong: %d ms", System.currentTimeMillis() - time)).queue();
    }
}
