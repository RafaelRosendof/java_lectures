#!/bin/bash

# --- VALIDAÇÃO DOS ARGUMENTOS ---
if [ "$#" -lt 3 ]; then
    echo "Erro: Faltam argumentos."
    echo "Uso: ./restart.sh <NomeComponente> <Porta> <Protocolo>"
    echo "Exemplo: ./restart.sh TransactionalProcessor 9001 GRPC"
    exit 1
fi

# --- INÍCIO DO DIAGNÓSTICO DE AMBIENTE ---
echo "--- INICIANDO DIAGNÓSTICO DE AMBIENTE ---"
echo "1. Diretório de Execução Atual (pwd):"
pwd
echo ""

echo "2. Verificando se o projeto está compilado (procurando por 'target/classes/TransactionalProcessor.class')..."
# Verifica se o arquivo .class existe a partir do diretório atual
if [ ! -f "target/classes/TransactionalProcessor.class" ]; then
    echo "---------------------------------------------------------------------"
    echo "ERRO CRÍTICO: O arquivo 'target/classes/TransactionalProcessor.class' NÃO FOI ENCONTRADO!"
    echo "CAUSA PROVÁVEL: Você não está executando este script a partir do diretório raiz do projeto (a pasta 'proj1')."
    echo "SOLUÇÃO: Navegue com 'cd' até a pasta raiz do seu projeto e execute o script de lá."
    echo "---------------------------------------------------------------------"
    exit 1
else
    echo "=> SUCESSO: Arquivo 'TransactionalProcessor.class' encontrado. O projeto parece estar compilado e no diretório correto."
fi
echo ""
# --- FIM DO DIAGNÓSTICO DE AMBIENTE ---


COMPONENT_NAME=$1
PORT=$2
TYPE=$3
IP="localhost"
PORT_GATEWAY=8082
LOG_DIR="./logs"

echo "Preparando para reiniciar o componente: $COMPONENT_NAME na porta $PORT..."

# O script continua como antes, mas agora com a certeza de que está no lugar certo
echo "Garantindo que o projeto está compilado..."
mvn compile -q
if [ $? -ne 0 ]; then
    echo "Erro na compilação com Maven! Abortando reinicialização."
    exit 1
fi

echo "Calculando classpath do Maven..."
CLASSPATH="target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q 2>/dev/null)"

LOG_FILE="$LOG_DIR/$(echo $COMPONENT_NAME | tr '[:upper:]' '[:lower:]')_$PORT.log"
JAVA_CMD=""

case "$COMPONENT_NAME" in
    "TransactionalProcessor")
        JAVA_CMD="java -cp \"$CLASSPATH\" TransactionalProcessor $PORT $IP $PORT_GATEWAY $TYPE"
        LOG_FILE="$LOG_DIR/txData.log"
        ;;
    "Miner")
        JAVA_CMD="java -cp \"$CLASSPATH\" Miner $PORT $IP $PORT_GATEWAY $TYPE"
        ;;
    "Gateway")
        JAVA_CMD="java -cp \"$CLASSPATH\" Gateway $PORT $TYPE"
        LOG_FILE="$LOG_DIR/gateway.log"
        ;;
    *)
        echo "Erro: Nome de componente '$COMPONENT_NAME' desconhecido."
        exit 1
        ;;
esac

echo "Iniciando processo com o comando:"
echo "$JAVA_CMD"
nohup $JAVA_CMD > "$LOG_FILE" 2>&1 &

sleep 1
echo "✅ Componente $COMPONENT_NAME reiniciado! Verifique o log em $LOG_FILE"