package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            log.error("No path to file was found! Usage: java Main <file> <type>");
            System.exit(1);
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            log.error("File not found {}", file.getAbsolutePath());
            System.exit(1);
        }

        SalaryParser salaryRecordParser = new TextSalaryParser();

        if (args[1].equals("XML")) {
            salaryRecordParser = new XmlSalaryParser();
        } else if (args[1].equals("TXT")) {
            salaryRecordParser = new TextSalaryParser();
        } else {
            log.error("Not supported type, available types are XML, TXT");
            System.exit(1);
        }

        List<SalaryRecord> salaryRecords = salaryRecordParser.parse(Path.of(file.toURI()));

        log.info("Total salary records: {}", salaryRecords.size());
        printAnalytics(salaryRecords);
    }

    private static void printAnalytics(List<SalaryRecord> records) {
        if (records == null || records.isEmpty()) {
            log.info("No records to analyze.");
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

        log.info("========== Salary Analytics ==========");
        log.info(String.format("%-25s %d", "Total records:", stats.getCount()));
        log.info(String.format("%-25s %,.2f", "Average salary:", stats.getAverage()));
        log.info(String.format("%-25s %,d", "Total salary paid:", stats.getSum()));
        log.info("---------------------------------------");
        log.info(String.format("%-25s %,d (%s)", "Max salary:", stats.getMax(), highestPaid.getName()));
        log.info(String.format("%-25s %,d (%s)", "Min salary:", stats.getMin(), lowestPaid.getName()));
        log.info("---------------------------------------");
        log.info(String.format("%-25s %s", "Earliest record date:", earliestDate.format(fmt)));
        log.info(String.format("%-25s %s", "Latest record date:", latestDate.format(fmt)));
        log.info("=======================================");
    }

}