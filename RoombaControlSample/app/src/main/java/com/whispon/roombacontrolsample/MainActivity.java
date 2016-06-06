package com.whispon.roombacontrolsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.physicaloid.lib.Physicaloid;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // ルンバとの通信速度(bps)
    private static final int SERIAL_BPS = 115200;

    //ボタンの
    private Button forwardButton;
    private Button rightButton;
    private Button leftButon;
    private Button backwardButton;
    private Button stopButton;
    private Button cleaningButton;
    private Button dockingButton;
    private Button oneRightRotationButton;
    private Button oneLeftRotationButton;


    private Physicaloid mPhysicaloid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // スリープ抑止
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        //関連づけ
        cleaningButton = (Button) findViewById(R.id.cleaningButton);
        forwardButton = (Button) findViewById(R.id.forwardButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        leftButon = (Button) findViewById(R.id.leftButton);
        backwardButton = (Button) findViewById(R.id.backwardButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        dockingButton = (Button) findViewById(R.id.dockingButton);
        oneRightRotationButton = (Button) findViewById(R.id.oneRightRotation);
        oneLeftRotationButton = (Button) findViewById(R.id.oneLeftRotation);

        forwardButton.setOnClickListener((view) -> {
            roombaForward();
        });

        rightButton.setOnClickListener((view) -> {
            roombaTurnRight();
        });

        leftButon.setOnClickListener((view) -> {
            roombaTurnLeft();
        });

        backwardButton.setOnClickListener((view) -> {
            roombaBackward();
        });

        stopButton.setOnClickListener((view) -> {
            roombaStop();
        });

        cleaningButton.setOnClickListener((view)-> {
            roombaCleaning();
        });

        dockingButton.setOnClickListener((view) -> {
            roombaDocking();
        });

        oneRightRotationButton.setOnClickListener((view) -> {
            roombaOneRightRotation();
        });

        oneLeftRotationButton.setOnClickListener((view) -> {
            roombaOneLeftRotation();
        });

        // Physicaloidのインスタンスを生成
        mPhysicaloid = new Physicaloid(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        roombaStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    クリーニング
     */
    void roombaCleaning() {
        byte[] commands = new byte[3];
        commands[0] = (byte) 128;
        commands[1] = (byte) 131;
        commands[2] = (byte) 135;
        sendCommand(commands);
    }


    /*
    ドックに戻る
     */
    void roombaDocking() {
        byte[] commands = new byte[3];
        commands[0] = (byte) 128;
        commands[1] = (byte) 131;
        commands[2] = (byte) 143;
        sendCommand(commands);
    }

    /**
     * ルンバ停止
     */
    void roombaStop() {
        sendDriveDirect(0, 0);
    }

    /**
     * ルンバ前進
     */
    void roombaForward() {
        sendDriveDirect(300, 300);
    }

    /**
     * ルンバ後退
     */
    void roombaBackward() {
        sendDriveDirect(-300, -300);
    }

    /**
     * ルンバ左回転
     *
     */
    void roombaTurnLeft() {
        sendDriveDirect(-300, 300);
    }

    /**
     * ルンバ右回転
     *
     */
    void roombaTurnRight() {
        sendDriveDirect(300, -300);
    }

    /**
     * ルンパを右１回転させる
     *
     */
    void roombaOneRightRotation() {
        sendDriveDirect(300, -300);
        try {
            Thread.sleep(2350);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        //停止
        sendDriveDirect(0, 0);
    }

    /**
     * ルンパを左１回転させる
     *
     */
    void roombaOneLeftRotation() {
        sendDriveDirect(-300, 300);
        try {
            Thread.sleep(2350);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        //停止
        sendDriveDirect(0, 0);
    }


    /**
     * 車輪制御コマンドを送信する
     *
     * @param l 左車輪パラメータ、-500〜500 (mm/s)
     * @param r 右車輪パラメータ、-500〜500 (mm/s)
     */
    private void sendDriveDirect(int l, int r) {
        byte[] commands = new byte[7];
        commands[0] = (byte) 128;        // Start

        commands[1] = (byte) 131;        // Safe

        commands[2] = (byte) 145;        // Drive Direct
        commands[3] = (byte) (r >> 8);   // Right velocity high byte
        commands[4] = (byte) r;          // Right velocity low byte
        commands[5] = (byte) (l >> 8);   // Left velocity high byte
        commands[6] = (byte) l;          // Left velocity low byte

        // バイト列をシリアルに送信
        sendCommand(commands);
    }

    /**
     * シリアル通信でルンバにコマンドを送信する
     *
     * @param commands
     */
    private void sendCommand(byte[] commands) {
        if (mPhysicaloid.open()) {
            mPhysicaloid.setBaudrate(SERIAL_BPS);
            mPhysicaloid.write(commands, commands.length);
            mPhysicaloid.close();
        }
    }
    /*
    @Override
    protected void onMessage(Context context, Intent intent) {


    }
    */
}
