package es.jdlopez.opendata.calendar;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class VEvent {
    private LocalDate dtStart;
    private LocalDate dtEnd;
    private LocalDateTime dtStamp;
    private String uid;
    private LocalDateTime created;
    private LocalDateTime lastModified;
    private String location;
    private Integer sequence;
    private String status;
    private String summary;
    private String transp; // enum?
    private String description;
    private Map<String, String> attrib = new HashMap<>();

    /*
BEGIN:VEVENT
DTSTART;VALUE=DATE:20240329
DTEND;VALUE=DATE:20240330
DTSTAMP:20230622T114153Z
UID:CSVConvert0938aa0b94ffaa4f3935b5c0943e66ed
CREATED:19000101T120000Z
LAST-MODIFIED:20230622T114016Z
LOCATION:Comunidad Autónoma de Aragón
SEQUENCE:0
STATUS:CONFIRMED
SUMMARY:Viernes Santo
TRANSP:OPAQUE
END:VEVENT

     */
}
