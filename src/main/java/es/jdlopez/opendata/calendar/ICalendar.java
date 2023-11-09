package es.jdlopez.opendata.calendar;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class ICalendar {
    private String version;
    private String calScale;
    private String method;
    private Map<String, String> vendor;
    private String name; // X-WR-CALNAME used everywhere

    private Map<String, String> attrib = new HashMap<>();

    private List<VEvent> events = new LinkedList<>();

/*

    VERSION:2.0
    CALSCALE:GREGORIAN
    METHOD:PUBLISH
    X-WR-CALNAME:Festivos Comunidad Autónoma de Aragón 2024
    X-WR-TIMEZONE:Europe/Madrid
    X-WR-CALDESC:Festivos Comunidad Autónoma de Aragón 2024
*/

}
