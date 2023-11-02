package es.jdlopez.opendata.spain;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.spain.Municipio;
import es.jdlopez.opendata.net.SSLTrustAllCerts;
import es.jdlopez.opendata.utils.TestUtils;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.stream.Stream;

public class TestINEMunicipios {

    // column format:
    // CODAUTO	CPRO	CMUN	DC	NOMBRE
    private static final int COL_CODAUTO = 0;
    private static final int COL_CPRO = 1;
    private static final int COL_CMUN = 2;
    private static final int COL_DC = 3;
    private static final int COL_NOMBRE = 4;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        SSLTrustAllCerts.install();
        // https://www.ine.es/daco/daco42/codmun/codmun20/20codmun.xlsx
        URL source = new URL("https://www.ine.es/daco/daco42/codmun/diccionario23.xlsx");
        LinkedList<Municipio> municipios = new LinkedList<>();

        try (InputStream is = source.openStream(); ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                // 2 lines of headers
                rows.skip(2).forEach(r -> {
                    Municipio municipio = new Municipio(
                            r.getCellText(COL_CPRO) + r.getCellText(COL_CMUN),
                            r.getCellText(COL_NOMBRE),
                            r.getCellText(COL_CPRO),
                            r.getCellText(COL_CODAUTO),
                            r.getCellText(COL_DC),
                            r.getCellText(COL_CMUN)
                    );
                    municipios.add(municipio);
                });
            } // rows
        } // download
        System.out.println("Total: " + municipios.size());
        String resName = "data/spanish/municipios.json";
        CodeList<Municipio> cl = new CodeList<>();
        cl.setSource("INE");
        cl.setName("municipios");
        cl.setDescription("Relación de todos los municipios según la denominación con la que figuran inscritos en el Registro de Entidades Locales (REL) del Ministerio de Política Territorial");
        cl.setVersion("v1.0.0.0");
        cl.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + resName));
        cl.setLastUpdated(LocalDateTime.now());
        cl.setData(municipios);
        TestUtils.writeToFile("./" + resName, cl);
    }
}
