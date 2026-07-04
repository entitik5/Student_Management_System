package dao;

import model.DiemSo;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiemSoDAO {

    private Connection conn;

    public DiemSoDAO() {
        this.conn = DBConnection.getInstance().getConnection();
        if (this.conn == null) {
            throw new RuntimeException("Không thể kết nối database!");
        }
    }

    // ==================== CREATE ====================

    public boolean them(DiemSo d) {
        String sql = "INSERT INTO tbl_diemso (maSV, maMH, lanThi, diemSo, xepLoai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getMaSV());
            ps.setString(2, d.getMaMH());
            ps.setInt   (3, d.getLanThi() > 0 ? d.getLanThi() : 1);
            ps.setDouble(4, d.getDiemSo());
            ps.setString(5, d.getXepLoai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm điểm: " + e.getMessage());
            return false;
        }
    }

    // ==================== READ ====================

    public List<DiemSo> layTatCa() {
        List<DiemSo> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM tbl_diemso ORDER BY maSV";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) danhSach.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy bảng điểm: " + e.getMessage());
        }
        return danhSach;
    }

    public List<DiemSo> layTheoSV(String maSV) {
        List<DiemSo> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM tbl_diemso WHERE maSV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) danhSach.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm theo SV: " + e.getMessage());
        }
        return danhSach;
    }

    public double layDiemTrungBinh(String maSV) {
        String sql = "SELECT AVG(diemSo) FROM tbl_diemso WHERE maSV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm TB: " + e.getMessage());
        }
        return 0.0;
    }

    // ==================== UPDATE ====================

        public boolean sua(DiemSo d) {
        String sql = "UPDATE tbl_diemso SET diemSo=?, xepLoai=? WHERE maDiem=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, d.getDiemSo());
            ps.setString(2, d.getXepLoai());
            ps.setInt   (3, d.getMaDiem());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi sửa điểm: " + e.getMessage());
            return false;
        }
    }

    // ==================== DELETE ====================

    public boolean xoa(int maDiem) {
        String sql = "DELETE FROM tbl_diemso WHERE maDiem = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDiem);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa điểm: " + e.getMessage());
            return false;
        }
    }

    // ==================== THỐNG KÊ ====================

    public List<Object[]> layDanhSachSinhVienDiem(
            String filterKhoa, String filterLop, String filterHocLuc, String tuKhoa) {

        List<Object[]> ds = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT v.maSV, v.hoTen, v.tenLop, k.tenKhoa, " +
            "       ROUND(v.diemTB, 2) AS diemTB, v.hocLuc " +
            "FROM v_diemtb_sinhvien v " +
            "LEFT JOIN tbl_sinhvien sv ON sv.maSV = v.maSV " +
            "LEFT JOIN tbl_khoa k ON k.maKhoa = sv.maKhoa " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (filterKhoa != null && !filterKhoa.isEmpty()) {
            sql.append("AND k.tenKhoa = ? ");
            params.add(filterKhoa);
        }
        if (filterLop != null && !filterLop.isEmpty()) {
            sql.append("AND v.tenLop = ? ");
            params.add(filterLop);
        }
        if (filterHocLuc != null && !filterHocLuc.isEmpty()) {
            sql.append("AND v.hocLuc = ? ");
            params.add(filterHocLuc);
        }
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append("AND (v.maSV LIKE ? OR v.hoTen LIKE ? OR v.tenLop LIKE ? OR k.tenKhoa LIKE ?) ");
            String p = "%" + tuKhoa.trim() + "%";
            params.add(p); params.add(p); params.add(p); params.add(p);
        }
        sql.append("ORDER BY k.tenKhoa, v.tenLop, v.diemTB DESC ");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ds.add(new Object[]{
                    rs.getString("maSV"),
                    rs.getString("hoTen"),
                    rs.getString("tenLop")  != null ? rs.getString("tenLop")  : "",
                    rs.getString("tenKhoa") != null ? rs.getString("tenKhoa") : "",
                    String.format("%.2f", rs.getDouble("diemTB")),
                    rs.getString("hocLuc")  != null ? rs.getString("hocLuc")  : ""
                });
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách sinh viên điểm: " + e.getMessage());
        }
        return ds;
    }

    public List<String> layDanhSachKhoa() {
        List<String> ds = new ArrayList<>();
        String sql =
            "SELECT DISTINCT k.tenKhoa " +
            "FROM tbl_sinhvien sv " +
            "JOIN tbl_khoa k ON k.maKhoa = sv.maKhoa " +
            "JOIN tbl_diemso d ON d.maSV = sv.maSV " +
            "ORDER BY tenKhoa";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) ds.add(rs.getString("tenKhoa"));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách khoa: " + e.getMessage());
        }
        return ds;
    }

    public List<String> layDanhSachLop() {
        List<String> ds = new ArrayList<>();
        String sql =
            "SELECT DISTINCT l.tenLop " +
            "FROM tbl_sinhvien sv " +
            "JOIN tbl_lophoc l ON l.maLop = sv.maLop " +
            "JOIN tbl_diemso d ON d.maSV = sv.maSV " +
            "ORDER BY tenLop";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) ds.add(rs.getString("tenLop"));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách lớp: " + e.getMessage());
        }
        return ds;
    }

    // ==================== HELPER ====================

    private DiemSo mapRow(ResultSet rs) throws SQLException {
        DiemSo d = new DiemSo();
        d.setMaDiem (rs.getInt   ("maDiem"));
        d.setMaSV   (rs.getString("maSV"));
        d.setMaMH   (rs.getString("maMH"));
        d.setLanThi (rs.getInt   ("lanThi"));
        d.setDiemSo (rs.getDouble("diemSo"));
        return d;
    }
}