/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import core.CollectionManager;
import core.Collection;


/**
 *
 * @author carlo
 */

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private final Color PRIMARY_COLOR = new Color(31, 60, 136); // #1F3C88
    private CollectionManager collection;
    private static final String DATA_FILE = "MovieCollection.csv";
    private static final String SEARCH_PLACEHOLDER = "Type here to search...";
    private final Color PLACEHOLDER_COLOR = new Color(150, 150, 150);
    private final Color NORMAL_TEXT_COLOR = Color.BLACK;
    private boolean searchPlaceholderActive = true; //Track search placeholder to not only rely on




    
    // ================= SIDEBAR COLORS =================
    private final Color SIDEBAR_BG = new Color(31, 60, 136);   // #1F3C88
    private final Color HOVER_BG   = new Color(105,125,175);
    private final Color ACTIVE_BG  = new Color(46, 196, 182);  // #2EC4B6 //(22, 49, 114);   // #163172 
    
    private javax.swing.JLabel activeButton = null;
    
    private java.awt.CardLayout cardLayout;

    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {

        collection = new CollectionManager();
        
        initComponents();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExit();
            }
        });

        
        
        initSidebarButtons();
        initSortComboBox();
        initCategoryFilter();
        initTableModel();
        initSearchPlaceholder();
        initTableSelectionListener();
        setupSearchFieldBehavior();
        
        cardLayout = (java.awt.CardLayout) panelContent.getLayout();

        loadDataFromFile(); //load csv records
        loadAllMedia(); //populate table
        
        panelContent.add(panelMedia, "MEDIA");
        panelContent.add(panelEvaluation, "EVAL");
        
        setActiveButton(btnMedia);
        cardLayout.show(panelContent, "MEDIA"); // default view
        
        updateActionButtonsState();
        disableViewAll();


    }
    
    // ================= SIDEBAR SETUP =================
    private void initSidebarButtons() {
        setupSidebarButton(btnMedia);
        setupSidebarButton(btnEval);
    }
    
    private void initSortComboBox() {
        cmbSort.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                updateTableView();
            }
        });
    }
    
    private void initCategoryFilter() {
        cmbCategoryFilter.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                updateTableView();
            }
        });
    }

    
    private void setupSidebarButton(javax.swing.JLabel lbl) {
        lbl.setOpaque(true);
        lbl.setBackground(SIDEBAR_BG);
        lbl.setForeground(java.awt.Color.WHITE);
        lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 12));

        lbl.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (lbl != activeButton) {
                    lbl.setBackground(HOVER_BG);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (lbl != activeButton) {
                    lbl.setBackground(SIDEBAR_BG);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                setActiveButton(lbl);

                if (lbl == btnMedia) {
                    cardLayout.show(panelContent, "MEDIA");
                } else if (lbl == btnEval) {
                    cardLayout.show(panelContent, "EVAL");
                } 
//                else if (lbl == btnFile) {
//                    cardLayout.show(panelContent, "FILE");
//                }

            }
        });
    }


    private void setActiveButton(javax.swing.JLabel selected) {
        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR_BG);
        }
        activeButton = selected;
        activeButton.setBackground(ACTIVE_BG);
    }
    
    private void openUpdateMediaDialog() {

        int selectedRow = tblMedia.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a media record to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int mediaId = (int) tblMedia.getValueAt(selectedRow, 0);

        UpdateMediaDialog dialog = new UpdateMediaDialog(
            this,          // parent frame
            true,          // modal
            collection,    // shared backend instance
            mediaId        // selected media ID
        );

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        loadAllMedia(); // refresh table after dialog closes
    }
    
    private void loadAllMedia() {
        populateTable(collection.getAll());
        updateActionButtonsState();

    }

    
    
    private void deleteSelectedMedia() {

        int selectedRow = tblMedia.getSelectedRow();

        // 1️⃣ Ensure a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a media record to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int mediaId = (int) tblMedia.getValueAt(selectedRow, 0);
        String title = tblMedia.getValueAt(selectedRow, 1).toString();

        // 2️⃣ Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete:\n\n\"" + title + "\" ?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        // 3️⃣ User cancelled
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 4️⃣ Perform deletion
        boolean deleted = collection.delete(mediaId);

        if (deleted) {
            JOptionPane.showMessageDialog(
                this,
                "Media record deleted successfully.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadAllMedia(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to delete media record.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    
    private void loadDataFromFile() {
        try {
            collection.loadFromFile(DATA_FILE);
        } catch (java.io.FileNotFoundException e) {
            // File does not exist yet – start with empty data
            System.out.println("No data file found. Starting with empty collection.");
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(
                this,
                "Failed to load data from file:\n" + e.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        
        System.out.println("Loaded records: " + collection.getAll().size());

    }

    private void initTableModel() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "ID", "Title", "Category", "Genre", "Year", "Rating", "Views"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is read-only
            }
        };

        tblMedia.setModel(model);
    }
    
    private void initSearchPlaceholder() {
        txtSearch.setText(SEARCH_PLACEHOLDER);
        txtSearch.setForeground(PLACEHOLDER_COLOR);
        searchPlaceholderActive = true;
    }

    
    private void openAddMediaDialog() {

        AddMediaDialog dialog = new AddMediaDialog(
            this,       // parent frame
            true,       // modal
            collection  // shared backend instance
        );

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        loadAllMedia(); // refresh table after dialog closes
    }
    
    private void populateTable(java.util.List<core.Collection> list) {

        DefaultTableModel model = (DefaultTableModel) tblMedia.getModel();
        model.setRowCount(0);

        for (core.Collection c : list) {
            model.addRow(new Object[]{
                c.getId(),
                c.getTitle(),
                c.getCategory(),
                c.getGenre(),
                c.getYear(),
                c.getRating(),
                c.getViews()
            });
        }
    }
    
    
    private void searchMedia() {

        String keyword = txtSearch.getText().trim().toLowerCase();
        
        //Prevent user from searching with empty search textfield or if the placeholder is still active
        if (keyword.isEmpty() || searchPlaceholderActive) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a search keyword.",
                "Empty Search",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        java.util.List<core.Collection> results = new java.util.ArrayList<>();

        for (core.Collection c : collection.getAll()) {

            if (c.getTitle().toLowerCase().contains(keyword)
                    || c.getGenre().toLowerCase().contains(keyword)
                    || String.valueOf(c.getYear()).contains(keyword)) {

                results.add(c);
            }
        }

   
        enableViewAll();
        updateTableView();

    }
    
    private void setupSearchFieldBehavior() {

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                clearPlaceholderIfNeeded();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    initSearchPlaceholder();
                }
            }
        });

        txtSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                clearPlaceholderIfNeeded();
            }
        });
    }

    private void clearPlaceholderIfNeeded() {
        if (txtSearch.getText().equals(SEARCH_PLACEHOLDER)) {
            txtSearch.setText("");
            txtSearch.setForeground(NORMAL_TEXT_COLOR);
            searchPlaceholderActive = false;
        }
    }

    private void runTopMoviesEvaluation() {

        EvaluateMoviesDialog dialog = new EvaluateMoviesDialog(
            this,
            true,
            collection
        );

        dialog.setVisible(true);

        if (dialog.getResults() != null) {
            displayTopMovies(dialog.getResults());

        }
    }
    
    
    private void clearEvaluationResults() {
        panelEvalResults.removeAll();
        panelEvalResults.revalidate();
        panelEvalResults.repaint();
    }
    
    private javax.swing.JPanel createRankCard(
            int rank,
            String title,
            String category,
            int year,
            double rating,
            int views
    ) {
        // === Card container ===
        javax.swing.JPanel card = new javax.swing.JPanel();
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 90));
        card.setLayout(new java.awt.BorderLayout(10, 0));

        // === Rank badge (square) ===
        javax.swing.JLabel lblRank = new javax.swing.JLabel(
            switch (rank) {
                case 1 -> "TOP 1";
                case 2 -> "TOP 2";
                case 3 -> "TOP 3";
                default -> "TOP " + rank;
            },
            javax.swing.SwingConstants.CENTER
        );

        //for top # cards
        lblRank.setOpaque(true);
        lblRank.setBackground(PRIMARY_COLOR);
        lblRank.setForeground(java.awt.Color.WHITE);
        lblRank.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblRank.setPreferredSize(new java.awt.Dimension(70, 70));
        lblRank.setMinimumSize(new java.awt.Dimension(70, 70));
        lblRank.setMaximumSize(new java.awt.Dimension(70, 70));

        // === Title ===
        javax.swing.JLabel lblTitle = new javax.swing.JLabel(title);
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblTitle.setForeground(java.awt.Color.BLACK);

        // === Meta info ===
        javax.swing.JLabel lblMeta = new javax.swing.JLabel(
            category + " | Year: " + year +
            " | Rating: " + rating +
            " | Views: " + views
        );
        lblMeta.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblMeta.setForeground(new java.awt.Color(90, 90, 90));

        // === Text container (VERTICALLY CENTERED) ===
        javax.swing.JPanel textPanel = new javax.swing.JPanel();
        textPanel.setOpaque(false);

        textPanel.setLayout(new javax.swing.BoxLayout(
                textPanel, javax.swing.BoxLayout.Y_AXIS
        ));

        textPanel.add(javax.swing.Box.createVerticalGlue());

        lblTitle.setAlignmentX(javax.swing.JComponent.LEFT_ALIGNMENT);
        textPanel.add(lblTitle);

        textPanel.add(javax.swing.Box.createVerticalStrut(2));

        lblMeta.setAlignmentX(javax.swing.JComponent.LEFT_ALIGNMENT);
        textPanel.add(lblMeta);

        textPanel.add(javax.swing.Box.createVerticalGlue());

        textPanel.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 0)
        );


        // === Assemble card ===
        card.add(lblRank, java.awt.BorderLayout.WEST);
        card.add(textPanel, java.awt.BorderLayout.CENTER);

        return card;
    }

    private void displayTopMovies(java.util.List<core.Movie> movies) {

        clearEvaluationResults();

        int rank = 1;
        for (core.Movie m : movies) {
            panelEvalResults.add(
                createRankCard(
                    rank++,
                    m.getTitle(),
                    "Movie",
                    m.getYear(),
                    m.getRating(),
                    m.getViews()
                )
            );
            panelEvalResults.add(javax.swing.Box.createVerticalStrut(10));
        }

        panelEvalResults.revalidate();
        panelEvalResults.repaint();
    }

    private void displayTopShows() {

        clearEvaluationResults();

        int rank = 1;
        for (core.Show s : collection.evaluateTopShowsByViews()) {
            panelEvalResults.add(
                createRankCard(
                    rank++,
                    s.getTitle(),
                    "Show",
                    s.getYear(),
                    s.getRating(),
                    s.getViews()
                )
            );
            panelEvalResults.add(javax.swing.Box.createVerticalStrut(10));
        }
    }

    private void displayTopDocumentaries() {

        clearEvaluationResults();

        int rank = 1;
        for (core.Documentary d : collection.evaluateTopDocumentariesByRating()) {
            panelEvalResults.add(
                createRankCard(
                    rank++,
                    d.getTitle(),
                    "Documentary",
                    d.getYear(),
                    d.getRating(),
                    d.getViews()
                )
            );
            panelEvalResults.add(javax.swing.Box.createVerticalStrut(10));
        }
    }
    
    private void updateActionButtonsState() {
        boolean hasSelection = tblMedia.getSelectedRow() != -1;

        btnUpdateMedia.setEnabled(hasSelection);
        btnDeleteMedia.setEnabled(hasSelection);

        // visual feedback (important for JLabel buttons)
        btnUpdateMedia.setBackground(
            hasSelection ? new Color(31, 60, 136) : new Color(180, 180, 180)
        );
        btnDeleteMedia.setBackground(
            hasSelection ? new Color(244, 66, 53) : new Color(180, 180, 180)
        );
    }



    private void initTableSelectionListener() {

        tblMedia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateActionButtonsState();
            }
        });
    }
    
    private void enableViewAll() {
        btnViewAll.setEnabled(true);
        btnViewAll.setBackground(new Color(31, 60, 136)); // primary
    }

    private void disableViewAll() {
        btnViewAll.setEnabled(false);
        btnViewAll.setBackground(new Color(180, 180, 180)); // disabled gray
    }


    private void loadFromFile() {
        loadDataFromFile();   // reuse your existing method
        loadAllMedia();       // refresh the table view
    }

    private void saveToFile() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "This will overwrite the existing CSV file.\n\nContinue?",
            "Confirm Save",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            collection.saveToFile(DATA_FILE);
            JOptionPane.showMessageDialog(
                this,
                "Data saved successfully to " + DATA_FILE + ".",
                "Save Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save data:\n" + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void confirmExit() {

        Object[] options = {
            "Save & Exit",
            "Exit Without Saving",
            "Cancel"
        };

        int choice = JOptionPane.showOptionDialog(
            this,
            "Do you want to save changes before exiting?",
            "Exit Application",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
            return; // user cancelled
        }

        if (choice == JOptionPane.YES_OPTION) {
            saveToFile();   // save first
        }

        // Exit without saving OR after saving
        System.exit(0);
    }
    
    
    
    private void refreshTable(java.util.List<core.Collection> data) {

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMedia.getModel();

        // Clear existing rows
        model.setRowCount(0);

        // Repopulate table
        for (core.Collection c : data) {

            Object[] row = {
                c.getId(),
                c.getTitle(),
                c.getCategory(),
                c.getGenre(),
                c.getYear(),
                c.getRating(),
                c.getViews()
            };

            model.addRow(row);
        }
    }
    

    
    private void updateTableView() {

        // Start from MASTER data
        java.util.List<core.Collection> workingList =
                new java.util.ArrayList<>(collection.getAll());

        // Apply CATEGORY filter
        String category = cmbCategoryFilter.getSelectedItem().toString();
        if (!category.contains("All")) {
            workingList.removeIf(c ->
                !c.getCategory().equalsIgnoreCase(category)
            );
        }

        // Apply SEARCH filter
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (!searchPlaceholderActive && !keyword.isEmpty()) {
            workingList.removeIf(c ->
                !(c.getTitle().toLowerCase().contains(keyword)
                  || c.getGenre().toLowerCase().contains(keyword)
                  || String.valueOf(c.getYear()).contains(keyword))
            );
        }

        // Apply SORT
        java.util.Comparator<core.Collection> comparator = getSelectedComparator();
        if (comparator != null) {
            workingList.sort(comparator);
        }

        // Update UI
        refreshTable(workingList);
    }
    
    private java.util.Comparator<core.Collection> getSelectedComparator() {

        return switch (cmbSort.getSelectedIndex()) {

            case 1 -> java.util.Comparator.comparing(
                core.Collection::getTitle,
                String.CASE_INSENSITIVE_ORDER
            );

            case 2 -> java.util.Comparator.comparing(
                core.Collection::getTitle,
                String.CASE_INSENSITIVE_ORDER
            ).reversed();

            case 3 -> java.util.Comparator.comparingInt(
                core.Collection::getYear
            );

            case 4 -> java.util.Comparator.comparingInt(
                core.Collection::getYear
            ).reversed();

            case 5 -> java.util.Comparator.comparingDouble(
                core.Collection::getRating
            ).reversed();

            case 6 -> java.util.Comparator.comparingDouble(
                core.Collection::getRating
            );

            case 7 -> java.util.Comparator.comparingInt(
                core.Collection::getViews
            ).reversed();

            case 8 -> java.util.Comparator.comparingInt(
                core.Collection::getViews
            );

            case 9 -> java.util.Comparator.comparing(
                core.Collection::getCategory,
                String.CASE_INSENSITIVE_ORDER
            );

            default -> null;
        };
    }




    
    






    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnMedia = new javax.swing.JLabel();
        btnEval = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panelContent = new javax.swing.JPanel();
        panelMedia = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnAddMedia = new javax.swing.JLabel();
        btnViewAll = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JLabel();
        btnUpdateMedia = new javax.swing.JLabel();
        btnDeleteMedia = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedia = new javax.swing.JTable();
        cmbSort = new javax.swing.JComboBox<>();
        cmbCategoryFilter = new javax.swing.JComboBox<>();
        panelEvaluation = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cmbEvalType = new javax.swing.JComboBox<>();
        btnGo = new javax.swing.JLabel();
        panelEvalResults = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuLoad = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuAbout = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Movie Collection Manager");
        setBackground(new java.awt.Color(31, 60, 136));
        setLocationByPlatform(true);
        setPreferredSize(new java.awt.Dimension(1600, 650));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(31, 60, 136));
        jPanel1.setName("panelSidebar"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(220, 650));

        btnMedia.setBackground(new java.awt.Color(105, 125, 175));
        btnMedia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnMedia.setForeground(new java.awt.Color(255, 255, 255));
        btnMedia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnMedia.setText("Media Management");
        btnMedia.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMedia.setIconTextGap(15);
        btnMedia.setOpaque(true);

        btnEval.setBackground(new java.awt.Color(105, 125, 175));
        btnEval.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEval.setForeground(new java.awt.Color(255, 255, 255));
        btnEval.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEval.setText("Evaluation & Rankings");
        btnEval.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnEval.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnEval.setIconTextGap(15);
        btnEval.setOpaque(true);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Movie Collection Manager");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("MCM");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMedia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEval, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(104, 104, 104)
                .addComponent(btnMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEval, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(370, Short.MAX_VALUE))
        );

        panelContent.setBackground(new java.awt.Color(245, 247, 250));
        panelContent.setLayout(new java.awt.CardLayout());

        panelMedia.setBackground(new java.awt.Color(245, 247, 250));
        panelMedia.setLayout(new java.awt.BorderLayout());

        btnAddMedia.setBackground(new java.awt.Color(31, 60, 136));
        btnAddMedia.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnAddMedia.setForeground(new java.awt.Color(255, 255, 255));
        btnAddMedia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddMedia.setText("Add Media");
        btnAddMedia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddMedia.setOpaque(true);
        btnAddMedia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddMediaMouseClicked(evt);
            }
        });

        btnViewAll.setBackground(new java.awt.Color(31, 60, 136));
        btnViewAll.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnViewAll.setForeground(new java.awt.Color(255, 255, 255));
        btnViewAll.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnViewAll.setText("View All");
        btnViewAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewAll.setOpaque(true);
        btnViewAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnViewAllMouseClicked(evt);
            }
        });

        txtSearch.setToolTipText("Search");
        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(31, 60, 136)));
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        btnSearch.setBackground(new java.awt.Color(31, 60, 136));
        btnSearch.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSearch.setIcon(new javax.swing.ImageIcon("C:\\Users\\carlo\\Documents\\NetBeansProjects\\MovieCollectionGUI\\icons\\icons8-search-24.png")); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.setOpaque(true);
        btnSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSearchMouseClicked(evt);
            }
        });

        btnUpdateMedia.setBackground(new java.awt.Color(31, 60, 136));
        btnUpdateMedia.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnUpdateMedia.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateMedia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnUpdateMedia.setText("Update Media");
        btnUpdateMedia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateMedia.setOpaque(true);
        btnUpdateMedia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUpdateMediaMouseClicked(evt);
            }
        });

        btnDeleteMedia.setBackground(new java.awt.Color(244, 66, 53));
        btnDeleteMedia.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnDeleteMedia.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteMedia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnDeleteMedia.setText("Delete Media");
        btnDeleteMedia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteMedia.setOpaque(true);
        btnDeleteMedia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMediaMouseClicked(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(31, 60, 136)));

        tblMedia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Title", "Type", "Genre", "Year", "Rating", "Views"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMedia.setGridColor(new java.awt.Color(99, 117, 161));
        tblMedia.setSelectionBackground(new java.awt.Color(46, 196, 182));
        tblMedia.setShowGrid(true);
        tblMedia.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblMedia);

        cmbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort by", "Title (A–Z)", "Title (Z–A)", "Year (Ascending)", "Year (Descending)", "Rating (High → Low)", "Rating (Low → High)", "Views (High → Low)", "Views (Low → High)", "Category (A–Z)" }));

        cmbCategoryFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Filter By All Category", "Movie", "Show", "Documentary" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1033, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAddMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnViewAll, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnUpdateMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbCategoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmbSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnViewAll, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDeleteMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdateMedia, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbSort, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbCategoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelMedia.add(jPanel2, java.awt.BorderLayout.CENTER);

        jLabel3.setText("Choose evaluation type:");

        cmbEvalType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Evaluation Type", "Top Movies by Year", "Most Viewed Shows", "Top Rated Documentaries" }));

        btnGo.setBackground(new java.awt.Color(31, 60, 136));
        btnGo.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnGo.setForeground(new java.awt.Color(255, 255, 255));
        btnGo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnGo.setText("Go");
        btnGo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGo.setOpaque(true);
        btnGo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGoMouseClicked(evt);
            }
        });

        panelEvalResults.setLayout(new javax.swing.BoxLayout(panelEvalResults, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout panelEvaluationLayout = new javax.swing.GroupLayout(panelEvaluation);
        panelEvaluation.setLayout(panelEvaluationLayout);
        panelEvaluationLayout.setHorizontalGroup(
            panelEvaluationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEvaluationLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(panelEvaluationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEvalResults, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelEvaluationLayout.createSequentialGroup()
                        .addComponent(cmbEvalType, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(393, Short.MAX_VALUE))
        );
        panelEvaluationLayout.setVerticalGroup(
            panelEvaluationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEvaluationLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEvaluationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbEvalType))
                .addGap(36, 36, 36)
                .addComponent(panelEvalResults, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jMenu1.setText("File");

        menuLoad.setText("Load Data from CSV");
        menuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLoadActionPerformed(evt);
            }
        });
        jMenu1.add(menuLoad);

        menuSave.setText("Save Data to CSV");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(menuSave);
        jMenu1.add(jSeparator1);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuExit);

        jMenuBar1.add(jMenu1);

        menuAbout.setText("About");

        jMenuItem1.setText("About MCM");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuAbout.add(jMenuItem1);

        jMenuBar1.add(menuAbout);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMedia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelEvaluation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                        .addComponent(panelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(panelMedia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 216, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(panelEvaluation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(315, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {                                          
        searchMedia();
    }                                         

    private void btnUpdateMediaMouseClicked(java.awt.event.MouseEvent evt) {                                            
        openUpdateMediaDialog();
    }                                           

    private void btnDeleteMediaMouseClicked(java.awt.event.MouseEvent evt) {                                            
        deleteSelectedMedia();
    }                                           

    private void btnAddMediaMouseClicked(java.awt.event.MouseEvent evt) {                                         
        openAddMediaDialog();
    }                                        

    private void btnSearchMouseClicked(java.awt.event.MouseEvent evt) {                                       
        searchMedia();
    }                                      

    private void btnViewAllMouseClicked(java.awt.event.MouseEvent evt) {                                        
        //loadAllMedia();
        initSearchPlaceholder();
        
        // Force focus away from text field
        panelMedia.requestFocusInWindow();
        disableViewAll();
        
        //for sort and filter to reset:
        cmbSort.setSelectedIndex(0);
        cmbCategoryFilter.setSelectedIndex(0);
        refreshTable(collection.getAll());
        
    }                                       

    private void btnGoMouseClicked(java.awt.event.MouseEvent evt) {                                   

        String selected = cmbEvalType.getSelectedItem().toString();

        
        if (selected.equals("Select Evaluation Type")) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an evaluation type.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        switch (selected) {
            case "Top Movies by Year" -> runTopMoviesEvaluation();
            case "Most Viewed Shows" -> displayTopShows();
            case "Top Rated Documentaries" -> displayTopDocumentaries();
        }
    }                                  

    private void menuLoadActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here
        loadFromFile();
    }                                        

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
        saveToFile();
    }                                        

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
        confirmExit();
    }                                        

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        AboutDialog dialog = new AboutDialog(this, true);
        dialog.setVisible(true);
    }                                          

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        */
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JLabel btnAddMedia;
    private javax.swing.JLabel btnDeleteMedia;
    private javax.swing.JLabel btnEval;
    private javax.swing.JLabel btnGo;
    private javax.swing.JLabel btnMedia;
    private javax.swing.JLabel btnSearch;
    private javax.swing.JLabel btnUpdateMedia;
    private javax.swing.JLabel btnViewAll;
    private javax.swing.JComboBox<String> cmbCategoryFilter;
    private javax.swing.JComboBox<String> cmbEvalType;
    private javax.swing.JComboBox<String> cmbSort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuLoad;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelEvalResults;
    private javax.swing.JPanel panelEvaluation;
    private javax.swing.JPanel panelMedia;
    private javax.swing.JTable tblMedia;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration                   
}
