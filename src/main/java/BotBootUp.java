import configModule.Config;
import configModule.Localization;
import logging.StandardLogger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import utility.ExitCode;

import java.io.IOException;

public class BotBootUp {
    private static final StandardLogger LOGGER = new StandardLogger("BotBootUp");
    public static final JDA JDA;

    static {
        LOGGER.log("Booting DnDBot...");

        String token = Config.INSTANCE.getAccessToken();
        if (token.isEmpty()){
            LOGGER.error("The AccessToken was not defined in config.json");
            System.exit(ExitCode.UNDEFINED_TOKEN.getCode());
        }

        try {
            Localization.load();
        } catch (IOException e) {
            LOGGER.error("Encountered Error while loading localization file");
            System.exit(ExitCode.UNDEFINED_TOKEN.getCode());
        }

        JDABuilder builder = JDABuilder.createDefault(token);

        builder.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS
        );

        builder.setActivity(Activity.watching(Localization.translate("activity.watching")));

        JDA = builder.build();
        try {
            JDA.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Config.INSTANCE.load(JDA);
    }
    public static void main(String[] args)
    {

    }
}
