package org.example.circuitos.schedulers;

import org.example.circuitos.CircuitBreaker;
import org.example.circuitos.enums.EstadoDoCircuito;
import org.example.circuitos.jobs.CircuitBreakerJob;
import org.example.circuitos.listeners.CircuitBreakerListener;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class CircuitBreakerScheduler implements CircuitBreakerListener {
    private final CircuitBreaker circuitBreaker;
    private Scheduler scheduler;

    public CircuitBreakerScheduler(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateChange(EstadoDoCircuito novoEstado) {
        try {
            if (novoEstado == EstadoDoCircuito.ABERTO || novoEstado == EstadoDoCircuito.SEMI_ABERTO) {
                System.out.println("Circuito aberto. O Scheduler será ativado");
                startScheduler();
            } else {
                System.out.println("O circuito foi fechado novamente. O Scheduler será pausado");
                stopScheduler();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void startScheduler() throws SchedulerException {
        scheduler.clear();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("circuitBreaker", circuitBreaker);

        JobDetail job = JobBuilder.newJob(CircuitBreakerJob.class)
                .withIdentity("circuitBreakerJob", circuitBreaker.toString())
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("circuitBreakerTrigger", circuitBreaker.toString())
                .startAt(DateBuilder.futureDate(20, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(20)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);

        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }


    private void stopScheduler() throws SchedulerException {
        if (scheduler.isStarted()) {
            scheduler.clear();
            scheduler.standby();
            System.out.println("Scheduler está pausado.");
        }
    }

    public void shutdown() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                System.out.println("O Scheduler foi desligado");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}