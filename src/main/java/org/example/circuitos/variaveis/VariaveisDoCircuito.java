package org.example.circuitos.variaveis;

import org.example.circuitos.enums.EstadoDoCircuito;

import static org.example.circuitos.enums.EstadoDoCircuito.*;

public class VariaveisDoCircuito {

    private EstadoDoCircuito estadoCircuito = FECHADO;

    private int percentualDeErros = 0;
    private int taxaDeAumentoDeErros;

    public VariaveisDoCircuito(int taxaDeAumentoDeErros) {
        this.taxaDeAumentoDeErros = taxaDeAumentoDeErros;
    }


    public EstadoDoCircuito getEstadoCircuito() {
        return estadoCircuito;
    }

    public void abreCircuito() {
        estadoCircuito = ABERTO;
    }

    public void novaMetricaDeErro() {
        percentualDeErros += taxaDeAumentoDeErros;

        if (percentualDeErros >= 100) {
            this.abreCircuito();
        }
    }

    public void suavizaPercentualDeErros() {
        percentualDeErros -= 25;

        if (percentualDeErros <= 25) {
            diminuiEstadoCircuito();
        }
    }

    private void diminuiEstadoCircuito() {
        if (estadoCircuito == ABERTO && percentualDeErros <= 25) {
            estadoCircuito = SEMI_ABERTO;
        } else if (estadoCircuito == SEMI_ABERTO && percentualDeErros <= 0) {
            estadoCircuito = FECHADO;
        }
    }
}