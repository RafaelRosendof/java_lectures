#!/bin/bash

echo "Parando processos do sistema..."

pkill -f "java Gateway"
pkill -f "java Miner"
pkill -f "java TransactionalProcessor"

cd "logs"
rm -f *.log
cd ..

rm -f *.class

echo "Todos os processos foram encerrados."