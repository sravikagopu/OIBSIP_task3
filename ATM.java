import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class User {
    private int userId;
    private int pin;
    private double balance;
    private Map<Integer, Transaction> transactionHistory;

    public User(int userId, int pin) {
        this.userId = userId;
        this.pin = pin;
        this.balance = 0.0;
        this.transactionHistory = new HashMap<>();
    }

    public int getUserId() {
        return userId;
    }

    public boolean validatePin(int pin) {
        return this.pin == pin;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        addToTransactionHistory(new Transaction(TransactionType.DEPOSIT, amount));
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient balance.");
        } else {
            balance -= amount;
            addToTransactionHistory(new Transaction(TransactionType.WITHDRAWAL, amount));
        }
    }

    public void transfer(User recipient, double amount) {
        if (amount > balance) {
            System.out.println("Insufficient balance for the transfer.");
        } else {
            balance -= amount;
            recipient.deposit(amount);
            addToTransactionHistory(new Transaction(TransactionType.TRANSFER, amount));
            recipient.addToTransactionHistory(new Transaction(TransactionType.RECEIVE_TRANSFER, amount));
        }
    }

    public void displayTransactionHistory() {
        System.out.println("Transaction History:");
        for (Transaction transaction : transactionHistory.values()) {
            System.out.println(transaction);
        }
    }

    private void addToTransactionHistory(Transaction transaction) {
        transactionHistory.put(transaction.getTransactionId(), transaction);
    }
}

class Transaction {
    private static int transactionCounter = 1;
    private int transactionId;
    private TransactionType type;
    private double amount;

    public Transaction(TransactionType type, double amount) {
        this.transactionId = transactionCounter++;
        this.type = type;
        this.amount = amount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
                ", Type: " + type +
                ", Amount: $" + amount;
    }
}

enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER,
    RECEIVE_TRANSFER
}

public class ATM {
    private Map<Integer, User> users;
    private User currentUser;
    private Scanner scanner;

    public ATM() {
        users = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.print("Enter User ID: ");
            int userId = scanner.nextInt();
            System.out.print("Enter PIN: ");
            int pin = scanner.nextInt();

            if (login(userId, pin)) {
                showMenu();
                break;
            } else {
                System.out.println("Invalid User ID or PIN. Please try again.");
            }
        }
    }

    private boolean login(int userId, int pin) {
        User user = users.get(userId);
        if (user != null && user.validatePin(pin)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    private void showMenu() {
        while (true) {
            System.out.println("\nATM Menu:");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    currentUser.displayTransactionHistory();
                    break;
                case 2:
                    System.out.print("Enter the withdrawal amount: $");
                    double withdrawAmount = scanner.nextDouble();
                    currentUser.withdraw(withdrawAmount);
                    break;
                case 3:
                    System.out.print("Enter the deposit amount: $");
                    double depositAmount = scanner.nextDouble();
                    currentUser.deposit(depositAmount);
                    break;
                case 4:
                    System.out.print("Enter the recipient's User ID: ");
                    int recipientId = scanner.nextInt();
                    System.out.print("Enter the transfer amount: $");
                    double transferAmount = scanner.nextDouble();
                    User recipient = users.get(recipientId);
                    if (recipient != null) {
                        currentUser.transfer(recipient, transferAmount);
                    } else {
                        System.out.println("Recipient not found.");
                    }
                    break;
                case 5:
                    System.out.println("Thank you for using the ATM.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();

        User user1 = new User(12345, 1234);
        User user2 = new User(67890, 5678);

        atm.addUser(user1);
        atm.addUser(user2);

        atm.run();
    }
}
