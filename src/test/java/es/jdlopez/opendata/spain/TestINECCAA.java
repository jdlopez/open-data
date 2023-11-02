package es.jdlopez.opendata.spain;

import es.jdlopez.opendata.domain.CodeList;
import es.jdlopez.opendata.domain.spain.ComunidadAutonoma;
import es.jdlopez.opendata.domain.spain.Provincia;
import es.jdlopez.opendata.utils.TestUtils;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class TestINECCAA {

    // column format:
    // "Código
    // INE"	Comunidad Autónoma	Cod.RCP	Cod.DIR2
    // A(2)	A(50)	A(2)	N(2)
    private static final int COL_CODIGO = 1;
    private static final int COL_NOMBRE = 2;
    private static final int COL_RCP = 3;
    private static final int COL_DIR2 = 4;
    private static final int SKIP_ROWS = 2;

    public static void main(String[] args) throws IOException {
        CodeList<ComunidadAutonoma> cl = new CodeList<>();

        try (InputStream is = new FileInputStream("./data/external/Catalogo-de-Comunidades-Autonomas.xlsx");
             ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                // 2 lines of headers
                rows.skip(SKIP_ROWS).forEach(r -> {
                    ComunidadAutonoma itm = new ComunidadAutonoma(
                            r.getCellText(COL_CODIGO),
                            r.getCellText(COL_NOMBRE),
                            r.getCellText(COL_RCP),
                            r.getCellText(COL_DIR2)
                    );
                    cl.getData().add(itm);
                });
            } // rows
        } // is
        System.out.println("Total: " + cl.getData().size());
        // now save it
        String resName = "data/spanish/ccaa.json";
        cl.setSource("INE");
        cl.setName("comunidades-autonomas");
        cl.setDescription("Relación de comunidades y ciudades autónomas con sus códigos. https://www.ine.es/daco/daco42/codmun/cod_ccaa.htm");
        cl.setVersion("v1.0.0.0");
        cl.setUrl(new URL(TestUtils.URL_OPENDATA_PREFIX + resName));
        cl.setLastUpdated(LocalDateTime.now());
        TestUtils.writeToFile("./" + resName, cl);

    }
}
