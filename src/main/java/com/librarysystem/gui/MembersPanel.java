package com.librarysystem.gui;

import com.librarysystem.model.Member;
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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Swing panel for managing members without relying on external libraries.
 */
public class MembersPanel extends JPanel {
    private final LibraryService libraryService;
    private final DefaultTableModel tableModel;
    private final JTable memberTable;

    public MembersPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Library Members");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Member ID", "Name", "Email", "Phone", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Add Member");
        JButton viewButton = new JButton("View Details");
        JButton updateInfoButton = new JButton("Update Info");
        JButton updateStatusButton = new JButton("Update Status");
        JButton refreshButton = new JButton("Refresh");
        buttonBar.add(addButton);
        buttonBar.add(viewButton);
        buttonBar.add(updateInfoButton);
        buttonBar.add(updateStatusButton);
        buttonBar.add(refreshButton);
        add(buttonBar, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddMemberDialog());
        viewButton.addActionListener(e -> showMemberDetails());
        updateInfoButton.addActionListener(e -> showUpdateInfoDialog());
        updateStatusButton.addActionListener(e -> showUpdateStatusDialog());
        refreshButton.addActionListener(e -> refresh());

        refresh();
    }

    public void refresh() {
        List<Member> members = libraryService.getAllMembers();
        tableModel.setRowCount(0);
        for (Member member : members) {
            tableModel.addRow(new Object[]{
                    member.getMemberId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone(),
                    member.getStatus()
            });
        }
    }

    private void showAddMemberDialog() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 5));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        form.add(new JLabel("Member ID:"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add New Member",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showError("All fields are required.");
                return;
            }

            try {
                libraryService.addMember(new Member(id, name, email, phone));
                refresh();
                showInfo("Member added successfully!");
            } catch (Exception ex) {
                showError("Unable to add member: " + ex.getMessage());
            }
        }
    }

    private void showMemberDetails() {
        Member selected = getSelectedMember();
        if (selected == null) {
            showError("Select a member first.");
            return;
        }

        String message = String.format(
                "ID: %s%nName: %s%nEmail: %s%nPhone: %s%nStatus: %s%nMax Checkouts: %d",
                selected.getMemberId(),
                selected.getName(),
                selected.getEmail(),
                selected.getPhone(),
                selected.getStatus(),
                selected.getMaxCheckouts()
        );
        JOptionPane.showMessageDialog(this, message, "Member Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateInfoDialog() {
        Member selected = getSelectedMember();
        if (selected == null) {
            showError("Select a member first.");
            return;
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 5));
        JTextField emailField = new JTextField(selected.getEmail());
        JTextField phoneField = new JTextField(selected.getPhone());

        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Update Member Information",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            libraryService.updateMemberInfo(
                    selected.getMemberId(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
            refresh();
            showInfo("Member information updated!");
        }
    }

    private void showUpdateStatusDialog() {
        Member selected = getSelectedMember();
        if (selected == null) {
            showError("Select a member first.");
            return;
        }

        JComboBox<Member.MembershipStatus> statusCombo =
                new JComboBox<>(Member.MembershipStatus.values());
        statusCombo.setSelectedItem(selected.getStatus());

        int result = JOptionPane.showConfirmDialog(
                this,
                statusCombo,
                "Update Membership Status",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            Member.MembershipStatus newStatus = (Member.MembershipStatus) statusCombo.getSelectedItem();
            libraryService.updateMemberStatus(selected.getMemberId(), newStatus);
            refresh();
            showInfo("Status updated!");
        }
    }

    private Member getSelectedMember() {
        int row = memberTable.getSelectedRow();
        if (row < 0) {
            return null;
        }

        int modelRow = memberTable.convertRowIndexToModel(row);
        String memberId = (String) tableModel.getValueAt(modelRow, 0);
        return libraryService.getMember(memberId);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
