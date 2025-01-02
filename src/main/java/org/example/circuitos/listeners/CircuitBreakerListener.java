package org.example.circuitos.listeners;

import org.example.circuitos.enums.EstadoDoCircuito;

public interface CircuitBreakerListener {
    void onStateChange(EstadoDoCircuito newState);
}