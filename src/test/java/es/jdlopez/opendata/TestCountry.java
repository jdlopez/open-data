package es.jdlopez.opendata;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.Country;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

public class TestCountry {
    private static final String PREFIX = "https://raw.githubusercontent.com/jdlopez/open-data/main/";

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.ENGLISH);
        String[] countries = Locale.getISOCountries();
        Locale[] locales = Locale.getAvailableLocales();

        LinkedList<Country> outList = new LinkedList<>();
        for (String c: countries) {
            Locale l =
                    Arrays.stream(locales).filter(x -> x.getCountry().equals(c)).findFirst().orElse(null);
            if (l == null)
                System.out.println("NO Locale for :" + c);
            else {
                Country country = new Country(l.getCountry(), l.getDisplayCountry(), l.getISO3Country());
                System.out.println(country);
                outList.add(country);
            }
        }
        System.out.println("Total: " + countries.length);
        CodeList<Country> country = new CodeList<>();
        country.setName("countries");
        country.setDescription("ISO 3166. Country Codes. The International Standard for country codes and codes for their subdivisions");
        country.setVersion("v1.0.0");
        country.setLastUpdated(LocalDateTime.now());
        country.setUrl(new URL(PREFIX + "data/country_en.json"));
        country.setData(outList);
        JsonbConfig config = new JsonbConfig()
                .withNullValues(true)
                .withFormatting(true);

        Jsonb jsonb = JsonbBuilder.create(config);

        String result = jsonb.toJson(country);
        FileWriter fw = new FileWriter("./data/country_en.json");
        fw.write(result);
        fw.close();

    }
}
