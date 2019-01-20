/*
 * Name: discord-bot
 * Date: 20/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.List;

class BotGuild {

    BotGuild(Guild guild) {
        this.guild = guild;
    }

    Guild guild;
    List<String> bannedWords = new ArrayList<>();
}
