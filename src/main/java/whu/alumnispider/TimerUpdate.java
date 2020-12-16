package whu.alumnispider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.TimeUnit;

import whu.alumnispider.BaiduSearchProcessor;
import whu.alumnispider.utils.Utility;


class MyTask extends TimerTask{

    @Override
    public void run() {
        String dateStr=Utility.getTimeStr();
        System.out.format("时间：%s，此刻开始进行爬虫和数据更新，请勿关闭电脑！",dateStr);

        updateProcess();

        dateStr = Utility.getTimeStr();
        System.out.format("时间：%s，已完成一次数据更新。",dateStr);
    }

    private void updateProcess(){
        BaiduSearchProcessor.searchAllAlumniFromWeb();
        BaiduSearchProcessor.updateAllGraduate();
        BaiduSearchProcessor.downloadAllPictures();
    }


}

public class TimerUpdate{
    public static void main(String[] args) {
        //创建定时器对象
        Timer t=new Timer();
        long tenMinute = 10 * 60 * 1000;
        //在3秒后执行MyTask类中的run方法
        t.scheduleAtFixedRate(new MyTask(), 0,tenMinute);

    }
}
