package com.librarysystem.gui;

import com.librarysystem.model.Book;
import com.librarysystem.model.Checkout;
import com.librarysystem.model.Member;
import com.librarysystem.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Swing panel for handling checkouts and returns.
 */
public class CheckoutsPanel extends JPanel {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final LibraryService libraryService;
    private final Runnable onCheckoutChange;
    private final DefaultTableModel tableModel;
    private final JTable checkoutTable;
    private final JComboBox<String> viewTypeCombo;

    public CheckoutsPanel(LibraryService libraryService, Runnable onCheckoutChange) {
        this.libraryService = libraryService;
        this.onCheckoutChange = onCheckoutChange;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.add(new JLabel("View:"));
        viewTypeCombo = new JComboBox<>(new String[]{"All Active Checkouts", "Overdue Checkouts"});
        topBar.add(viewTypeCombo);
        add(topBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Checkout ID", "Member", "Book", "Checkout Date", "Due Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        checkoutTable = new JTable(tableModel);
        checkoutTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(checkoutTable), BorderLayout.CENTER);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton checkoutButton = new JButton("Checkout Book");
        JButton returnButton = new JButton("Return Book");
        JButton detailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");
        buttonBar.add(checkoutButton);
        buttonBar.add(returnButton);
        buttonBar.add(detailsButton);
        buttonBar.add(refreshButton);
        add(buttonBar, BorderLayout.SOUTH);

        viewTypeCombo.addActionListener(e -> refresh());
        checkoutButton.addActionListener(e -> showCheckoutDialog());
        returnButton.addActionListener(e -> returnSelectedCheckout());
        detailsButton.addActionListener(e -> showCheckoutDetails());
        refreshButton.addActionListener(e -> refresh());

        refresh();
    }

    public void refresh() {
        List<Checkout> checkouts = "Overdue Checkouts".equals(viewTypeCombo.getSelectedItem())
                ? libraryService.getOverdueCheckouts()
                : libraryService.getAllActiveCheckouts();

        tableModel.setRowCount(0);
        for (Checkout checkout : checkouts) {
            tableModel.addRow(new Object[]{
                    checkout.getCheckoutId(),
                    memberSummary(checkout.getMemberId()),
                    bookSummary(checkout.getIsbn()),
                    DATE_FORMAT.format(checkout.getCheckoutDate()),
                    DATE_FORMAT.format(checkout.getDueDate()),
                    checkout.getStatus()
            });
        }
    }

    private void showCheckoutDialog() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 5));

        List<Member> members = libraryService.getAllMembers();
        JComboBox<Member> memberCombo = new JComboBox<>(members.toArray(new Member[0]));
        memberCombo.setRenderer(new MemberRenderer());

        List<Book> books = libraryService.getAvailableBooks();
        JComboBox<Book> bookCombo = new JComboBox<>(books.toArray(new Book[0]));
        bookCombo.setRenderer(new BookRenderer());

        form.add(new JLabel("Member:"));
        form.add(memberCombo);
        form.add(new JLabel("Book:"));
        form.add(bookCombo);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Checkout Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            Member member = (Member) memberCombo.getSelectedItem();
            Book book = (Book) bookCombo.getSelectedItem();
            if (member == null || book == null) {
                showError("Pick both a member and a book.");
                return;
            }

            try {
                libraryService.checkoutBook(member.getMemberId(), book.getIsbn());
                refresh();
                onCheckoutChange.run();
                showInfo("Checkout complete!");
            } catch (Exception ex) {
                showError("Unable to checkout: " + ex.getMessage());
            }
        }
    }

    private void returnSelectedCheckout() {
        String checkoutId = getSelectedCheckoutId();
        if (checkoutId == null) {
            showError("Select a checkout first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Return checkout " + checkoutId + "?",
                "Return Book",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                libraryService.returnBook(checkoutId);
                refresh();
                onCheckoutChange.run();
                showInfo("Book returned!");
            } catch (Exception ex) {
                showError("Unable to return book: " + ex.getMessage());
            }
        }
    }

    private void showCheckoutDetails() {
        String checkoutId = getSelectedCheckoutId();
        if (checkoutId == null) {
            showError("Select a checkout first.");
            return;
        }

        Checkout checkout = findCheckout(checkoutId);
        if (checkout == null) {
            showError("Checkout not found.");
            return;
        }

        double fee = libraryService.calculateOverdueFee(checkoutId);
        String message = String.format(
                "Checkout ID: %s%nMember: %s%nBook: %s%nCheckout Date: %s%nDue Date: %s%nStatus: %s%nOverdue Days: %d%nFee: $%.2f",
                checkout.getCheckoutId(),
                memberSummary(checkout.getMemberId()),
                bookSummary(checkout.getIsbn()),
                DATE_FORMAT.format(checkout.getCheckoutDate()),
                DATE_FORMAT.format(checkout.getDueDate()),
                checkout.getStatus(),
                checkout.getOverdueDays(),
                fee
        );
        JOptionPane.showMessageDialog(this, message, "Checkout Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getSelectedCheckoutId() {
        int row = checkoutTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int modelRow = checkoutTable.convertRowIndexToModel(row);
        return (String) tableModel.getValueAt(modelRow, 0);
    }

    private Checkout findCheckout(String checkoutId) {
        for (Checkout checkout : libraryService.getAllActiveCheckouts()) {
            if (checkout.getCheckoutId().equals(checkoutId)) {
                return checkout;
            }
        }
        for (Checkout checkout : libraryService.getOverdueCheckouts()) {
            if (checkout.getCheckoutId().equals(checkoutId)) {
                return checkout;
            }
        }
        return null;
    }

    private String memberSummary(String memberId) {
        Member member = libraryService.getMember(memberId);
        return member == null ? memberId : member.getName() + " (" + member.getMemberId() + ")";
    }

    private String bookSummary(String isbn) {
        Book book = libraryService.getBook(isbn);
        return book == null ? isbn : book.getTitle() + " (" + book.getIsbn() + ")";
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class MemberRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Member member) {
                value = member.getMemberId() + " - " + member.getName();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    private static class BookRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Book book) {
                value = book.getIsbn() + " - " + book.getTitle();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
