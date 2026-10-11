package Modelo;

import java.util.Optional;

// Marcela Landaeta
public class SensorViento extends Sensor implements Alertable {

    public SensorViento(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public String getUnidad() {
        return "km/h";
    }

    @Override
    public boolean esValorAdmisible(float valor) {
        return valor >= 0 && valor <= 250;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.VIENTO;
    }

    // Métodos de alertable
    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {
        if (valor >= 90) {
            return Optional.of(NivelAlerta.ALARMA);
        } else if (valor >= 70) {
            return Optional.of(NivelAlerta.ALERTA);
        } else if (valor >= 50) {
            return Optional.of(NivelAlerta.AVISO);
        }
        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {
        return "Viento intenso";
    }
}