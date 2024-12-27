package Logic;

import DataLayer.Archive;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArchiveRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/archive"; // URL для подключения к базе данных
    private static final String USER = "postgres"; // Имя пользователя
    private static final String PASS = "0000"; // Пароль

    static {
        try {
            // Регистрация драйвера PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found.", e);
        }
    }

    private void throwRuntimeException(String message) {
        throw new RuntimeException(message);
    }

    public void createArchive(String archiveName) {
        if (archiveExists(archiveName)) {
            throwRuntimeException("Архив с именем " + archiveName + " уже существует.");
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ARCHIVES (NAME, CREATION_DATE) VALUES (?, ?)")) {
            stmt.setString(1, archiveName);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при создании архива " + archiveName + ": " + e.getMessage());
        }
    }

    public void deleteArchive(String archiveName) {
        if (!archiveExists(archiveName)) {
            throwRuntimeException("Архив с именем " + archiveName + " не существует.");
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM ARCHIVES WHERE NAME = ?")) {
            stmt.setString(1, archiveName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при удалении архива " + archiveName + ": " + e.getMessage());
        }
    }

    public List<Archive> listArchives() {
        List<Archive> archives = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ARCHIVES")) {
            while (rs.next()) {
                archives.add(new Archive(
                        rs.getString("NAME"),
                        rs.getDate("CREATION_DATE").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при получении списка архивов: " + e.getMessage());
        }
        return archives;
    }

    public void addFileToArchive(String archiveName, String fileName) {
        if (!archiveExists(archiveName)) {
            throwRuntimeException("Архив с именем " + archiveName + " не существует.");
        }
        if (fileExistsInArchive(archiveName, fileName)) {
            throwRuntimeException("Файл с именем " + fileName + " уже существует в архиве " + archiveName);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO FILES (ARCHIVE_ID, NAME) VALUES ((SELECT ID FROM ARCHIVES WHERE NAME = ?), ?)")) {
            stmt.setString(1, archiveName);
            stmt.setString(2, fileName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при добавлении файла " + fileName + " в архив " + archiveName + ": " + e.getMessage());
        }
    }

    public void deleteFileFromArchive(String archiveName, String fileName) {
        if (!archiveExists(archiveName)) {
            throwRuntimeException("Архив с именем " + archiveName + " не существует.");
        }
        if (!fileExistsInArchive(archiveName, fileName)) {
            throwRuntimeException("Файл с именем " + fileName + " не существует в архиве " + archiveName);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM FILES WHERE NAME = ? AND ARCHIVE_ID = (SELECT ID FROM ARCHIVES WHERE NAME = ?)")) {
            stmt.setString(1, fileName);
            stmt.setString(2, archiveName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при удалении файла " + fileName + " из архива " + archiveName + ": " + e.getMessage());
        }
    }

    public List<String> listFilesInArchive(String archiveName) {
        if (!archiveExists(archiveName)) {
            throwRuntimeException("Архив с именем " + archiveName + " не существует.");
        }
        List<String> files = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT NAME FROM FILES WHERE ARCHIVE_ID = (SELECT ID FROM ARCHIVES WHERE NAME = ?)")) {
            stmt.setString(1, archiveName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(rs.getString("NAME"));
            }
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при получении списка файлов в архиве " + archiveName + ": " + e.getMessage());
        }
        return files;
    }

    private boolean archiveExists(String archiveName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM ARCHIVES WHERE NAME = ?")) {
            stmt.setString(1, archiveName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при проверке существования архива " + archiveName + ": " + e.getMessage());
        }
        return false;
    }

    private boolean fileExistsInArchive(String archiveName, String fileName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM FILES WHERE NAME = ? AND ARCHIVE_ID = (SELECT ID FROM ARCHIVES WHERE NAME = ?)")) {
            stmt.setString(1, fileName);
            stmt.setString(2, archiveName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throwRuntimeException("Ошибка при проверке существования файла " + fileName + " в архиве " + archiveName + ": " + e.getMessage());
        }
        return false;
    }
}