package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSalaryParser implements SalaryParser{

    static Pattern pattern = Pattern.compile("^(\\w+),(\\d+),(\\d{2}-\\d{2}-\\d{4})$");
    static int NAME_COLUMN = 1;
    static int SALARY_COLUMN = 2;
    static int SALARY_DATE_COLUMN = 3;


    @Override
    public List<SalaryRecord> parse(Path path) throws IOException {
        List<String> allLines = Files.readAllLines(path);
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
        return resultRecords;
    }
}
