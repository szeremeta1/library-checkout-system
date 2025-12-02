package com.librarysystem;

import com.librarysystem.model.Book;
import com.librarysystem.model.Checkout;
import com.librarysystem.model.Member;
import com.librarysystem.service.LibraryService;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Library Checkout System
 */
public class LibrarySystem {
    private final LibraryService libraryService;
    private final Scanner scanner;

    public LibrarySystem() {
        this.libraryService = new LibraryService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Initialize the system with sample data
     */
    private void initializeSampleData() {
        // Only initialize if there's no existing data
        if (libraryService.getTotalBooks() > 0 || libraryService.getTotalMembers() > 0) {
            System.out.println("Loaded existing library data.\n");
            return;
        }

        // Add sample books
        libraryService.addBook(new Book("978-0-13-468599-1", "Clean Code", 
                "Robert C. Martin", "Programming", 3));
        libraryService.addBook(new Book("978-0-13-468750-6", "The Pragmatic Programmer", 
                "David Thomas", "Programming", 2));
        libraryService.addBook(new Book("978-0-13-468751-3", "Design Patterns", 
                "Gang of Four", "Programming", 1));
        libraryService.addBook(new Book("978-0-07-149143-0", "Thinking in Java", 
                "Bruce Eckel", "Programming", 2));
        libraryService.addBook(new Book("978-0-59-651298-4", "Head First Java", 
                "Kathy Sierra", "Programming", 4));

        // Add sample members
        libraryService.addMember(new Member("M001", "John Doe", 
                "john@example.com", "732-555-1001"));
        libraryService.addMember(new Member("M002", "Jane Smith", 
                "jane@example.com", "609-555-1002"));
        libraryService.addMember(new Member("M003", "Bob Johnson", 
                "bob@example.com", "848-555-1003"));

        System.out.println("Sample data initialized successfully!\n");
    }

    /**
     * Main menu
     */
    private void showMainMenu() {
        System.out.println("\n========== SZEREMETA LIBRARY SYSTEM ==========");
        System.out.println("           Monmouth County Libraries");
        System.out.println("==============================================");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Checkout Operations");
        System.out.println("4. View Statistics");
        System.out.println("5. Exit");
        System.out.print("Select option: ");
    }

    /**
     * Book management submenu
     */
    private void bookManagement() {
        while (true) {
            System.out.println("\n--- Book Management ---");
            System.out.println("1. View all books");
            System.out.println("2. Search by title");
            System.out.println("3. Search by author");
            System.out.println("4. Add new book");
            System.out.println("5. Back to main menu");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllBooks();
                case "2" -> searchByTitle();
                case "3" -> searchByAuthor();
                case "4" -> addNewBook();
                case "5" -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    
    /**
     * Member management submenu
     */
    private void memberManagement() {
        while (true) {
            System.out.println("\n--- Member Management ---");
            System.out.println("1. View all members");
            System.out.println("2. Add new member");
            System.out.println("3. Update member info");
            System.out.println("4. Update member status");
            System.out.println("5. View member checkouts");
            System.out.println("6. Back to main menu");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllMembers();
                case "2" -> addNewMember();
                case "3" -> updateMemberInfo();
                case "4" -> updateMemberStatus();
                case "5" -> viewMemberCheckouts();
                case "6" -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Checkout operations submenu
     */
    private void checkoutOperations() {
        while (true) {
            System.out.println("\n--- Checkout Operations ---");
            System.out.println("1. Checkout book");
            System.out.println("2. Return book");
            System.out.println("3. View active checkouts");
            System.out.println("4. View overdue checkouts");
            System.out.println("5. Back to main menu");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> checkoutBook();
                case "2" -> returnBook();
                case "3" -> viewActiveCheckouts();
                case "4" -> viewOverdueCheckouts();
                case "5" -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ==================== BOOK OPERATIONS ====================

    private void viewAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        System.out.println("\n--- All Books ---");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private void searchByTitle() {
        System.out.print("Enter title keyword: ");
        String keyword = scanner.nextLine().trim();
        List<Book> results = libraryService.searchByTitle(keyword);
        
        if (results.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        for (Book book : results) {
            System.out.println(book);
        }
    }

    private void searchByAuthor() {
        System.out.print("Enter author name: ");
        String author = scanner.nextLine().trim();
        List<Book> results = libraryService.searchByAuthor(author);
        
        if (results.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        for (Book book : results) {
            System.out.println(book);
        }
    }

    private void addNewBook() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Enter number of copies: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());
        
        try {
            libraryService.addBook(new Book(isbn, title, author, genre, copies));
            System.out.println("Book added successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==================== MEMBER OPERATIONS ====================

    private void viewAllMembers() {
        List<Member> members = libraryService.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members in library.");
            return;
        }
        System.out.println("\n--- All Members ---");
        for (Member member : members) {
            System.out.println(member);
        }
    }

    private void addNewMember() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();
        
        try {
            libraryService.addMember(new Member(memberId, name, email, phone));
            System.out.println("Member added successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateMemberInfo() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine().trim();
        
        Member member = libraryService.getMember(memberId);
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        
        System.out.println("Current info - Email: " + member.getEmail() + ", Phone: " + member.getPhone());
        System.out.print("Enter new email (or press Enter to keep current): ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter new phone (or press Enter to keep current): ");
        String phone = scanner.nextLine().trim();
        
        libraryService.updateMemberInfo(memberId, email, phone);
        System.out.println("Member information updated successfully!");
    }

    private void updateMemberStatus() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.println("1. ACTIVE\n2. INACTIVE\n3. SUSPENDED");
        System.out.print("Select status: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        Member.MembershipStatus status = switch (choice) {
            case 1 -> Member.MembershipStatus.ACTIVE;
            case 2 -> Member.MembershipStatus.INACTIVE;
            case 3 -> Member.MembershipStatus.SUSPENDED;
            default -> null;
        };
        
        if (status != null) {
            libraryService.updateMemberStatus(memberId, status);
            System.out.println("Member status updated!");
        }
    }

    private void viewMemberCheckouts() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine().trim();
        List<Checkout> checkouts = libraryService.getMemberCheckouts(memberId);
        
        if (checkouts.isEmpty()) {
            System.out.println("No checkouts for this member.");
            return;
        }
        
        System.out.println("\n--- Member Checkouts ---");
        for (Checkout checkout : checkouts) {
            System.out.println(checkout);
        }
    }

    // ==================== CHECKOUT OPERATIONS ====================

    private void checkoutBook() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        
        try {
            libraryService.checkoutBook(memberId, isbn);
            System.out.println("Book checked out successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.print("Enter checkout ID: ");
        String checkoutId = scanner.nextLine().trim();
        
        try {
            libraryService.returnBook(checkoutId);
            System.out.println("Book returned successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewActiveCheckouts() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine().trim();
        List<Checkout> checkouts = libraryService.getActiveCheckouts(memberId);
        
        if (checkouts.isEmpty()) {
            System.out.println("No active checkouts for this member.");
            return;
        }
        
        System.out.println("\n--- Active Checkouts ---");
        for (Checkout checkout : checkouts) {
            System.out.println(checkout);
        }
    }

    private void viewOverdueCheckouts() {
        List<Checkout> overdue = libraryService.getOverdueCheckouts();
        
        if (overdue.isEmpty()) {
            System.out.println("No overdue checkouts.");
            return;
        }
        
        System.out.println("\n--- Overdue Checkouts ---");
        for (Checkout checkout : overdue) {
            int days = checkout.getOverdueDays();
            double fee = libraryService.calculateOverdueFee(checkout.getCheckoutId());
            System.out.printf("%s - Overdue by %d days - Fee: $%.2f%n", checkout, days, fee);
        }
    }

    /**
     * Run the main application loop
     */
    public void run() {
        System.out.println("==============================================");
        System.out.println("   Welcome to Szeremeta Library System");
        System.out.println("        Monmouth County Libraries");
        System.out.println("==============================================");
        initializeSampleData();

        while (true) {
            showMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> bookManagement();
                case "2" -> memberManagement();
                case "3" -> checkoutOperations();
                case "4" -> libraryService.printStatistics();
                case "5" -> {
                    System.out.println("Thank you for using Szeremeta Library System!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    public static void main(String[] args) {
        LibrarySystem system = new LibrarySystem();
        system.run();
    }
}
