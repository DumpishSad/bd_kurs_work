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

public class Routes {
    private DatabaseConnection databaseConnection;
    private static final Logger logger = LogManager.getLogger(Routes.class);

    public Routes(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void showAddRoutesDialog() {
        String name = JOptionPane.showInputDialog("Введите название маршрута");
        if (name != null) {
            try {
                Connection connection = this.databaseConnection.getConnection();
                String query = "INSERT INTO routes (name) VALUES (?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                JOptionPane.showMessageDialog((Component) null, "Успешно добавлено!");
            } catch (SQLException var5) {
                var5.printStackTrace();
                logger.error("SQL error: " + var5.getMessage(), var5);
                JOptionPane.showMessageDialog((Component) null, "Ошибка!");
            }
        }
    }

    public void showDelRoutesDialog() {
        String routeIdInput = JOptionPane.showInputDialog("Введите ID маршрута");
        if (routeIdInput == null) {
            return;
        }

        int routeId = Integer.parseInt(routeIdInput);

        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "DELETE FROM routes WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, routeId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog((Component) null, "Успешно удалено!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog((Component) null, "Ошибка!");
        }
    }

    public void showUpRoutesDialog() {
        int routeId = Integer.parseInt(JOptionPane.showInputDialog("Введите ID маршрута для изменения"));
        String newName = JOptionPane.showInputDialog("Введите новое название маршрута");

        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "UPDATE routes SET name = ? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, routeId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog((Component) null, "Успешно обновлено!");
        } catch (SQLException var6) {
            var6.printStackTrace();
            logger.error("SQL error: " + var6.getMessage(), var6);
            JOptionPane.showMessageDialog((Component) null, "Ошибка!");
        }
    }

    public void showRoutesTable() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "SELECT * FROM routes";
            logger.info("SQL запрос: " + query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] columnNames = new String[]{"ID", "Наименование маршрута"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                tableModel.addRow(new Object[]{id, name});
            }

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Таблица маршрутов");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int confirmed = JOptionPane.showConfirmDialog(null,
                            "Вы уверены, что хотите закрыть окно?",
                            "Подтверждение",
                            JOptionPane.YES_NO_OPTION);

                    if (confirmed == JOptionPane.YES_OPTION) {
                        frame.dispose();
                    }
                }
            });
            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
            preparedStatement.close();
        } catch (SQLException var11) {
            var11.printStackTrace();
            logger.error("SQL error: " + var11.getMessage(), var11);
            JOptionPane.showMessageDialog((Component) null, "Ошибка при загрузке данных.");
        }
    }
}
