package me.tnkcodes;

import javax.security.auth.login.LoginException;

import io.github.cdimascio.dotenv.Dotenv;
import me.commands.CommandManager;
import me.tnkcodes.listeners.EventListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;


public class TnkBot {

  private final Dotenv config;
  private final ShardManager shardManager;

  public TnkBot() throws LoginException {
    // Calls valubales from .env
    config = Dotenv.configure().load();
    String token = config.get("TOKEN");

    // Build bot.
    DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
    builder.setStatus(OnlineStatus.ONLINE);
    builder.setActivity(Activity.watching("canvas to check for updates!"));
    shardManager = builder.build();

    // Checks for new events! #To configure this check EventListener.Java
    shardManager.addEventListener(new EventListener(), new CommandManager());
  }

  public Dotenv getConfig() {
    return config;
  }

  /* 
   * Retrives the bot shard manager.
   * @return the ShardManager instance for the bot
   */
  public ShardManager getShardManager(){
    return shardManager;
  }



  public static void main(String[] args) {
    //*Bot Login:
    try {
          TnkBot bot = new TnkBot();
    } catch (LoginException e){
      System.out.println("ERROR: Provided bot token is invalid!");
    }
  }
}