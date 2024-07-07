import java.io.*;
import java.util.*;

class ExpenseTracker {
    private Map<String, User> users;
    private Scanner scanner;

    public ExpenseTracker() {
        users = new HashMap<>();
        scanner = new Scanner(System.in);
        loadUsers();
    }

    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.run();
    }

    private void run() {
        System.out.println("Welcome to the Expense Tracker!");
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    saveUsers();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("Username already taken. Try a different one.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        users.put(username, new User(username, password));
        System.out.println("Registration successful.");
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful.");
            userMenu(user);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void userMenu(User user) {
        while (true) {
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. View Category Summation");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    addExpense(user);
                    break;
                case 2:
                    viewExpenses(user);
                    break;
                case 3:
                    viewCategorySummation(user);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addExpense(User user) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        user.addExpense(new Expense(date, category, amount));
        System.out.println("Expense added successfully.");
    }

    private void viewExpenses(User user) {
        System.out.println("Expenses:");
        for (Expense expense : user.getExpenses()) {
            System.out.println(expense);
        }
    }

    private void viewCategorySummation(User user) {
        Map<String, Double> categorySummation = user.getCategorySummation();
        System.out.println("Category-wise Summation:");
        for (String category : categorySummation.keySet()) {
            System.out.println(category + ": " + categorySummation.get(category));
        }
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Ignore if file not found
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}

class User implements Serializable {
    private String username;
    private String password;
    private List<Expense> expenses;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.expenses = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public Map<String, Double> getCategorySummation() {
        Map<String, Double> summation = new HashMap<>();
        for (Expense expense : expenses) {
            summation.put(expense.getCategory(), summation.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }
        return summation;
    }
}

class Expense implements Serializable {
    private String date;
    private String category;
    private double amount;

    public Expense(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return date + " | " + category + " | " + amount;
    }
}
