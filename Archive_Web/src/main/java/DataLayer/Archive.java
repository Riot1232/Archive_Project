package DataLayer;

import java.time.LocalDate;

public class Archive {
    private String name;
    private LocalDate creationDate;

    // Конструктор
    public Archive(String name, LocalDate creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    // Сеттеры (если нужны)
    public void setName(String name) {
        this.name = name;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}