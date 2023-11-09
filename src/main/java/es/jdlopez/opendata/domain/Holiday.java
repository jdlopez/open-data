package es.jdlopez.opendata.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Holiday {
    private LocalDate date;
    private String name;
    private String country;
    private String region;
    private String municipality;
}
