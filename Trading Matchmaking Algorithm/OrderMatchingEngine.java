import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;


public class OrderMatchingEngine extends JFrame {

    // Order book holds buy orders (bids) and sell orders (asks)
    private final PriorityQueue<Order> buyOrders; // Max heap for buy orders (highest price first)
    private final PriorityQueue<Order> sellOrders; // Min heap for sell orders (lowest price first)
    private long nextOrderId = 1;
    private final List<Trade> executedTrades;
    private final List<Double> tradeHistory; // Records prices of executed trades
    private double lastTradePrice = 50.0; // Starting price point
    private final String[] availableSymbols = {"AAPL", "MSFT", "GOOGL", "AMZN", "TSLA"};
    
    // Visualization components
    private final JPanel tradingPanel;
    private final JPanel orderBookPanel;
    private final JTextArea logArea;
    private javax.swing.Timer orderGenerationTimer; // Explicitly using javax.swing.Timer
    private final JButton startButton;
    private final JButton stopButton;
    private final JComboBox<String> speedSelector;
    
    // Trading parameters
    private double basePrice = 50.0;
    private final double volatility = 0.05;
    private int maxOrderSize = 1000;

    public OrderMatchingEngine() {
        super("Order Matching Engine Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        
        // Buy orders sorted by price (descending) and then by time (ascending)
        buyOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o2.getPrice(), o1.getPrice()); // Higher buy prices first
            }
            return Long.compare(o1.getTimestamp(), o2.getTimestamp()); // Earlier orders first
        });

        // Sell orders sorted by price (ascending) and then by time (ascending)
        sellOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o1.getPrice(), o2.getPrice()); // Lower sell prices first
            }
            return Long.compare(o1.getTimestamp(), o2.getTimestamp()); // Earlier orders first
        });

        executedTrades = new ArrayList<>();
        tradeHistory = new ArrayList<>();
        
        // Create UI layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Control panel at top
        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Trading");
        stopButton = new JButton("Stop Trading");
        stopButton.setEnabled(false);
        String[] speeds = {"Slow", "Medium", "Fast", "Very Fast"};
        speedSelector = new JComboBox<>(speeds);
        speedSelector.setSelectedIndex(1);
        
        controlPanel.add(new JLabel("Trading Speed:"));
        controlPanel.add(speedSelector);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        // Trading graph panel in center
        tradingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTradingGraph(g);
            }
        };
        tradingPanel.setBackground(Color.WHITE);
        
        // Order book on the right
        orderBookPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawOrderBook(g);
            }
        };
        orderBookPanel.setBackground(Color.WHITE);
        orderBookPanel.setPreferredSize(new Dimension(300, 0));
        
        // Log panel at bottom
        logArea = new JTextArea(5, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        
        // Add components to main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tradingPanel, orderBookPanel);
        centerSplit.setResizeWeight(0.7);
        mainPanel.add(centerSplit, BorderLayout.CENTER);
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add event handlers
        startButton.addActionListener(e -> startTrading());
        stopButton.addActionListener(e -> stopTrading());
        
        // Initialize with some data
        for (int i = 0; i < 10; i++) {
            generateRandomOrder();
        }
    }
    
    private void startTrading() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        // Determine timer delay based on selected speed
        int delay;
        switch (speedSelector.getSelectedIndex()) {
            case 0: delay = 2000; break; // Slow
            case 1: delay = 1000; break; // Medium
            case 2: delay = 500; break;  // Fast
            case 3: delay = 200; break;  // Very Fast
            default: delay = 1000;
        }
        
        orderGenerationTimer = new javax.swing.Timer(delay, e -> {
            generateRandomOrder();
            repaint();
        });
        orderGenerationTimer.start();
    }
    
    private void stopTrading() {
        if (orderGenerationTimer != null) {
            orderGenerationTimer.stop();
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
    
    private void drawTradingGraph(Graphics g) {
        int width = tradingPanel.getWidth();
        int height = tradingPanel.getHeight();
        
        // Draw axes
        g.setColor(Color.BLACK);
        g.drawLine(50, height - 50, width - 50, height - 50); // X-axis
        g.drawLine(50, 50, 50, height - 50); // Y-axis
        
        // Draw axis labels
        g.drawString("Time →", width / 2, height - 20);
        g.drawString("Price", 10, height / 2);
        
        // Draw price labels on Y-axis
        double minPrice = basePrice * 0.8;
        double maxPrice = basePrice * 1.2;
        for (int i = 0; i <= 10; i++) {
            int y = height - 50 - (int)(i * (height - 100) / 10.0);
            double price = minPrice + (maxPrice - minPrice) * i / 10.0;
            g.drawString(String.format("%.2f", price), 10, y);
            g.drawLine(45, y, 50, y); // Tick mark
        }
        
        // Draw trade history
        if (tradeHistory.size() > 1) {
            g.setColor(Color.BLUE);
            int pointWidth = Math.max(1, (width - 100) / Math.max(100, tradeHistory.size()));
            
            for (int i = 0; i < tradeHistory.size() - 1; i++) {
                int x1 = 50 + i * pointWidth;
                int x2 = 50 + (i + 1) * pointWidth;
                
                double price1 = tradeHistory.get(i);
                double price2 = tradeHistory.get(i + 1);
                
                int y1 = height - 50 - (int)((price1 - minPrice) / (maxPrice - minPrice) * (height - 100));
                int y2 = height - 50 - (int)((price2 - minPrice) / (maxPrice - minPrice) * (height - 100));
                
                g.drawLine(x1, y1, x2, y2);
                
                // Draw point for each trade
                if (i == tradeHistory.size() - 2) {
                    g.setColor(Color.RED);
                    g.fillOval(x2 - 3, y2 - 3, 6, 6);
                }
            }
        }
    }
    
    private void drawOrderBook(Graphics g) {
        int width = orderBookPanel.getWidth();
        int height = orderBookPanel.getHeight();
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("ORDER BOOK", width / 2 - 50, 20);
        
        g.drawLine(0, 30, width, 30);
        g.drawString("BUY (BIDs)", 20, 50);
        g.drawString("SELL (ASKs)", width - 120, 50);
        
        g.drawLine(width / 2, 30, width / 2, height);
        
        // Draw buy orders (left side)
        List<Order> buyOrdersList = new ArrayList<>(buyOrders);
        buyOrdersList.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
        
        for (int i = 0; i < Math.min(15, buyOrdersList.size()); i++) {
            Order order = buyOrdersList.get(i);
            g.setColor(new Color(0, 150, 0));
            String orderText = String.format("%.2f × %d", order.getPrice(), order.getQuantity());
            g.drawString(orderText, 20, 80 + i * 20);
        }
        
        // Draw sell orders (right side)
        List<Order> sellOrdersList = new ArrayList<>(sellOrders);
        sellOrdersList.sort((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
        
        for (int i = 0; i < Math.min(15, sellOrdersList.size()); i++) {
            Order order = sellOrdersList.get(i);
            g.setColor(new Color(180, 0, 0));
            String orderText = String.format("%.2f × %d", order.getPrice(), order.getQuantity());
            g.drawString(orderText, width / 2 + 20, 80 + i * 20);
        }
        
        // Draw last price
        if (!tradeHistory.isEmpty()) {
            g.setColor(Color.BLUE);
            g.setFont(new Font("Monospaced", Font.BOLD, 16));
            g.drawString("Last: " + String.format("%.2f", tradeHistory.get(tradeHistory.size() - 1)), 
                         width / 2 - 50, height - 20);
        }
    }
    
    /**
     * Generate a random order with realistic price behavior
     */
    private void generateRandomOrder() {
        // Randomly choose a symbol
        String symbol = availableSymbols[ThreadLocalRandom.current().nextInt(availableSymbols.length)];
        
        // Determine if it's a buy or sell order (slightly more buys than sells when price is low,
        // and more sells than buys when price is high)
        double priceRatio = lastTradePrice / basePrice;
        double buyProbability = 0.5 - (priceRatio - 1.0) * 0.3;
        buyProbability = Math.max(0.3, Math.min(0.7, buyProbability));
        
        String side = ThreadLocalRandom.current().nextDouble() < buyProbability ? "BUY" : "SELL";
        
        // Generate a price based on the last trade price with some randomness
        double priceVariation = lastTradePrice * volatility * (ThreadLocalRandom.current().nextDouble() * 2 - 1);
        double price = lastTradePrice + priceVariation;
        
        // If it's a buy order, typically bid slightly below last price
        // If it's a sell order, typically ask slightly above last price
        if (side.equals("BUY")) {
            price -= lastTradePrice * volatility * 0.5 * ThreadLocalRandom.current().nextDouble();
        } else {
            price += lastTradePrice * volatility * 0.5 * ThreadLocalRandom.current().nextDouble();
        }
        
        price = Math.max(0.01, price); // Ensure price is positive
        
        // Generate random quantity
        int quantity = ThreadLocalRandom.current().nextInt(10, maxOrderSize + 1);
        
        // Submit the order
        long orderId = submitOrder(side, quantity, price, symbol);
        
        // Log the order
        logMessage("Generated " + side + " order #" + orderId + ": " + quantity + " @ $" + String.format("%.2f", price));
    }
    
    /**
     * Submit a new order to the matching engine and process any matches.
     * 
     * @param side      "BUY" or "SELL"
     * @param quantity  Number of units to trade
     * @param price     Price per unit (use -1 for market orders)
     * @param symbol    Trading symbol (e.g., "AAPL")
     * @return The ID of the created order
     */
    public long submitOrder(String side, int quantity, double price, String symbol) {
        Order order = new Order(nextOrderId++, side, quantity, price, symbol, System.currentTimeMillis());
        
        if (side.equals("BUY")) {
            matchOrder(order, sellOrders, buyOrders);
        } else if (side.equals("SELL")) {
            matchOrder(order, buyOrders, sellOrders);
        } else {
            throw new IllegalArgumentException("Side must be either BUY or SELL");
        }
        
        return order.getId();
    }

    /**
     * Try to match an order against the opposite side of the order book.
     * 
     * @param incomingOrder  The newly submitted order
     * @param oppositeOrders The queue of orders on the opposite side (potential matches)
     * @param sameOrders     The queue where this order would go if not fully matched
     */
    private void matchOrder(Order incomingOrder, PriorityQueue<Order> oppositeOrders, PriorityQueue<Order> sameOrders) {
        boolean isIncomingBuy = incomingOrder.getSide().equals("BUY");
        
        while (!oppositeOrders.isEmpty() && incomingOrder.getQuantity() > 0) {
            Order topOrder = oppositeOrders.peek();
            
            // Check price conditions for matching
            boolean priceMatches = incomingOrder.getPrice() == -1 || // Market order
                                  topOrder.getPrice() == -1 || // Market order
                                  (isIncomingBuy && incomingOrder.getPrice() >= topOrder.getPrice()) || // Buy price >= sell price
                                  (!isIncomingBuy && incomingOrder.getPrice() <= topOrder.getPrice()); // Sell price <= buy price
            
            if (!priceMatches) {
                break; // No more matches possible based on price
            }
            
            oppositeOrders.poll(); // Remove the order for processing
            
            // Calculate trade quantity
            int tradeQuantity = Math.min(incomingOrder.getQuantity(), topOrder.getQuantity());
            double tradePrice = topOrder.getPrice() == -1 ? incomingOrder.getPrice() : topOrder.getPrice();
            
            // Record the trade
            Trade trade = new Trade(
                incomingOrder.getId(), 
                topOrder.getId(),
                incomingOrder.getSide(),
                topOrder.getSide(),
                tradeQuantity,
                tradePrice,
                incomingOrder.getSymbol(),
                System.currentTimeMillis()
            );
            executedTrades.add(trade);
            tradeHistory.add(tradePrice);
            lastTradePrice = tradePrice;
            
            // Log the trade
            logMessage("TRADE: " + tradeQuantity + " @ $" + String.format("%.2f", tradePrice) + 
                      " (Buy #" + trade.getBuyOrderId() + ", Sell #" + trade.getSellOrderId() + ")");
            
            // Update order quantities
            incomingOrder.setQuantity(incomingOrder.getQuantity() - tradeQuantity);
            topOrder.setQuantity(topOrder.getQuantity() - tradeQuantity);
            
            // If the matched order still has quantity, put it back in the queue
            if (topOrder.getQuantity() > 0) {
                oppositeOrders.add(topOrder);
            }
        }
        
        // If the incoming order wasn't fully matched, add it to the order book
        if (incomingOrder.getQuantity() > 0) {
            sameOrders.add(incomingOrder);
        }
    }
    
    private void logMessage(String message) {
        logArea.append(message + "\n");
        // Auto-scroll to bottom
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Represents an order in the matching engine.
     */
    public static class Order {
        private final long id;
        private final String side; // "BUY" or "SELL"
        private int quantity;
        private final double price; // -1 represents a market order
        private final String symbol;
        private final long timestamp;

        public Order(long id, String side, int quantity, double price, String symbol, long timestamp) {
            this.id = id;
            this.side = side;
            this.quantity = quantity;
            this.price = price;
            this.symbol = symbol;
            this.timestamp = timestamp;
        }

        public long getId() {
            return id;
        }

        public String getSide() {
            return side;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getSymbol() {
            return symbol;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Represents an executed trade between two orders.
     */
    public static class Trade {
        private final long buyOrderId;
        private final long sellOrderId;
        private final String buySide;
        private final String sellSide;
        private final int quantity;
        private final double price;
        private final String symbol;
        private final long timestamp;

        public Trade(long order1Id, long order2Id, String order1Side, String order2Side,
                    int quantity, double price, String symbol, long timestamp) {
            if (order1Side.equals("BUY")) {
                this.buyOrderId = order1Id;
                this.sellOrderId = order2Id;
                this.buySide = order1Side;
                this.sellSide = order2Side;
            } else {
                this.buyOrderId = order2Id;
                this.sellOrderId = order1Id;
                this.buySide = order2Side;
                this.sellSide = order1Side;
            }
            this.quantity = quantity;
            this.price = price;
            this.symbol = symbol;
            this.timestamp = timestamp;
        }

        public long getBuyOrderId() {
            return buyOrderId;
        }

        public long getSellOrderId() {
            return sellOrderId;
        }
    }

    /**
     * Main method to demonstrate the matching engine.
     */
    public static void main(String[] args) {
        // Use Swing's event dispatch thread for UI operations
        SwingUtilities.invokeLater(() -> {
            OrderMatchingEngine engine = new OrderMatchingEngine();
            engine.setVisible(true);
        });
    }
}