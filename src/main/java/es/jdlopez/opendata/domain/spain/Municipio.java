package es.jdlopez.opendata.domain.spain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Municipio {
    // must be unique
    private String code;
    private String name;
    // first two -> standard codelist
    // next -> more info
    private String provCode;
    private String ccaaCode;
    private String dc;
    private String code3; // 3 digit code (needs prov)

}
