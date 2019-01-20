/*
 * Name: discord-bot
 * Date: 20/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing
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

    static final List<String> RESTRICTED_WORDS = Arrays.asList(
            "windows is better than linux", "pineapple"
    );

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String msg = event.getMessage().getContentRaw().toLowerCase();

        if (!event.getGuild().getSelfMember().canInteract(event.getMember())) {
            return;
        }

        for (String word : RESTRICTED_WORDS) {

            if (msg.contains(word)) {
                if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE)) {
                    System.out.println("inavlid permissions to delete message");
                    return;

                } else if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    return;
                }

                event.getMessage().delete().queue(done -> {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Your message was deleted because it contains one or more restricted words.").queue();
                }, error -> {
                    System.out.println("failed to delete message containing restricted word");
                    error.printStackTrace();
                });
                return;

            }

        }

        for (BotGuild botGuild : Main.activeGuilds) {
            for (String string : botGuild.bannedWords) {
                System.out.println(botGuild.guild.getName() + " : " + string);
            }
        }

        if (Main.getBotGuild(event.getGuild()) != null) {
            for (String string : Main.getBotGuild(event.getGuild()).bannedWords) {
                if (event.getMessage().getContentRaw().equals(string)) {
                    event.getMessage().delete().queue(done -> {
                        event.getChannel().sendMessage(event.getMember().getAsMention() + ", Your message was deleted because it contains one or more restricted words").queue();
                    });
                    return;
                }
            }
        } else {
            System.err.println("ERROR: Word filter could not get guilds banned word list, activeGuilds may not have been updated.");
        }

    }

    static int addBannedPhrase(Guild guild, String bannedPhrase) {

        File bannedWordsList = new File(Main.GUILD_PROFILES_PATH + guild.getId() + "/banned-phrases.profile");
        if (!bannedWordsList.exists()) {
            try {
                if (bannedWordsList.createNewFile()) {
                    System.out.println("INFO: Created banned-phrases profile for guild: \"" + guild.getName() + "\" at " + bannedWordsList.getPath());
                } else {
                    System.err.println("ERROR: Could not create banned-phrases profile for guild: \"" + guild.getName() + "\"");
                    return 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("ERROR: Could not create banned-phrases profile for guild: \"" + guild.getName() + "\"");
                return 1;
            }
        } else {
            try {
                Scanner scanner = new Scanner(bannedWordsList);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.equals(bannedPhrase)) {
                        scanner.close();
                        return 2;
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.err.println("ERROR: Could not check for duplicate banned word.");
                return 1;
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(Main.GUILD_PROFILES_PATH + guild.getId() + "/banned-phrases.profile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(bannedPhrase);
            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not modify banned-phrases profile for guild: \"" + guild.getName() + "\"");
            return 1;
        }
        Main.updateGuildProperties(guild);
        return 0;
    }

    static int removeBannedWord(Guild guild, String bannedPhrase) {

        File bannedWordsList = new File(Main.GUILD_PROFILES_PATH + guild.getId() + "/banned-phrases.profile");
        ArrayList<String> bannedWords = new ArrayList<>();
        boolean foundWord = false;

        if (!bannedWordsList.exists()) {
            return 2;
        }

        try {
            Scanner scanner = new Scanner(bannedWordsList);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.equals(bannedPhrase)) {
                    bannedWords.add(line);
                } else {
                    foundWord = true;
                }
            }
            scanner.close();

            if (!foundWord) {
                return 2;
            }

            FileWriter fileWriter = new FileWriter(Main.GUILD_PROFILES_PATH + guild.getId() + "/banned-phrases.profile");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            for (String string : bannedWords) {
                printWriter.println(string);
            }

            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not remove banned word");
            return 1;
        }
        Main.updateGuildProperties(guild);
        return 0;
    }

    static int clearBannedPhrases(Guild guild){

        File profile = Main.getGuildBannedPhrasesProfile(guild);

        System.out.println(profile.getPath());

        if(profile.exists()){
            if(profile.delete()){
                Main.updateGuildProperties(guild);
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }

    }

}
