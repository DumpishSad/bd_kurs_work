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

public class AutoPersonnel {
    private DatabaseConnection databaseConnection;
    private static final Logger logger = LogManager.getLogger(AutoPersonnel.class);

    public AutoPersonnel(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void showAddAutoPersonnelDialog() {
        String firstName = JOptionPane.showInputDialog("Введите имя сотрудника");
        String lastName = JOptionPane.showInputDialog("Введите фамилию сотрудника");
        String patherName = JOptionPane.showInputDialog("Введите отчество сотрудника");

        if (firstName != null && lastName != null && patherName != null) {
            try {
                Connection connection = this.databaseConnection.getConnection();
                String query = "INSERT INTO auto_personnel (first_name, last_name, pather_name) VALUES (?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, patherName);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                JOptionPane.showMessageDialog((Component) null, "Успешно добавлено!");
            } catch (SQLException var7) {
                var7.printStackTrace();
                logger.error("SQL error: " + var7.getMessage(), var7);
                JOptionPane.showMessageDialog((Component) null, "Ошибка!");
            }
        }
    }

    public void showDelAutoPersonnelDialog() {
        String personnelIdInput = JOptionPane.showInputDialog("Введите ID сотрудника");
        if (personnelIdInput == null) {
            return;
        }

        int personnelId = Integer.parseInt(personnelIdInput);

        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "DELETE FROM auto_personnel WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, personnelId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog((Component) null, "Успешно удалено!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("SQL error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog((Component) null, "Ошибка!");
        }
    }

    public void showUpAutoPersonnelDialog() {
        int personnelId = Integer.parseInt(JOptionPane.showInputDialog("Введите ID сотрудника для изменения"));
        String newFirstName = JOptionPane.showInputDialog("Введите новое имя сотрудника");
        String newLastName = JOptionPane.showInputDialog("Введите новую фамилию сотрудника");
        String newPatherName = JOptionPane.showInputDialog("Введите новое отчество сотрудника");

        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "UPDATE auto_personnel SET first_name = ?, last_name = ?, pather_name = ? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newFirstName);
            preparedStatement.setString(2, newLastName);
            preparedStatement.setString(3, newPatherName);
            preparedStatement.setInt(4, personnelId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            JOptionPane.showMessageDialog((Component) null, "Успешно обновлено!");
        } catch (SQLException var7) {
            var7.printStackTrace();
            logger.error("SQL error: " + var7.getMessage(), var7);
            JOptionPane.showMessageDialog((Component) null, "Ошибка!");
        }
    }

    public void showAutoPersonnelTable() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            String query = "SELECT * FROM auto_personnel";
            logger.info("SQL запрос: " + query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] columnNames = new String[]{"ID", "Имя", "Фамилия", "Отчество"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String patherName = resultSet.getString("pather_name");
                tableModel.addRow(new Object[]{id, firstName, lastName, patherName});
            }

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Таблица сотрудников");
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
