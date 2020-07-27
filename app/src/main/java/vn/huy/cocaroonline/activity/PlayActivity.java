package vn.huy.cocaroonline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vn.huy.cocaroonline.R;
import vn.huy.cocaroonline.model.Postion;
import vn.huy.cocaroonline.viewmodel.CaroViewModel;

public class PlayActivity extends AppCompatActivity {

    private static final int COLUMN = 30;
    private static final int ROW = 30;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    private static final int X_COLOR = Color.RED;
    private static final int O_COLOR = Color.BLUE;
    private TextView txtPlayer;
    private int[][] arrayBanCo;
    private TextView[][] arrayTVBanCo;
    CaroViewModel caroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        txtPlayer = findViewById(R.id.txtPlayer);

        caroViewModel = new ViewModelProvider(this).get(CaroViewModel.class);
        if(caroViewModel.getCurrent(true).getValue()){
            txtPlayer.setText("Đến lượt đi của O");
        }else {
            txtPlayer.setText("Đến lượt đi của X");
        }
        caroViewModel.getCurrent(true).observe(this, coCaro -> {
            if(!caroViewModel.getIsWin().getValue()) {
                if (coCaro) {
                    txtPlayer.setText("Đến lượt đi của O");
                } else {
                    txtPlayer.setText("Đến lượt đi của X");
                }
            }
        });

        arrayTVBanCo = new TextView[ROW][COLUMN];


        TableLayout tblBanCo = findViewById(R.id.tblBanCo);
        arrayBanCo = new int[ROW][COLUMN];
        for (int i = 0; i < ROW; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < COLUMN; j++) {
                TextView textView = new TextView(this);
                textView.setWidth(WIDTH);
                textView.setHeight(HEIGHT);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(25);
                textView.setTextColor(X_COLOR);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setBackgroundResource(R.drawable.row_border_ban_co);
                final int finalI = i;
                final int finalJ = j;
                textView.setOnClickListener(v -> {
                    if(arrayBanCo[finalI][finalJ] != 0 || caroViewModel.getIsWin().getValue()){
                        return;
                    }
                    if(caroViewModel.getCurrent(true).getValue()){
                        ((TextView) v).setText("O");
                        ((TextView) v).setTextColor(O_COLOR);
                        arrayBanCo[finalI][finalJ] = 1;
                    }else {
                        ((TextView) v).setText("X");
                        arrayBanCo[finalI][finalJ] = 2;
                    }
                    if(checkWin(finalI, finalJ)){
                        Toast.makeText(PlayActivity.this, (caroViewModel.getCurrent(true).getValue() ? "O": "X") + " đã win", Toast.LENGTH_LONG).show();
                        txtPlayer.setText((caroViewModel.getCurrent(true).getValue() ? "O": "X") + " đã win");
                        caroViewModel.setWin(true);
                    }
                    caroViewModel.addBuocDi(finalI, finalJ);
                });
                arrayTVBanCo[i][j] = textView;
                row.addView(textView);
            }
//            row.setBackgroundResource(R.drawable.row_border_ban_co);
            tblBanCo.addView(row);
        }
        for(int i = 0; i < caroViewModel.getBuocDi().getValue().size(); i++){
            TextView textView = arrayTVBanCo[caroViewModel.getBuocDi().getValue().get(i).getX()][caroViewModel.getBuocDi().getValue().get(i).getY()];
            if(caroViewModel.getBuocDi().getValue().get(i).isPlayer()){
                textView.setText("O");
                textView.setTextColor(O_COLOR);
                arrayBanCo[caroViewModel.getBuocDi().getValue().get(i).getX()][caroViewModel.getBuocDi().getValue().get(i).getY()] = 1;
            }else {
                textView.setText("X");
                arrayBanCo[caroViewModel.getBuocDi().getValue().get(i).getX()][caroViewModel.getBuocDi().getValue().get(i).getY()] = 2;
            }
        }
    }

    private boolean checkWin(int x, int y) {
        int value = arrayBanCo[x][y];
        int countRow = 0;
        int x1 = x;
        int y1 = y;
        while (x1 - 1 >= 0 && arrayBanCo[x1 - 1][y1] == value) {
            countRow++;
            x1--;
        }
        x1 = x;
        while (x1 + 1 < COLUMN && arrayBanCo[x1 + 1][y1] == value) {
            countRow++;
            x1++;
        }
        if (countRow >= 4) {
            Log.d("LQH_WIN", "Win hàng ngang");
            return true;
        }
        int countCol = 0;
        x1 = x;
        y1 = y;
        while (y1 - 1 >= 0 && arrayBanCo[x1][y1 - 1] == value) {
            countCol++;
            y1--;
        }
        y1 = y;
        while (y1 + 1 < ROW && arrayBanCo[x1][y1 + 1] == value) {
            countCol++;
            y1++;
        }

        if (countCol >= 4) {
            Log.d("LQH_WIN", "Win hàng dọc");

            return true;
        }

        int countCross = 0;
        x1 = x;
        y1 = y;
        while (x1 - 1 >= 0 && y1 - 1 >= 0 && arrayBanCo[x1 - 1][y1 - 1] == value) {
            countCross++;
            x1--;
            y1--;
        }
        x1 = x;
        y1 = y;
        while (x1 + 1 < COLUMN && y1 + 1 < ROW && arrayBanCo[x1 + 1][y1 + 1] == value) {
            countCross++;
            x1++;
            y1++;
        }

        if (countCross >= 4) {
            Log.d("LQH_WIN", "Win hàng chéo xuống");

            return true;
        }

        countCross = 0;
        x1 = x;
        y1 = y;
        while (x1 - 1 >= 0 && y1 + 1 < ROW && arrayBanCo[x1 - 1][y1 + 1] == value) {
            countCross++;
            x1--;
            y1++;
        }
        x1 = x;
        y1 = y;
        while (x1 + 1 < COLUMN && y1 - 1 >=0 && arrayBanCo[x1 + 1][y1 - 1] == value) {
            countCross++;
            x1++;
            y1--;
        }

        if (countCross >= 4) {
            Log.d("LQH_WIN", "Win hàng chéo lên");

            return true;
        }
        return false;
    }

    public void quayLaiBuocTruoc(View view) {
        List<Postion> postionList = caroViewModel.getBuocDi().getValue();
        if(postionList.size() > 0) {
            Postion remove = postionList.remove(postionList.size() - 1);
            arrayTVBanCo[remove.getX()][remove.getY()].setText("");
            arrayBanCo[remove.getX()][remove.getY()] = 0;
            caroViewModel.switchCurrent();
            caroViewModel.setWin(false);
        }
    }

    public void restart(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void goHome(View view) {
        finish();
    }
}