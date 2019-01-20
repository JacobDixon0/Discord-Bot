import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.List;

class BotGuild{

    BotGuild(Guild guild){
        this.guild = guild;
    }

    Guild guild;
    List<String> bannedWords = new ArrayList<>();
}
