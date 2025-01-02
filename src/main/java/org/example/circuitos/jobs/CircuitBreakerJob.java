package org.example.circuitos.jobs;

import org.example.circuitos.CircuitBreaker;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.example.circuitos.enums.EstadoDoCircuito.ABERTO;
import static org.example.circuitos.enums.EstadoDoCircuito.SEMI_ABERTO;

public class CircuitBreakerJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        CircuitBreaker circuitBreaker = (CircuitBreaker) context.getJobDetail().getJobDataMap().get("circuitBreaker");

        if (circuitBreaker.getVariaveis().getEstadoCircuito().equals(ABERTO)) {
            System.out.println("CircuitBreaker está aberto");
            circuitBreaker.getVariaveis().suavizaPercentualDeErros();
        } else if (circuitBreaker.getVariaveis().getEstadoCircuito().equals(SEMI_ABERTO)) {
            System.out.println("CircuitBreaker semi aberto. Preparando para fechamento");
            circuitBreaker.getVariaveis().suavizaPercentualDeErros();
        } else {
            System.out.println("CircuitBreaker fechado");
        }
    }

}
