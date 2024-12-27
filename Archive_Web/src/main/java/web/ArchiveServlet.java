package web;

import DataLayer.Archive;
import Logic.ArchiveRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/archive")
public class ArchiveServlet extends HttpServlet {
    private ArchiveRepository repository = new ArchiveRepository();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String archiveName = req.getParameter("archiveName");

        resp.setContentType("application/json; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*"); // Для CORS

        try {
            switch (action) {
                case "listArchives":
                    List<Archive> archives = repository.listArchives();
                    System.out.println("Sending archives: " + archives); // Отладка
                    resp.getWriter().write(gson.toJson(archives));
                    break;
                case "listFilesInArchive":
                    if (archiveName == null || archiveName.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("{\"error\": \"Имя архива не указано\"}");
                        return;
                    }
                    List<String> files = repository.listFilesInArchive(archiveName);
                    System.out.println("Files from DB for archive " + archiveName + ": " + files);
                    resp.getWriter().write(gson.toJson(files)); // JSON-массив
                    System.out.println("Files sent as JSON array: " + gson.toJson(files));
                    break;
            }
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage()); // Отладка
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String archiveName = req.getParameter("archiveName");
        String fileName = req.getParameter("fileName");

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        try {
            switch (action) {
                case "createArchive":
                    if (archiveName == null || archiveName.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("{\"error\": \"Имя архива не указано\"}");
                        return;
                    }
                    repository.createArchive(archiveName);
                    resp.getWriter().write("{\"message\": \"Архив создан успешно\"}");
                    break;
                case "deleteArchive":
                    if (archiveName == null || archiveName.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("{\"error\": \"Имя архива не указано\"}");
                        return;
                    }
                    repository.deleteArchive(archiveName);
                    resp.getWriter().write("{\"message\": \"Архив удален успешно\"}");
                    break;
                case "addFileToArchive":
                    if (archiveName == null || archiveName.isEmpty() || fileName == null || fileName.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("{\"error\": \"Имя архива или файла не указано\"}");
                        return;
                    }
                    repository.addFileToArchive(archiveName, fileName);
                    resp.getWriter().write("{\"message\": \"Файл добавлен успешно\"}");
                    break;
                case "deleteFileFromArchive":
                    if (archiveName == null || archiveName.isEmpty() || fileName == null || fileName.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("{\"error\": \"Имя архива или файла не указано\"}");
                        return;
                    }
                    repository.deleteFileFromArchive(archiveName, fileName);
                    resp.getWriter().write("{\"message\": \"Файл удален успешно\"}");
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\": \"Неизвестное действие\"}");
            }
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}