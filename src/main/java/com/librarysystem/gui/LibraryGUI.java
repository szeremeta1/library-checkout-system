package com.librarysystem.gui;

import com.librarysystem.model.Book;
import com.librarysystem.model.Member;
import com.librarysystem.service.LibraryService;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

/**
 * Main GUI application built with Swing so it works with only the JDK.
 */
public class LibraryGUI extends JFrame {
    private final LibraryService libraryService;
    private final BooksPanel booksPanel;
    private final MembersPanel membersPanel;
    private final CheckoutsPanel checkoutsPanel;
    private final StatisticsPanel statisticsPanel;

    public LibraryGUI() {
        super("Szeremeta Library System - Monmouth County");
        this.libraryService = new LibraryService();

        initializeSampleData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1100, 750);
        setLocationRelativeTo(null);

        booksPanel = new BooksPanel(libraryService);
        membersPanel = new MembersPanel(libraryService);
        statisticsPanel = new StatisticsPanel(libraryService);
        checkoutsPanel = new CheckoutsPanel(libraryService, this::refreshAllPanels);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", booksPanel);
        tabbedPane.addTab("Members", membersPanel);
        tabbedPane.addTab("Checkouts", checkoutsPanel);
        tabbedPane.addTab("Statistics", statisticsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshAllPanels() {
        booksPanel.refresh();
        membersPanel.refresh();
        checkoutsPanel.refresh();
        statisticsPanel.refresh();
    }

    private void initializeSampleData() {
        if (libraryService.getTotalBooks() > 0 || libraryService.getTotalMembers() > 0) {
            return;
        }

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

        libraryService.addMember(new Member("M001", "John Doe",
                "john@example.com", "732-555-1001"));
        libraryService.addMember(new Member("M002", "Jane Smith",
                "jane@example.com", "609-555-1002"));
        libraryService.addMember(new Member("M003", "Bob Johnson",
                "bob@example.com", "848-555-1003"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
