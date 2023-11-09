package es.jdlopez.opendata.calendar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;

class CalendarBuilderTest {

    @Test
    public void testLoadCalendar() throws IOException {
        ICalendar cal = CalendarBuilder
                .fromCharReader(new FileReader("./data/external/ar-open-data-festivos-comunidad-2024.ics"));
        System.out.println("Calendar: version" + cal.getVersion() + " event_size=" + cal.getEvents().size());

        Assertions.assertEquals(cal.getAttrib().get("VERSION"), "2.0");
        Assertions.assertEquals(cal.getEvents().size(), 12);
    }

}