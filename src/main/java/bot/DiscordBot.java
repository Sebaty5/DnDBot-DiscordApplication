package bot;

import bot.events.InteractionEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot
{
    public static JDA jda;
    public static void main(String[] args) throws InterruptedException
    {
        JDABuilder jdaBuilder = JDABuilder.createDefault("");

        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES);

        //Adds EventListeners
        jdaBuilder.addEventListeners(new InteractionEventListener());

        //Starts the bot
        jda = jdaBuilder.build().awaitReady();
        throw new RuntimeException();
    }
}
