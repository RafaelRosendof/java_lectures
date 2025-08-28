#!/bin/bash

ip="localhost"
portGateway=8080

type=$1
NUM_MINERS=$2
porta_inicial=9002

if [ -z "$type" ] || [ -z "$NUM_MINERS" ]; then
    echo "Uso: ./run.sh <UDP|TCP|GRPC|HTTP> NUM_MINERS"
    exit 1
fi

dir="./logs"

if [ ! -d "$dir" ]; then
  mkdir "$dir"
fi

rm -f *.class
javac *.java

rm -f $dir/*.log

echo "Iniciando Gateway..."
java Gateway $portGateway $type > $dir/gateway.log 2>&1 &
sleep 1

echo "Iniciando TransactionalProcessor..."
java TransactionalProcessor 9001 $ip $portGateway $type > $dir/txData.log 2>&1 &



for ((i=0; i<NUM_MINERS; i++)); do
    port=$((porta_inicial + i))
    echo "Iniciando Miner na porta $port..."
    java Miner $port $ip $portGateway $type > $dir/miner_$port.log 2>&1 &
    
done





echo "Todos os processos foram iniciados! Logs em $dir/"