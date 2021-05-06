/*
@class:处理机的实现类，利用内存当中存储的PCB信息进行调度
@author:Sun
 */
public class CPU implements Runnable{

    private String Cname;
    private PCB pcb;
    public Object object;
    private Boolean[] havereturn;
    public CPU(String Cname,Object object){
        this.Cname = Cname;
        this.object = object;
        this.havereturn = new Boolean[]{false};
    }

    public String GetCname(){
        return this.Cname;
    }

    @Override
    public void run() {
            while(true) {
                synchronized (this.object){
                    this.pcb = PCBManager.SortPCB(this,this.havereturn);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(this.havereturn[0] == true&&this.pcb != null) {
                    PCBManager.Run(this.pcb, this);
                }

            }
    }
}
