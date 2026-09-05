import java.time.LocalDate;

public class SalaryRecord {
    private final String name;
    private final int salary;
    private final LocalDate date;

    public SalaryRecord(String name, int salary, LocalDate date) {
        this.name = name;
        this.salary = salary;
        this.date = date;
    }

    public int getSalary() {
        return salary;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
