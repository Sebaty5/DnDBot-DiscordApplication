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
import java.util.EnumSet;
import java.util.HashSet;

public class CreateCampaignCommand implements Command {

    private static final HashSet<Permission> everything = new HashSet<>(EnumSet.allOf(Permission.class));


    private static final HashSet<Permission> grantedPermissions    = new HashSet<>();
    static {
        grantedPermissions.add(Permission.VIEW_CHANNEL);
        grantedPermissions.add(Permission.MESSAGE_SEND);
        grantedPermissions.add(Permission.MESSAGE_SEND_IN_THREADS);
        grantedPermissions.add(Permission.MESSAGE_EMBED_LINKS);
        grantedPermissions.add(Permission.MESSAGE_ATTACH_FILES);
        grantedPermissions.add(Permission.MESSAGE_ADD_REACTION);
        grantedPermissions.add(Permission.MESSAGE_HISTORY);
        grantedPermissions.add(Permission.MESSAGE_SEND_POLLS);
        grantedPermissions.add(Permission.VOICE_CONNECT);
        grantedPermissions.add(Permission.VOICE_SPEAK);
        grantedPermissions.add(Permission.VOICE_STREAM);
        grantedPermissions.add(Permission.VOICE_USE_VAD);
    }


    private static final HashSet<Permission> grantedPermissionsDM  = new HashSet<>(grantedPermissions);
    static {
        grantedPermissionsDM.add(Permission.MANAGE_CHANNEL);
        grantedPermissionsDM.add(Permission.MANAGE_PERMISSIONS);
        grantedPermissionsDM.add(Permission.CREATE_PUBLIC_THREADS);
        grantedPermissionsDM.add(Permission.CREATE_PRIVATE_THREADS);
        grantedPermissionsDM.add(Permission.MESSAGE_MANAGE);
        grantedPermissionsDM.add(Permission.MANAGE_THREADS);
        grantedPermissionsDM.add(Permission.VOICE_MUTE_OTHERS);
        grantedPermissionsDM.add(Permission.VOICE_DEAF_OTHERS);
        grantedPermissionsDM.add(Permission.VOICE_MOVE_OTHERS);
        grantedPermissionsDM.add(Permission.MESSAGE_MENTION_EVERYONE);
        grantedPermissionsDM.add(Permission.VOICE_SET_STATUS);
        grantedPermissionsDM.add(Permission.PRIORITY_SPEAKER);
    }


    private static final HashSet<Permission> deniedPermissions = new HashSet<>(everything);
    static {
        deniedPermissions.removeAll(grantedPermissions);
    }

    private static final HashSet<Permission> deniedPermissionsDM = new HashSet<>(everything);
    static {
        deniedPermissionsDM.removeAll(grantedPermissionsDM);
    }

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
        if(canCreateRole(campaignName, guild, event) && canCreateRole(campaignName + "DM", guild, event) && canCreateCategory(campaignName, guild, event)) {
            Role role = createRole(campaignName, guild);
            Role roleDM = createRole(campaignName + "DM", guild);
            Category category = createCategory(campaignName, guild);
            category.getManager()
                    .putPermissionOverride(role, grantedPermissions, deniedPermissions)
                    .putPermissionOverride(roleDM, grantedPermissionsDM, deniedPermissionsDM)
                    .putPermissionOverride(guild.getPublicRole(), null, everything)
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

    public static Category createCategory(String categoryName, Guild guild)
    {
         return guild.createCategory(categoryName).complete();
    }

    public static Role createRole(String roleName, Guild guild)
    {
        return guild.createRole()
                .setName(roleName)
                .setColor(Color.LIGHT_GRAY)
                .setPermissions(0L)
                .complete();
    }
}
