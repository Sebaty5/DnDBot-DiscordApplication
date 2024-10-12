package bot.events;

import bot.functions.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import bot.functions.Debug;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class InteractionEventListener extends ListenerAdapter
{/*
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        super.onSlashCommandInteraction(event);

        String commandName = event.getName();
        Guild guild = event.getGuild();
        System.out.printf("Interaction! %s\n", commandName);
        switch (commandName)
        {
            case "debug-delete" ->
            {
                Debug.deleteAllCommands();
                event.reply("deleted all guild commands").setEphemeral(true).queue();
            }
            case "debug-force-update" ->
            {
                Utility.updateCommands();
                event.reply("updated all guild commands").setEphemeral(true).queue();
            }
            case "create-new-campaign" ->
            {
                OptionMapping opt = event.getOption("name");
                String campaignName = opt == null ? null : opt.getAsString();
                if(campaignName == null)
                {
                    event.reply("An error occurred during campaign creation. Missing name").setEphemeral(true).queue();
                    break;
                }

                if(!Utility.createCampaign(campaignName, guild, event)) break;

                event.reply("Created new campaign").setEphemeral(true).queue();
            }
            default ->
            {
                event.reply("This should not have happened").setEphemeral(true).queue();
            }
        }
    }*/
}
