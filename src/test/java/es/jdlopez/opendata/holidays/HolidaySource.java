package es.jdlopez.opendata.holidays;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidaySource {
    private String name;
    private String resourcePath;
    private URL url;
    private SourceType type;

}
