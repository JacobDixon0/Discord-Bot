/*
 * Name: discord-bot
 * Date: 15/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing.com
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Commands {

    private static String punishmentReason = "";

    public static ArrayList<Member> activeTimeouts = new ArrayList<>();

    public static class HostInfoCommand extends Command {

        HostInfoCommand() {
            this.name = "hostname";
            this.aliases = new String[]{"servername", "hostinfo", "host", "serverinfo", "server"};
            this.help = "gets the server's hostname";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {
            event.reply(event.getAuthor().getAsMention() + " Current bot host: " + Main.hostName);
        }
    }

    public static class PingCommand extends Command {

        PingCommand() {
            this.name = "ping";
            this.help = "ping SlothBot";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {
            event.reply("pong ni:b::b:a");
        }
    }

    public static class InfoCommand extends Command {

        InfoCommand() {
            this.name = "info";
            this.aliases = new String[]{"VERSION"};
            this.help = "bot info";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            if (!event.getAuthor().isBot()) {
                event.reply("SlothBot\u2122 v" + Main.VERSION + ", created by Royal_Sloth_King#4117 @royalslothking.com");
            }
        }
    }

    public static class UptimeCommand extends Command {

        UptimeCommand() {
            this.name = "uptime";
            this.aliases = new String[]{"runtime"};
            this.help = "get bot uptime";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {
            if (!event.getAuthor().isBot()) {
                event.reply("Uptime: " + Main.getUptime() + "s");
            }
        }
    }

    public static class HelloWorldCommand extends Command {

        HelloWorldCommand() {
            this.name = "helloworld";
            this.aliases = new String[]{"hw"};
            this.help = "says hello";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {
            event.reply("Hello world!");
        }

    }

    public static class KickCommand extends Command {

        KickCommand() {
            this.name = "kick";
            this.aliases = new String[]{"k"};
            this.help = "kick user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Guild guild = event.getGuild();

            if (guild == null) {
                event.reply("no server");
                return;
            }

            Member author = event.getMessage().getMember();

            if (!author.hasPermission(Permission.KICK_MEMBERS)) {
                event.reply("invalid permissions");
                return;
            }

            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (mentionedMembers.isEmpty()) {
                event.reply("no user specified");
                return;
            }

            guild.getController().kick(mentionedMembers.get(0)).queue(success -> {
                event.reply("Kicked User: " + mentionedMembers.get(0).getUser().getName());
            }, error -> {
                event.reply("could not kick user");
            });

        }
    }

    public static class BanCommand extends Command {

        BanCommand() {
            this.name = "ban";
            this.aliases = new String[]{"b"};
            this.help = "ban user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Guild guild = event.getGuild();

            String[] args = event.getArgs().split(" ");

            if (guild == null) {
                event.reply("no server");
                return;
            }

            Member author = event.getMessage().getMember();
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (!author.hasPermission(Permission.BAN_MEMBERS)) {
                event.reply("invalid permissions");
                return;
            } else if (mentionedMembers.isEmpty()) {
                event.reply("no user specified");
                return;
            }

            for (int i = 1; i < args.length; i++) {
                punishmentReason = (i + 1 < args.length) ? punishmentReason.concat(args[i] + " ") : punishmentReason.concat(args[i]);
            }

            if (punishmentReason.isEmpty()) {
                guild.getController().ban(mentionedMembers.get(0).getUser(), 0).queue(success -> {
                    event.reply("Banned User: " + mentionedMembers.get(0).getUser().getName());
                }, error -> {
                    event.reply("could not ban user");
                });
            } else {
                guild.getController().ban(mentionedMembers.get(0).getUser(), 0, punishmentReason).queue(success -> {
                    event.reply("Banned User: " + mentionedMembers.get(0).getUser().getName() + " For Reason: \"" + punishmentReason + "\"");
                }, error -> {
                    event.reply("could not ban user");
                });
            }

        }

    }

    public static class RevokeBanCommand extends Command {

        RevokeBanCommand() {
            this.name = "unban";
            this.aliases = new String[]{"revokeban", "removeban", "uban", "ub"};
            this.help = "ban a user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            User author = event.getAuthor();
            Member member = event.getMember();
            Guild guild = event.getGuild();
            List<User> mentionedUsers = event.getMessage().getMentionedUsers();
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
            User mentionedUser;
            Member mentionedMember;
            GuildController gc = guild.getController();

        }
    }

    public static class BannedWordsAdd extends Command {

        BannedWordsAdd(){
            this.name = "banphrase";
            this.aliases = new String[]{"bp", "banword", "bannedphraseadd", "bannedwordadd", "addbannedword", "addbannedphrase"};
            this.help = "[RESERVED - Moderator] adds a banned phrase.";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Member member = event.getMember();
            Guild guild = event.getGuild();

            if(!member.hasPermission(Permission.MESSAGE_MANAGE)){
                event.reply("Invalid Permissions");
                return;
            }

            if(WordFilter.addBannedPhrase(guild, event.getArgs())){
                event.reply("Added Banned Phrase");
            } else {
                event.reply("An internal error occurred while attempting to add banned phrase, if this problem persists, please contact the bot administrator.");
            }

        }

    }

    public static class BannedWordsRemove extends Command {

        BannedWordsRemove(){
            this.name = "unbanphrase";
            this.aliases = new String[]{"ubp", "unbanword", "bannedphraseremove", "bannedwordremove", "removebannedword", "removebannedphrase"};
            this.help = "[RESERVED - Moderator] removes a banned phrase.";
        }

        @Override
        protected void execute(CommandEvent event) {

            Member member = event.getMember();
            Guild guild = event.getGuild();

            if(!member.hasPermission(Permission.MESSAGE_MANAGE)){
                event.reply("Invalid Permissions");
                return;
            }

            if(WordFilter.removeBannedWord(guild, event.getArgs())){
                event.reply("Removed Banned Phrase");
            } else {
                event.reply("An internal error occurred while attempting to remove banned phrase, if this problem persists, please contact the bot administrator.");
            }

        }
    }

    public static class TempMuteCommand extends Command {

        TempMuteCommand() {
            this.name = "timeout";
            this.aliases = new String[]{"tempmute", "tm"};
            this.help = "timeout member";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            long duration;
            String[] args = event.getArgs().split(" ");

            if (!args[0].matches("<@([!&])?\\d+>")) {
                event.reply("Invalid Command");
                return;
            }

            if (event.getMessage().getMentionedMembers().isEmpty()) {
                event.reply("no user specified");
                return;
            }

            if (event.getMessage().getContentRaw().matches(Main.commandPrefix.replace("$", "\\$") + name + " <@!?\\d+> \\d+$")) {
                duration = Long.parseLong(event.getArgs().replaceAll("(<@!?\\d+>)", "").replaceAll("[^\\d+]", ""));
            } else if (event.getMessage().getContentRaw().matches(Main.commandPrefix.replace("$", "\\$") + name + " <@!?\\d+>$")) {
                duration = Main.defaultTimeout;
            } else {
                event.reply("invalid command format ..");
                sendErrorMessage(event.getTextChannel(), event.getMember());
                return;
            }

            GuildController gc = event.getGuild().getController();

            if (!gc.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                    !event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                    event.getMessage().getMentionedMembers().get(0).isOwner()) {
                event.reply("invalid permissions");
                return;
            }

            gc.addSingleRoleToMember(event.getMessage().getMentionedMembers().get(0),
                    event.getGuild().getRolesByName("Server Muted", true).get(0)).queue(success -> {
                        activeTimeouts.add(event.getMessage().getMentionedMembers().get(0));
                gc.removeSingleRoleFromMember(event.getMessage().getMentionedMembers().get(0),
                        event.getGuild().getRolesByName("Server Muted", true).get(0)).queueAfter(duration, TimeUnit.SECONDS, success2 -> {
                            activeTimeouts.remove(event.getMessage().getMentionedMembers().get(0));
                    event.reply("Timeout Expired For User: " + event.getMessage().getMentionedUsers().get(0).getName());
                }, error -> {
                    event.reply("error removing timeout on user");
                });
                event.reply("Timed Out Member: " + event.getMessage().getMentionedUsers().get(0).getName() + " for " + duration + "s");

            }, error -> {
                event.reply("could not timeout user");
            });

        }

        void sendErrorMessage(TextChannel channel, Member member) {

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Invalid Usage");
            builder.setAuthor(member.getUser().getName(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
            builder.setColor(Color.decode("#FF1010"));
            builder.setDescription("Proper usage: " + Main.commandPrefix + "timeout [@user] [duration]");
            builder.addField("[] required", "", false);
            channel.sendMessage(builder.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));

        }

    }

    public static class SoftBanCommand extends Command {

        SoftBanCommand() {
            this.name = "softban";
            this.aliases = new String[]{"sban", "sb", "purge"};
            this.help = "softban user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Guild guild = event.getGuild();
            Member author = event.getMessage().getMember();
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (guild == null) {
                event.reply("no server");
            }

            if (!author.hasPermission(Permission.BAN_MEMBERS)) {
                event.reply("invalid permissions");
                return;
            }

            if (mentionedMembers.isEmpty()) {
                event.reply("no user specified");
                return;
            }

            guild.getController().ban(mentionedMembers.get(0).getUser(), 7).queue(done -> {

                guild.getController().unban(mentionedMembers.get(0).getUser()).queue(done2 -> {
                    event.reply("Softbanned User: " + mentionedMembers.get(0).getUser().getName() + "#" + mentionedMembers.get(0).getUser().getDiscriminator());
                }, error -> {
                    event.reply("could not softban user: " + error);
                });

            }, error -> {
                event.reply("could not softban user: " + error);
            });

        }

    }

    public static class MuteCommand extends Command {

        MuteCommand() {
            this.name = "mute";
            this.aliases = new String[]{"m"};
            this.help = "mute user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Guild guild = event.getGuild();

            GuildController guildController = guild.getController();

            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (!event.getMessage().getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.reply("invalid permissions");
                return;
            }

            if (mentionedMembers.isEmpty()) {
                event.reply("no user specified");
                return;
            }

            Member member = mentionedMembers.get(0);
            Role roleMuted = guild.getRoles().stream().filter(r -> r.getName().equals("servermuted")).findFirst().orElse(null);

            if (member.isOwner()) {
                event.reply("cannot server mute server owner");
                return;
            }

            if (roleMuted == null) {
                event.reply("muted role does not exist");
                return;
            }

            if (mentionedMembers.get(0).getRoles().contains(roleMuted)) {
                event.reply("user is already muted");
                return;
            }

            guildController.addSingleRoleToMember(member, roleMuted).queue(success -> {
                event.reply("Muted User: " + member.getUser().getName());
            }, error -> {
                event.reply("could not mute user: " + error);
            });
            guildController.setMute(member, true).queue();

        }

    }

    public static class UnmuteCommand extends Command {

        UnmuteCommand() {
            this.name = "unmute";
            this.aliases = new String[]{"um"};
            this.help = "unmute user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Guild guild = event.getGuild();

            GuildController guildController = guild.getController();

            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (!event.getMessage().getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.reply("invalid permissions");
                return;
            }

            if (mentionedMembers.isEmpty()) {
                event.reply("no user specified");
                return;
            }

            Member member = mentionedMembers.get(0);

            Role roleMuted = guild.getRoles().stream().filter(r -> r.getName().equals("servermuted")).findFirst().orElse(null);

            if (member.isOwner()) {
                event.reply("cannot server unmute server owner");
                return;
            }

            if (roleMuted == null) {
                event.reply("muted role does not exist");
                return;
            }

            if (mentionedMembers.get(0).getRoles().contains(roleMuted)) {
                guildController.removeSingleRoleFromMember(member, roleMuted).queue(success -> {
                    event.reply("Unmuted User: " + member.getUser().getName());
                }, error -> {
                    event.reply("could not unmute user");
                });
                guildController.setMute(member, false).queue();
            } else {
                event.reply("user is not muted");
            }

        }

    }

    public static class TempMuteCommandOld extends Command {

        TempMuteCommandOld() {
            this.name = "timeout10";
            this.aliases = new String[]{"tm10", "tempmute10"};
            this.help = "[DEPRECATED] temporally mute user for " + Main.defaultTimeout + "s";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            Guild guild = event.getGuild();
            GuildController guildController = guild.getController();
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (!event.getMessage().getMember().hasPermission(Permission.KICK_MEMBERS)) {
                event.reply("invalid permissions");
                return;
            }

            if (mentionedMembers.isEmpty()) {
                event.reply("no user specified");
                return;
            }

            Member member = mentionedMembers.get(0);

            Role roleMuted = guild.getRoles().stream().filter(r -> r.getName().equals("Server Muted")).findFirst().orElse(null);

            if (roleMuted == null) {
                event.reply("muted role does not exist");
                return;
            }

            if (mentionedMembers.get(0).getRoles().contains(roleMuted)) {
                event.reply("user is already muted");
                return;
            }

            guildController.addSingleRoleToMember(member, roleMuted).queue(success -> {
                event.reply("Muted User: " + mentionedMembers.get(0).getUser().getName());
                guildController.removeSingleRoleFromMember(member, roleMuted).queueAfter(Main.defaultTimeout, TimeUnit.SECONDS, success2 -> {
                    event.reply("Unmuted User: " + member.getUser().getName());
                }, error -> {
                    event.reply("could not unmute user: " + error);
                });

            }, error -> {
                event.reply("could not mute user: " + error);
            });

        }

    }

    public static class AssignRole extends Command {

        AssignRole() {
            this.name = "assign";
            this.aliases = new String[]{"roleassign", "role", "assign", "assignrole", "ar", "ra"};
            this.help = "[RESERVED - SK]assigns role to user";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            // Role Assignment for SK Gaming Community
            if (event.getChannel().getId().equals(EventHandler.SK_ROLE_CHANNEL_ID)) {

                if (event.getArgs().equals("Rainbow Six")) {
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("Rainbow Six", false).get(0)).queue(success -> {
                        event.reply(event.getAuthor().getAsMention() + " Assigned Role: " + event.getGuild().getRolesByName("Rainbow Six", false).get(0).getName());
                    });
                } else if (event.getArgs().equals("Live Notifications")) {
                    event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRolesByName("Live Notifications", false).get(0)).queue(success -> {
                        event.reply(event.getAuthor().getAsMention() + " Assigned Role: " + event.getGuild().getRolesByName("Live Notifications", false).get(0).getName());
                    });
                } else if (event.getArgs().equals("Rainbow Six News")) {
                    event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRolesByName("Rainbow Six News", false).get(0)).queue(success -> {
                        event.reply(event.getAuthor().getAsMention() + " Assigned Role: " + event.getGuild().getRolesByName("Rainbow Six News", false).get(0).getName());
                    });
                }
            } else {
                event.reply(event.getAuthor().getAsMention() + " Role assignment is reserved for SK Gaming Community::role-assignment.");
            }
        }

    }

    public static class ListRoles extends Command {

        ListRoles() {
            this.name = "roles";
            this.aliases = new String[]{"listroles", "lsroles", "roleslist", "roleshelp"};
            this.help = "[RESERVED - SK] lists all available roles";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getChannel().getId().equals(EventHandler.SK_ROLE_CHANNEL_ID)) {
                event.reply("**ROLES:**\nRainbow Six\nRainbow Six News\n");
            } else {
                event.reply(event.getAuthor().getAsMention() + " This command is reserved for SK Gaming Community::role-assignment.");
            }
        }
    }

    public static class VoiceConnectCommand extends Command {

        VoiceConnectCommand() {
            this.name = "join";
            this.aliases = new String[]{"c", "connect", "voiceconnect"};
            this.help = "joins a voice channel";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            VoiceChannel channel = event.getMember().getVoiceState().getChannel();

            if (event.getAuthor().isBot()) return;

            if (!event.getGuild().getSelfMember().hasPermission(Permission.VOICE_CONNECT)) {
                event.reply("could not connect to voice channel due to invalid permissions");
                return;
            }

            if (channel == null) {
                event.reply("no channel specified");
                return;
            }

            AudioManager audioManager = event.getGuild().getAudioManager();

            if (audioManager.isAttemptingToConnect()) {
                event.reply("attempting to connect to channel");
                return;
            }

            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
                audioManager.openAudioConnection(channel);
                event.reply("Connected To Voice Channel: " + channel.getName());
            } else {
                event.reply("bot is already connected to a channel");
            }

        }
    }

    public static class VoiceDisconnectCommand extends Command {

        VoiceDisconnectCommand() {
            this.name = "leave";
            this.aliases = new String[]{"dc", "disconnect", "voicedisconnect"};
            this.help = "leaves a voice channel";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            VoiceChannel channel = event.getGuild().getSelfMember().getVoiceState().getChannel();

            if (channel == null) {
                event.reply("bot is not connected to a voice channel");
            } else {
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.closeAudioConnection();
                event.reply("Disconnected From Voice Channel: " + channel.getName());
            }

        }

    }

    static class AdminCommand extends Command {

        AdminCommand() {
            this.name = "admin";
            this.help = "[RESERVED - Bot Admin] changes bot settings for guilds.";
            Main.commands.add(this);
        }

        @Override
        protected void execute(CommandEvent event) {

            if (event.getAuthor().getId().equals(Main.ADMIN_ID)) {
            } else {
                event.reply(event.getAuthor().getAsMention() + " This command is reserved for SlothBot administrators, this is a temporary measure until the bot is setup for individual guilds/servers.");
            }

        }

    }

}
