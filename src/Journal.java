import java.awt.Component;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Journal {
    private DatabaseConnection databaseConnection;
    private static final Logger logger = LogManager.getLogger(Journal.class);

    public Journal(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void showAddJournalEntryDialog() {
        try {
            Connection connection = this.databaseConnection.getConnection();

            // Prompting user to input values for a new entry
            String timeOutStr = JOptionPane.showInputDialog("Введите время выхода (YYYY-MM-DD HH:mm:ss)");

            // Checking if user canceled the dialog
            if (timeOutStr == null) {
                return;
            }

            // Validate the format of the time input
            if (!timeOutStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(null, "Некорректный формат времени. Используйте формат YYYY-MM-DD HH:mm:ss");
                return;
            }

            String timeInStr = JOptionPane.showInputDialog("Введите время входа (YYYY-MM-DD HH:mm:ss)");
            String autoIdStr = JOptionPane.showInputDialog("Введите ID автомобиля");
            String routeIdStr = JOptionPane.showInputDialog("Введите ID маршрута");

            // Parsing user input
            int autoId = Integer.parseInt(autoIdStr);
            int routeId = Integer.parseInt(routeIdStr);

            // Converting string input to Timestamp objects
            Timestamp timeOut = Timestamp.valueOf(timeOutStr);
            Timestamp timeIn = Timestamp.valueOf(timeInStr);

            // Preparing SQL query
            String query = "INSERT INTO journal (time_out, time_in, auto_id, route_id) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Setting parameters
            preparedStatement.setTimestamp(1, timeOut);
            preparedStatement.setTimestamp(2, timeIn);
            preparedStatement.setInt(3, autoId);
            preparedStatement.setInt(4, routeId);

            // Executing the query
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog(null, "Успешно добавлено!");
        } catch (SQLException | NumberFormatException ex) {
            if (ex.getMessage().contains("Время прибытия не может быть раньше времени отправки!")) {
                JOptionPane.showMessageDialog(null, "Время прибытия не может быть раньше времени отправки!");
            }
            if (ex.getMessage().contains("Автомобиль ещё не вернулся в парк")) {
                JOptionPane.showMessageDialog(null, "Автомобиль ещё не вернулся в парк");
            }
            else {
                ex.printStackTrace();
                logger.error("SQL error: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(null, "Ошибка!");
            }
        }
    }

    public void showDeleteJournalEntryDialog() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            String entryIdInput = JOptionPane.showInputDialog("Введите ID записи в журнале для удаления");

            // Checking if user canceled the dialog
            if (entryIdInput == null) {
                return;
            }

            // Parsing user input
            int entryId = Integer.parseInt(entryIdInput);

            // Preparing SQL query
            String query = "DELETE FROM journal WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Setting parameter
            preparedStatement.setInt(1, entryId);

            // Executing the query
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog(null, "Успешно удалено!");
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "Ошибка!");
        }
    }

    public void showUpdateJournalEntryDialog() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            String entryIdInput = JOptionPane.showInputDialog("Введите ID записи в журнале для обновления");

            // Checking if user canceled the dialog
            if (entryIdInput == null) {
                return;
            }

            String timeOut = JOptionPane.showInputDialog("Введите новое время выхода (YYYY-MM-DD HH:mm:ss)");
            String timeIn = JOptionPane.showInputDialog("Введите новое время входа (YYYY-MM-DD HH:mm:ss)");
            String autoIdStr = JOptionPane.showInputDialog("Введите новый ID автомобиля");
            String routeIdStr = JOptionPane.showInputDialog("Введите новый ID маршрута");

            // Parsing user input
            int entryId = Integer.parseInt(entryIdInput);
            int autoId = Integer.parseInt(autoIdStr);
            int routeId = Integer.parseInt(routeIdStr);

            // Preparing SQL query
            String query = "UPDATE journal SET time_out = ?, time_in = ?, auto_id = ?, route_id = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Setting parameters
            preparedStatement.setString(1, timeOut);
            preparedStatement.setString(2, timeIn);
            preparedStatement.setInt(3, autoId);
            preparedStatement.setInt(4, routeId);
            preparedStatement.setInt(5, entryId);

            // Executing the query
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog(null, "Успешно обновлено!");
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "Ошибка!");

        }
    }


    public void showJournalTable() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "SELECT * FROM journal";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] columnNames = new String[]{"ID", "Время выхода", "Время входа", "ID автомобиля", "ID маршрута"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String timeOut = resultSet.getString("time_out");
                String timeIn = resultSet.getString("time_in");
                int autoId = resultSet.getInt("auto_id");
                int routeId = resultSet.getInt("route_id");
                tableModel.addRow(new Object[]{id, timeOut, timeIn, autoId, routeId});
            }

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Журнал");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке данных.");
        }
    }
    public void findShortestTravelTime() {
        try {
            String routeIdStr = JOptionPane.showInputDialog("Введите ID маршрута для поиска самого короткого времени проезда");
            if (routeIdStr == null) {
                return;
            }

            int routeId = Integer.parseInt(routeIdStr);

            Connection connection = this.databaseConnection.getConnection();

            String query = "SELECT MIN(EXTRACT(HOUR FROM time_in - time_out) * 3600 + " +
                    "EXTRACT(MINUTE FROM time_in - time_out) * 60 + " +
                    "EXTRACT(SECOND FROM time_in - time_out)) AS shortest_time, " +
                    "auto_id " +
                    "FROM journal " +
                    "WHERE route_id = ? " +
                    "GROUP BY auto_id " +
                    "ORDER BY shortest_time ASC " +
                    "LIMIT 1";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, routeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int shortestTimeSeconds = resultSet.getInt("shortest_time");
                int hours = shortestTimeSeconds / 3600;
                int minutes = (shortestTimeSeconds % 3600) / 60;
                int seconds = shortestTimeSeconds % 60;
                int autoId = resultSet.getInt("auto_id");
                JOptionPane.showMessageDialog(null, "Самое короткое время проезда: " +
                        String.format("%02d:%02d:%02d", hours, minutes, seconds) +
                        "\nАвтомобиль, поставивший рекорд: " + autoId);
            } else {
                JOptionPane.showMessageDialog(null, "На данном маршруте пока нет записей в журнале.");
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Некорректный формат введенных данных!");
        }
    }
    public void countCarsInRoute() {
        try {
            String routeIdStr = JOptionPane.showInputDialog("Введите ID маршрута для подсчета количества автомобилей в рейсе");
            if (routeIdStr == null) {
                return;
            }

            int routeId = Integer.parseInt(routeIdStr);

            Connection connection = this.databaseConnection.getConnection();

            String query = "SELECT COUNT(DISTINCT auto_id) AS car_count " +
                    "FROM journal " +
                    "WHERE route_id = ? " +
                    "AND time_in > CURRENT_TIMESTAMP";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, routeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int carCount = resultSet.getInt("car_count");
                JOptionPane.showMessageDialog(null, "Количество автомобилей, находящихся в рейсе: " + carCount);
            } else {
                JOptionPane.showMessageDialog(null, "На данном маршруте пока нет автомобилей в рейсе.");
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Некорректный формат введенных данных!");
        }
    }
}
