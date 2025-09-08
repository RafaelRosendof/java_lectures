#!/bin/bash
ip="localhost"
portGateway=8082
type=$1
NUM_MINERS=$2
porta_inicial=9003

if [ -z "$type" ] || [ -z "$NUM_MINERS" ]; then
    echo "Uso: ./run.sh <UDP|TCP|GRPC|HTTP> NUM_MINERS"
    exit 1
fi

dir="./logs"
if [ ! -d "$dir" ]; then
  mkdir "$dir"
fi


echo "Compilando com Maven..."
mvn clean install
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Erro na compilação com Maven!"
    exit 1
fi


CLASSPATH="target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q 2>/dev/null)"

rm -f $dir/*.log

echo "Iniciando Gateway..."
java -cp "$CLASSPATH" Gateway $portGateway $type > $dir/gateway.log 2>&1 &
sleep 1

echo "Iniciando TransactionalProcessor..."
java -cp "$CLASSPATH" TransactionalProcessor 9002 $ip $portGateway $type > $dir/txData.log 2>&1 &

for ((i=0; i<NUM_MINERS; i++)); do
    port=$((porta_inicial + i))
    echo "Iniciando Miner na porta $port..."
    java -cp "$CLASSPATH" Miner $port $ip $portGateway $type > $dir/miner_$port.log 2>&1 &
done

echo "Todos os processos foram iniciados! Logs em $dir/"