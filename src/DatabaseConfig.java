
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private String url;
    private String user;
    private String password;

    public DatabaseConfig(String configFilePath) {
        Properties properties = new Properties();

        try {
            FileInputStream inputStream = new FileInputStream(configFilePath);

            try {
                properties.load(inputStream);
                this.url = properties.getProperty("db.url");
                this.user = properties.getProperty("db.user");
                this.password = properties.getProperty("db.password");
            } catch (Throwable var7) {
                try {
                    inputStream.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }

                throw var7;
            }

            inputStream.close();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

    }

    public String getUrl() {
        return this.url;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }
}
