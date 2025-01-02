# Circuit Breaker

**Este projeto faz parte de uma prova de conceito para um Circuit Breaker.**

Para a implementação e utilização dele em um servidor, visite o seguinte repositório:
[Servidor Teste Circuit Breaker](https://github.com/CarlosGRSchneider/servidor-teste-circuit-breaker).

## Visão Geral

O projeto demonstra o funcionamento de um Circuit Breaker, um padrão de design utilizado para aumentar a resiliência de sistemas distribuídos. Ele monitora as requisições realizadas a um servidor e, com base em métricas de erro, alterna entre os seguintes estados:

- **Fechado:** Todas as requisições são processadas normalmente.
- **Aberto:** Requisições são bloqueadas imediatamente e uma mensagem de fallback é retornada.
- **Semi-aberto:** Permite que algumas requisições sejam processadas para verificar se o serviço está estável novamente.

## Como Funciona

1. **Monitoramento de Erros:** O Circuit Breaker acompanha o percentual de erros com base na variável `percentualDeErros`. Se este valor atinge 100%, o circuito é aberto.
2. **Estados do Circuito:**
   - **Fechado:** O sistema processa todas as requisições normalmente.
   - **Aberto:** Requisições são interrompidas e uma mensagem de fallback é retornada.
   - **Semi-aberto:** Permite uma quantidade limitada de requisições para testar a recuperação do serviço.
3. **Scheduler e Job:**
   - Quando o circuito está aberto ou semi-aberto, um **scheduler** entra em ação para suavizar o percentual de erros gradualmente, possibilitando a transição de estados.
4. **Listener de Estado:** Cada alteração de estado é acompanhada por um listener que executa ações específicas, como ativar ou desativar o scheduler.

## Principais Classes

- **`CircuitBreaker`**: Implementa a lógica central do circuito, incluindo a manipulação de requisições com base no estado atual.
- **`VariaveisDoCircuito`**: Gerencia o estado do circuito e a métrica de erros.
- **`CircuitBreakerScheduler`**: Responsável por agendar tarefas que ajudam a recuperar o estado do circuito.
- **`CircuitBreakerJob`**: Executa as tarefas agendadas, como a suavização do percentual de erros.
- **`CircuitBreakerListener`**: Interface para monitorar mudanças de estado do circuito.
- **`EstadoDoCircuito`**: Enum que define os estados do Circuit Breaker (ABERTO, FECHADO, SEMI_ABERTO).

## Respostas de Requisições
- Quando o circuito está **aberto**, o usuário recebe uma mensagem de fallback:
  > "O servidor está em obras no momento, e logo estará disponível novamente."

## Tecnologias Utilizadas
- **Java**
- **Quartz Scheduler**: Para gerenciar tarefas assíncronas.
- **HttpServer**: Para simular requisições e respostas.

## Como Executar
1. Clone este repositório.
2. Compile o projeto com o Maven ou uma IDE como IntelliJ ou Eclipse.
3. Configure o servidor de teste disponível no repositório [Servidor Teste Circuit Breaker](https://github.com/CarlosGRSchneider/servidor-teste-circuit-breaker).
4. Execute a aplicação e observe as mensagens no console para verificar a mudança de estados e o comportamento do Circuit Breaker.

