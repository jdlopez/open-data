package es.jdlopez.opendata.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarBuilder {

    public static ICalendar fromCharReader(Reader reader) throws IOException {
        return fromCharReader(reader, "yyyyMMdd");
    }

    public static ICalendar fromCharReader(Reader reader, String dateFormatPattern) throws IOException {
        if (reader == null)
            throw new IllegalArgumentException("reader must be not null");
        DateTimeFormatter dt = DateTimeFormatter.ofPattern(dateFormatPattern);
        ICalendar cal = null;
        boolean inCal = false;
        boolean inEvent = false;
        VEvent currentEvent = null;
        try (BufferedReader breader = new BufferedReader(reader)) {
            String line = breader.readLine();

            while (line != null) {
                if (!line.trim().equals("")) {
                    if (line.contains("BEGIN:VCALENDAR")) { // files with BOM??
                        inCal = true;
                        cal = new ICalendar();
                    } else if (line.startsWith("BEGIN:VEVENT")) {
                        inEvent = true;
                        currentEvent = new VEvent();
                    } else if (line.startsWith("END:VEVENT")) {
                        inEvent = false;
                        cal.getEvents().add(currentEvent);
                        currentEvent = null;
                    } else if (line.startsWith("END:VCALENDAR")) {
                        inCal = false; // r we done? -> break?
                    } else if (inEvent) {
                        String[] attr = splitLine(line);
                        if (attr != null) {
                            parseAttrib(currentEvent, attr[0], attr[1], dt);
                        }
                    } else if (inCal) {
                        String[] attr = splitLine(line);
                        if (attr != null) {
                            parseAttrib(cal, attr[0], attr[1]);
                        }
                    }
                } // line not empty
                line = breader.readLine();
            } // end while
        } // try
        return cal;
    }

    private static String[] splitLine(String line) {
        if (line == null || line.indexOf(':') == -1)
            return null;
        else
            return line.split(":", 2);
    }

    private static void parseAttrib(ICalendar cal, String name, String value) {
        cal.getAttrib().put(name, value);
        if ("version".equalsIgnoreCase(name))
            cal.setVersion(value);
        else if ("CALSCALE".equalsIgnoreCase(name))
            cal.setCalScale(value);
        else if ("X-WR-CALNAME".equalsIgnoreCase(name))
            cal.setName(value);
    }

    private static void parseAttrib(VEvent event, String name, String value, DateTimeFormatter df) {
        event.getAttrib().put(name, value);

        if ("UID".equalsIgnoreCase(name))
            event.setUid(value);
        else if ("LOCATION".equalsIgnoreCase(name))
            event.setLocation(value);
        else if ("STATUS".equalsIgnoreCase(name))
            event.setStatus(value);
        else if ("DTSTART;VALUE=DATE".equalsIgnoreCase(name))
            event.setDtStart(LocalDate.from(df.parse(value)));
        else if ("DTEND;VALUE=DATE".equalsIgnoreCase(name))
            event.setDtEnd(LocalDate.from(df.parse(value)));
        else if ("SUMMARY".equalsIgnoreCase(name))
            event.setSummary(value);
        else if ("DESCRIPTION".equalsIgnoreCase(name))
            event.setDescription(value);

//        else if ("LOCATION".equalsIgnoreCase(name))
//            event.setLocation(value);
    }


}
