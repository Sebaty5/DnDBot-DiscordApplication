package bot.functions;

import bot.DiscordBot;
import bot.Enviroment;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility
{
    static ArrayList<Permission> everything = new ArrayList<>(Arrays.asList(Permission.values()));

    //debug commands
    private static final ArrayList<CommandData> debugCommandArrayList;
    static
    {
        debugCommandArrayList = new ArrayList<>();
        debugCommandArrayList.add(Commands.slash("debug-delete", "deletes every command till restart"));
        debugCommandArrayList.add(Commands.slash("debug-force-update", "updates all commands"));
    }

    //normal commands
    private static final ArrayList<CommandData> commandArrayList;
    static
    {
        commandArrayList = new ArrayList<>();
        commandArrayList.add
                (
                        Commands.slash("create-new-campaign", "sets up a new campaign category and the roles for it")
                                .addOption(OptionType.STRING,"name", "the name of the new campaign", true)
                );
    }

    public static boolean guildHasRole (String roleName, Guild guild)
    {
        for (Role role:guild.getRoles())
        {
            if(role.getName().equals(roleName))  return true;
        }
        return false;
    }

    public static boolean guildHasCategory (String categoryName, Guild guild)
    {
        for (Category category:guild.getCategories())
        {
            if(category.getName().equals(categoryName))  return true;
        }
        return false;
    }

    public static FunctionResponse canCreateCategory(String categoryName, Guild guild)
    {
        if(categoryName == null)                    return FunctionResponse.ERROR_MISSING_PARAM;
        if(guildHasCategory(categoryName, guild))   return FunctionResponse.ERROR_INVALID_ACTION;
        return FunctionResponse.SUCCESS;
    }

   public static void createCategory(String categoryName, Guild guild)
   {
       guild.createCategory(categoryName).complete();
   }

   public static FunctionResponse canCreateRole(String roleName, Guild guild)
   {
       if(roleName == null)                 return FunctionResponse.ERROR_MISSING_PARAM;
       if(guildHasRole(roleName, guild))    return FunctionResponse.ERROR_INVALID_ACTION;
       return FunctionResponse.SUCCESS;
   }
    public static void createRole(String roleName, Guild guild)
    {
        guild.createRole()
                .setName(roleName)
                .setColor(Color.LIGHT_GRAY)
                .setPermissions(0L)
                .complete();
    }

    public static boolean createCampaign(String campaignName, Guild guild, SlashCommandInteractionEvent event)
    {
        ArrayList<Permission> grantedPermissions    = new ArrayList<>();
        grantedPermissions.add(Permission.VIEW_CHANNEL);
        grantedPermissions.add(Permission.MESSAGE_SEND);
        grantedPermissions.add(Permission.MESSAGE_SEND_IN_THREADS);
        grantedPermissions.add(Permission.MESSAGE_EMBED_LINKS);
        grantedPermissions.add(Permission.MESSAGE_ATTACH_FILES);
        grantedPermissions.add(Permission.MESSAGE_ADD_REACTION);
        grantedPermissions.add(Permission.MESSAGE_EXT_EMOJI);
        grantedPermissions.add(Permission.MESSAGE_EXT_STICKER);
        grantedPermissions.add(Permission.MESSAGE_HISTORY);
        grantedPermissions.add(Permission.VOICE_CONNECT);
        grantedPermissions.add(Permission.VOICE_SPEAK);
        grantedPermissions.add(Permission.VOICE_STREAM);
        grantedPermissions.add(Permission.VOICE_USE_VAD);
        grantedPermissions.add(Permission.VOICE_START_ACTIVITIES);

        ArrayList<Permission> grantedPermissionsDM  = new ArrayList<>(grantedPermissions);
        grantedPermissionsDM.add(Permission.MANAGE_CHANNEL);
        grantedPermissionsDM.add(Permission.MANAGE_PERMISSIONS);
        grantedPermissionsDM.add(Permission.MANAGE_WEBHOOKS);
        grantedPermissionsDM.add(Permission.CREATE_PUBLIC_THREADS);
        grantedPermissionsDM.add(Permission.CREATE_PRIVATE_THREADS);
        grantedPermissionsDM.add(Permission.MESSAGE_MANAGE);
        grantedPermissionsDM.add(Permission.MANAGE_THREADS);
        grantedPermissionsDM.add(Permission.VOICE_MUTE_OTHERS);
        grantedPermissionsDM.add(Permission.VOICE_DEAF_OTHERS);
        grantedPermissionsDM.add(Permission.VOICE_MOVE_OTHERS);
        grantedPermissionsDM.add(Permission.MANAGE_EVENTS);

        ArrayList<Permission> deniedPermissionsDM   = new ArrayList<>(Arrays.asList(Permission.values()));
        deniedPermissionsDM.removeAll(grantedPermissionsDM);

        ArrayList<Permission> deniedPermissions     = new ArrayList<>(Arrays.asList(Permission.values()));
        deniedPermissionsDM.removeAll(grantedPermissionsDM);


        FunctionResponse roleResponse = Utility.canCreateRole(campaignName, guild);
        FunctionResponse roleDMResponse = Utility.canCreateRole(campaignName + "DM", guild);
        FunctionResponse categoryResponse = Utility.canCreateCategory(campaignName, guild);

        if(roleResponse == FunctionResponse.SUCCESS)
        {
            if(roleDMResponse == FunctionResponse.SUCCESS)
            {
                if(categoryResponse == FunctionResponse.SUCCESS)
                {
                    createRole(campaignName, guild);
                    createRole(campaignName + "DM", guild);
                    createCategory(campaignName, guild);

                    Category category = guild.getCategoriesByName(campaignName, true).get(0);
                    Role role = guild.getRolesByName(campaignName, true).get(0);
                    Role roleDM = guild.getRolesByName(campaignName + "DM", true).get(0);

                    category.getManager()
                            .putPermissionOverride(guild.getPublicRole(),null, everything)
                            .putPermissionOverride(role,grantedPermissions, deniedPermissions)
                            .putPermissionOverride(roleDM,grantedPermissionsDM, deniedPermissionsDM)
                            .complete();
                    return true;
                }
                else if (categoryResponse == FunctionResponse.ERROR_INVALID_ACTION)
                {
                    event.reply("An error occurred during category creation. Category with name " + campaignName + " exists already").setEphemeral(true).queue();
                    return false;
                }
                else if (categoryResponse == FunctionResponse.ERROR_MISSING_PARAM)
                {
                    event.reply("An error occurred during category creation. Missing Name Parameter. This should never happen.").setEphemeral(true).queue();
                    return false;
                }
                else
                {
                    event.reply("An error occurred during category creation. Unknown Error").setEphemeral(true).queue();
                    return false;
                }
            }
            else if (roleDMResponse == FunctionResponse.ERROR_INVALID_ACTION)
            {
                event.reply("An error occurred during roleDM creation. Role with name " + campaignName + "DM" + " exists already").setEphemeral(true).queue();
                return false;
            }
            else if (roleDMResponse == FunctionResponse.ERROR_MISSING_PARAM)
            {
                event.reply("An error occurred during roleDM creation. Missing Name Parameter. This should never happen.").setEphemeral(true).queue();
                return false;
            }
            else
            {
                event.reply("An error occurred during roleDM creation. Unknown Error").setEphemeral(true).queue();
                return false;
            }
        }
        else if (roleResponse == FunctionResponse.ERROR_INVALID_ACTION)
        {
            event.reply("An error occurred during role creation. Role with name " + campaignName + " exists already").setEphemeral(true).queue();
            return false;
        }
        else if (roleResponse == FunctionResponse.ERROR_MISSING_PARAM)
        {
            event.reply("An error occurred during role creation. Missing Name Parameter. This should never happen.").setEphemeral(true).queue();
            return false;
        }
        else
        {
            event.reply("An error occurred during role creation. Unknown Error").setEphemeral(true).queue();
            return false;
        }
    }

    public static boolean canManageCategoryPermissionsForRole (Category category, Role role, ArrayList<Permission> grantedPermissions, ArrayList<Permission> denyedPermissions)
    {
        Guild guild = category.getGuild();
        if(!guildHasRole(role.getName(), guild)) return false;
        return true;
    }

    public static void manageCategoryPermissionsForRole (Category category, Role role, ArrayList<Permission> grantedPermissions, ArrayList<Permission> denyedPermissions)
    {
        if(!canManageCategoryPermissionsForRole(category, role, grantedPermissions, denyedPermissions)) return;

        category.getManager().putPermissionOverride(role, grantedPermissions, denyedPermissions).complete();
    }

    public static void updateCommands()
    {
        //slash commands
        List<Guild> guilds = DiscordBot.jda.getGuilds();

        //Updates commands in all guilds
        for (Guild guild : guilds)
        {
            if (guild.getId().equals(Enviroment.masterGuildID))
            {
                guild.updateCommands().addCommands(commandArrayList).addCommands(debugCommandArrayList).queue();
            }
            else
            {
                guild.updateCommands().addCommands(commandArrayList).queue();
            }
        }
    }

    public static void updateCommands(Guild guild)
    {
        if (guild.getId().equals(Enviroment.masterGuildID))
        {
            guild.updateCommands().addCommands(commandArrayList).addCommands(debugCommandArrayList).queue();
        }
        else
        {
            guild.updateCommands().addCommands(commandArrayList).queue();
        }
    }
}
