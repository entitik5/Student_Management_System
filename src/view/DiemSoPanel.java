package view;

import controller.DiemSoController;
import dao.MonHocDAO;
import model.MonHoc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class DiemSoPanel extends JPanel {

    private JTable              table;
    private DefaultTableModel   tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField          txtMaSV;
    private JTextField          txtLanThi;
    private JComboBox<MonHoc>   cmbMonHoc;
    private JTextField          txtDiem;
    private JTextField          txtTimKiem;
    private JLabel              lblKetQua;
    private DiemSoController    controller;

    public DiemSoPanel() {
        String[] columns = {"Mã điểm", "Mã SV", "Mã MH", "Lần thi", "Điểm số", "Xếp loại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        controller = new DiemSoController(this, tableModel);
        initUI();
        controller.taiDuLieu();
        capNhatSoLuong();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐIỂM SỐ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 80, 160));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(new Color(245, 247, 250));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setBackground(new Color(245, 247, 250));

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 13));

        txtTimKiem = new JTextField(22);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnXoaTK = new JButton("X");
        btnXoaTK.setFont(new Font("Arial", Font.BOLD, 12));
        btnXoaTK.setBackground(new Color(220, 53, 69));
        btnXoaTK.setForeground(Color.WHITE);
        btnXoaTK.setFocusPainted(false);
        btnXoaTK.setBorderPainted(false);
        btnXoaTK.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoaTK.addActionListener(e -> txtTimKiem.setText(""));

        lblKetQua = new JLabel("Tổng: 0 bản ghi");
        lblKetQua.setFont(new Font("Arial", Font.ITALIC, 12));
        lblKetQua.setForeground(Color.GRAY);

        searchBar.add(lblSearch);
        searchBar.add(txtTimKiem);
        searchBar.add(btnXoaTK);
        searchBar.add(Box.createHorizontalStrut(10));
        searchBar.add(lblKetQua);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(searchBar, BorderLayout.CENTER);
        add(topBar, BorderLayout.NORTH);

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(30, 80, 160));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(190, 215, 255));
        table.setGridColor(new Color(220, 225, 235));
        table.setShowGrid(true);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e)  { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int mr = table.convertRowIndexToModel(row);
                    txtMaSV.setText(tableModel.getValueAt(mr, 1).toString());
                    txtLanThi.setText(tableModel.getValueAt(mr, 3).toString());
                    String maMH = tableModel.getValueAt(mr, 2).toString();
                    for (int i = 0; i < cmbMonHoc.getItemCount(); i++) {
                        if (cmbMonHoc.getItemAt(i).getMaMH().equals(maMH)) {
                            cmbMonHoc.setSelectedIndex(i); break;
                        }
                    }
                    txtDiem.setText(tableModel.getValueAt(mr, 4).toString());
                }
            }
        });

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 230)),
            "Nhập / Sửa điểm", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(30, 80, 160)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtMaSV = new JTextField(14);
        txtMaSV.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMaSV.setToolTipText("Nhập mã sinh viên (VD: SV001)");

        txtLanThi = new JTextField("1", 14);
        txtLanThi.setFont(new Font("Arial", Font.PLAIN, 13));
        txtLanThi.setToolTipText("Lần thi thứ mấy (mặc định: 1)");

        cmbMonHoc = new JComboBox<>();
        cmbMonHoc.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbMonHoc.setPreferredSize(new Dimension(180, 28));
        new MonHocDAO().layTatCa().forEach(mh -> cmbMonHoc.addItem(mh));

        txtDiem = new JTextField(14);
        txtDiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDiem.setToolTipText("Nhập điểm từ 0.0 đến 10.0");

        String[] labels = {"Mã SV *", "Lần thi *", "Môn học *", "Điểm số *"};
        Component[] inputs = {txtMaSV, txtLanThi, cmbMonHoc, txtDiem};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(inputs[i], gbc);
        }

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 6, 6));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 8, 8, 8));
        JButton btnThem = taoNut("Thêm", new Color(40, 160, 80));
        JButton btnSua  = taoNut("Sửa",  new Color(30, 120, 200));
        JButton btnXoa  = taoNut("Xóa",  new Color(200, 50, 50));
        btnPanel.add(btnThem);
        btnPanel.add(btnSua);
        btnPanel.add(btnXoa);

        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2; gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(btnPanel, gbc);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        btnThem.addActionListener(e -> {
            MonHoc mh = (MonHoc) cmbMonHoc.getSelectedItem();
            int lanThi = 1;
            try { lanThi = Integer.parseInt(txtLanThi.getText().trim()); }
            catch (Exception ex) { lanThi = 1; }
            if (controller.nhapDiem(
                    txtMaSV.getText().trim(),
                    mh != null ? mh.getMaMH() : null,
                    lanThi,
                    txtDiem.getText().trim())) {
                xoaForm();
                capNhatSoLuong();
            }
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int mr = table.convertRowIndexToModel(row);
            int maDiem = (int) tableModel.getValueAt(mr, 0);
            MonHoc mh  = (MonHoc) cmbMonHoc.getSelectedItem();
            if (controller.suaDiem(maDiem,
                    mh != null ? mh.getMaMH() : null,
                    txtDiem.getText().trim())) {
                xoaForm();
                capNhatSoLuong();
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn điểm cần xóa!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int mr = table.convertRowIndexToModel(row);
            controller.xoaDiem((int) tableModel.getValueAt(mr, 0));
            xoaForm();
            capNhatSoLuong();
        });
    }

    private void xoaForm() {
        txtMaSV.setText("");
        txtLanThi.setText("1");
        txtDiem.setText("");
        table.clearSelection();
    }

    private void applyFilter() {
        String text = txtTimKiem.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
        capNhatSoLuong();
    }

    public void capNhatSoLuong() {
        int hienThi  = table.getRowCount();
        int tongCong = tableModel.getRowCount();
        lblKetQua.setText(hienThi == tongCong
            ? "Tổng: " + tongCong + " bản ghi"
            : "Hiển thị: " + hienThi + " / " + tongCong + " bản ghi");
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}