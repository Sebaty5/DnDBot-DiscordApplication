package bot.events;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyEventListener extends ListenerAdapter
{
    @Override
    public void onReady(ReadyEvent event)
    {
        super.onReady(event);

        System.out.printf("The Bot is ready and online!\n");
    }
}
