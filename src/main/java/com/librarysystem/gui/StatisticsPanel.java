package com.librarysystem.gui;

import com.librarysystem.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Displays high level statistics using basic Swing components.
 */
public class StatisticsPanel extends JPanel {
    private final LibraryService libraryService;
    private final JLabel totalBooksLabel = new JLabel();
    private final JLabel availableCopiesLabel = new JLabel();
    private final JLabel checkedOutCopiesLabel = new JLabel();
    private final JLabel totalMembersLabel = new JLabel();
    private final JLabel activeCheckoutsLabel = new JLabel();
    private final JLabel overdueCheckoutsLabel = new JLabel();

    public StatisticsPanel(LibraryService libraryService) {
        this.libraryService = libraryService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Library Statistics Dashboard");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 15, 15));
        grid.add(createStatBox("Book Counts", totalBooksLabel, availableCopiesLabel, checkedOutCopiesLabel));
        grid.add(createStatBox("Member Counts", totalMembersLabel));
        grid.add(createStatBox("Checkout Counts", activeCheckoutsLabel, overdueCheckoutsLabel));
        add(grid, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Statistics");
        refreshButton.addActionListener(e -> refresh());
        add(refreshButton, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createStatBox(String title, JLabel... labels) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        for (JLabel label : labels) {
            panel.add(label);
            panel.add(Box.createVerticalStrut(5));
        }
        return panel;
    }

    public void refresh() {
        totalBooksLabel.setText("Total Books: " + libraryService.getTotalBooks());
        availableCopiesLabel.setText("Available Copies: " + libraryService.getTotalAvailableCopies());
        checkedOutCopiesLabel.setText("Checked Out: " + libraryService.getTotalCheckedOutCopies());
        totalMembersLabel.setText("Total Members: " + libraryService.getTotalMembers());
        activeCheckoutsLabel.setText("Active Checkouts: " + libraryService.getTotalActiveCheckouts());
        overdueCheckoutsLabel.setText("Overdue Checkouts: " + libraryService.getOverdueCheckouts().size());
    }
}
