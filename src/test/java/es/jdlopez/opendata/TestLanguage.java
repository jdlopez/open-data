package es.jdlopez.opendata;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.Language;
import es.jdlopez.opendata.utils.TestUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

public class TestLanguage {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.ENGLISH);
        String[] languages = Locale.getISOLanguages();
        Locale[] locales = Locale.getAvailableLocales();

        LinkedList<Language> outList = new LinkedList<>();
        for (String c: languages) {
            Locale l =
                    Arrays.stream(locales).filter(x -> x.getLanguage().equals(c)).findFirst().orElse(null);
            if (l == null)
                System.out.println("NO Locale for :" + c);
            else {
                Language lang = new Language(l.getLanguage(), l.getDisplayLanguage(), l.getISO3Language());
                System.out.println(lang);
                outList.add(lang);
            }
        }
        System.out.println("Total: " + languages.length);
        CodeList<Language> langList = new CodeList<>();
        langList.setName("languages");
        langList.setDescription("ISO 639 standardized nomenclature used to classify languages. " +
                "Two-letter (639-1) and three-letter (639-2 and 639-3) lowercase abbreviation");
        langList.setVersion("v1.0.0");
        langList.setSource("jvm locales");
        langList.setLastUpdated(LocalDateTime.now());
        langList.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + "data/language_en.json"));
        langList.setData(outList);
        TestUtils.writeToFile("./data/language_en.json", langList);

    }
}
