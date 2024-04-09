//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;


public class User {
    private String username;
    private String password;
    private static final Logger logger = LogManager.getLogger();

    public User(String username, String password) {
        this.username = username;
        this.password = hashPassword(password);
    }

    private static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    public boolean register(Connection connection) {
        try {
            String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.password);
            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();
            return rowsInserted > 0;
        } catch (SQLException var5) {
            var5.printStackTrace();
            return false;
        }
    }


    public static boolean login(Connection connection, String username, String password) {
        try {
            String query = "SELECT password_hash FROM users WHERE username = ?";

            // Создаем строку запроса с подставленными параметрами
            String fullQuery = query.replaceFirst("\\?", "'" + username + "'");

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            logger.info("SQL запрос: {}", fullQuery);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    preparedStatement.close();
                    return true;
                }
            }
            preparedStatement.close();
            return false;
        } catch (SQLException var7) {
            var7.printStackTrace();
            return false;
        }
    }

}
