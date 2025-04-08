from kafka import KafkaConsumer
import json
from tabulate import tabulate
from colorama import Fore, Style, init

# Initialize colorama (for colored output on Windows)
init()

# Kafka configuration
KAFKA_BROKER = 'localhost:9092'
TOPIC = 'stock_prices'

# Define colors for each stock symbol
stock_colors = {
    'AAPL': Fore.RED,       # Red for Apple
    'GOOGL': Fore.GREEN,    # Green for Google
    'MSFT': Fore.BLUE,      # Blue for Microsoft
    'TSLA': Fore.YELLOW,    # Yellow for Tesla
    'AMZN': Fore.MAGENTA    # Magenta for Amazon
}

# Initialize Kafka consumer
consumer = KafkaConsumer(
    TOPIC,
    bootstrap_servers=KAFKA_BROKER,
    value_deserializer=lambda v: json.loads(v.decode('utf-8'))
)

print(Fore.CYAN + "\n📡 Listening for stock price updates...\n" + Style.RESET_ALL)

# Print table headers
print(Fore.YELLOW + tabulate(
    [["Symbol", "Price ($)", "Volume"]],
    headers="firstrow",
    tablefmt="fancy_grid"
) + Style.RESET_ALL)

# Read messages and format as a table
for message in consumer:
    stock_data = message.value

    # Get the assigned color for the stock
    stock_color = stock_colors.get(stock_data["symbol"], Fore.WHITE)

    # Convert stock data to tabular format
    table_data = [[stock_data["symbol"], stock_data["price"], stock_data["volume"]]]

    # Print stock data in its designated color
    print(stock_color + tabulate(table_data, tablefmt="fancy_grid") + Style.RESET_ALL)

