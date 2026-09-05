package it.unifi.bautur.store.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;
import javax.swing.WindowConstants;

public class InventorySwingView extends JFrame implements InventoryView {

	private static final long serialVersionUID = 1L;

	private final DefaultTableModel productTableModel;
	private final JTable productTable;

	private final JTextField productNameField;
	private final JTextField productPriceField;

	private final JComboBox<CategoryItem> categoryComboBox;

	private final JButton addProductButton;
	private final JButton deleteProductButton;
	private final JButton assignCategoryButton;
	private final JButton refreshButton;

	private transient InventoryPresenter presenter;

	public InventorySwingView() {
		super("Inventory Management");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 500);
		setLocationRelativeTo(null);

		productTableModel = new DefaultTableModel(new Object[] { "ID", "Name", "Price", "Category" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		productTable = new JTable(productTableModel);

		productTable.setName("productTable");
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane tableScrollPane = new JScrollPane(productTable);

		tableScrollPane.setBorder(BorderFactory.createTitledBorder("Products"));

		productNameField = new JTextField();

		productNameField.setName("productNameField");

		productPriceField = new JTextField();

		productPriceField.setName("productPriceField");

		categoryComboBox = new JComboBox<>();

		categoryComboBox.setName("categoryComboBox");

		addProductButton = new JButton("Add Product");

		addProductButton.setName("addProductButton");

		deleteProductButton = new JButton("Delete Product");

		deleteProductButton.setName("deleteProductButton");

		assignCategoryButton = new JButton("Assign Category");

		assignCategoryButton.setName("assignCategoryButton");

		refreshButton = new JButton("Refresh");

		refreshButton.setName("refreshButton");

		JPanel productInputPanel = createProductInputPanel();

		JPanel actionPanel = createActionPanel();

		JPanel bottomPanel = new JPanel(new BorderLayout());

		bottomPanel.add(productInputPanel, BorderLayout.CENTER);

		bottomPanel.add(actionPanel, BorderLayout.SOUTH);

		add(tableScrollPane, BorderLayout.CENTER);

		add(bottomPanel, BorderLayout.SOUTH);

		registerListeners();
	}

	private JPanel createProductInputPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 3, 10, 5));

		panel.setBorder(BorderFactory.createTitledBorder("Product"));

		panel.add(new JLabel("Name:"));

		panel.add(new JLabel("Price:"));

		panel.add(new JLabel("Category:"));

		panel.add(productNameField);
		panel.add(productPriceField);
		panel.add(categoryComboBox);

		return panel;
	}

	private JPanel createActionPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		panel.add(addProductButton);
		panel.add(deleteProductButton);
		panel.add(assignCategoryButton);
		panel.add(refreshButton);

		return panel;
	}

	private void registerListeners() {
		addProductButton.addActionListener(event -> addProduct());

		deleteProductButton.addActionListener(event -> deleteSelectedProduct());

		assignCategoryButton.addActionListener(event -> assignCategory());

		refreshButton.addActionListener(event -> refresh());
	}

	public void setPresenter(InventoryPresenter presenter) {
		this.presenter = presenter;
	}

	private void addProduct() {
		try {
			String name = productNameField.getText();

			double price = Double.parseDouble(productPriceField.getText());

			presenter.addProduct(name, price);
		} catch (NumberFormatException exception) {
			showError("Price must be a valid number");
		}
	}

	private void deleteSelectedProduct() {
		Long productId = getSelectedProductId();

		if (productId == null) {
			showError("Please select a product");

			return;
		}

		presenter.deleteProduct(productId);
	}

	private void assignCategory() {
		Long productId = getSelectedProductId();

		if (productId == null) {
			showError("Please select a product");

			return;
		}

		CategoryItem category = (CategoryItem) categoryComboBox.getSelectedItem();

		if (category == null) {
			showError("Please select a category");

			return;
		}

		presenter.assignCategory(productId, category.id());
	}

	private Long getSelectedProductId() {
		int selectedRow = productTable.getSelectedRow();

		if (selectedRow < 0) {
			return null;
		}

		Object value = productTableModel.getValueAt(selectedRow, 0);

		return (Long) value;
	}

	private void refresh() {
		presenter.loadProducts();
		presenter.loadCategories();
	}

	@Override
	public void showProducts(List<Product> products) {
		productTableModel.setRowCount(0);

		for (Product product : products) {
			Category category = product.getCategory();

			String categoryName = category == null ? "" : category.getName();

			productTableModel
					.addRow(new Object[] { product.getId(), product.getName(), product.getPrice(), categoryName });
		}
	}

	@Override
	public void showCategories(List<Category> categories) {
		categoryComboBox.removeAllItems();

		for (Category category : categories) {
			categoryComboBox.addItem(new CategoryItem(category.getId(), category.getName()));
		}
	}

	@Override
	public void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private record CategoryItem(Long id, String name) {

		@Override
		public String toString() {
			return name;
		}
	}
}