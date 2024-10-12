package configModule;

import logging.StandardLogger;

import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.nio.file.Paths;

public class Config extends JsonHandler<Config.Data> {
    private static final StandardLogger LOGGER = new StandardLogger("Config");

    public static final Config INSTANCE;

    static {
        try {
            INSTANCE = new Config();
            INSTANCE.accessToken = INSTANCE.fetchAccessToken();
            INSTANCE.forceSave();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JDA jda;

    private String accessToken;

    private Config() throws IOException {
        super(Paths.get("./config/config.json"), 600_000L, Data.class, Data::new);
        LOGGER.log("Initializing Config");
        super.init();
    }

    public void load (JDA jda) {
        this.jda = jda;
    }

    public JDA getJDA() {
        return jda;
    }

    public String getAccessToken() {
        return accessToken;
    }


    private String fetchAccessToken() {
        try {
            this.readLock.lock();
            return this.getData().accessToken != null ? this.getData().accessToken : "";
        } finally {
            this.readLock.unlock();
        }
    }

    static final class Data {
        private String accessToken = "";

    }

}
