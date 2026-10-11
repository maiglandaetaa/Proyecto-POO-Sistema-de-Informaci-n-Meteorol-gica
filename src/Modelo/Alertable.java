package Modelo;

import java.util.Optional;

public interface Alertable {
    public Optional<NivelAlerta> evaluaAlerta(float valor);

    public String getFenomeno(float valor);

    default boolean generaAlerta(float valor) {
        return evaluaAlerta(valor).isPresent();
    }
}