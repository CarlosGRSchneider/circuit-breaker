package org.example.circuitos;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.circuitos.enums.EstadoDoCircuito;
import org.example.circuitos.listeners.CircuitBreakerListener;
import org.example.circuitos.variaveis.VariaveisDoCircuito;

import java.io.IOException;
import java.util.Random;

public class CircuitBreaker implements HttpHandler {

    private final HttpHandler handler;
    private final VariaveisDoCircuito variaveis;
    private CircuitBreakerListener listener;
    private EstadoDoCircuito estadoAnterior = EstadoDoCircuito.FECHADO;
    private Random random = new Random();

    public CircuitBreaker(HttpHandler handler, VariaveisDoCircuito variaveis) {
        this.handler = handler;
        this.variaveis = variaveis;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        confereEstadoDoCircuito();

        if (variaveis.getEstadoCircuito().equals(EstadoDoCircuito.ABERTO)) {
            System.out.println("O circuito está aberto, encerrando requisição com erro 503");
            trataCircuitoFechado(exchange);

        } else if (variaveis.getEstadoCircuito().equals(EstadoDoCircuito.SEMI_ABERTO)) {

            System.out.println("Circuito semi aberto, redirecionando quantidade tentativa de requisições");

            if (random.nextDouble() < 0.4) {
                trataCircuitoAberto(exchange);
            } else {
                trataCircuitoFechado(exchange);
            }

        } else {
            System.out.println("Fluxo normal da aplicação");
            trataCircuitoAberto(exchange);
        }
    }

    private void trataCircuitoAberto(HttpExchange exchange) throws IOException {
        handler.handle(exchange);
    }

    private void trataCircuitoFechado(HttpExchange exchange) throws IOException {

        String fallbackResponse = "O servidor está em obras no momento, e logo estará disponível novamente";
        byte[] responseBytes = fallbackResponse.getBytes("UTF-8");

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(503, responseBytes.length);


        try {
            exchange.getResponseBody().write(responseBytes);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private void confereEstadoDoCircuito() {

        EstadoDoCircuito estadoAtual = variaveis.getEstadoCircuito();
        if (listener != null && estadoAtual != estadoAnterior) {
            listener.onStateChange(estadoAtual);
            estadoAnterior = estadoAtual;
        }
    }

    public VariaveisDoCircuito getVariaveis() {
        return variaveis;
    }

    public void setCircuitBreakerListener(CircuitBreakerListener listener) {
        this.listener = listener;
    }
}
