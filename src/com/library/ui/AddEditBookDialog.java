package com.library.ui;

import com.library.model.Book;
import com.library.model.EBook;
import com.library.model.LibraryItem;

import javax.swing.*;
import java.awt.*;

public class AddEditBookDialog extends JDialog {
    private JTextField idField, titleField, authorField, categoryField, totalCopiesField, availCopiesField, extraField1, extraField2;
    private JComboBox<String> typeCombo;
    private JLabel extraLabel1, extraLabel2;
    private boolean saved = false;
    private LibraryItem item;

    public AddEditBookDialog(Frame owner, String title, LibraryItem existingItem) {
        super(owner, title, true);
        this.item = existingItem;
        initUI();
        if (existingItem != null) populateFields();
    }

    private void initUI() {
        setSize(450, 420);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Item ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);

        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Item Type:"));
        typeCombo = new JComboBox<>(new String[]{"PHYSICAL_BOOK", "EBOOK"});
        typeCombo.addActionListener(e -> updateExtraLabels());
        formPanel.add(typeCombo);

        formPanel.add(new JLabel("Total Copies:"));
        totalCopiesField = new JTextField("5");
        formPanel.add(totalCopiesField);

        extraLabel1 = new JLabel("ISBN:");
        formPanel.add(extraLabel1);
        extraField1 = new JTextField("978-0123456789");
        formPanel.add(extraField1);

        extraLabel2 = new JLabel("Page Count:");
        formPanel.add(extraLabel2);
        extraField2 = new JTextField("350");
        formPanel.add(extraField2);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Item");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void updateExtraLabels() {
        String type = (String) typeCombo.getSelectedItem();
        if ("EBOOK".equals(type)) {
            extraLabel1.setText("Download URL:");
            extraLabel2.setText("File Size (MB):");
            extraField1.setText("https://library.org/download");
            extraField2.setText("10.5");
        } else {
            extraLabel1.setText("ISBN:");
            extraLabel2.setText("Page Count:");
            extraField1.setText("978-0123456789");
            extraField2.setText("350");
        }
    }

    private void populateFields() {
        idField.setText(item.getItemId());
        idField.setEditable(false);
        titleField.setText(item.getTitle());
        authorField.setText(item.getAuthor());
        categoryField.setText(item.getCategory());
        totalCopiesField.setText(String.valueOf(item.getTotalCopies()));
        typeCombo.setSelectedItem(item.getItemType());

        if (item instanceof Book b) {
            extraField1.setText(b.getIsbn());
            extraField2.setText(String.valueOf(b.getPages()));
        } else if (item instanceof EBook eb) {
            extraField1.setText(eb.getDownloadUrl());
            extraField2.setText(String.valueOf(eb.getFileSizeMb()));
        }
    }

    private void onSave() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String cat = categoryField.getText().trim();
            int total = Integer.parseInt(totalCopiesField.getText().trim());
            String type = (String) typeCombo.getSelectedItem();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Title, and Author are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("EBOOK".equals(type)) {
                double size = Double.parseDouble(extraField2.getText().trim());
                item = new EBook(id, title, author, cat, total, total, extraField1.getText().trim(), size);
            } else {
                int pages = Integer.parseInt(extraField2.getText().trim());
                item = new Book(id, title, author, cat, total, total, extraField1.getText().trim(), pages);
            }

            saved = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Copies, Page Count, and File Size must be valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public LibraryItem getItem() { return item; }
}
