package br.com.kjscripts.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Vehicle(@JsonAlias("Valor") String value,
                      @JsonAlias("Marca") String brand,
                      @JsonAlias("Modelo") String model,
                      @JsonAlias("AnoModelo") Integer yearModel,
                      @JsonAlias("Combustivel") String fuel,
                      @JsonAlias("CodigoFipe") String fipeCodode) {
}
