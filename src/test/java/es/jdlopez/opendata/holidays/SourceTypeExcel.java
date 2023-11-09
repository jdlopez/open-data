package es.jdlopez.opendata.holidays;

import es.jdlopez.opendata.domain.Holiday;
import es.jdlopez.opendata.domain.spain.Municipio;
import es.jdlopez.opendata.utils.TestUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class SourceTypeExcel extends SourceType {

    private int skip;
    private int colDate;
    private int colName;
    private int colType;
    private String vTypeCountry;
    private String vTypeRegion;
    private String vTypeMunicipality;
    private String countryCode;
    private String regionCode;
    private String cityCode;
    private String dateFormat;
    private List<Municipio> municipios;

    @Override
    public List<Holiday> loadFromInputStream(InputStream is) throws IOException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(this.dateFormat);
        List<Holiday> ret = new LinkedList<>();
        try (ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                // headers
                rows.skip(skip).forEach(r -> {
                    String m = null;
                    if (colType != -1) {
                        String t = r.getCellText(colType);
                        if (vTypeMunicipality.toLowerCase().contains(t.toLowerCase())) {
                            m = TestUtils.safeString( r.getCellText(Integer.parseInt(cityCode)) );
                            if (municipios != null) // mapping name to code
                                m = findCodeMunicipio(m);
                        }
                    }
                    Holiday h = new Holiday(
                            safeCellDate(r, colDate, df),
                            TestUtils.safeString( r.getCellText(colName) ),
                            countryCode,
                            regionCode,
                            m
                    );
                    if (h.getDate() != null) // not empty
                        ret.add(h);
                });
            } // rows
        } // is

        return ret;
    }

    private LocalDate safeCellDate(Row row, int index, DateTimeFormatter df) {
        try {
            Optional<LocalDateTime> dt = row.getCellAsDate(index);
            if (dt.isPresent())
                return LocalDate.from(dt.get());
            else
                return null;
        } catch (RuntimeException e) {
            return TestUtils.safeLocalDate( row.getCellText(index), df, null);
        }
    }

    private String findCodeMunicipio(String name) {
        Municipio muni = municipios.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (muni != null)
            return muni.getCode();
        else
            return null;
    }
}
