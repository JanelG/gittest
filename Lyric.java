package com.example.playaudiotest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Lyric extends AppCompatActivity  {

    LyricView view;
    EditText editText;
    Button btn;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1){

                if(lrc_index == list.size()){
                    handler.removeMessages(1);
                }
                lrc_index++;

                System.out.println("******"+lrc_index+"*******");
                view.scrollToIndex(lrc_index);
                handler.sendEmptyMessageDelayed(1,4000);
            }
            return false;
        }
    });
    private ArrayList<LrcMusic> lrcs;
    private ArrayList<String> list;
    private ArrayList<Long> list1;
    private int lrc_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric);
        initViews();
        initEvents();
    }
    private void initViews(){
        view = (LyricView) findViewById(R.id.view);
        editText = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);
    }
    private void initEvents(){
       // InputStream is = getResources().openRawResource(R.raw.filename);读取res/raw目录下的文件名
        // lrcs = Utils.redLrc(is);
        // BufferedReader br = new BufferedReader(new InputStreamReader(is));


        list = new ArrayList<String>();
        list1 = new ArrayList<>();
        for(int i = 0; i< lrcs.size(); i++){
            list.add(lrcs.get(i).getLrc());
            System.out.println(lrcs.get(i).getLrc()+"=====");
            list1.add(0l);//lrcs.get(i).getTime()
        }
        view.setLyricText(list, list1);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.scrollToIndex(0);
            }
        },1000);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                int index = 0;
                index = Integer.parseInt(text);
                view.scrollToIndex(index);
            }
        });
        view.setOnLyricScrollChangeListener(new LyricView.OnLyricScrollChangeListener() {
            @Override
            public void onLyricScrollChange(final int index, int oldindex) {
                editText.setText(""+index);
                lrc_index = index;
                System.out.println("===="+index+"======");
                //滚动handle不能放在这，因为，这是滚动监听事件，滚动到下一次，handle又会发送一次消息，出现意想不到的效果
            }
        });
        handler.sendEmptyMessageDelayed(1,4000);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacksAndMessages(null);

                        System.out.println("取消了");
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("开始了");
                        handler.sendEmptyMessageDelayed(1,2000);
                        break;
                    case MotionEvent.ACTION_CANCEL://时间别消耗了
                        break;
                }
                return false;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
 class Utils {
    public static ArrayList<LrcMusic> redLrc(InputStream in) {
        ArrayList<LrcMusic> alist = new ArrayList<LrcMusic>();
        //File f = new File(path.replace(".mp3", ".lrc"));
        try {
            //FileInputStream fs = new FileInputStream(f);
            InputStreamReader input = new InputStreamReader(in, "utf-8");
            BufferedReader br = new BufferedReader(input);
            String s = "";

            while ((s = br.readLine()) != null) {
                if (!TextUtils.isEmpty(s)) {
                    String lyLrc = s.replace("[", "");
                    String[] data_ly = lyLrc.split("]");
                    if (data_ly.length > 1) {
                        String time = data_ly[0];
                        String lrc = data_ly[1];
                        LrcMusic lrcMusic = new LrcMusic(lrcData(time), lrc);
                        alist.add(lrcMusic);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alist;
    }
    public static int lrcData(String time) {
        time = time.replace(":", "#");
        time = time.replace(".", "#");

        String[] mTime = time.split("#");

        //[03:31.42]
        int mtime = Integer.parseInt(mTime[0]);
        int stime = Integer.parseInt(mTime[1]);
        int mitime = Integer.parseInt(mTime[2]);

        int ctime = (mtime*60+stime)*1000+mitime*10;

        return ctime;
    }
}
 class LrcMusic {
    private int time;
    private String lrc;

    public LrcMusic() {
    }

    public LrcMusic(int time, String lrc) {
        this.time = time;
        this.lrc = lrc;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }
}
