package view;

import dao.DiemSoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ThongKePanel extends JPanel {

    private DiemSoDAO diemDAO;

    private JComboBox<String>    cmbKhoa;
    private DefaultTableModel    modelKhoa;
    private JLabel               lblTongKhoa;
    private JTextField           txtTimKhoa;

    private JComboBox<String>    cmbLop;
    private DefaultTableModel    modelLop;
    private JLabel               lblTongLop;
    private JTextField           txtTimLop;

    private JComboBox<String>    cmbHocLuc;
    private DefaultTableModel    modelHL;
    private JLabel               lblTongHL;
    private JTextField           txtTimHL;

    private javax.swing.Timer    debounceTimer;

    private static final String[] XEP_LOAI   = {"A+","A","A-","B+","B","B-","C+","C","D+","D","F"};
    private static final String[] TABLE_COLS  = {"STT","Mã SV","Họ tên","Lớp","Khoa","Điểm TB","Xếp loại"};

    public ThongKePanel() {
        diemDAO = new DiemSoDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("THỐNG KÊ SINH VIÊN & ĐIỂM SỐ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 80, 160));
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.addTab("Theo Khoa",    buildTabKhoa());
        tabs.addTab("Theo Lớp",     buildTabLop());
        tabs.addTab("Theo Học lực", buildTabHocLuc());

        // Chỉ load tab đang active, tránh query 3 lần khi khởi tạo
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if      (idx == 0) taiKhoa();
            else if (idx == 1) taiLop();
            else if (idx == 2) taiHocLuc();
        });

        add(tabs, BorderLayout.CENTER);

        JButton btnLamMoi = taoNut("Làm mới", new Color(30, 80, 160));
        btnLamMoi.addActionListener(e -> lamMoi());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(new Color(245, 247, 250));
        south.add(btnLamMoi);
        add(south, BorderLayout.SOUTH);
    }

    // ── TAB 1 ──────────────────────────────────────────────────
    private JPanel buildTabKhoa() {
        JPanel panel = new JPanel(new BorderLayout(5, 8));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.add(boldLabel("Khoa:"));
        cmbKhoa = new JComboBox<>();
        cmbKhoa.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbKhoa.setPreferredSize(new Dimension(220, 28));
        cmbKhoa.addItem("-- Tất cả khoa --");
        diemDAO.layDanhSachKhoa().forEach(cmbKhoa::addItem);
        toolbar.add(cmbKhoa);
        toolbar.add(Box.createHorizontalStrut(10));
        txtTimKhoa = searchField();
        toolbar.add(txtTimKhoa);
        JButton bx = xoaBtn(); bx.addActionListener(e -> txtTimKhoa.setText(""));
        toolbar.add(bx);
        lblTongKhoa = countLabel();
        toolbar.add(lblTongKhoa);
        panel.add(toolbar, BorderLayout.NORTH);

        modelKhoa = buildModel();
        JPanel wrapper = titledPanel("Danh sách sinh viên theo khoa");
        wrapper.add(new JScrollPane(buildTable(modelKhoa)));
        panel.add(wrapper, BorderLayout.CENTER);

        cmbKhoa.addActionListener(e -> taiKhoa());
        txtTimKhoa.getDocument().addDocumentListener(dl(() -> taiKhoa()));
        return panel;
    }

    private void taiKhoa() {
        String khoa   = cmbKhoa.getSelectedIndex() == 0 ? null : (String) cmbKhoa.getSelectedItem();
        String tuKhoa = txtTimKhoa.getText();
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                return diemDAO.layDanhSachSinhVienDiem(khoa, null, null, tuKhoa);
            }
            @Override protected void done() {
                try { renderTable(modelKhoa, get(), lblTongKhoa); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ── TAB 2 ──────────────────────────────────────────────────
    private JPanel buildTabLop() {
        JPanel panel = new JPanel(new BorderLayout(5, 8));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.add(boldLabel("Lớp:"));
        cmbLop = new JComboBox<>();
        cmbLop.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbLop.setPreferredSize(new Dimension(200, 28));
        cmbLop.addItem("-- Tất cả lớp --");
        diemDAO.layDanhSachLop().forEach(cmbLop::addItem);
        toolbar.add(cmbLop);
        toolbar.add(Box.createHorizontalStrut(10));
        txtTimLop = searchField();
        toolbar.add(txtTimLop);
        JButton bx = xoaBtn(); bx.addActionListener(e -> txtTimLop.setText(""));
        toolbar.add(bx);
        lblTongLop = countLabel();
        toolbar.add(lblTongLop);
        panel.add(toolbar, BorderLayout.NORTH);

        modelLop = buildModel();
        JPanel wrapper = titledPanel("Danh sách sinh viên theo lớp");
        wrapper.add(new JScrollPane(buildTable(modelLop)));
        panel.add(wrapper, BorderLayout.CENTER);

        cmbLop.addActionListener(e -> taiLop());
        txtTimLop.getDocument().addDocumentListener(dl(() -> taiLop()));
        return panel;
    }

    private void taiLop() {
        String lop    = cmbLop.getSelectedIndex() == 0 ? null : (String) cmbLop.getSelectedItem();
        String tuKhoa = txtTimLop.getText();
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                return diemDAO.layDanhSachSinhVienDiem(null, lop, null, tuKhoa);
            }
            @Override protected void done() {
                try { renderTable(modelLop, get(), lblTongLop); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ── TAB 3 ──────────────────────────────────────────────────
    private JPanel buildTabHocLuc() {
        JPanel panel = new JPanel(new BorderLayout(5, 8));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.add(boldLabel("Xếp loại:"));
        cmbHocLuc = new JComboBox<>();
        cmbHocLuc.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbHocLuc.setPreferredSize(new Dimension(160, 28));
        cmbHocLuc.addItem("Tất cả");
        for (String xl : XEP_LOAI) cmbHocLuc.addItem(xl);
        toolbar.add(cmbHocLuc);
        JLabel lblNhom = new JLabel("  (A+→A-: Giỏi/XS | B: Khá | C: TB | D: Yếu | F: Không đạt)");
        lblNhom.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNhom.setForeground(Color.GRAY);
        toolbar.add(lblNhom);
        toolbar.add(Box.createHorizontalStrut(10));
        txtTimHL = searchField();
        toolbar.add(txtTimHL);
        JButton bx = xoaBtn(); bx.addActionListener(e -> txtTimHL.setText(""));
        toolbar.add(bx);
        lblTongHL = countLabel();
        toolbar.add(lblTongHL);
        panel.add(toolbar, BorderLayout.NORTH);

        modelHL = buildModel();
        JPanel wrapper = titledPanel("Danh sách sinh viên theo học lực");
        wrapper.add(new JScrollPane(buildTable(modelHL)));
        panel.add(wrapper, BorderLayout.CENTER);

        cmbHocLuc.addActionListener(e -> taiHocLuc());
        txtTimHL.getDocument().addDocumentListener(dl(() -> taiHocLuc()));
        return panel;
    }

    private void taiHocLuc() {
        String hl     = cmbHocLuc.getSelectedIndex() == 0 ? null : (String) cmbHocLuc.getSelectedItem();
        String tuKhoa = txtTimHL.getText();
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                return diemDAO.layDanhSachSinhVienDiem(null, null, hl, tuKhoa);
            }
            @Override protected void done() {
                try { renderTable(modelHL, get(), lblTongHL); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ── HELPERS ────────────────────────────────────────────────
    private void renderTable(DefaultTableModel model, List<Object[]> ds, JLabel lblTong) {
        model.setRowCount(0);
        int stt = 1;
        for (Object[] row : ds)
            model.addRow(new Object[]{stt++, row[0], row[1], row[2], row[3], row[4], row[5]});
        lblTong.setText("Tổng: " + ds.size() + " sinh viên");
    }

    private DefaultTableModel buildModel() {
        return new DefaultTableModel(TABLE_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(30);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(30, 80, 160));
        t.getTableHeader().setForeground(Color.BLACK);
        t.setSelectionBackground(new Color(190, 215, 255));
        t.setGridColor(new Color(220, 225, 235));
        t.setShowGrid(true);
        t.getColumnModel().getColumn(0).setMaxWidth(50);
        t.getColumnModel().getColumn(0).setMinWidth(40);
        return t;
    }

    private JPanel titledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 230)),
            title, 0, 0, new Font("Arial", Font.BOLD, 13), new Color(30, 80, 160)));
        return p;
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton xoaBtn() {
        JButton btn = new JButton("X");
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(220, 53, 69)); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(32, 28));
        return btn;
    }

    private JTextField searchField() {
        JTextField tf = new JTextField(18);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setToolTipText("Tìm theo mã SV, họ tên, lớp, khoa...");
        return tf;
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        return l;
    }

    private JLabel countLabel() {
        JLabel l = new JLabel("Tổng: 0 sinh viên");
        l.setFont(new Font("Arial", Font.ITALIC, 12));
        l.setForeground(Color.GRAY);
        return l;
    }

    private javax.swing.event.DocumentListener dl(Runnable r) {
        return new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { debounce(r); }
            public void removeUpdate(DocumentEvent e)  { debounce(r); }
            public void changedUpdate(DocumentEvent e) { debounce(r); }
        };
    }

    private void debounce(Runnable r) {
        if (debounceTimer != null && debounceTimer.isRunning()) debounceTimer.stop();
        debounceTimer = new javax.swing.Timer(400, e -> r.run());
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }

    private void lamMoi() {
        removeAll();
        diemDAO = new DiemSoDAO();
        initUI();
        revalidate();
        repaint();
    }
}