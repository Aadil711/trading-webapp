from kafka import KafkaProducer
import json
import random
import time
from colorama import Fore, Style, init

# Initialize colorama (for Windows support)
init()

# Kafka configuration
KAFKA_BROKER = 'localhost:9092'
TOPIC = 'stock_prices'

# Initialize Kafka producer
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BROKER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# List of stock symbols with their respective colors
stock_colors = {
    'AAPL': Fore.RED,       # Red for Apple
    'GOOGL': Fore.GREEN,    # Green for Google
    'MSFT': Fore.BLUE,      # Blue for Microsoft
    'TSLA': Fore.YELLOW,    # Yellow for Tesla
    'AMZN': Fore.MAGENTA    # Magenta for Amazon
}

print(Fore.CYAN + "🚀 Producing stock price data...\n" + Style.RESET_ALL)

while True:
    # Generate stock data
    stock_symbol = random.choice(list(stock_colors.keys()))
    stock_data = {
        "symbol": stock_symbol,
        "price": round(random.uniform(100, 2000), 2),
        "volume": random.randint(1000, 100000)
    }
    
    # Get the assigned color for the stock
    stock_color = stock_colors[stock_symbol]

    # Pretty print the JSON data
    formatted_data = json.dumps(stock_data, indent=4)

    # Print with color formatting
    print(stock_color + f"📡 Producing:\n{formatted_data}\n" + Style.RESET_ALL)

    # Send data to Kafka topic
    producer.send(TOPIC, stock_data)
    
    time.sleep(1)  # Generate data every second
