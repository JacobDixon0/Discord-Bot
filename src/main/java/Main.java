/*
 * Name: discord-bot
 * Date: 20/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static final String EXE_PATH = System.getProperty("user.dir");
    static final String GUILD_PROFILES_PATH = EXE_PATH + "/guild-profiles/";
    static final String VERSION = "0.8a";
    static final String ID = "433070903614636032";
    static final String ADMIN_ID = "282331714603450368";
    private static String token;

    static JDA jda;

    private static long startTime = Instant.now().getEpochSecond();

    static String hostName = "unknown";
    static String gamePlaying = "RoyalSlothKing.com";
    static String commandPrefix = "s$";
    static int defaultTimeout = 10;
    private static boolean headless = true;

    private static CommandClientBuilder commandClientBuilder = new CommandClientBuilder();

    private static File tokenFile = new File(EXE_PATH + "/token.txt");
    private static File configFile = new File(EXE_PATH + "/discord-bot.config");

    static ArrayList<Command> commands = new ArrayList<>();

    static List<BotGuild> activeGuilds = new LinkedList<>();

    public static void main(String[] args) throws LoginException {

        if (!tokenFile.exists()) {
            System.err.println("ERROR: Missing Discord application token file \"token.txt\".");
            System.exit(-1);
        }

        try {
            FileReader reader = new FileReader(tokenFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                token = line;
            }

            bufferedReader.close();
            System.out.println("INFO: Loaded token from " + tokenFile.getPath());

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not load token file \"token.txt\"");
            System.exit(-1);
        }

        loadConfig();

        commandClientBuilder.setOwnerId(ADMIN_ID);
        commandClientBuilder.useHelpBuilder(true);
        commandClientBuilder.addCommands(
                new Commands.HelloWorldCommand(), new Commands.KickCommand(),
                new Commands.BanCommand(), new Commands.SoftBanCommand(),
                new Commands.MuteCommand(), new Commands.TempMuteCommandOld(),
                new Commands.PingCommand(), new Commands.UnmuteCommand(),
                new Commands.InfoCommand(), new Commands.VoiceConnectCommand(),
                new Commands.VoiceDisconnectCommand(), new Commands.TempMuteCommand(),
                new Commands.HostInfoCommand(), new Commands.UptimeCommand(),
                new Commands.RevokeBanCommand(), new Commands.AdminCommand(),
                new Commands.AssignRole(), new Commands.ListRoles(),
                new Commands.BannedPhrasesAdd(), new Commands.BannedPhrasesRemove(),
                new Commands.BannedPhrasesList()
        );

        /*jda = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new EventHandler(), commandClientBuilder.build(), new WordFilter()).buildAsync();*/
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new EventHandler(), commandClientBuilder.build(), new WordFilter()).buildBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to build JDA");
            System.exit(-1);
        }

        loadGuildProfiles();
        updateGuildList();
        updateGuildProperties();

        if (headless) GraphicalInterface.initialize(args);

    }

    static void terminalInput() {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();

        if (s.equals("stop")) {
            jda.shutdown();
            System.exit(0);
            return;
        } else if (s.equals("exit")) {
            return;
        }
        terminalInput();
    }

    static long getUptime() {
        return Instant.now().getEpochSecond() - startTime;
    }

    private static void createConfigFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("discord-bot.config"));
            writer.write("headless: 1\n");
            writer.write("host-name: \"undefined\"\n");
            writer.write("default-game: \"" + gamePlaying + "\"\n");
            writer.write("command-prefix: \"" + commandPrefix + "\"\n");
            writer.write("default-timeout-duration: " + defaultTimeout + "\n");
            writer.close();
            System.out.println("INFO: Created config file with default configuration.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not create config file.");
        }
    }

    static void restartJDA() {
        jda.shutdown();
        loadConfig();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex0) {
            ex0.printStackTrace();
        }
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(Main.token).addEventListener(new EventHandler(), Main.commandClientBuilder.build(), new WordFilter()).buildAsync();
            startTime = Instant.now().getEpochSecond();
        } catch (LoginException ex1) {
            ex1.printStackTrace();
        }

    }

    private static void loadConfig() {

        try {
            Scanner scanner = new Scanner(configFile);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher m0 = Pattern.compile("^headless: ([01])$").matcher(line);
                Matcher m1 = Pattern.compile("^host-name: \"([\\w\\s]+)\"$").matcher(line);
                Matcher m2 = Pattern.compile("^default-game: \"(.+)\"$").matcher(line);
                Matcher m3 = Pattern.compile("^command-prefix: \"(.+)\"$").matcher(line);
                Matcher m4 = Pattern.compile("^default-timeout-duration: (\\d+)$").matcher(line);

                if (m0.find()) {
                    headless = m0.group(1).equals("0");
                } else if (m1.find()) {
                    hostName = m1.group(1);
                } else if (m2.find()) {
                    gamePlaying = m2.group(1);
                } else if (m3.find()) {
                    commandPrefix = m3.group(1);
                } else if (m4.find()) {
                    defaultTimeout = Integer.parseInt(m4.group(1));
                }

            }

            scanner.close();
            System.out.println("INFO: Loaded config file from " + configFile.getPath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not load config file \"discord-bot.config\", using default configuration.");
            createConfigFile();
        }

        commandClientBuilder.setGame(Game.playing(gamePlaying));
        commandClientBuilder.setPrefix(commandPrefix);
    }

    static void consoleOut(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MM/yyy HH:mm:ss] ");
        System.out.println(dateFormat.format(new Date()) + msg);
    }

    static void sendAnnouncement(String msg) {

        if (!checkMessageValidity(msg)) {
            System.err.println("ERROR: Invalid Message - no content");
            return;
        }

        for (Guild guild : Main.jda.getGuilds()) {
            if (guild.getSelfMember().hasPermission(Permission.MESSAGE_WRITE)) {
                guild.getDefaultChannel().sendMessage(msg).queue();
            }
        }
        consoleOut("Sent PSA to all activeGuilds in default channel with message: \"" + msg + "\"");
    }

    private static void loadGuildProfiles() {
        if (!new File(GUILD_PROFILES_PATH).exists()) {
            File guildProfiles = new File(GUILD_PROFILES_PATH);
            if (guildProfiles.mkdir()) {
                System.out.println("INFO: Created guild-profiles directory at " + guildProfiles.getPath());
            } else {
                System.err.println("ERROR: Could not create guild-profiles directory");
                return;
            }
        }

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }*/
        for (Guild guild : jda.getGuilds()) {
            if (!new File(GUILD_PROFILES_PATH + "/" + guild.getId()).exists()) {
                File guildProfile = new File(GUILD_PROFILES_PATH + "/" + guild.getId());
                if (guildProfile.mkdir()) {
                    System.out.println("INFO: Created guild profile for guild: \"" + guild.getName() + "\" at " + guildProfile.getPath());
                } else {
                    System.err.println("ERROR: Could not create guild profile for guild: \"" + guild.getName() + "\"");
                }
            }
        }
    }

    static void updateGuildList() {
        if (activeGuilds.isEmpty()) {
            for (Guild guild : jda.getGuilds()) {
                activeGuilds.add(new BotGuild(guild));
            }
        }
        for (Guild guild : jda.getGuilds()) {
            boolean hasBotGuild = false;
            for (BotGuild botGuild : activeGuilds) {
                if (botGuild.guild.equals(guild)) {
                    hasBotGuild = true;
                    break;
                }
            }
            if (!hasBotGuild) {
                activeGuilds.add(new BotGuild(guild));
            }
        }
    }

    private static void updateGuildProperties() {
        for (Guild guild : jda.getGuilds()) {
            updateGuildProperties(guild);
        }
    }

    static void updateGuildProperties(Guild guild) {
        for (BotGuild botGuild : activeGuilds) {
            if (botGuild.guild.equals(guild)) {
                File bannedPhrasesProfile = new File(GUILD_PROFILES_PATH + guild.getId() + "/" + "banned-phrases.profile");
                botGuild.bannedWords.clear();
                if (!bannedPhrasesProfile.exists()) {
                    continue;
                }
                try {
                    Scanner s = new Scanner(bannedPhrasesProfile);
                    while (s.hasNextLine()) {
                        String line = s.nextLine();
                        botGuild.bannedWords.add(line);
                    }
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("ERROR: Failed to update guild property");
                }
            }
        }
    }

    static BotGuild getBotGuild(Guild guild) {
        for (BotGuild botGuild : activeGuilds) {
            if (botGuild.guild.equals(guild)) {
                return botGuild;
            }
        }
        return null;
    }

    static File getGuildProfile(Guild guild) {
        return new File(EXE_PATH + "/" + GUILD_PROFILES_PATH + guild.getId());
    }

    static File getGuildBannedPhrasesProfile(Guild guild) {
        return new File(getGuildProfile(guild) + "/banned-phrases.profile");
    }

    static boolean checkMessageValidity(String str) {

        for (char c : str.toCharArray()) {
            if (c != ' ') {
                return true;
            }
        }
        return false;
    }

    /*@Override
    public void onMessageReceived(MessageReceivedEvent event) {

        System.out.println("received message from " +
                event.getAuthor().getName() + ": " +
                event.getMessage().getContentDisplay()
        );

        if(event.getAuthor().isBot()){
            return;
        }

        if(event.getMessage().getContentRaw().equals("!ping")){
            event.getChannel().sendMessage("Pong Ni:b::b:a ").queue();

        } else if(event.getMessage().getContentRaw().matches("^!.+")){
            event.getChannel().sendMessage( "<@" + event.getAuthor().getId() + ">" + " \"" + event.getMessage().getContentDisplay() + "\" is not a recognised command.").queue();
        }


    }*/

}
