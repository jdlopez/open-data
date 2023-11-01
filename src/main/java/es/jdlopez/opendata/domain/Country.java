package es.jdlopez.opendata.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    private String code;
    private String name;
    private String iso3;
    // currency an so on...
}
