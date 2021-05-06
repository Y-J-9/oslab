import java.time.chrono.IsoChronology;

/*
@class:处理机的实例类，用于同步运行作业
@author:sun
 */
public class CPU extends Thread{
    /*
    @attribute:私有PCB属性，为该处理机目前运行的进程
     */
    private PCB pcb;
    private String Cname;//此处理机的名称
    public boolean DONTHAVE = true;//不含有进程
    public boolean Signal = false;//信号量，用于进程同步
    /*
    @method:方法
     */
    public void GetPcb(PCB pcb){
        //把进程调进处理机进行运行的方法
        this.pcb = pcb;
        this.DONTHAVE = false;
    }

    public void ClearPcb(){
        //清除该处理机内的进程的方法
        this.DONTHAVE = true;
    }

    public void setCname(String Cname){
        //设置该处理机名称的方法
        this.Cname = Cname;
    }

    public boolean isBusy(){
        //判断该处理机是否忙的方法
        return !this.DONTHAVE;
    }

    public void run() {
        //处理该进程的方法
       while(true) {
            if(!this.DONTHAVE) {
                this.Signal = true;
                pcb.GetCpu(this.Cname);
                pcb.run();
            }
            this.Signal = false;
            this.ClearPcb();
       }
    }
}
