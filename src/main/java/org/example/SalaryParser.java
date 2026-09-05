package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SalaryParser {
    List<SalaryRecord> parse(Path path) throws IOException;
}
