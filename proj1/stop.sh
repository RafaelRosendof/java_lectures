#!/bin/bash


stop_process_on_port() {
    PORT=$1
    
    PID=$(lsof -t -i:$PORT)

    if [ -n "$PID" ]; then
        echo "Encontrado processo com PID $PID na porta $PORT. Encerrando..."
        
        kill -9 $PID
    else
        echo "Nenhum processo encontrado na porta $PORT."
    fi
}


stop_process_on_port 8082

stop_process_on_port 9002


echo "Procurando por processos de Miner..."

MINER_PIDS=$(pgrep -f "java .* Miner")

if [ -n "$MINER_PIDS" ]; then
    echo "Encerrando PIDs dos Miners: $MINER_PIDS"
    
    kill -9 $MINER_PIDS
else
    echo "Nenhum processo de Miner encontrado."
fi


sleep 1

echo "Limpando logs e arquivos .class..."
rm -f logs/*.log
rm -f *.class

mvn clean 

echo "Todos os processos foram encerrados."