package com.librarysystem.gui;

import com.librarysystem.model.Book;
import com.librarysystem.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Simple Swing panel for viewing and managing books.
 */
public class BooksPanel extends JPanel {
    private final LibraryService libraryService;
    private final DefaultTableModel tableModel;
    private final JTable bookTable;
    private final JTextField searchField;
    private final JComboBox<String> searchTypeCombo;

    public BooksPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchTypeCombo = new JComboBox<>(new String[]{"All Books", "By Title", "By Author"});
        searchField = new JTextField(25);
        JButton clearButton = new JButton("Clear");
        searchBar.add(new JLabel("Search:"));
        searchBar.add(searchTypeCombo);
        searchBar.add(searchField);
        searchBar.add(clearButton);
        add(searchBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ISBN", "Title", "Author", "Genre", "Total", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Add Book");
        JButton viewButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");
        buttonBar.add(addButton);
        buttonBar.add(viewButton);
        buttonBar.add(refreshButton);
        add(buttonBar, BorderLayout.SOUTH);

        searchTypeCombo.addActionListener(e -> performSearch());
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::performSearch));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            searchTypeCombo.setSelectedIndex(0);
            refresh();
        });

        addButton.addActionListener(e -> showAddBookDialog());
        viewButton.addActionListener(e -> showBookDetails());
        refreshButton.addActionListener(e -> refresh());

        refresh();
    }

    public void refresh() {
        updateTable(libraryService.getAllBooks());
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        List<Book> results;
        if (searchTerm.isEmpty() || "All Books".equals(searchType)) {
            results = libraryService.getAllBooks();
        } else if ("By Title".equals(searchType)) {
            results = libraryService.searchByTitle(searchTerm);
        } else {
            results = libraryService.searchByAuthor(searchTerm);
        }

        updateTable(results);
    }

    private void updateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getTotalCopies(),
                    book.getAvailableCopies()
            });
        }
    }

    private void showAddBookDialog() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 5));
        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField copiesField = new JTextField();

        form.add(new JLabel("ISBN:"));
        form.add(isbnField);
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Author:"));
        form.add(authorField);
        form.add(new JLabel("Genre:"));
        form.add(genreField);
        form.add(new JLabel("Copies:"));
        form.add(copiesField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add New Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String isbn = isbnField.getText().trim();
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = genreField.getText().trim();
                int copies = Integer.parseInt(copiesField.getText().trim());

                if (isbn.isEmpty() || title.isEmpty() || author.isEmpty()) {
                    showError("ISBN, title, and author are required.");
                    return;
                }

                libraryService.addBook(new Book(isbn, title, author, genre, copies));
                refresh();
                showInfo("Book added successfully!");
            } catch (NumberFormatException ex) {
                showError("Copies must be a whole number.");
            } catch (Exception ex) {
                showError("Unable to add book: " + ex.getMessage());
            }
        }
    }

    private void showBookDetails() {
        Book selected = getSelectedBook();
        if (selected == null) {
            showError("Select a book first.");
            return;
        }

        String message = String.format(
                "ISBN: %s%nTitle: %s%nAuthor: %s%nGenre: %s%nTotal Copies: %d%nAvailable Copies: %d",
                selected.getIsbn(),
                selected.getTitle(),
                selected.getAuthor(),
                selected.getGenre(),
                selected.getTotalCopies(),
                selected.getAvailableCopies()
        );
        JOptionPane.showMessageDialog(this, message, "Book Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private Book getSelectedBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int modelRow = bookTable.convertRowIndexToModel(row);
        String isbn = (String) tableModel.getValueAt(modelRow, 0);
        return libraryService.getBook(isbn);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable runnable;

        SimpleDocumentListener(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            runnable.run();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            runnable.run();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            runnable.run();
        }
    }
}
