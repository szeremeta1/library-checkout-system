package com.librarysystem.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a checkout transaction record.
 */
public class Checkout implements Serializable, Comparable<Checkout> {
    private static final long serialVersionUID = 1L;

    private String checkoutId;
    private String memberId;
    private String isbn;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private CheckoutStatus status;
    private int daysAllowed;

    public enum CheckoutStatus {
        ACTIVE, RETURNED, OVERDUE
    }

    public Checkout(String checkoutId, String memberId, String isbn, 
                   LocalDate checkoutDate, int daysAllowed) {
        this.checkoutId = checkoutId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.checkoutDate = checkoutDate;
        this.daysAllowed = daysAllowed;
        this.dueDate = checkoutDate.plusDays(daysAllowed);
        this.status = CheckoutStatus.ACTIVE;
        this.returnDate = null;
    }

    // Getters
    public String getCheckoutId() {
        return checkoutId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getIsbn() {
        return isbn;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public CheckoutStatus getStatus() {
        return status;
    }

    public int getDaysAllowed() {
        return daysAllowed;
    }

    public boolean isOverdue() {
        if (status == CheckoutStatus.ACTIVE) {
            return LocalDate.now().isAfter(dueDate);
        }
        return false;
    }

    public int getOverdueDays() {
        if (isOverdue()) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }

    // Setters
    public void returnBook(LocalDate returnDate) {
        if (status != CheckoutStatus.ACTIVE) {
            throw new IllegalStateException("Cannot return a book that is not checked out");
        }
        this.returnDate = returnDate;
        this.status = CheckoutStatus.RETURNED;
    }

    public void markOverdue() {
        if (isOverdue() && status == CheckoutStatus.ACTIVE) {
            this.status = CheckoutStatus.OVERDUE;
        }
    }

    @Override
    public int compareTo(Checkout other) {
        return this.dueDate.compareTo(other.dueDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checkout checkout = (Checkout) o;
        return checkoutId.equals(checkout.checkoutId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutId);
    }

    @Override
    public String toString() {
        return String.format(
            "Checkout{id='%s', member='%s', isbn='%s', checkout=%s, due=%s, status=%s}",
            checkoutId, memberId, isbn, checkoutDate, dueDate, status
        );
    }
}
