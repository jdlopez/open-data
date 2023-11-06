package es.jdlopez.opendata.utils;

import es.jdlopez.opendata.domain.CodeList;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TestUtils {

    public static final String URL_OPENDATA_PREFIX = "https://raw.githubusercontent.com/jdlopez/open-data/main/";

    public static void writeToFile(String filepath, CodeList<?> codeList) throws IOException {
        JsonbConfig config = new JsonbConfig()
                .withNullValues(true)
                .withFormatting(true);

        Jsonb jsonb = JsonbBuilder.create(config);

        String result = jsonb.toJson(codeList);
        try (FileWriter fw = new FileWriter(filepath)) {
            fw.write(result);
        }
    }

    public static Integer safeBigDecimalToInteger(BigDecimal bd) {
        return bd == null?null:bd.intValue();
    }

    public static Integer safeInteger(String value) {
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    public static Boolean safeBoolSN(String siNoText) {
        return "S".equalsIgnoreCase( siNoText );
    }

    public static String safeString(String value) {
        if (value != null) {
            value = value.trim();
            if ("".equals(value))
                return null;
            else
                return value;
        }
        return null;
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static LocalDate safeLocalDate(String dateString) {
        dateString = safeString(dateString);
        if (dateString != null) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // fixed? today?
                return LocalDate.ofYearDay(1970, 1);
            }
        }
        return null;
    }

}
