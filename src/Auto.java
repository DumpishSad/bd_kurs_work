import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Auto {
    private DatabaseConnection databaseConnection;
    private static final Logger logger = LogManager.getLogger(Auto.class);

    public Auto(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void showAddAutoDialog() {
        String num = JOptionPane.showInputDialog("Введите номер автомобиля");
        String color = JOptionPane.showInputDialog("Введите цвет автомобиля");
        String mark = JOptionPane.showInputDialog("Введите марку автомобиля");
        String personnelIdInput = JOptionPane.showInputDialog("Введите ID персонала");
        if (num != null && color != null && mark != null && personnelIdInput != null) {
            try {
                int personnelId = Integer.parseInt(personnelIdInput);
                Connection connection = this.databaseConnection.getConnection();
                String query = "INSERT INTO auto (num, color, mark, personnel_id) VALUES (?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, num);
                preparedStatement.setString(2, color);
                preparedStatement.setString(3, mark);
                preparedStatement.setInt(4, personnelId);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                JOptionPane.showMessageDialog((Component) null, "Успешно добавлено!");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                logger.error("SQL error: " + e.getMessage(), e);
                JOptionPane.showMessageDialog((Component) null, "Ошибка!");
            }
        }
    }

    public void showDelAutoDialog() {
        String autoIdInput = JOptionPane.showInputDialog("Введите ID автомобиля");
        if (autoIdInput == null) {
            return;
        }

        int autoId = Integer.parseInt(autoIdInput);

        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "DELETE FROM auto WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, autoId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog((Component) null, "Успешно удалено!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog((Component) null, "Ошибка!");
        }
    }

    public void showUpAutoDialog() {
        int autoId = Integer.parseInt(JOptionPane.showInputDialog("Введите ID автомобиля для изменения"));
        String num = JOptionPane.showInputDialog("Введите новый номер автомобиля");
        String color = JOptionPane.showInputDialog("Введите новый цвет автомобиля");
        String mark = JOptionPane.showInputDialog("Введите новую марку автомобиля");
        String personnelIdInput = JOptionPane.showInputDialog("Введите ID нового персонала");

        try {
            int personnelId = Integer.parseInt(personnelIdInput);
            Connection connection = this.databaseConnection.getConnection();
            String query = "UPDATE auto SET num = ?, color = ?, mark = ?, personnel_id = ? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, num);
            preparedStatement.setString(2, color);
            preparedStatement.setString(3, mark);
            preparedStatement.setInt(4, personnelId);
            preparedStatement.setInt(5, autoId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog((Component) null, "Успешно обновлено!");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            logger.error("SQL error: " + e.getMessage(), e);
            JOptionPane.showMessageDialog((Component) null, "Ошибка!");
        }
    }

    public void showAutoTable() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "SELECT * FROM auto";
            logger.info("SQL запрос: " + query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] columnNames = new String[]{"ID", "Номер", "Цвет", "Марка", "ID персонала"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String num = resultSet.getString("num");
                String color = resultSet.getString("color");
                String mark = resultSet.getString("mark");
                int personnelId = resultSet.getInt("personnel_id");
                tableModel.addRow(new Object[]{id, num, color, mark, personnelId});
            }

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Таблица автомобилей");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Предотвращение закрытия приложения при закрытии окна
                    System.out.println("Application is closing...");
                }
            });
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog((Component) null, "Ошибка при загрузке данных.");
        }
    }
}
