/*
 * Name: discord-bot
 * Date: 15/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing.com
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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

        static boolean addBannedPhrase(Guild guild, String bannedPhrase){

            File bannedWordsList = new File(Main.GUILD_PROFILES_PATH + "/" + guild.getId() + "/banned-phrases.profile");

            if(!bannedWordsList.exists()){
                try {
                    if(bannedWordsList.createNewFile()){
                        System.out.println("INFO: Created banned-phrases profile for guild: \"" + guild.getName() + "\" at " + bannedWordsList.getPath());
                    } else {
                        System.err.println("ERROR: Could not create banned-phrases profile for guild: \"" + guild.getName() + "\"");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                    System.err.println("ERROR: Could not create banned-phrases profile for guild: \"" + guild.getName() + "\"");
                    return false;
                }
            }

            try {
                FileWriter fileWriter = new FileWriter(Main.GUILD_PROFILES_PATH + "/" + guild.getId() + "/banned-phrases.profile", true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                printWriter.println(bannedPhrase);
                printWriter.close();

            } catch (IOException e){
                e.printStackTrace();
                System.err.println("ERROR: Could not modify banned-phrases profile for guild: \"" + guild.getName() + "\"");
                return false;
            }

            return true;
        }

        static boolean removeBannedWord(Guild guild, String bannedWord){

            File bannedWordsList = new File(Main.GUILD_PROFILES_PATH + "/" + guild.getId() + "/banned-phrases.profile");

            if(!bannedWordsList.exists()){
                return true;
            }

            ArrayList<String> bannedWords = new ArrayList<>();

            try {
                Scanner scanner = new Scanner(bannedWordsList);

                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if(!line.equals(bannedWord)){
                        bannedWords.add(line);
                    }
                }

                FileWriter fileWriter = new FileWriter(Main.GUILD_PROFILES_PATH + "/" + guild.getId() + "/banned-phrases.profile");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);

                for(String string : bannedWords){
                    printWriter.println(string);
                }

                printWriter.close();

            } catch (IOException e){
                e.printStackTrace();
                System.err.println("ERROR: Could not remove banned word");
                return false;
            }
            return true;
        }

    }
