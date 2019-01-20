/*
 * Name: discord-bot
 * Date: 20/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {

    // SK
    static String SK_GUILD_ID = "534074960617144350";
    static String SK_RULES_CHANNEL_ID = "534078439716552726";
    static String SK_ROLE_CHANNEL_ID = "534113401958301727";
    static String SK_MEMBER_ROLE_ID = "534076042613358603";
    static String SK_WELCOME_CHANNEL_ID = "534107890752159747";
    static String SK_WELCOME_MESSAGE_ID = "534774710995779604";

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Main.updateGuildList();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (!event.getAuthor().getId().equals(Main.ID)) {
            Main.consoleOut("(" + event.getGuild().getName() + "::" + event.getChannel().getName() + ") " + event.getAuthor().getName() + ": \"" + event.getMessage().getContentRaw() + "\"");
        } else {
            Main.consoleOut("(" + event.getGuild().getName() + "::" + event.getChannel().getName() + ") " + event.getAuthor().getName() + ": \"" + event.getMessage().getContentRaw() + "\"");
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.isFromType(ChannelType.PRIVATE) && !event.getAuthor().getId().equals(Main.ID)) {
            event.getPrivateChannel().getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage("This bot is not setup to handle private messages, please contact an administrator for your server/guild.").queue();
            });
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        // SK
        if (event.getMessageId().equals(SK_WELCOME_MESSAGE_ID) && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)) {
            if (event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById(SK_MEMBER_ROLE_ID)).queue();
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("**Welcome to the server!**").queue();
                });
            }
        }

        if (event.getMessageId().equals("536618491151908875") && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)){
            event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById("534099092779958293")).queue(success ->{
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Assigned Role: " + event.getGuild().getRoleById("534099092779958293").getName()).queue();
                });
            });
        } else if (event.getMessageId().equals("536618511045230605") && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)){
            event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById("534409645566066699")).queue(success ->{
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Assigned Role: " + event.getGuild().getRoleById("534409645566066699").getName()).queue();
                });
            });
        } else if (event.getMessageId().equals("536618538245554211") && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)){
            event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById("534084013715881994")).queue(success ->{
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Assigned Role: " + event.getGuild().getRoleById("534084013715881994").getName()).queue();
                });
            });
        }

    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event){

        // SK
        if (event.getMessageId().equals("536618491151908875") && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)){
            event.getGuild().getController().removeSingleRoleFromMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById("534099092779958293")).queue(success ->{
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Removed Role: " + event.getGuild().getRoleById("534099092779958293").getName()).queue();
                });
            });
        } else if (event.getMessageId().equals("536618511045230605") && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)){
            event.getGuild().getController().removeSingleRoleFromMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById("534409645566066699")).queue(success ->{
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Removed Role: " + event.getGuild().getRoleById("534409645566066699").getName()).queue();
                });
            });
        } else if (event.getMessageId().equals("536618538245554211") && event.getReactionEmote().getName().equals("\u2705" /* ✅ */)){
            event.getGuild().getController().removeSingleRoleFromMember(event.getGuild().getMemberById(event.getUser().getId()), event.getGuild().getRoleById("534084013715881994")).queue(success ->{
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Removed Role: " + event.getGuild().getRoleById("534084013715881994").getName()).queue();
                });
            });
        }
    }

}
