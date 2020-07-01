package vn.huy.cocaroonline.model;

public class CoCaro {
    private boolean current;
    private int[][] arrBanCo;

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int[][] getArrBanCo() {
        return arrBanCo;
    }

    public void setArrBanCo(int[][] arrBanCo) {
        this.arrBanCo = arrBanCo;
    }
}
