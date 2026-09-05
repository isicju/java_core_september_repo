package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class SalaryRecord {
    private final String name;
    private final int salary;
    private final LocalDate date;
}
