package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static Pattern pattern = Pattern.compile("^(\\w+),(\\d+),(\\d{2}-\\d{2}-\\d{4})$");
    static int NAME_COLUMN = 1;
    static int SALARY_COLUMN = 2;
    static int SALARY_DATE_COLUMN = 3;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("No path to file was found! Usage: java Main <file>");
            System.exit(1);
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("File not found " + file.getAbsolutePath());
            System.exit(1);
        }

        Path filePath = file.toPath();
        List<String> allLines = Files.readAllLines(filePath);
        List<SalaryRecord> resultRecords = new ArrayList<>();

        for (String line : allLines) {
            Matcher matcher = pattern.matcher(line);
            try {
                if (matcher.matches()) {
                    String name = matcher.group(NAME_COLUMN);
                    int salary = Integer.parseInt(matcher.group(SALARY_COLUMN));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate localDate = LocalDate.parse(matcher.group(SALARY_DATE_COLUMN), formatter);
                    resultRecords.add(new SalaryRecord(name, salary, localDate));
                }
            } catch (Exception e) {
                System.err.println("Error while parsing line " + line);
            }
        }
        System.out.println("Total salary records: " + resultRecords.size());
        printAnalytics(resultRecords);
    }

    private static void printAnalytics(List<SalaryRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("No records to analyze.");
            return;
        }

        IntSummaryStatistics stats = records.stream()
                .mapToInt(SalaryRecord::getSalary)
                .summaryStatistics();

        SalaryRecord highestPaid = records.stream()
                .max(Comparator.comparingInt(SalaryRecord::getSalary))
                .orElseThrow();

        SalaryRecord lowestPaid = records.stream()
                .min(Comparator.comparingInt(SalaryRecord::getSalary))
                .orElseThrow();

        LocalDate earliestDate = records.stream()
                .map(SalaryRecord::getDate)
                .min(LocalDate::compareTo)
                .orElseThrow();

        LocalDate latestDate = records.stream()
                .map(SalaryRecord::getDate)
                .max(LocalDate::compareTo)
                .orElseThrow();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        System.out.println("========== Salary Analytics ==========");
        System.out.printf("%-25s %d%n", "Total records:", stats.getCount());
        System.out.printf("%-25s %,.2f%n", "Average salary:", stats.getAverage());
        System.out.printf("%-25s %,d%n", "Total salary paid:", stats.getSum());
        System.out.println("---------------------------------------");
        System.out.printf("%-25s %,d (%s)%n", "Max salary:", stats.getMax(), highestPaid.getName());
        System.out.printf("%-25s %,d (%s)%n", "Min salary:", stats.getMin(), lowestPaid.getName());
        System.out.println("---------------------------------------");
        System.out.printf("%-25s %s%n", "Earliest record date:", earliestDate.format(fmt));
        System.out.printf("%-25s %s%n", "Latest record date:", latestDate.format(fmt));
        System.out.println("=======================================");
    }

}
