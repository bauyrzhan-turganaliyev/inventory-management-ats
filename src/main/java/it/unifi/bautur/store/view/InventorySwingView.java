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
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import it.unifi.bautur.store.model.Category;
import it.unifi.bautur.store.model.Product;

public class InventorySwingView extends JFrame implements InventoryView {

	private static final long serialVersionUID = 1L;

	private final DefaultTableModel productTableModel;
	private final JTable productTable;

	private final JTextField productNameField;
	private final JTextField productQuantityField;
	private final JTextField productPriceField;
	private final JTextField stockQuantityField;

	private final JComboBox<CategoryItem> categoryComboBox;

	private final JButton addProductButton;
	private final JButton deleteProductButton;
	private final JButton updateStockButton;
	private final JButton assignCategoryButton;
	private final JButton refreshButton;

	private transient InventoryPresenter presenter;

	public InventorySwingView() {
		super("Inventory Management");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setSize(900, 550);
		setLocationRelativeTo(null);

		productTableModel = new DefaultTableModel(new Object[] { "ID", "Name", "Quantity", "Price", "Category" }, 0) {
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

		productQuantityField = new JTextField();
		productQuantityField.setName("productQuantityField");

		productPriceField = new JTextField();
		productPriceField.setName("productPriceField");

		stockQuantityField = new JTextField();
		stockQuantityField.setName("stockQuantityField");

		categoryComboBox = new JComboBox<>();
		categoryComboBox.setName("categoryComboBox");

		addProductButton = new JButton("Add Product");

		addProductButton.setName("addProductButton");

		deleteProductButton = new JButton("Delete Product");

		deleteProductButton.setName("deleteProductButton");

		updateStockButton = new JButton("Update Stock");

		updateStockButton.setName("updateStockButton");

		assignCategoryButton = new JButton("Assign Category");

		assignCategoryButton.setName("assignCategoryButton");

		refreshButton = new JButton("Refresh");

		refreshButton.setName("refreshButton");

		JPanel productInputPanel = createProductInputPanel();

		JPanel stockPanel = createStockPanel();

		JPanel actionPanel = createActionPanel();

		JPanel controlsPanel = new JPanel(new BorderLayout());

		controlsPanel.add(productInputPanel, BorderLayout.NORTH);

		controlsPanel.add(stockPanel, BorderLayout.CENTER);

		controlsPanel.add(actionPanel, BorderLayout.SOUTH);

		add(tableScrollPane, BorderLayout.CENTER);

		add(controlsPanel, BorderLayout.SOUTH);

		registerListeners();
	}

	private JPanel createProductInputPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));

		panel.setBorder(BorderFactory.createTitledBorder("Product"));

		panel.add(new JLabel("Name:"));
		panel.add(new JLabel("Quantity:"));
		panel.add(new JLabel("Price:"));
		panel.add(new JLabel("Category:"));

		panel.add(productNameField);
		panel.add(productQuantityField);
		panel.add(productPriceField);
		panel.add(categoryComboBox);

		return panel;
	}

	private JPanel createStockPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		panel.setBorder(BorderFactory.createTitledBorder("Stock"));

		panel.add(new JLabel("New quantity:"));

		stockQuantityField.setColumns(10);

		panel.add(stockQuantityField);
		panel.add(updateStockButton);

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

		updateStockButton.addActionListener(event -> updateStock());

		assignCategoryButton.addActionListener(event -> assignCategory());

		refreshButton.addActionListener(event -> refresh());
	}

	public void setPresenter(InventoryPresenter presenter) {
		this.presenter = presenter;
	}

	private void addProduct() {
		try {
			String name = productNameField.getText();

			int quantity = Integer.parseInt(productQuantityField.getText());

			double price = Double.parseDouble(productPriceField.getText());

			presenter.addProduct(name, quantity, price);
		} catch (NumberFormatException exception) {
			showError("Quantity and price must be valid numbers");
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

	private void updateStock() {
		Long productId = getSelectedProductId();

		if (productId == null) {
			showError("Please select a product");

			return;
		}

		try {
			int quantity = Integer.parseInt(stockQuantityField.getText());

			presenter.updateProductStock(productId, quantity);
		} catch (NumberFormatException exception) {
			showError("Stock quantity must be a valid number");
		}
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

			productTableModel.addRow(new Object[] { product.getId(), product.getName(), product.getQuantity(),
					product.getPrice(), categoryName });
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