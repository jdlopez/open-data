package es.jdlopez.opendata.domain.spain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Provincia {
    private String code;
    private String name;
    // first two -> standard codelist
    // next -> more info
    private String ccaaCode;

}
