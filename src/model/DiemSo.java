package model;

public class DiemSo {

    private int    maDiem;
    private String maSV;
    private String maMH;
    private int    lanThi;
    private double diemSo;
    private String xepLoai;

    public DiemSo() {}

    public DiemSo(String maSV, String maMH, double diemSo) {
        this.maSV    = maSV;
        this.maMH    = maMH;
        this.lanThi  = 1;
        this.diemSo  = diemSo;
        this.xepLoai = tinhXepLoai(diemSo);
    }

    public DiemSo(String maSV, String maMH, int lanThi, double diemSo) {
        this.maSV    = maSV;
        this.maMH    = maMH;
        this.lanThi  = lanThi;
        this.diemSo  = diemSo;
        this.xepLoai = tinhXepLoai(diemSo);
    }

    public DiemSo(int maDiem, String maSV, String maMH, int lanThi, double diemSo) {
        this.maDiem  = maDiem;
        this.maSV    = maSV;
        this.maMH    = maMH;
        this.lanThi  = lanThi;
        this.diemSo  = diemSo;
        this.xepLoai = tinhXepLoai(diemSo);
    }

    public int    getMaDiem()              { return maDiem; }
    public void   setMaDiem(int maDiem)    { this.maDiem = maDiem; }

    public String getMaSV()                { return maSV; }
    public void   setMaSV(String maSV)     { this.maSV = maSV; }

    public String getMaMH()                { return maMH; }
    public void   setMaMH(String maMH)     { this.maMH = maMH; }

    public int    getLanThi()              { return lanThi; }
    public void   setLanThi(int lanThi)    { this.lanThi = lanThi; }

    public double getDiemSo()              { return diemSo; }
    public void   setDiemSo(double diemSo) {
        this.diemSo  = diemSo;
        this.xepLoai = tinhXepLoai(diemSo);
    }

    public String getXepLoai()             { return xepLoai; }

    public static String tinhXepLoai(double diem) {
        if (diem >= 9.5) return "A+";
        if (diem >= 9.0) return "A";
        if (diem >= 8.5) return "A-";
        if (diem >= 8.0) return "B+";
        if (diem >= 7.5) return "B";
        if (diem >= 7.0) return "B-";
        if (diem >= 6.5) return "C+";
        if (diem >= 6.0) return "C";
        if (diem >= 5.5) return "D+";
        if (diem >= 5.0) return "D";
        return "F";
    }

    @Override
    public String toString() {
        return "SV: " + maSV + " | MH: " + maMH + " | Điểm: " + diemSo + " | " + xepLoai;
    }

    public String getInfo() {
        return "Mã điểm: " + maDiem  + "\n" +
               "Mã SV: "   + maSV    + "\n" +
               "Mã MH: "   + maMH    + "\n" +
               "Điểm: "    + diemSo  + "\n" +
               "Xếp loại: "+ xepLoai;
    }
}