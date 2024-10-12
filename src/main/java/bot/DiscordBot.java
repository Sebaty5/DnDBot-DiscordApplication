package bot;

import bot.events.*;
import bot.functions.Utility;
import configModule.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot
{/*
    public static JDA jda;
    public static void main(String[] args) throws InterruptedException
    {
        JDABuilder jdaBuilder = JDABuilder.createDefault(Config.INSTANCE.getAccessToken());

        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES);

        //Adds EventListeners
        jdaBuilder.addEventListeners(new ReadyEventListener(), new MessageEventListener(), new InteractionEventListener(), new JoinNewGuildEventListener());

        //Starts the bot
        jda = jdaBuilder.build().awaitReady();
        Utility.updateCommands();
    }*/
}
