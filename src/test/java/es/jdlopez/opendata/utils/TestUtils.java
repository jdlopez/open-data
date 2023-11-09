package es.jdlopez.opendata.utils;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.spain.Municipio;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {

    public static final String URL_OPENDATA_PREFIX = "https://raw.githubusercontent.com/jdlopez/open-data/main/";

    public static void writeToFile(String filepath, Object obj) throws IOException {
        JsonbConfig config = new JsonbConfig()
                .withNullValues(false)
                .withFormatting(true);

        Jsonb jsonb = JsonbBuilder.create(config);

        String result = jsonb.toJson(obj);
        File fout = new File(filepath);
        if (!fout.getParentFile().exists())
            fout.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(fout)) {
            fw.write(result);
        }
    }

    public static List<Municipio> buildMunicipiosByProvincia(String codProvincia) throws FileNotFoundException {
        Jsonb jsonb = JsonbBuilder.create();
        // howto get correct class: https://stackoverflow.com/questions/57462695/deserializing-json-array-with-json-b
        CodeList<Municipio> muni = jsonb.fromJson(new FileInputStream("./data/spanish/municipios.json"),
                new CodeList<Municipio>(){}.getClass().getGenericSuperclass());
        return muni.getData().stream().filter(m -> m.getProvCode().equals(codProvincia)).collect(Collectors.toList());
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

    private static DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static LocalDate safeLocalDate(String dateString) {
        return safeLocalDate(dateString, defaultFormatter, LocalDate.ofYearDay(1970, 1));
    }

    public static LocalDate safeLocalDate(String dateString, DateTimeFormatter formatter, LocalDate defaultDate) {
        dateString = safeString(dateString);
        if (dateString != null) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // fixed? today? null?
                return defaultDate;
            }
        }
        return null;
    }

}
