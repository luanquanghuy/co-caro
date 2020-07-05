package vn.huy.cocaroonline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.huy.cocaroonline.R;
import vn.huy.cocaroonline.click.ClickO;
import vn.huy.cocaroonline.service.CaroRetrofit;
import vn.huy.cocaroonline.service.CaroStatus;
import vn.huy.cocaroonline.viewmodel.CaroViewModel;

public class PlayOnlineActivity extends AppCompatActivity {

    private static final int COLUMN = 30;
    private static final int ROW = 30;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    private static final int X_COLOR = Color.RED;
    private static final int O_COLOR = Color.BLUE;
    private TextView txtPlayer, txtTimer;
    private ImageButton btnReset;
    private int[][] arrayBanCo;
    private TextView[][] arrayTVBanCo;
    CaroViewModel caroViewModel;
    Socket socket;

    Handler handler;
    ClickO clickO;
    CountDownTimer countDownTimer;
    int TIME_COUNT_DOWN = 30000;
    private boolean isStart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_online);

        handler = new Handler(Looper.getMainLooper());

        txtPlayer = findViewById(R.id.txtPlayer);
        txtTimer = findViewById(R.id.txtTimer);
        btnReset = findViewById(R.id.btnRestart);
        btnReset.setVisibility(View.INVISIBLE);
        caroViewModel = new ViewModelProvider(PlayOnlineActivity.this).get(CaroViewModel.class);

        CaroRetrofit.getRoomName().enqueue(new Callback<CaroStatus>() {
            @Override
            public void onResponse(Call<CaroStatus> call, Response<CaroStatus> response) {
                CaroStatus caroStatus = response.body();
                Log.d("LQH_ROOM", caroStatus.getRoomName());
                Log.d("LQH_ROOM", caroStatus.isStatus() ? "Đánh trước" : "Đánh sau");
                clickO = (code, x, y) -> {
                    JSONObject objSend = new JSONObject();

                    try {
                        objSend.put("x", x);
                        objSend.put("y", y);
                        objSend.put("code", code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (code == 1 || code == 2) {
                        isStart = false;
                    }
                    socket.emit(caroStatus.getRoomName() + "data", objSend);
                };


                try {
                    socket = IO.socket("https://cocaro.glitch.me");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                CaroStatus finalCaroStatus = caroStatus;
                socket.on(Socket.EVENT_CONNECT, args -> {
                    socket.emit("join", finalCaroStatus.getRoomName());
                    handler.post(()->{
                            txtPlayer.setText("Đang kết nối...");
                    });
                }).
                        on(finalCaroStatus.getRoomName(), args -> {
                            socket.emit(finalCaroStatus.getRoomName() + "connect", "connecting server");
                            socket.on(finalCaroStatus.getRoomName() + "start", args1 -> {
                                JSONObject obj = (JSONObject) args1[0];
                                try {
                                    if (obj.getInt("status") == 0) {
                                        Log.d("LQH_connect", "success");
                                        isStart = true;

                                        handler.post(() -> {
                                            caroViewModel.getCurrent(caroStatus.isStatus()).observe(PlayOnlineActivity.this, coCaro -> {
                                                if (coCaro) {
                                                    txtPlayer.setText("Đến lượt đi của bạn");
                                                    vibrate(500);
                                                    if (!caroViewModel.getIsWin().getValue()) {
                                                        countDownTimer = new CountDownTimer(TIME_COUNT_DOWN, 1000) {
                                                            @Override
                                                            public void onTick(long millisUntilFinished) {
                                                                txtTimer.setText("" + (millisUntilFinished / 1000));
                                                            }

                                                            @Override
                                                            public void onFinish() {
                                                                if (!caroViewModel.getIsWin().getValue()) {
                                                                    caroViewModel.setWin(true);
                                                                    Log.d("LQH", "onfinishTimer");
                                                                    txtTimer.setText("");
                                                                    Toast.makeText(PlayOnlineActivity.this, "Bạn đã thua", Toast.LENGTH_SHORT).show();
                                                                    txtPlayer.setText("Bạn đã thua");
                                                                    btnReset.setVisibility(View.VISIBLE);
                                                                    vibrate(1000);
                                                                    clickO.clickO(1, 0, 0);
                                                                }
                                                            }
                                                        }.start();
                                                    }
                                                } else if (!caroViewModel.getIsWin().getValue()) {
                                                    txtPlayer.setText("Chờ đối phương đánh");
                                                }
                                            });
                                        });

                                        socket.on(finalCaroStatus.getRoomName() + "dataRe", args2 -> {
                                            if (!isStart) {
                                                return;
                                            }
                                            JSONObject objRecived = (JSONObject) args2[0];
                                            int x = 0, y = 0, code = 0;
                                            try {
                                                x = objRecived.getInt("x");
                                                y = objRecived.getInt("y");
                                                code = objRecived.getInt("code");
                                                Log.d("LQH_DATA", "X=" + x + ", Y=" + y);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (code == 0) {
                                                arrayBanCo[x][y] = 2;
                                                int finalX = x;
                                                int finalY = y;
                                                handler.post(() -> {
                                                    arrayTVBanCo[finalX][finalY].setText("X");
                                                    caroViewModel.addBuocDi(finalX, finalY);
                                                });
                                            } else if (code == 1) {
                                                handler.post(() -> {
                                                    Toast.makeText(PlayOnlineActivity.this, "Bạn đã thắng", Toast.LENGTH_SHORT).show();
                                                    txtPlayer.setText("Bạn đã thắng");
                                                    btnReset.setVisibility(View.VISIBLE);
                                                    caroViewModel.setWin(true);
                                                });
                                            } else if (code == 2) {
                                                arrayBanCo[x][y] = 2;
                                                int finalX = x;
                                                int finalY = y;
                                                handler.post(() -> {
                                                    arrayTVBanCo[finalX][finalY].setText("X");
                                                    caroViewModel.setWin(true);
                                                    caroViewModel.addBuocDi(finalX, finalY);
                                                    Toast.makeText(PlayOnlineActivity.this, "Bạn đã thua", Toast.LENGTH_SHORT).show();
                                                    txtPlayer.setText("Bạn đã thua");
                                                    btnReset.setVisibility(View.VISIBLE);
                                                    vibrate(1000);
                                                });
                                            }
                                        });

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }).on(Socket.EVENT_DISCONNECT, args -> {
                    handler.post(() -> {
                        btnReset.setVisibility(View.VISIBLE);
                    });
                    Log.d("LQH", "disconnected socket");
                });
                socket.connect();
            }

            @Override
            public void onFailure(Call<CaroStatus> call, Throwable t) {

            }
        });

        arrayTVBanCo = new TextView[ROW][COLUMN];


        TableLayout tblBanCo = findViewById(R.id.tblBanCo);
        arrayBanCo = new int[ROW][COLUMN];
        for (int i = 0; i < ROW; i++) {
            TableRow row = new TableRow(PlayOnlineActivity.this);
            for (int j = 0; j < COLUMN; j++) {
                TextView textView = new TextView(PlayOnlineActivity.this);
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
                    if (arrayBanCo[finalI][finalJ] != 0 || caroViewModel.getIsWin().getValue() || !isStart) {
                        return;
                    }

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    txtTimer.setText("");

                    int code = 0;
                    if (caroViewModel.getCurrent(true).getValue()) {
                        ((TextView) v).setText("O");
                        ((TextView) v).setTextColor(O_COLOR);
                        arrayBanCo[finalI][finalJ] = 1;

                        if (checkWin(finalI, finalJ)) {
                            Toast.makeText(PlayOnlineActivity.this, "Bạn đã thắng", Toast.LENGTH_LONG).show();
                            txtPlayer.setText("Bạn đã thắng");
                            btnReset.setVisibility(View.VISIBLE);
                            vibrate(1000);
                            code = 2;
                            caroViewModel.setWin(true);
                        }

                        clickO.clickO(code, finalI, finalJ);
                        caroViewModel.addBuocDi(finalI, finalJ);
                    }
                });
                arrayTVBanCo[i][j] = textView;
                row.addView(textView);
            }
            tblBanCo.addView(row);
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
        while (x1 + 1 < COLUMN && y1 - 1 >= 0 && arrayBanCo[x1 + 1][y1 - 1] == value) {
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

    private void vibrate(long milis) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(milis);
        }
    }

    public void goHome(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d("LQH", "onDestroy");
        if(!caroViewModel.getIsWin().getValue()) {
            clickO.clickO(1, 0, 0);
        }
        super.onDestroy();
    }

    public void restart(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}