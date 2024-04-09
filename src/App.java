import java.awt.Component;
import java.awt.Desktop;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class App {
    private Routes routes;
    private AutoPersonnel autoPersonnel;
    private Auto auto;
    private Journal journal;
    private boolean userLoggedIn = false;
    DatabaseConfig config = new DatabaseConfig("src/config");
    DatabaseConnection databaseConnection;

    public App() {
        this.databaseConnection = new DatabaseConnection(this.config.getUrl(), this.config.getUser(), this.config.getPassword());
        this.routes = new Routes(this.databaseConnection);
        this.autoPersonnel = new AutoPersonnel(this.databaseConnection);
        this.auto = new Auto(this.databaseConnection);
        this.journal = new Journal(this.databaseConnection);
        JMenu userMenu = new JMenu("Пользователь");
        JMenuItem registerMenuItem = new JMenuItem("Регистрация");
        JMenuItem loginMenuItem = new JMenuItem("Вход");
        final JMenu routesMenu = new JMenu("Маршруты");
        final JMenu autoPersonnelMenu = new JMenu("Персонал");
        final JMenu autoMenu = new JMenu("Автомобили");
        final JMenu journalMenu = new JMenu("Журнал");
        final JMenu reportMenu = new JMenu("Отчет");
        final JMenu logMenu = new JMenu("Логи запросов");
        final JMenu shortest = new JMenu("Самый быстрый");
        final JMenu carsInRoute = new JMenu("Занятые авто");
        JMenuItem openLogMenuItem = new JMenuItem("Открыть логи запросов");
        logMenu.add(openLogMenuItem);
        JFrame frame = new JFrame("DB");
        frame.setDefaultCloseOperation(3);
        JMenuBar menuBar = new JMenuBar();
        userMenu.add(registerMenuItem);
        userMenu.add(loginMenuItem);
        menuBar.add(userMenu);
        menuBar.add(logMenu);
        registerMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Введите имя пользователя:");
                if (username != null) {
                    String password = JOptionPane.showInputDialog("Введите пароль:");
                    if (password != null) {
                        User user = new User(username, password);
                        if (user.register(App.this.databaseConnection.getConnection())) {
                            JOptionPane.showMessageDialog((Component) null, "Регистрация успешна, " + username + "!");
                            App.this.userLoggedIn = true;
                            routesMenu.setEnabled(App.this.userLoggedIn);
                            autoPersonnelMenu.setEnabled(App.this.userLoggedIn);
                            autoMenu.setEnabled(App.this.userLoggedIn);
                            journalMenu.setEnabled(App.this.userLoggedIn);
                            reportMenu.setEnabled(App.this.userLoggedIn);
                            logMenu.setEnabled(App.this.userLoggedIn);
                            shortest.setEnabled(App.this.userLoggedIn);
                            carsInRoute.setEnabled(App.this.userLoggedIn);
                        } else {
                            JOptionPane.showMessageDialog((Component) null, "Ошибка при регистрации.");
                        }
                    }
                }
            }
        });

        loginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Введите имя пользователя:");
                if (username != null) {
                    String password = JOptionPane.showInputDialog("Введите пароль:");
                    if (password == null) {
                        JOptionPane.showMessageDialog((Component) null, "Ошибка при регистрации.");
                    } else {
                        if (User.login(App.this.databaseConnection.getConnection(), username, password)) {
                            JOptionPane.showMessageDialog((Component) null, "Вход успешен, " + username + "!");
                            App.this.userLoggedIn = true;
                            routesMenu.setEnabled(App.this.userLoggedIn);
                            autoPersonnelMenu.setEnabled(App.this.userLoggedIn);
                            autoMenu.setEnabled(App.this.userLoggedIn);
                            journalMenu.setEnabled(App.this.userLoggedIn);
                            reportMenu.setEnabled(App.this.userLoggedIn);
                            logMenu.setEnabled(App.this.userLoggedIn);
                            shortest.setEnabled(App.this.userLoggedIn);
                            carsInRoute.setEnabled(App.this.userLoggedIn);
                        } else {
                            JOptionPane.showMessageDialog((Component) null, "Ошибка входа.");
                        }
                    }
                }
            }
        });

        routesMenu.setEnabled(this.userLoggedIn);
        autoPersonnelMenu.setEnabled(this.userLoggedIn);
        autoMenu.setEnabled(this.userLoggedIn);
        journalMenu.setEnabled(this.userLoggedIn);
        reportMenu.setEnabled(this.userLoggedIn);
        logMenu.setEnabled(this.userLoggedIn);
        shortest.setEnabled(this.userLoggedIn);
        carsInRoute.setEnabled(this.userLoggedIn);

        JMenuItem addRouteMenuItem = new JMenuItem("Добавить маршрут");
        routesMenu.add(addRouteMenuItem);
        menuBar.add(routesMenu);
        JMenuItem deleteRouteMenuItem = new JMenuItem("Удалить маршрут");
        routesMenu.add(deleteRouteMenuItem);
        menuBar.add(routesMenu);
        JMenuItem updateRouteMenuItem = new JMenuItem("Обновить маршрут");
        routesMenu.add(updateRouteMenuItem);
        menuBar.add(routesMenu);
        JMenuItem showRouteTable = new JMenuItem("Показать таблицу");
        routesMenu.add(showRouteTable);
        menuBar.add(routesMenu);

        JMenuItem addPersonnelMenuItem = new JMenuItem("Добавить сотрудника");
        autoPersonnelMenu.add(addPersonnelMenuItem);
        menuBar.add(autoPersonnelMenu);
        JMenuItem deletePersonnelMenuItem = new JMenuItem("Удалить сотрудника");
        autoPersonnelMenu.add(deletePersonnelMenuItem);
        menuBar.add(autoPersonnelMenu);
        JMenuItem updatePersonnelMenuItem = new JMenuItem("Обновить сотрудника");
        autoPersonnelMenu.add(updatePersonnelMenuItem);
        menuBar.add(autoPersonnelMenu);
        JMenuItem showPersonnelTable = new JMenuItem("Показать таблицу");
        autoPersonnelMenu.add(showPersonnelTable);
        menuBar.add(autoPersonnelMenu);

        JMenuItem addAutoMenuItem = new JMenuItem("Добавить автомобиль");
        autoMenu.add(addAutoMenuItem);
        menuBar.add(autoMenu);
        JMenuItem deleteAutoMenuItem = new JMenuItem("Удалить автомобиль");
        autoMenu.add(deleteAutoMenuItem);
        menuBar.add(autoMenu);
        JMenuItem updateAutoMenuItem = new JMenuItem("Обновить автомобиль");
        autoMenu.add(updateAutoMenuItem);
        menuBar.add(autoMenu);
        JMenuItem showAutoTable = new JMenuItem("Показать таблицу");
        autoMenu.add(showAutoTable);
        menuBar.add(autoMenu);

        JMenuItem addJournalMenuItem = new JMenuItem("Добавить запись");
        journalMenu.add(addJournalMenuItem);
        menuBar.add(journalMenu);
        JMenuItem deleteJournalMenuItem = new JMenuItem("Удалить запись");
        journalMenu.add(deleteJournalMenuItem);
        menuBar.add(journalMenu);
        JMenuItem updateJournalMenuItem = new JMenuItem("Обновить запись");
        journalMenu.add(updateJournalMenuItem);
        menuBar.add(journalMenu);
        JMenuItem showJournalTable = new JMenuItem("Показать таблицу");
        journalMenu.add(showJournalTable);
        menuBar.add(journalMenu);

        final ExcelExporter excelExporter = new ExcelExporter(this.databaseConnection);
        excelExporter.exportDataToExcel();

        JMenuItem generateReportMenuItem = new JMenuItem("Сгенерировать отчет");
        reportMenu.add(generateReportMenuItem);
        menuBar.add(reportMenu);
        /////
        JMenuItem shortestTravelTimeButton = new JMenuItem("Посмотреть");
        shortest.add(shortestTravelTimeButton);
        menuBar.add(shortest);

        JMenuItem carsInRouteButton = new JMenuItem("Посмотреть");
        carsInRoute.add(carsInRouteButton);
        menuBar.add(carsInRoute);

        frame.setJMenuBar(menuBar);
        frame.setSize(800, 400);
        frame.setLayout((LayoutManager)null);
        frame.setVisible(true);

        shortestTravelTimeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                journal.findShortestTravelTime();
            }
        });


        carsInRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                journal.countCarsInRoute();
            }
        });
///////


        addRouteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                routes.showAddRoutesDialog();
            }
        });

        deleteRouteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                routes.showDelRoutesDialog();
            }
        });

        updateRouteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                routes.showUpRoutesDialog();
            }
        });

        showRouteTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                routes.showRoutesTable();
            }
        });

        addPersonnelMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoPersonnel.showAddAutoPersonnelDialog();
            }
        });

        deletePersonnelMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoPersonnel.showDelAutoPersonnelDialog();
            }
        });

        updatePersonnelMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoPersonnel.showUpAutoPersonnelDialog();
            }
        });

        showPersonnelTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoPersonnel.showAutoPersonnelTable();
            }
        });

        addAutoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                auto.showAddAutoDialog();
            }
        });

        deleteAutoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                auto.showDelAutoDialog();
            }
        });

        updateAutoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                auto.showUpAutoDialog();
            }
        });

        showAutoTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                auto.showAutoTable();
            }
        });

        addJournalMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                journal.showAddJournalEntryDialog();
            }
        });

        deleteJournalMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                journal.showDeleteJournalEntryDialog();
            }
        });

        updateJournalMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                journal.showUpdateJournalEntryDialog();
            }
        });

        showJournalTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                journal.showJournalTable();
            }
        });

        generateReportMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excelExporter.exportDataToExcel();

                try {
                    Desktop.getDesktop().open(new File("report.xlsx"));
                } catch (IOException var4) {
                    var4.printStackTrace();
                    JOptionPane.showMessageDialog((Component)null, "Ошибка при открытии отчета.");
                }

            }
        });

        openLogMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File logFile = new File("C:/Users/alex3/IdeaProjects/BD_KURS_REAL_2/sql-squeries.log");
                    if (logFile.exists()) {
                        Desktop.getDesktop().open(logFile);
                    } else {
                        JOptionPane.showMessageDialog((Component)null, "Файл с логами не найден.");
                    }
                } catch (IOException var3) {
                    var3.printStackTrace();
                    JOptionPane.showMessageDialog((Component)null, "Ошибка при открытии файла с логами.");
                }

            }
        });
    }
}
