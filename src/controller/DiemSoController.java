package controller;

import dao.DiemSoDAO;
import model.DiemSo;
import view.DiemSoPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DiemSoController {

    private DiemSoPanel       view;
    private DiemSoDAO         diemDAO;
    private DefaultTableModel tableModel;

    public DiemSoController(DiemSoPanel view, DefaultTableModel tableModel) {
        this.view       = view;
        this.diemDAO    = new DiemSoDAO();
        this.tableModel = tableModel;
    }

    public void taiDuLieu() {
        tableModel.setRowCount(0);
        diemDAO.layTatCa().forEach(d -> tableModel.addRow(new Object[]{
            d.getMaDiem(), d.getMaSV(), d.getMaMH(),
            d.getLanThi(), d.getDiemSo(), d.getXepLoai()
        }));
    }

    public boolean nhapDiem(String maSV, String maMH, int lanThi, String diemStr) {
        if (maSV == null || maMH == null || diemStr == null ||
            maSV.trim().isEmpty() || maMH.trim().isEmpty() || diemStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (lanThi <= 0) {
            JOptionPane.showMessageDialog(view, "Lần thi phải lớn hơn 0!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        double diem;
        try {
            diem = Double.parseDouble(diemStr.trim());
            if (diem < 0 || diem > 10) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Điểm không hợp lệ! Nhập từ 0.0 đến 10.0",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        DiemSo d = new DiemSo(maSV.trim(), maMH.trim(), lanThi, diem);
        if (diemDAO.them(d)) {
            JOptionPane.showMessageDialog(view,
                "Nhập điểm thành công!\nXếp loại: " + d.getXepLoai(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
            return true;
        } else {
            JOptionPane.showMessageDialog(view,
                "Nhập điểm thất bại!\n(Sinh viên đã có điểm lần thi này cho môn đã chọn)",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean suaDiem(int maDiem, String maMH, String diemStr) {
        if (diemStr == null || diemStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập điểm!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        double diem;
        try {
            diem = Double.parseDouble(diemStr.trim());
            if (diem < 0 || diem > 10) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Điểm không hợp lệ! Nhập từ 0.0 đến 10.0",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        DiemSo d = new DiemSo();
        d.setMaDiem(maDiem);
        d.setMaMH(maMH);
        d.setDiemSo(diem);
        if (diemDAO.sua(d)) {
            JOptionPane.showMessageDialog(view,
                "Cập nhật điểm thành công!\nXếp loại mới: " + d.getXepLoai(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
            return true;
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean xoaDiem(int maDiem) {
        int confirm = JOptionPane.showConfirmDialog(view,
            "Xóa điểm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && diemDAO.xoa(maDiem)) {
            JOptionPane.showMessageDialog(view, "Xóa thành công!", "OK",
                JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
            return true;
        }
        return false;
    }
}