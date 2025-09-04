#!/bin/bash

echo "Parando processos do sistema de forma robusta..."

# Função para parar um processo escutando em uma porta específica
stop_process_on_port() {
    PORT=$1
    # lsof -t -i:<porta> retorna apenas o PID do processo na porta
    PID=$(lsof -t -i:$PORT)

    if [ -n "$PID" ]; then
        echo "Encontrado processo com PID $PID na porta $PORT. Encerrando..."
        # kill -9 envia um sinal de encerramento forçado (SIGKILL)
        kill -9 $PID
    else
        echo "Nenhum processo encontrado na porta $PORT."
    fi
}

# 1. Parar o Gateway na sua porta fixa
stop_process_on_port 8082

# 2. Parar o TransactionalProcessor na sua porta fixa
stop_process_on_port 9002

# 3. Parar TODOS os processos de Miner, independentemente da porta
echo "Procurando por processos de Miner..."
# pgrep -f "java .* Miner" encontra os PIDs de todos os processos cujo comando contém "java" e "Miner"
MINER_PIDS=$(pgrep -f "java .* Miner")

if [ -n "$MINER_PIDS" ]; then
    echo "Encerrando PIDs dos Miners: $MINER_PIDS"
    # Mata todos os PIDs encontrados de uma vez
    kill -9 $MINER_PIDS
else
    echo "Nenhum processo de Miner encontrado."
fi

# Uma pequena pausa para o sistema operacional liberar as portas
sleep 1

echo "Limpando logs e arquivos .class..."
rm -f logs/*.log
rm -f *.class

echo "Todos os processos foram encerrados."