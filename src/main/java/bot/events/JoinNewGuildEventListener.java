package bot.events;

import bot.functions.Utility;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinNewGuildEventListener extends ListenerAdapter
{
    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        super.onGuildJoin(event);
        Utility.updateCommands(event.getGuild());
    }
}
