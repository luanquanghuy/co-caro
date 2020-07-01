package vn.huy.cocaroonline.model;

public class Postion {
    private boolean player;
    private int x, y;

    public Postion(boolean player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public boolean isPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
