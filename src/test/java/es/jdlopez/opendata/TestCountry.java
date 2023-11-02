package es.jdlopez.opendata;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.Country;
import es.jdlopez.opendata.utils.TestUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

import static es.jdlopez.opendata.utils.TestUtils.URL_OPENDATA_PREFIX;

public class TestCountry {

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
        country.setSource("jvm locales");
        country.setLastUpdated(LocalDateTime.now());
        country.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + "data/country_en.json"));
        country.setData(outList);

        TestUtils.writeToFile("./data/country_en.json", country);

    }
}
