package com.giousa.handlerthreadtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tv_textview)
    TextView mTvTextview;
    @InjectView(R.id.btn_1)
    Button mBtn1;
    @InjectView(R.id.btn_2)
    Button mBtn2;
    @InjectView(R.id.btn_3)
    Button mBtn3;

    private final String TAG = MainActivity.class.getSimpleName();
    private HandlerThread myHandlerThread;
    private Handler mCheckMsgHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initBackThread();
    }

    private void initBackThread() {
        //创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread( "handler-thread") ;
        //开启一个线程
        myHandlerThread.start();
        //在这个线程中创建一个handler对象
        mCheckMsgHandler = new Handler( myHandlerThread.getLooper() ){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                Log.d( "handler " , "消息： " + msg.what + "  线程： " + Thread.currentThread().getName()  ) ;
                switch (msg.what){
                    case 1:
                        Log.d(TAG,"我被调用了 = "+1);
                        break;

                    case 2:
                        Log.d(TAG,"我被调用了 = "+2);
                        break;

                    case 3:
                        Bundle data = msg.getData();
                        int here = data.getInt("here");
                        Log.d(TAG,"here = "+here);
                        break;
                }
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        myHandlerThread.quit() ;
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                //主线程发送消息
                Message message = new Message();
                message.what = 1;
                mCheckMsgHandler.sendMessage(message);
                break;
            case R.id.btn_2:
                //子线程发送消息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 2;
                        mCheckMsgHandler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.btn_3:
                Message msg = new Message();
                Bundle data = new Bundle();
                //放置数据
                data.putInt("here", 100);
                msg.what = 3;
                msg.setData(data);
                mCheckMsgHandler.sendMessage(msg);
                break;
        }
    }
}
