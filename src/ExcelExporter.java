import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporter {
    private DatabaseConnection databaseConnection;

    public ExcelExporter(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void exportDataToExcel() {
        try {
            Connection connection = this.databaseConnection.getConnection();
            Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook());
            this.exportTableToExcel(connection, "routes", workbook);
            this.exportTableToExcel(connection, "auto_personnel", workbook);
            this.exportTableToExcel(connection, "auto", workbook);
            this.exportTableToExcel(connection, "journal", workbook);
            FileOutputStream fileOut = new FileOutputStream("report.xlsx");
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception var4) {
            var4.printStackTrace();
            System.err.println("Ошибка при экспорте данных в Excel.");
        }

    }

    private void exportTableToExcel(Connection connection, String tableName, Workbook workbook) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        Sheet sheet = workbook.createSheet(tableName);
        Row headerRow = sheet.createRow(0);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();

        int rowIdx;
        for(rowIdx = 1; rowIdx <= columnCount; ++rowIdx) {
            Cell cell = headerRow.createCell(rowIdx - 1);
            cell.setCellValue(rsmd.getColumnName(rowIdx));
        }

        rowIdx = 1;

        while(resultSet.next()) {
            Row row = sheet.createRow(rowIdx++);

            for(int i = 1; i <= columnCount; ++i) {
                Cell cell = row.createCell(i - 1);
                cell.setCellValue(resultSet.getString(i));
            }
        }

        preparedStatement.close();
    }
}
