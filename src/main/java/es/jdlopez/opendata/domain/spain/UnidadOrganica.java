package es.jdlopez.opendata.domain.spain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnidadOrganica {
    private String code;
    private String name;
    // first two -> standard codelist
    // next -> more info
    private Integer nivelAdmon;
    private String tipo;
    private Integer nivelJerarquico;
    private String codProvinciaCompetencia;
    private String unidadSuperior;
    private String unidadRaiz;
    private Boolean esDerechoPublico;
    private String codUnidadDerechoPublico;
    private String estado;
    private LocalDate fechaVigencia;
    private String nif;
}
