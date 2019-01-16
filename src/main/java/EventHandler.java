/*
 * Name: discord-bot
 * Date: 15/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing.com
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {

    // SK
    static String SK_GUILD_ID           = "534074960617144350";
    static String SK_RULES_CHANNEL_ID   = "534078439716552726";
    static String SK_ROLE_CHANNEL_ID    = "534113401958301727";
    static String SK_MEMBER_ROLE_ID     = "534076042613358603";
    static String SK_WELCOME_CHANNEL_ID = "534107890752159747";
    static String SK_WELCOME_MESSAGE_ID = "534774710995779604";

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        // Welcome for SK Gaming Community
        if (event.getGuild().getId().equals(SK_GUILD_ID)) {
            event.getGuild().getTextChannelById(SK_GUILD_ID).sendMessage("**Welcome to the server ** " +
                    event.getMember().getAsMention() + "**!** To become a member, read <#" + SK_RULES_CHANNEL_ID + ">, then come back here and type \"s$accept\".").queue();
        }

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {}

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.isFromType(ChannelType.PRIVATE) && !event.getAuthor().getId().equals(Main.ID)) {
            event.getPrivateChannel().getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage("This bot is not setup to handle private messages, please contact an administrator for your server/guild.").queue();
            });
            return;
        }

        if (!event.getAuthor().getId().equals(Main.ID)) {
            Main.consoleOut("(" + event.getGuild().getName() + "::" + event.getTextChannel().getName() + ") " + event.getAuthor().getName() + ": \"" + event.getMessage().getContentRaw() + "\"");
        } else {
            Main.consoleOut("(" + event.getGuild().getName() + "::" + event.getTextChannel().getName() + ") " + event.getAuthor().getName() + ": \"" + event.getMessage().getContentRaw() + "\"");
        }

    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        if (event.getMessageId().equals(SK_WELCOME_MESSAGE_ID) && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)) {
            if (event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById(SK_MEMBER_ROLE_ID)).queue();
                event.getGuild().getTextChannelById(SK_ROLE_CHANNEL_ID).sendMessage("Welcome to the server " + event.getMember().getAsMention() + "!").queue();
            }
        }

    }

}
