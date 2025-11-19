package com.librarysystem.service;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.librarysystem.data.HashMap;
import com.librarysystem.data.LinkedList;
import com.librarysystem.model.Book;
import com.librarysystem.model.Checkout;
import com.librarysystem.model.Member;

/**
 * Main library system service managing books, members, and checkouts.
 */
public class LibraryService {
    private final HashMap<String, Book> books;  // ISBN -> Book
    private final HashMap<String, Member> members;  // Member ID -> Member
    private final HashMap<String, LinkedList<Checkout>> memberCheckouts;  // Member ID -> Checkouts
    private final HashMap<String, Checkout> checkoutRecords;  // Checkout ID -> Checkout
    private int checkoutCounter;
    private static final int DEFAULT_CHECKOUT_DAYS = 14;
    private static final double OVERDUE_FEE_PER_DAY = 1.0;
    private static final String DATA_FILE = "library_data.ser";

    public LibraryService() {
        this.books = new HashMap<>();
        this.members = new HashMap<>();
        this.memberCheckouts = new HashMap<>();
        this.checkoutRecords = new HashMap<>();
        this.checkoutCounter = 0;
        loadData();
    }

    // ==================== BOOK MANAGEMENT ====================

    /**
     * Add a new book to the library
     */
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        books.put(book.getIsbn(), book);
        saveData();
    }

    /**
     * Remove a book from the library
     */
    public void removeBook(String isbn) {
        books.remove(isbn);
    }

    /**
     * Get book by ISBN
     */
    public Book getBook(String isbn) {
        return books.get(isbn);
    }

    /**
     * Search books by title (contains search)
     */
    public List<Book> searchByTitle(String titleKeyword) {
        List<Book> results = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(titleKeyword.toLowerCase())) {
                results.add(book);
            }
        }
        Collections.sort(results);
        return results;
    }

    /**
     * Search books by author
     */
    public List<Book> searchByAuthor(String authorName) {
        List<Book> results = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAuthor().toLowerCase().contains(authorName.toLowerCase())) {
                results.add(book);
            }
        }
        return results;
    }

    /**
     * Get all books in library
     */
    public List<Book> getAllBooks() {
        List<Book> allBooks = new ArrayList<>(books.values());
        Collections.sort(allBooks);
        return allBooks;
    }

    /**
     * Get available books
     */
    public List<Book> getAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAvailableCopies() > 0) {
                available.add(book);
            }
        }
        return available;
    }

    // ==================== MEMBER MANAGEMENT ====================

    /**
     * Add a new member to the library
     */
    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        members.put(member.getMemberId(), member);
        memberCheckouts.put(member.getMemberId(), new LinkedList<>());
        saveData();
    }

    /**
     * Remove a member from the library
     */
    public void removeMember(String memberId) {
        members.remove(memberId);
        memberCheckouts.remove(memberId);
    }

    /**
     * Get member by ID
     */
    public Member getMember(String memberId) {
        return members.get(memberId);
    }

    /**
     * Get all members
     */
    public List<Member> getAllMembers() {
        List<Member> allMembers = new ArrayList<>(members.values());
        Collections.sort(allMembers);
        return allMembers;
    }

    /**
     * Update member status
     */
    public void updateMemberStatus(String memberId, Member.MembershipStatus status) {
        Member member = members.get(memberId);
        if (member != null) {
            member.setStatus(status);
            saveData();
        }
    }

    /**
     * Update member information (email, phone)
     */
    public void updateMemberInfo(String memberId, String email, String phone) {
        Member member = members.get(memberId);
        if (member != null) {
            if (email != null && !email.isEmpty()) {
                member.setEmail(email);
            }
            if (phone != null && !phone.isEmpty()) {
                member.setPhone(phone);
            }
            saveData();
        }
    }

    // ==================== CHECKOUT OPERATIONS ====================

    /**
     * Checkout a book for a member
     */
    public boolean checkoutBook(String memberId, String isbn) {
        Member member = members.get(memberId);
        Book book = books.get(isbn);

        if (member == null) {
            throw new IllegalArgumentException("Member not found: " + memberId);
        }
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + isbn);
        }
        if (!member.isActive()) {
            throw new IllegalStateException("Member is not active");
        }

        LinkedList<Checkout> memberCheckoutList = memberCheckouts.get(memberId);
        int activeCheckouts = 0;
        for (Checkout checkout : memberCheckoutList) {
            if (checkout.getStatus() == Checkout.CheckoutStatus.ACTIVE) {
                activeCheckouts++;
            }
        }

        if (activeCheckouts >= member.getMaxCheckouts()) {
            throw new IllegalStateException("Member has reached maximum checkouts");
        }

        if (!book.checkoutCopy()) {
            throw new IllegalStateException("Book is not available");
        }

        String checkoutId = generateCheckoutId();
        Checkout checkout = new Checkout(checkoutId, memberId, isbn, 
                                        LocalDate.now(), DEFAULT_CHECKOUT_DAYS);
        
        memberCheckoutList.add(checkout);
        checkoutRecords.put(checkoutId, checkout);
        saveData();

        return true;
    }

    /**
     * Return a book
     */
    public boolean returnBook(String checkoutId) {
        Checkout checkout = checkoutRecords.get(checkoutId);

        if (checkout == null) {
            throw new IllegalArgumentException("Checkout not found: " + checkoutId);
        }
        if (checkout.getStatus() != Checkout.CheckoutStatus.ACTIVE) {
            throw new IllegalStateException("Checkout is not active");
        }

        Book book = books.get(checkout.getIsbn());
        if (book == null) {
            throw new IllegalStateException("Book not found for checkout");
        }

        checkout.returnBook(LocalDate.now());
        book.returnCopy();
        saveData();

        return true;
    }

    /**
     * Get checkouts for a member
     */
    public List<Checkout> getMemberCheckouts(String memberId) {
        LinkedList<Checkout> checkouts = memberCheckouts.get(memberId);
        if (checkouts == null) {
            return new ArrayList<>();
        }
        
        List<Checkout> result = new ArrayList<>();
        for (Checkout checkout : checkouts) {
            result.add(checkout);
        }
        return result;
    }

    /**
     * Get active checkouts for a member
     */
    public List<Checkout> getActiveCheckouts(String memberId) {
        List<Checkout> active = new ArrayList<>();
        LinkedList<Checkout> checkouts = memberCheckouts.get(memberId);
        
        if (checkouts != null) {
            for (Checkout checkout : checkouts) {
                if (checkout.getStatus() == Checkout.CheckoutStatus.ACTIVE) {
                    active.add(checkout);
                }
            }
        }
        return active;
    }

    /**
     * Get all active checkouts (across all members)
     */
    public List<Checkout> getAllActiveCheckouts() {
        List<Checkout> active = new ArrayList<>();
        for (Checkout checkout : checkoutRecords.values()) {
            if (checkout.getStatus() == Checkout.CheckoutStatus.ACTIVE) {
                active.add(checkout);
            }
        }
        return active;
    }

    /**
     * Get overdue checkouts
     */
    public List<Checkout> getOverdueCheckouts() {
        List<Checkout> overdue = new ArrayList<>();
        
        for (Checkout checkout : checkoutRecords.values()) {
            if (checkout.isOverdue()) {
                checkout.markOverdue();
                overdue.add(checkout);
            }
        }
        
        // Sort by due date (earliest first)
        overdue.sort(Checkout::compareTo);
        return overdue;
    }

    /**
     * Calculate overdue fee for a checkout
     */
    public double calculateOverdueFee(String checkoutId) {
        Checkout checkout = checkoutRecords.get(checkoutId);
        if (checkout == null) {
            return 0.0;
        }
        return checkout.getOverdueDays() * OVERDUE_FEE_PER_DAY;
    }

    /**
     * Renew a checkout
     */
    public boolean renewCheckout(String checkoutId) {
        Checkout checkout = checkoutRecords.get(checkoutId);

        if (checkout == null) {
            throw new IllegalArgumentException("Checkout not found: " + checkoutId);
        }
        if (checkout.getStatus() != Checkout.CheckoutStatus.ACTIVE) {
            throw new IllegalStateException("Cannot renew inactive checkout");
        }
        if (checkout.isOverdue()) {
            throw new IllegalStateException("Cannot renew overdue checkout");
        }

        checkout = new Checkout(
            checkout.getCheckoutId(),
            checkout.getMemberId(),
            checkout.getIsbn(),
            LocalDate.now(),
            DEFAULT_CHECKOUT_DAYS
        );
        
        checkoutRecords.put(checkoutId, checkout);
        return true;
    }

    // ==================== STATISTICS ====================

    /**
     * Get total number of books in library
     */
    public int getTotalBooks() {
        return books.size();
    }

    /**
     * Get total number of available copies
     */
    public int getTotalAvailableCopies() {
        int total = 0;
        for (Book book : books.values()) {
            total += book.getAvailableCopies();
        }
        return total;
    }

    /**
     * Get total number of checked out copies
     */
    public int getTotalCheckedOutCopies() {
        int total = 0;
        for (Book book : books.values()) {
            total += book.getCheckedOutCopies();
        }
        return total;
    }

    /**
     * Get member statistics
     */
    public int getTotalMembers() {
        return members.size();
    }

    /**
     * Get active checkout count
     */
    public int getTotalActiveCheckouts() {
        int count = 0;
        for (Checkout checkout : checkoutRecords.values()) {
            if (checkout.getStatus() == Checkout.CheckoutStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Generate unique checkout ID
     */
    private String generateCheckoutId() {
        return "CO" + String.format("%06d", ++checkoutCounter);
    }

    /**
     * Print library statistics
     */
    public void printStatistics() {
        System.out.println("\n========== LIBRARY STATISTICS ==========");
        System.out.println("Total Books: " + getTotalBooks());
        System.out.println("Available Copies: " + getTotalAvailableCopies());
        System.out.println("Checked Out Copies: " + getTotalCheckedOutCopies());
        System.out.println("Total Members: " + getTotalMembers());
        System.out.println("Active Checkouts: " + getTotalActiveCheckouts());
        System.out.println("Overdue Checkouts: " + getOverdueCheckouts().size());
        System.out.println("=======================================\n");
    }

    // ==================== PERSISTENCE ====================

    /**
     * Save library data to file
     */
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            LibraryData data = new LibraryData();
            data.books = new ArrayList<>(books.values());
            data.members = new ArrayList<>(members.values());
            data.checkouts = new ArrayList<>(checkoutRecords.values());
            data.checkoutCounter = this.checkoutCounter;
            
            out.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Load library data from file
     */
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;  // No saved data, start fresh
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            LibraryData data = (LibraryData) in.readObject();
            
            // Restore books
            for (Book book : data.books) {
                books.put(book.getIsbn(), book);
            }
            
            // Restore members
            for (Member member : data.members) {
                members.put(member.getMemberId(), member);
                memberCheckouts.put(member.getMemberId(), new LinkedList<>());
            }
            
            // Restore checkouts
            for (Checkout checkout : data.checkouts) {
                checkoutRecords.put(checkout.getCheckoutId(), checkout);
                LinkedList<Checkout> memberList = memberCheckouts.get(checkout.getMemberId());
                if (memberList != null) {
                    memberList.add(checkout);
                }
            }
            
            this.checkoutCounter = data.checkoutCounter;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    /**
     * Inner class to hold serializable library data
     */
    private static class LibraryData implements Serializable {
        private static final long serialVersionUID = 1L;
        List<Book> books;
        List<Member> members;
        List<Checkout> checkouts;
        int checkoutCounter;
    }
}
