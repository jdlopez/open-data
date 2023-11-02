package es.jdlopez.opendata.domain;

import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Code List representation
 * Inspired in: https://docs.oasis-open.org/codelist/genericode/doc/oasis-code-list-representation-genericode.html
 */
@Data
public class CodeList<T> {
    private String name;
    private String description;
    private String version;
    private String source;
    private LocalDateTime lastUpdated;
    private URL url; // definition self
    private List<T> data = new LinkedList<>();
}
