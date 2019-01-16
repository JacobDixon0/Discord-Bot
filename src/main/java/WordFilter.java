/*
 * Name: discord-bot
 * Date: 15/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing.com
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;

public class WordFilter extends ListenerAdapter {

        private final List<String> RESTRICTED_WORDS = Arrays.asList(
                "windows is better than linux", "pineapple"
        );

        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event){

            String msg = event.getMessage().getContentRaw().toLowerCase();

            for(String word : RESTRICTED_WORDS){

                if(msg.contains(word)){

                    if(!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE)) {
                        System.out.println("inavlid permissions to delete message");
                        return;

                    } else if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        return;
                    }

                    event.getMessage().delete().queue(done -> {
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Your message was deleted because it contains one or more restricted words").queue();
                    }, error -> {
                        System.out.println("failed to delete message containing restricted string");
                        error.printStackTrace();
                    });
                    return;

                }

            }

        }

    }
