package es.jdlopez.opendata.holidays;

import es.jdlopez.opendata.calendar.CalendarBuilder;
import es.jdlopez.opendata.calendar.ICalendar;
import es.jdlopez.opendata.calendar.VEvent;
import es.jdlopez.opendata.domain.Holiday;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
public class SourceTypeICS extends SourceType {
    private String countryCode;
    private String regionCode;
    private String cityCode;
    private boolean descOrSummary = true;
    private String eventDateFormatPattern = "yyyyMMdd";
    private String filterLocation;

    public SourceTypeICS(String countryCode, String regionCode, String cityCode, boolean descOrSummary) {
        this.countryCode = countryCode;
        this.regionCode = regionCode;
        this.cityCode = cityCode;
        this.descOrSummary = descOrSummary;
    }

    @Override
    public List<Holiday> loadFromInputStream(InputStream is) throws IOException {
        ICalendar cal = CalendarBuilder.fromCharReader(new InputStreamReader(is), eventDateFormatPattern);
        List<Holiday> ret = new LinkedList<>();
        for (VEvent e: cal.getEvents()) {
            if (filterLocation != null) {
                if (e.getLocation().equalsIgnoreCase(filterLocation))
                    ret.add(new Holiday(e.getDtStart(), descOrSummary(e), this.countryCode, this.regionCode, this.cityCode));
            } else
                ret.add(new Holiday(e.getDtStart(), descOrSummary(e), this.countryCode, this.regionCode, this.cityCode));
        }
        return  ret;
    }

    private String descOrSummary(VEvent e) {
        if (this.descOrSummary)
            return e.getDescription() != null? e.getDescription(): e.getSummary();
        else
            return e.getSummary() != null? e.getSummary(): e.getDescription();
    }
}
