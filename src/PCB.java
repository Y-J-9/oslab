import javax.swing.*;
/*
@class:模拟进程块，内含PCB的基本定义
@author:Sun
 */
public class PCB extends Thread{
    /*
    @attribute:属性模块
     */
    private final int PIECETIME = 1000;//时间片
    private boolean NEVEREND = true;//还没有结束过
    public String Pname;//进程名
    private static int ID = 100;
    public int PID = 100;//进程ID
    private int All_time;//要求运行时间
    public int Now_time;//剩余运行时间
    public int PCB_SIZE = (int)(Math.random()*5+5);//所占用内存
    public int Start_Memory;//内存起始点
    public int Weight;// 优先权，权重越大越先运行
    public int Prestatus;//当前状态，分为准备，就绪，运行，阻塞，完成五态
    private boolean IsSimple;//是否为独立进程
    public int Pre_ID = 0;//前趋进程的PID
    private int Sub_ID;//后继进程的PID
    private String Cname;//处理机名称
    public boolean PISALIVE = true;//默认活着
    /*
    @method:方法模块
     */
    public PCB(String Pname,int All_time,int weight,int prestatus){
        //独立进程的构造函数,前趋后继进程将默认为0
        this.Pname = Pname;
        ID++;
        this.PID = ID;
        this.All_time = All_time;
        this.Now_time = All_time;
        this.Weight = weight;
        this.Prestatus = prestatus;
        this.IsSimple = true;
        this.Pre_ID = 0;
        this.Sub_ID = 0;
    }
    public boolean PreIsAlive(){
        //判断前趋进程是否活着的方法
        if(FindPreFR() == null){
            return false;
        }
        if(FindPreFR().Prestatus == PCBManager.RUN_STATUS||FindPreFR().Prestatus == PCBManager.PAUSE_STATUS || FindPreFR().Prestatus == PCBManager.READY_STATUS){
            return true;
        }
        else{
            return false;
        }
    }

    public PCB FindPreFR(){
        //从后备队列中查询前趋进程的方法
        for(int i = 0;i < PCBManager.Create_PCB.size();i++){
            if(PCBManager.Create_PCB.get(i).PID == this.Pre_ID){
                return PCBManager.Create_PCB.get(i);
            }
        }
        //从队列中查询前趋进程的方法
        for(int i = 0;i < PCBManager.Ready_PCB.size();i++){
            if(PCBManager.Ready_PCB.get(i).PID == this.Pre_ID){
                return PCBManager.Ready_PCB.get(i);
            }
        }
        for(int i = 0;i < PCBManager.Run_PCB.size();i++){
            if(PCBManager.Run_PCB.get(i).PID == this.Pre_ID){
                return PCBManager.Run_PCB.get(i);
            }
        }
        for(int i = 0;i < PCBManager.Pause_PCB.size();i++){
            if(PCBManager.Pause_PCB.get(i).PID == this.Pre_ID){
                return PCBManager.Pause_PCB.get(i);
            }
        }
        for (int i = 0;i < PCBManager.EXIT_PCB.size();i++){
            if(PCBManager.EXIT_PCB.get(i).PID == this.Pre_ID){
                return PCBManager.EXIT_PCB.get(i);
            }
        }
        return null;
    }
    public PCB FindPre(){
        //从后备队列中查询前趋进程的方法
        for(int i = 0;i < PCBManager.Create_PCB.size();i++){
            if(PCBManager.Create_PCB.get(i).PID == this.Pre_ID){
                return PCBManager.Create_PCB.get(i);
            }
        }
        return null;
    }
    public PCB(String Pname,int PID,int All_time,int weight,int prestatus,int Pre_ID,int Sub_ID){
        //同步进程的构造函数进行赋值
        this.Pname = Pname;
        this.PID = PID;
        this.All_time = All_time;
        this.Now_time = All_time;
        this.Weight = weight;
        this.Prestatus = prestatus;
        this.IsSimple = false;
        this.Pre_ID = Pre_ID;
        this.Sub_ID = Sub_ID;
    }

    public void Change_Status(int STATUS){
        //改变进程当前状态的方法
        this.Prestatus = STATUS;
    }

    public String Get_Status(){
        //获取进程当前状态的方法
        String re = null;
        switch (this.Prestatus){
            case PCBManager.CREATE_STATUS:re = "初始";
                                          break;
            case PCBManager.READY_STATUS:re = "就绪";
                                        break;
            case PCBManager.PAUSE_STATUS:re = "挂起";
                                        break;
            case PCBManager.RUN_STATUS:re = "运行";
                                        break;
            case PCBManager.EXIT_STATUS:re = "完成";
                                        break;
        }
        return re;
    }

    public int GetWeight(){
        return this.Weight;//获取当前进程的优先权的方法
    }

    public void GetCpu(String CName){
        this.Cname = CName;
    }

    public boolean GetNever(){
        return this.NEVEREND;
    }

    @Override
    public void run() {
        //实现Runnable类的run方法,进程运行时的情况
        if(this.Now_time > 0) {
            System.out.println(this.toString() + " " + this.PID + "在"+this.Cname+"上运行哦"+" 还剩"+this.Now_time+" 秒");
            //运行一个时间片
            try {
                Thread.sleep(this.PIECETIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            --this.Now_time;
            --this.Weight;//运行完一个时间片后更改剩余时间以及权重
            //否则还未运行完，一个时间片后进入就绪队列
            if(Now_time <= 0){
                //如果剩余时间小于等于0的话，该进程已经运行完了
                PCBManager.Exit(this);
                System.out.println(this.toString()+"进入完成态啦");
                NEVEREND = !NEVEREND;
            }
            if(this.Prestatus == PCBManager.RUN_STATUS) {
                PCBManager.Ready_FR(this);
            }
        }
        else if(NEVEREND){
            //如果剩余时间小于等于0的话，该进程已经运行完了
            PCBManager.Exit(this);
            System.out.println(this.toString()+"进入完成态啦");
            NEVEREND = !NEVEREND;
        }
    }
}
