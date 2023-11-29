package es.jdlopez.opendata.spain;

import es.jdlopez.opendata.domain.Holiday;
import es.jdlopez.opendata.domain.spain.Municipio;
import es.jdlopez.opendata.holidays.HolidaySource;
import es.jdlopez.opendata.holidays.SourceTypeCSV;
import es.jdlopez.opendata.holidays.SourceTypeExcel;
import es.jdlopez.opendata.holidays.SourceTypeICS;
import es.jdlopez.opendata.net.SSLTrustAllCerts;
import es.jdlopez.opendata.utils.TestUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestHolidays {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        LinkedList<HolidaySource> sources = new LinkedList<>();
        // add here samples

        // test and fail:

        // needs custom date parser with year -> java.time.format.DateTimeParseException: Text '6 de enero' could not be parsed: Unable to obtain LocalDate from TemporalAccessor: {MonthOfYear=1, DayOfMonth=6},ISO of type java.time.format.Parsed
//        sources.add( new HolidaySource("castilla-la-mancha", "./data/holidays/spain/castilla-la-mancha/2023.json",
//                        new URL("https://datosabiertos.castillalamancha.es/sites/datosabiertos.castillalamancha.es/files/Calendario%20laboral%202023.csv"),
////                6 de enero, Epifanía del Señor.
////                6 de abril, Jueves Santo.
//                    new SourceTypeCSV(0, 0, 1, -1,
//                                    null, null, null,
//                                    "ES", "08", null,
//                                    "d 'de' MMMM", "windows-1252", ","))
//        );

        // no local codes -> text TBD: map texts to code for municipality

        sources.addAll(buildSources());

        for (HolidaySource source: sources)
            downloadSourceAndCreateJson(source);

    }

    private static LinkedList<HolidaySource> buildSources() throws MalformedURLException, FileNotFoundException {
        LinkedList<HolidaySource> sources = new LinkedList<>();

        sources.add( new HolidaySource("segsocial2023", "./data/holidays/spain/2023.json",
                new URL("https://www.seg-social.es/wps/PA_POINCALAB/CalendarioServlet?exportacion=CSV&tipo=1"),
                //COD_PROV,COD_LOC,FECHA,TIPO,DESCRIPCION
                //50,00,02-01-2023,1,"Lunes siguiente a Año Nuevo"
                new SourceTypeCSV(1, 2, 4, 3,
                        "0", "1", "2",
                        "ES", "col-0", "col-1",
                        "dd-MM-yyyy", "windows-1252", ","))
        );

        sources.add( new HolidaySource("euskad2024", "./data/holidays/spain/pais-vasco/2024.json",
                new URL("https://opendata.euskadi.eus/contenidos/ds_eventos/calendario_laboral_2024/opendata/calendario_laboral_2024.ics"),
                new SourceTypeICS("ES", "16", null, false, "yyyyMMdd'T'HHmmss", "CAE - EAE")
        ));
        sources.add( new HolidaySource("euskad2023", "./data/holidays/spain/pais-vasco/2023.json",
                new URL("https://opendata.euskadi.eus/contenidos/ds_eventos/calendario_laboral_2023/opendata/calendario_laboral_2023.ics"),
                new SourceTypeICS("ES", "16", null, false, "yyyyMMdd'T'HHmmss", "CAE - EAE")
        ));
        sources.add( new HolidaySource("euskad2022", "./data/holidays/spain/pais-vasco/2022.json",
                new URL("https://opendata.euskadi.eus/contenidos/ds_eventos/calendario_laboral_2022/opendata/calendario_laboral_2022.ics"),
                new SourceTypeICS("ES", "16", null, false, "yyyyMMdd'T'HHmmss", "CAE / EAE")
        ));
        sources.add( new HolidaySource("euskad2021", "./data/holidays/spain/pais-vasco/2021.json",
                new URL("https://opendata.euskadi.eus/contenidos/ds_eventos/calendario_laboral_2021/opendata/calendario_laboral_2021.ics"),
                new SourceTypeICS("ES", "16", null, false, "yyyyMMdd'T'HHmmss", "CAE / EAE")
        ));

        sources.add( new HolidaySource("asturias2023", "./data/holidays/spain/asturias/2023.json",
                new URL("https://sede.asturias.es/documents/217768/291977/Calendario+festivos+Asturias.xlsx/6264daab-9417-f76a-c2f4-116f8f1810f9?t=1658389031223"),
                new SourceTypeExcel(1, 2, 3, 0,
                        "ESTATAL", "AUTONOMICO", "LOCAL",
                        "ES", "03", "1", "dd/MM/yyyy", TestUtils.buildMunicipiosByProvincia("33"))
        ));

        sources.add( new HolidaySource("andalucia2024", "./data/holidays/spain/andalucia/2024.json",
                new URL("https://www.juntadeandalucia.es/datosabiertos/portal/dataset/b2dde33f-2859-4825-9cd4-e205d36cb2d4/resource/b3d9e678-d05c-4bec-9eb6-87e841e5b3fc/download/calendario-de-fiestas-laborales-de-andalucia-2024.ics"),
                new SourceTypeICS("ES", "01", null, false)
        ));
        sources.add( new HolidaySource("andalucia2023", "./data/holidays/spain/andalucia/2023.json",
                new URL("https://www.juntadeandalucia.es/datosabiertos/portal/dataset/aced5df0-3ae4-4bc3-b258-05fa541f66c2/resource/dc7cbeb7-e9d6-48fd-af22-a9d15dfcb0cc/download/calendario_fiestas_laborales_2023.ics"),
                new SourceTypeICS("ES", "01", null, false)
        ));
        sources.add( new HolidaySource("santander", "./data/holidays/spain/cantabria/santander.json",
                new URL("http://datos.santander.es/api/rest/datasets/calendario_laboral.csv?items=126&rnd=114374253"),
                //dc:identifier,dc:modified,ayto:dia,dc:description,ayto:tipo,uri
                //00000FEST000000159,2023-11-08T23:05:09.457Z,2018-11-01,Todos los Santos,FN,http://datos.santander.es/api/datos/calendario_laboral/00000fest000000159.csv
                new SourceTypeCSV(1, 2, 3, 4,
                        "FN", "FA", "FL",
                        "ES", "06", "39075",
                        "yyyy-MM-dd", "UTF-8", ","))
        );
        sources.add( new HolidaySource("barcelona", "./data/holidays/spain/cataluña/barcelona.json",
                new URL("https://ajuntament.barcelona.cat/calendarifestius/files/calendarifestius_es.ics"),
                new SourceTypeICS("ES", "09", null, false)
        ));

        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2024.json",
                new URL("https://opendata.aragon.es/ckan/dataset/1422f319-f289-4a1a-8165-2bdecc37ac32/resource/e17522dd-47c3-4ca1-8527-314ae2011c63/download/ar-open-data-festivos-comunidad-2024.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));
        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2023.json",
                new URL("https://opendata.aragon.es/ckan/dataset/033f4ac2-11ee-4194-8e4e-24d6ed4198ea/resource/18994907-ff80-44fd-b231-e9ef651752e8/download/ar-open-data-festivos-comunidad-2023.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));
        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2022.json",
                new URL("https://opendata.aragon.es/ckan/dataset/1adf2436-348f-49cf-b63d-35e3932b0f04/resource/a2ee660a-6cb3-45fe-8560-18848da6732d/download/upload_00082631.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));
        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2021.json",
                new URL("https://opendata.aragon.es/ckan/dataset/f044b702-e987-430c-a5bf-f4bd9e78c90a/resource/d9fb7396-f44a-4f8d-afc8-2deed18d8655/download/ar-open-data-festivos-comunidad-2021.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));
        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2020.json",
                new URL("https://opendata.aragon.es/ckan/dataset/5c482081-e3d1-4089-853d-157f494220a6/resource/32f6b3b0-2fde-4363-8fee-4616b0c0e630/download/ar-open-data-festivos-comunidad-2020.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));
        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2019.json",
                new URL("https://opendata.aragon.es/ckan/dataset/e0b96031-861a-49fa-9bf2-036ad7ec7458/resource/669b38ff-1fa8-4952-a12f-c3bfd3df8f13/download/ar-open-data-festivos-comunidad-2019.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));
        sources.add( new HolidaySource("aragon", "./data/holidays/spain/aragon/2018.json",
                new URL("https://opendata.aragon.es/ckan/dataset/baa30df2-9faa-44da-889e-894051a1ae8a/resource/37e86cd7-535c-475a-855c-8ba301575f1d/download/ar-open-data-festivos-comunidad-2018.ics"),
                new SourceTypeICS("ES", "02", null, true)
        ));

        sources.add( new HolidaySource("madrid", "./data/holidays/spain/madrid/madrid.json",
                new URL("https://datos.madrid.es/egob/catalogo/300082-0-calendario_laboral.csv"),
                // 01/01/2013;martes;festivo;Festivo nacional;Año Nuevo
                new SourceTypeCSV(1, 0, 4, 3,
                        "Festivo nacional", "Festivo de la Comunidad de Madrid", "Festivo local",
                        "ES", "28", "28079",
                        "dd/MM/yyyy", "UTF-8"))
        );

        sources.add( new HolidaySource("castilla-y-leon", "./data/holidays/spain/castilla-y-leon.json",
                new URL("https://datosabiertos.jcyl.es/web/jcyl/risp/es/empleo/laboral-cyl/1284165791785.csv"),
//        Fichero actualizado a fecha: 2020-04-16 14:00:40;;;
//        Nombre de la fiesta;Fecha festivo;Trasladado;Fecha disfrute
//        A�o Nuevo;01/01/2011;No;01/01/2011
                new SourceTypeCSV(2, 3, 0, -1,
                        null, null, null,
                        "ES", "07", null,
                        "dd/MM/yyyy", "windows-1252"))
        );

        // galicia no se puede unir por años, cada uno tiene una URL distinta pero sobretodo diferente formato
        // se hace uno por año
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2014.json",
                        new URL("https://ficheiros-web.xunta.gal/abertos/calendarios/CalendariodeFestivosdeGaliciapara2014.csv"),
//        Calendario de Festivos de Galicia para 2014;;;;
//        Descrici�n do festivo; Data;Lugar (Estado/Comunidade aut�noma/Concello);�mbito;ID Municipio
//        D�a das Letras Galegas;17/05/2014;Galicia;Auton�mico;
//        D�a da Constituci�n;06/12/2014;Espa�a;Estatal;
//        Luns das Dores;01/01/2014;Forcarei;Local;36018
                        new SourceTypeCSV(2, 1, 0, 3,
                                "Estatal", "Autonómico", "Local",
                                "ES", "12", "col-4",
                                "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2015.json",
                new URL("https://ficheiros-web.xunta.gal/abertos/calendarios/Calendario_laboral_2015.csv"),
                new SourceTypeCSV(2, 1, 0, 3,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2016.json",
                new URL("https://abertos.xunta.gal/catalogo/economia-empresa-emprego/-/dataset/0361/calendario-laboral-2016/002/descarga-directa-del-fichero.csv"),
                new SourceTypeCSV(2, 1, 0, 3,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2017.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2017.csv"),
                /*
                    festividade do Ecce-Homo;31/07/2017;Local;Cerdedo-Cotobade;36999
                    San Roque;21/08/2017;Local;Cerdedo-Cotobade;36999
                 */
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2018.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2018.csv"),
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2019.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2019.csv"),
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2020.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2020.csv"),
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252"))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2021.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2021.csv"),
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "dd/MM/yyyy", "windows-1252", ","))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2022.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2022.csv"),
                // 2022-08-22,San Roque,municipal,36902,Cerdedo-Cotobade
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "Local",
                        "ES", "12", "col-4",
                        "yyyy-MM-dd", "windows-1252", ","))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2023.json",
                new URL("https://www.xunta.gal/ficheiros/abertos/calendarios/calendario_laboral_2023.csv"),
                // 21/08/23,San Roque,municipal,36902,Cerdedo-Cotobade
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "municipal",
                        "ES", "12", "col-4",
                        "dd/MM/yy", "windows-1252", ","))
        );
        sources.add( new HolidaySource("galicia", "./data/holidays/spain/galicia/2024.json",
                new URL("https://ficheiros-web.xunta.gal/abertos/calendarios/calendario-laboral-2024.csv"),
                // 2024-08-19;San Roque;municipal;36902;Cerdedo-Cotobade
                new SourceTypeCSV(2, 1, 0, 2,
                        "Estatal", "Autonómico", "municipal",
                        "ES", "12", "col-4",
                        "yyyy-MM-dd", "windows-1252"))
        );
        return sources;
    }

    private static void downloadSourceAndCreateJson(HolidaySource source) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        List<Holiday> h = downloadSource(source);
        System.out.println(source.getName() + " Total: " + h.size());

        TestUtils.writeToFile(source.getResourcePath(), h);
    }

    private static List<Holiday> downloadSource(HolidaySource source) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        List<Holiday> holidays = new LinkedList<>();
        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(SSLTrustAllCerts.sslContext())
                .build();
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory).build();
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(source.getUrl().toString()).build();
            httpclient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                if (response.getCode() == 200) {
                    final HttpEntity entity1 = response.getEntity();
                    holidays.addAll(source.getType().loadFromInputStream(entity1.getContent()));
                }
                return null;
            }); // execute -> response

        } // try
        return holidays;
    }

}
