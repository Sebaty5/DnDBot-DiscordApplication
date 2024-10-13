package de.sebaty5.penandpaperbot.commandModule;

import de.sebaty5.penandpaperbot.commandModule.Commands.CreateCampaignCommand;
import de.sebaty5.penandpaperbot.commandModule.Commands.PingCommand;
import de.sebaty5.penandpaperbot.configModule.Config;
import de.sebaty5.penandpaperbot.utility.ExitCode;
import de.sebaty5.penandpaperbot.configModule.Localization;
import de.sebaty5.penandpaperbot.logging.StandardLogger;

import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class CommandRegistry implements EventListener {
    private static final StandardLogger LOGGER = new StandardLogger("CommandRegistry");
    private static CommandRegistry INSTANCE;

    private final Map<String, Command> commands = new HashMap<>();
    private final JDA jda = Config.INSTANCE.getJDA();

    private CommandRegistry() {
        this.populate();
        this.sendUpdate();
        jda.addEventListener(this);
    }
    private void populate() {
        this.register(new PingCommand());
        this.register(new CreateCampaignCommand());
    }
    private void register(Command command) {
        this.commands.put(command.getData().getName(), command);
    }
    private void sendUpdate() {
        jda.updateCommands().addCommands(this.commands.values().stream().map(Command::getData).toList()).queue();
    }
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent slashEvent) {
            Command command = this.commands.get(slashEvent.getName());
            if (command != null) {
                command.onEvent(slashEvent);
            } else {
                slashEvent.reply(Localization.translate("msg.commandNotFound")).queue();
            }
        }
    }
    public static void initialize() {
        if(INSTANCE == null) {
            LOGGER.log("Initializing CommandRegistry...");
            INSTANCE = new CommandRegistry();
        } else {
            LOGGER.error("Trying to initialize CommandRegistry when it has already been registered.");
            System.exit(ExitCode.LOGIC_ERROR.getCode());
        }
        LOGGER.log("CommandRegistry initialization complete.");
    }
}
