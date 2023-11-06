package es.jdlopez.opendata.spain;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.spain.UnidadOrganica;
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
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

public class TestDir3Unidades {

    /*
        C_ID_UD_ORGANICA	Código de la UNIDAD orgánica	Código que permite identificar la UNIDAD de manera unívoca
        C_DNM_UD_ORGANICA	Nombre de la UNIDAD orgánica	Denominación de la UNIDAD
        C_ID_NIVEL_ADMON	Nivel  de Administración	"Nivel  territorial  de administración  al que pertenece la UNIDAD orgánica, según el artículo 2 de la Ley 39/2015.
        Ver Catálogo de valores posibles"
        C_ID_TIPO_ENT_PUBLICA	Tipo de Entidad Pública	"Clasifica distintos tipos de entidades que constituyen la Administración Pública. Por lo general, representa una estructura de unidades orgánicas que constituye una entidad (órgano / organismo) de la Administración Pública.
        Ver Catálogo de valores posibles"

        C_ID_AMB_PROVINCIA	Código de Provincia 	Código identificador de la Provincia en la que ejerce las competencias la UNIDAD
        C_DESC_PROV	Descripción de Provincia	Descripción de la Provincia en la que ejerce las competencias la UNIDAD
        N_NIVEL_JERARQUICO	Nivel Jerárquico	Secuencial que identifica el nivel jerárquico que ocupa la unidad en la estructura de la entidad a la que pertenece
        C_ID_DEP_UD_SUPERIOR	UNIDAD Superior Jerárquica	Código de la UNIDAD orgánica inmediatamente superior de la que depende jerárquicamente la UNIDAD. Si la UNIDAD representa el máximo nivel del propio ente público, se hará referencia a ella misma.
        C_DNM_UD_ORGANICA_SUPERIOR	Nombre de la unidad superior	Denominación de la UNIDAD superior
        C_ID_DEP_UD_PRINCIPAL	UNIDAD orgánica raíz	Código de la UNIDAD orgánica, al nivel máximo de jerarquía, de la que depende la UNIDAD. Si la UNIDAD representa el máximo nivel del propio ente público, se hará referencia a ella misma.
        C_DNM_UD_ORGANICA_PRINCIPAL	Nombre UNIDAD orgánica raíz	Denominacion UNIDAD orgánica raíz
        B_SW_DEP_EDP_PRINCIPAL	Indicador de Entidad de Derecho Público	Define si la UNIDAD es o depende de una Entidad de Derecho Público, según el artículo 2 de la Ley 39/2015
        C_ID_DEP_EDP_PRINCIPAL	Unidad raíz de la Entidad de Derecho Público asociada	Código de la Entidad de Derecho Público asociada a la UNIDAD. Si la UNIDAD representa a la propia Entidad, hará referencia a ella misma.
        C_ID_DEP_EDP_PRINCIPAL	Nombre de la Unidad raíz de la Entidad de Derecho Público asociada	Nombre de la Unidad raíz de la Entidad de Derecho Público asociada
        C_ID_ESTADO	Estado	"Estatus o situación jurídico/funcional  de la UNIDAD.
        Ver Catálogo de valores posibles"
        D_VIG_ALTA_OFICIAL	Fecha de Creación Oficial	Fecha en la que se crea o establece la UNIDAD, según una disposición legal.
        NIF_CIF	NIF de la unidad	NIF de la unidad
     */
    private static final int COL_CODIGO = 1;
    private static final int COL_NOMBRE = 2;
    // column format:
    // C_ID_UD_ORGANICA
    // C_DNM_UD_ORGANICA
    // C_ID_NIVEL_ADMON
    // C_ID_TIPO_ENT_PUBLICA
    // N_NIVEL_JERARQUICO
    // C_ID_DEP_UD_SUPERIOR
    // C_DNM_UD_ORGANICA_SUPERIOR
    // C_ID_DEP_UD_PRINCIPAL
    // C_DNM_UD_ORGANICA_PRINCIPAL
    // B_SW_DEP_EDP_PRINCIPAL
    // C_ID_DEP_EDP_PRINCIPAL
    // C_DNM_UD_ORGANICA_EDP_PRINCIPAL
    // C_ID_ESTADO
    // D_VIG_ALTA_OFICIAL
    // NIF_CIF

    private static final int COL_NIVELADMON = 3;
    private static final int COL_TIPO = 4;
    private static final int COL_NIVELJERARQUICO = 5;
    private static final int COL_CODPROVINCIACOMPETENCIA = 6; // solo en EELL
    private static final int COL_UNIDADSUPERIOR = 6;
    private static final int COL_UNIDADRAIZ = 8;
    private static final int COL_ESDERECHOPUBLICO = 10;
    private static final int COL_CODUNIDADDERECHOPUBLICO = 11;
    private static final int COL_ESTADO = 13;
    private static final int COL_FECHAVIGENCIA = 14;
    private static final int COL_NIF = 15;
    private static final int SKIP_ROWS = 2;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException, InterruptedException {
        downloadUnidadesAndCreateJson(
                "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Listado+Unidades+AGE.xlsx?idIniciativa=238&idElemento=2741",
                "data/spanish/unidades-age",
                "Listado de información básica de Unidades Orgánicas de la AGE. https://administracionelectronica.gob.es/ctt/dir3"
        );
        downloadUnidadesAndCreateJson(
                "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Listado+Unidades+CCAA.xlsx?idIniciativa=238&idElemento=2742",
                "data/spanish/unidades-ccaa",
                "Listado de información básica de Unidades Orgánicas de las CCAA. https://administracionelectronica.gob.es/ctt/dir3"
        );

        downloadUnidadesAndCreateJson(
                "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Listado+Unidades+EELL.xlsx?idIniciativa=238&idElemento=2744",
                "data/spanish/unidades-eell",
                "Listado de información básica de Unidades Orgánicas de las EELL. https://administracionelectronica.gob.es/ctt/dir3",
                true
        );

        downloadUnidadesAndCreateJson(
                "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Listado+Unidades+Universidades.xlsx?idIniciativa=238&idElemento=2808",
                "data/spanish/unidades-universidades",
                "Listado de información básica de Unidades Orgánicas de las Universidades. https://administracionelectronica.gob.es/ctt/dir3"
        );
        downloadUnidadesAndCreateJson(
                "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Listado+Unidades+Institucionales.xlsx?idIniciativa=238&idElemento=2810",
                "data/spanish/unidades-institucionales",
                "Listado de información básica de Unidades Orgánicas Otras Instituciones. https://administracionelectronica.gob.es/ctt/dir3"
        );
        downloadUnidadesAndCreateJson(
                "https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Listado-unidades-organicas-Justicia.xlsx?idIniciativa=238&idElemento=7326",
                "data/spanish/unidades-justicia",
                "Listado de información básica de Unidades Orgánicas de Justicia. https://administracionelectronica.gob.es/ctt/dir3"
        );

    }

    private static void downloadUnidadesAndCreateJson(String url, String resource, String description) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        downloadUnidadesAndCreateJson(url, resource, description, false);
    }
    private static void downloadUnidadesAndCreateJson(String url, String resource, String description, boolean conProvincia) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(SSLTrustAllCerts.sslContext())
                .build();
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory).build();
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url)
                    .build();
            httpclient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                final HttpEntity entity1 = response.getEntity();
                loadUnidades(entity1.getContent(), resource, description, conProvincia);

                return null;
            });
        } // try
    }

    private static void loadUnidades(InputStream is, String resource, String description, boolean conProvincia) throws IOException {
        CodeList<UnidadOrganica> cl = new CodeList<>();
        int despProv = conProvincia?2:0; // si hay provincia se suman 2
        UnidadOrganica empty = new UnidadOrganica();
        try (ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                // 2 lines of headers
                rows.skip(SKIP_ROWS).forEach(r -> {
                    UnidadOrganica u = new UnidadOrganica(
                            TestUtils.safeString( r.getCellText(COL_CODIGO) ),
                            TestUtils.safeString( r.getCellText(COL_NOMBRE) ),
                            TestUtils.safeInteger( r.getCellText(COL_NIVELADMON) ),
                            TestUtils.safeString( r.getCellText(COL_TIPO) ),
                            TestUtils.safeInteger( r.getCellText(COL_NIVELJERARQUICO)),
                            conProvincia?r.getCellText(COL_CODPROVINCIACOMPETENCIA):null, // provincia
                            TestUtils.safeString( r.getCellText(despProv + COL_UNIDADSUPERIOR) ),
                            TestUtils.safeString( r.getCellText(despProv + COL_UNIDADRAIZ) ),
                            TestUtils.safeBoolSN( r.getCellText(despProv + COL_ESDERECHOPUBLICO) ),
                            TestUtils.safeString( r.getCellText(despProv + COL_CODUNIDADDERECHOPUBLICO) ),
                            TestUtils.safeString( r.getCellText(despProv + COL_ESTADO) ),
                            TestUtils.safeLocalDate( r.getCellText(despProv + COL_FECHAVIGENCIA) ),
                            TestUtils.safeString( r.getCellText(despProv + COL_NIF) )
                    );
                    if (u.getCode() != null) // not empty
                        cl.getData().add(u);
                });
            } // rows
        } // is
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        // now save it
        cl.setSource("DIR3");
        cl.setName(resource.substring(resource.lastIndexOf('/') + 1));
        cl.setDescription(description);
        cl.setVersion("v1.0.0.0");
        cl.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + resource + ".json"));
        cl.setLastUpdated(LocalDateTime.now());

        System.out.println("Total["+ cl.getName()+ "]: " + cl.getData().size());
        TestUtils.writeToFile("./" + resource + ".json", cl);

    }


}
