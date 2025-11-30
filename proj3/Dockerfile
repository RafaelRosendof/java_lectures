FROM python:3.11-slim

RUN apt-get update && apt-get install -y \
    nodejs \
    npm \
    && rm -rf /var/lib/apt/lists/*

RUN npm install -g supergateway

WORKDIR /app

# Instala yfinance e MCP SDK
RUN pip install --no-cache-dir yfinance mcp

# Copia o servidor
COPY yfinance_server.py .

EXPOSE 8084

CMD ["npx", "supergateway", "--port", "8084", "--stdio", "python yfinance_server.py"]
