package es.jdlopez.opendata.spain;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.spain.Municipio;
import es.jdlopez.opendata.domain.spain.Provincia;
import es.jdlopez.opendata.net.SSLTrustAllCerts;
import es.jdlopez.opendata.utils.TestUtils;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.stream.Stream;

public class TestINEProvincias {

    // column format:
    //"Código
    //    INE"	Denominación Provincia	Cod.CA
    //    A(2)	A(50)	A(2)
    private static final int COL_CODIGO = 1;
    private static final int COL_NOMBRE = 2;
    private static final int COL_CA = 3;
    private static final int SKIP_ROWS = 2;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException, InterruptedException {
        /*
        FIXME: Unable to read 302 http response :-( with HttpClient
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
    ClassicHttpRequest httpGet = ClassicRequestBuilder.get("http://httpbin.org/get")
            .build();
    // The underlying HTTP connection is still held by the response object
    // to allow the response content to be streamed directly from the network socket.
    // In order to ensure correct deallocation of system resources
    // the user MUST call CloseableHttpResponse#close() from a finally clause.
    // Please note that if response content is not fully consumed the underlying
    // connection cannot be safely re-used and will be shut down and discarded
    // by the connection manager.
    httpclient.execute(httpGet, response -> {
        System.out.println(response.getCode() + " " + response.getReasonPhrase());
        final HttpEntity entity1 = response.getEntity();
        // do something useful with the response body
        // and ensure it is fully consumed
        EntityUtils.consume(entity1);
        return null;
    });


        SSLTrustAllCerts.install();
        // https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Catalogo-de-Comunidades-Autonomas.xlsx?idIniciativa=238&idElemento=426
        // https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Catalogo%20de%20Provincias.xlsx?idIniciativa=238&idElemento=427
        String source = "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Catalogo+de+Provincias.xlsx?idIniciativa=238&idElemento=427";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(source))
                .GET()
                .build();
        String resName = "data/spanish/provincias.json";
        LinkedList<Provincia> provincias = new LinkedList<>();

        SSLTrustAllCerts.install();
        HttpResponse<byte[]> response = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .sslContext(SSLTrustAllCerts.sslContext())
                .build()
                .send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
        } // download
        System.out.println("Total: " + provincias.size());
        } else {
            System.out.println( response.headers().allValues("location") );
            System.out.println("ERROR download: " + source + " code: " + response.statusCode());
        }
         */
        CodeList<Provincia> cl = new CodeList<>();

        try (InputStream is = new FileInputStream("./data/external/Catalogo de Provincias.xlsx");
             ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                // 2 lines of headers
                rows.skip(SKIP_ROWS).forEach(r -> {
                    Provincia provincia = new Provincia(
                            r.getCellText(COL_CODIGO),
                            r.getCellText(COL_NOMBRE),
                            r.getCellText(COL_CA)
                    );
                    cl.getData().add(provincia);
                });
            } // rows
        } // is
        System.out.println("Total: " + cl.getData().size());
        // now save it
        String resName = "data/spanish/provincias.json";
        cl.setSource("INE");
        cl.setName("provincias");
        cl.setDescription("Relación de provincias con sus códigos. https://www.ine.es/daco/daco42/codmun/cod_provincia.htm");
        cl.setVersion("v1.0.0.0");
        cl.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + resName));
        cl.setLastUpdated(LocalDateTime.now());
        TestUtils.writeToFile("./" + resName, cl);


/* html parser ...
        Document doc = Jsoup.connect("https://www.ine.es/daco/daco42/codmun/cod_provincia.htm").get();
        System.out.println(doc.title());
        Elements dataTable = doc.select("#contieneHtml > table > tbody > tr > td");

        for (Element cell : dataTable) {
            System.out.printf
                ("%s\n",
                    cell.text() );
        }
        CodeList<Provincia> cl = new CodeList<>();
        cl.setSource("INE");
        cl.setName("provincias");
        cl.setDescription("");
        cl.setVersion("v1.0.0.0");
        cl.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + resName));
        cl.setLastUpdated(LocalDateTime.now());
        cl.setData(provincias);
        TestUtils.writeToFile("./" + resName, cl);

 */
    }
}
