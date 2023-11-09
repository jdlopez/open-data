package es.jdlopez.opendata.holidays;

import es.jdlopez.opendata.domain.Holiday;
import es.jdlopez.opendata.utils.TestUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class SourceTypeCSV extends SourceType {
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
    private DateTimeFormatter df;
    private String charSet = "UTF-8";
    private int cityColumn = -1;
    private int maxCols = 0;
    private String separator = ";";

    public SourceTypeCSV(int skip, int colDate, int colName, int colType, String vTypeCountry, String vTypeRegion, String vTypeMunicipality,
                         String countryCode, String regionCode, String cityCode, String dateFormat, String charSet, String separator) {
        init(skip, colDate, colName, colType, vTypeCountry, vTypeRegion, vTypeMunicipality, countryCode, regionCode, cityCode, dateFormat, charSet, separator);
    }
    public SourceTypeCSV(int skip, int colDate, int colName, int colType, String vTypeCountry, String vTypeRegion, String vTypeMunicipality,
                         String countryCode, String regionCode, String cityCode, String dateFormat, String charSet) {
        init(skip, colDate, colName, colType, vTypeCountry, vTypeRegion, vTypeMunicipality, countryCode, regionCode, cityCode, dateFormat, charSet, ";");
    }

    protected void init(int skip, int colDate, int colName, int colType, String vTypeCountry, String vTypeRegion, String vTypeMunicipality,
                        String countryCode, String regionCode, String cityCode, String dateFormat, String charSet, String separator) {
        this.skip = skip;
        this.colDate = colDate;
        this.colName = colName;
        this.colType = colType;
        this.vTypeCountry = vTypeCountry!=null?vTypeCountry.toLowerCase():null;
        this.vTypeRegion = vTypeRegion!=null?vTypeRegion.toLowerCase():null;
        this.vTypeMunicipality = vTypeMunicipality!=null?vTypeMunicipality.toLowerCase():null;
        this.countryCode = countryCode;
        this.regionCode = regionCode;
        this.cityCode = cityCode;
        this.df = DateTimeFormatter.ofPattern(dateFormat);
        this.charSet = charSet;
        if (cityCode != null && cityCode.startsWith("col-"))
            this.cityColumn = Integer.parseInt(cityCode.substring("col-".length()));
        this.maxCols = Math.max(cityColumn, Math.max(colDate, Math.max(colName, colType))) + 1;
        this.separator = separator;
    }

    @Override
    public List<Holiday> loadFromInputStream(InputStream is) throws IOException {
        LinkedList<Holiday> ret = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charSet));
        reader.lines().skip(this.skip).filter(line -> line != null && !"".equals(line))
                .forEach(
                line -> {
                    String[] cols = line.split(separator, maxCols);
                    String c = null, r = null, m = null;
                    c = this.countryCode;
                    if (this.colType == -1) { // no type fixed type
                        r = this.regionCode;
                        m = this.cityCode;
                    } else {
                        String v = cols[this.colType].toLowerCase();
                        if (v.contains(vTypeCountry)) {
                            r = null;
                            m = null;
                        } else if (v.contains(vTypeRegion)) {
                            r = regionCode;
                            m = null;
                        } else if (v.contains(vTypeMunicipality)) {
                            r = regionCode;
                            if (cityColumn > -1)
                                m = cols[cityColumn];
                            else
                                m = cityCode;
                        } else {
                            // ignore date
                            c = null;
                        }
                    }
                    if (c != null)
                        ret.add(new Holiday(
                            TestUtils.safeLocalDate(cols[this.colDate], this.df, null),
                            cols[this.colName], c, r, m)
                    );
                });
        return ret;
    }
}
