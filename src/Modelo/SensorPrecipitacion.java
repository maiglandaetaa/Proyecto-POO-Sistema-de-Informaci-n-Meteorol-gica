package Modelo;

import java.util.Optional;

// Marcela Landaeta
public class SensorPrecipitacion extends Sensor implements Alertable{

    public SensorPrecipitacion(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public String getUnidad() {
        return "mm";
    }

    @Override
    public boolean esValorAdmisible(float valor) {
        return valor >= 0 && valor <= 500;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.PRECIPITACION;
    }

    // Métodos de alertable
    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {
        if (valor >= 60) {
            return Optional.of(NivelAlerta.ALARMA);
        } else if (valor >= 40) {
            return Optional.of(NivelAlerta.ALERTA);
        } else if (valor >= 20) {
            return Optional.of(NivelAlerta.AVISO);
        }
        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {
        return "Precipitación intensa";
    }
}