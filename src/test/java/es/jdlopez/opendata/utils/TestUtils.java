package es.jdlopez.opendata.utils;

import es.jdlopez.opendata.domain.CodeList;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.FileWriter;
import java.io.IOException;

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
}
