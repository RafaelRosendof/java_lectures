#!/usr/bin/env python3
import asyncio
import yfinance as yf
from mcp.server import Server
from mcp.types import Tool, TextContent
import sys

app = Server("yfinance-mcp")

@app.list_tools()
async def list_tools() -> list[Tool]:
    return [
        Tool(
            name="get_stock_price",
            description="Get current stock price and info",
            inputSchema={
                "type": "object",
                "properties": {
                    "symbol": {
                        "type": "string", 
                        "description": "Stock symbol (e.g., AAPL, MSFT, GOOGL)"
                    }
                },
                "required": ["symbol"]
            }
        ),
        Tool(
            name="get_stock_history",
            description="Get historical stock data",
            inputSchema={
                "type": "object",
                "properties": {
                    "symbol": {"type": "string"},
                    "period": {
                        "type": "string",
                        "enum": ["1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y"],
                        "default": "1mo"
                    }
                },
                "required": ["symbol"]
            }
        )
    ]

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[TextContent]:
    try:
        if name == "get_stock_price":
            symbol = arguments["symbol"]
            ticker = yf.Ticker(symbol)
            info = ticker.info
            
            price = info.get('currentPrice', info.get('regularMarketPrice', 'N/A'))
            company = info.get('longName', symbol)
            
            result = f"{company} ({symbol})\n"
            result += f" Current Price: ${price}\n"
            result += f" Day High: ${info.get('dayHigh', 'N/A')}\n"
            result += f" Day Low: ${info.get('dayLow', 'N/A')}\n"
            result += f" Volume: {info.get('volume', 'N/A'):,}"
            
            return [TextContent(type="text", text=result)]
            
        elif name == "get_stock_history":
            symbol = arguments["symbol"]
            period = arguments.get("period", "1mo")
            
            ticker = yf.Ticker(symbol)
            hist = ticker.history(period=period)
            
            if hist.empty:
                return [TextContent(type="text", text=f"No data found for {symbol}")]
            
            result = f" Historical data for {symbol} ({period}):\n\n"
            for date, row in hist.tail(5).iterrows():
                result += f"{date.strftime('%Y-%m-%d')}: ${row['Close']:.2f}\n"
            
            return [TextContent(type="text", text=result)]
        
        return [TextContent(type="text", text="Unknown tool")]
        
    except Exception as e:
        return [TextContent(type="text", text=f"Error: {str(e)}")]

async def main():
    from mcp.server.stdio import stdio_server
    
    async with stdio_server() as (read_stream, write_stream):
        await app.run(
            read_stream,
            write_stream,
            app.create_initialization_options()
        )

if __name__ == "__main__":
    asyncio.run(main())
