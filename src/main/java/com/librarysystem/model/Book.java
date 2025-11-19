package com.librarysystem.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a book in the library system.
 */
public class Book implements Serializable, Comparable<Book> {
    private static final long serialVersionUID = 1L;

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getCheckedOutCopies() {
        return totalCopies - availableCopies;
    }

    // Setters
    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and " + totalCopies);
        }
        this.availableCopies = availableCopies;
    }

    public void addCopies(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Cannot add negative copies");
        }
        this.totalCopies += count;
        this.availableCopies += count;
    }

    public boolean checkoutCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public boolean returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format(
            "Book{isbn='%s', title='%s', author='%s', genre='%s', available=%d/%d}",
            isbn, title, author, genre, availableCopies, totalCopies
        );
    }
}
