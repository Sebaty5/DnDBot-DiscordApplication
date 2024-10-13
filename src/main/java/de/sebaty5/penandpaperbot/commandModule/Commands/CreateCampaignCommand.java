package de.sebaty5.penandpaperbot.commandModule.Commands;

import de.sebaty5.penandpaperbot.commandModule.Command;
import de.sebaty5.penandpaperbot.configModule.Localization;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateCampaignCommand implements Command {

    @Override
    public SlashCommandData getData() {
        return Commands.slash("create-new-campaign", Localization.translate("cmd.campaignCreation.desc"))
                .addOption(OptionType.STRING,"name", Localization.translate("cmd.campaignCreation.name.desc"), true);
    }

    @Override
    public void onEvent(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        OptionMapping opt = event.getOption("name");
        String campaignName = opt == null ? null : opt.getAsString();
        if(campaignName == null) {
            event.reply(Localization.translate("msg.campaignCreation.missingArgument.name")).setEphemeral(true).queue();
            return;
        }

        if(createCampaign(campaignName, guild, event)){
            event.reply(Localization.translate("msg.campaignCreation.success")).setEphemeral(true).queue();
        }
    }

    private boolean createCampaign(String campaignName, Guild guild, SlashCommandInteractionEvent event)
    {
        ArrayList<Permission> everything = new ArrayList<>(Arrays.asList(Permission.values()));

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




        if(canCreateRole(campaignName, guild, event) && canCreateRole(campaignName + "DM", guild, event) && canCreateCategory(campaignName, guild, event)) {
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
        return false;
    }

    public static boolean canCreateCategory(String categoryName, Guild guild, SlashCommandInteractionEvent event)
    {
        if(categoryName == null) {
            event.reply(Localization.translate("msg.campaignCreation.missingArgument.name")).setEphemeral(true).queue();
        } else if(guild.getCategories().stream().anyMatch(category -> category.getName().equals(categoryName))) {
            event.reply(String.format(Localization.translate("msg.campaignCreation.duplicate.category"), categoryName)).setEphemeral(true).queue();
        } else {
            return true;
        }
        return false;
    }

    public static boolean canCreateRole(String roleName, Guild guild, SlashCommandInteractionEvent event)
    {
        if(roleName == null) {
            event.reply(Localization.translate("msg.campaignCreation.missingArgument.name")).setEphemeral(true).queue();
        } else if(guild.getRoles().stream().anyMatch(role -> role.getName().equals(roleName))) {
            event.reply(String.format(Localization.translate("msg.campaignCreation.duplicate.role"), roleName)).setEphemeral(true).queue();
        } else {
            return true;
        }
        return false;
    }

    public static void createCategory(String categoryName, Guild guild)
    {
        guild.createCategory(categoryName).complete();
    }

    public static void createRole(String roleName, Guild guild)
    {
        guild.createRole()
                .setName(roleName)
                .setColor(Color.LIGHT_GRAY)
                .setPermissions(0L)
                .complete();
    }
}
