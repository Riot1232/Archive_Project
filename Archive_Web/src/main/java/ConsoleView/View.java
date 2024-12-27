package ConsoleView;

import DataLayer.Archive;
import Logic.ArchiveRepository;

import java.util.List;
import java.util.Scanner;

public class View {
    private static final ArchiveRepository archiveRepository = new ArchiveRepository();

    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Создать архив");
            System.out.println("2. Удалить архив");
            System.out.println("3. Просмотреть архивы");
            System.out.println("4. Добавить файл в архив");
            System.out.println("5. Удалить файл из архива");
            System.out.println("6. Просмотреть файлы в архиве");
            System.out.println("7. Выйти");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Считываем остаток строки после ввода числа

            try {
                switch (choice) {
                    case 1:
                        createArchive(scanner);
                        break;
                    case 2:
                        deleteArchive(scanner);
                        break;
                    case 3:
                        listArchives();
                        break;
                    case 4:
                        addFileToArchive(scanner);
                        break;
                    case 5:
                        deleteFileFromArchive(scanner);
                        break;
                    case 6:
                        listFilesInArchive(scanner);
                        break;
                    case 7:
                        System.out.println("Выход из программы...");
                        return;
                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createArchive(Scanner scanner) {
        System.out.println("Введите имя архива:");
        String archiveName = scanner.nextLine();
        archiveRepository.createArchive(archiveName);
    }

    private void deleteArchive(Scanner scanner) {
        System.out.println("Введите имя архива для удаления:");
        String archiveName = scanner.nextLine();
        archiveRepository.deleteArchive(archiveName);
    }

    private void listArchives() {
        List<Archive> archives = archiveRepository.listArchives();
        if (archives.isEmpty()) {
            System.out.println("Архивы не найдены.");
        } else {
            for (Archive archive : archives) {
                System.out.println(archive);
            }
        }
    }

    private void addFileToArchive(Scanner scanner) {
        System.out.println("Введите имя архива:");
        String archiveName = scanner.nextLine();
        System.out.println("Введите имя файла для добавления:");
        String fileName = scanner.nextLine();
        archiveRepository.addFileToArchive(archiveName, fileName);
    }

    private void deleteFileFromArchive(Scanner scanner) {
        System.out.println("Введите имя архива:");
        String archiveName = scanner.nextLine();
        System.out.println("Введите имя файла для удаления:");
        String fileName = scanner.nextLine();
        archiveRepository.deleteFileFromArchive(archiveName, fileName);
    }

    private void listFilesInArchive(Scanner scanner) {
        System.out.println("Введите имя архива:");
        String archiveName = scanner.nextLine();
        List<String> files = archiveRepository.listFilesInArchive(archiveName);
        if (files.isEmpty()) {
            System.out.println("Файлы в архиве не найдены.");
        } else {
            for (String file : files) {
                System.out.println(file);
            }
        }
    }
}