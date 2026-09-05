package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

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

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("No path to file was found! Usage: java Main <file> <type>");
            System.exit(1);
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("File not found " + file.getAbsolutePath());
            System.exit(1);
        }

        SalaryParser salaryRecordParser = new TextSalaryParser();

        if (args[1].equals("XML")) {
            salaryRecordParser = new XmlSalaryParser();
        } else if (args[1].equals("TXT")) {
            salaryRecordParser = new TextSalaryParser();
        } else {
            System.err.println("Not supported type, available types are XML, TXT");
            System.exit(1);
        }

        List<SalaryRecord> salaryRecords = salaryRecordParser.parse(Path.of(file.toURI()));

        System.out.println("Total salary records: " + salaryRecords.size());
        printAnalytics(salaryRecords);
        drawSalaryChart(salaryRecords);
    }

    private static void drawSalaryChart(List<SalaryRecord> records) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (SalaryRecord r : records) {
            dataset.addValue(r.getSalary(), "Salary", r.getName());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Salary by Employee",   // chart title
                "Employee",             // x-axis label
                "Salary",               // y-axis label
                dataset,
                PlotOrientation.VERTICAL,
                false,                  // include legend
                true,                   // tooltips
                false                   // URLs
        );

        File outputFile = new File("charts/salary-chart.png");
        outputFile.getParentFile().mkdirs();

        try {
            ChartUtils.saveChartAsPNG(outputFile, barChart, 800, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
