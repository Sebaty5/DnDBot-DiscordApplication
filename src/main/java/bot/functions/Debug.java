package bot.functions;

import bot.DiscordBot;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class Debug
{
    static List<Guild> guilds = DiscordBot.jda.getGuilds();
    public static void deleteAllCommands()
    {
        DiscordBot.jda.updateCommands().queue();
        for (Guild guild : guilds)
        {
            guild.updateCommands().queue();
        }
    }
}
