import java.util.ArrayList;

/*
@class:PCB调度器，用于生成，管理进程
@author:Sun
 */
public class PCBManager{
    /*
    @attribute:属性模块
     */
    public static UIWindow win0;//可视化界面
    public static final int CREATE_STATUS = 0;//进程的起始态，准备入就绪队列
    public static final int READY_STATUS = 1;//进程的就绪态，已经在就绪队列中了等待调度机运行
    public static final int RUN_STATUS = 2;//进程的运行态
    public static final int PAUSE_STATUS = 3;//进程的阻塞态
    public static final int EXIT_STATUS = 4;//进程的完成态
    public static ArrayList<PCB> Create_PCB;//初始状态的进程列表
    public static ArrayList<PCB> Ready_PCB;//就绪状态的进程列表
    public static ArrayList<PCB> Run_PCB;//运行状态的进程列表
    public static ArrayList<PCB> Pause_PCB;//阻塞状态的进程列表
    public static ArrayList<PCB> EXIT_PCB;//完成状态的进程列表
    /*
    @method:方法模块
     */
    static{
        //进程块，用来初始化进程及处理机列表
        Create_PCB = new ArrayList<PCB>();
        Ready_PCB = new ArrayList<PCB>();
        Run_PCB = new ArrayList<PCB>();
        Pause_PCB = new ArrayList<PCB>();
        EXIT_PCB = new ArrayList<PCB>();
        win0 = new UIWindow();
    }

    public static PCB Create_Simple(String Pname,int All_time,int weight){
        //创建独立进程的方法
        PCB pcb = new PCB(Pname,All_time,weight,CREATE_STATUS);
        Create_PCB.add(pcb);
        win0.CREATE_LIST.setListData(Create_PCB.toArray());//刷新后对可视化平台进行刷新
        return pcb;
        //Ready_FC(pcb);
    }

    public static void Ready_FC(PCB pcb){
        //使进程块从初始态进入就绪态的方法
        pcb.Change_Status(READY_STATUS);
        Create_PCB.remove(pcb);
        Ready_PCB.add(pcb);
        win0.CREATE_LIST.setListData(Create_PCB.toArray());
        win0.READY_LIST.setListData(Ready_PCB.toArray());
    }

    public static void Ready_FP(PCB pcb){
        //使进程块从阻塞态进入就绪态的方法
        pcb.Change_Status(READY_STATUS);
        Pause_PCB.remove(pcb);
        Ready_PCB.add(pcb);
        win0.PAUSE_LIST.setListData(Pause_PCB.toArray());
        win0.READY_LIST.setListData(Ready_PCB.toArray());
        MemoryManager.PushinMemory(pcb);
        win0.memoryPanel.paint(win0.memoryPanel.getGraphics());
    }

    public static void Ready_FR(PCB pcb){
        //使进程块从运行态时间片轮转到就绪态的方法
        pcb.Change_Status(READY_STATUS);
        Run_PCB.remove(pcb);
        Ready_PCB.add(pcb);
        win0.READY_LIST.setListData(Ready_PCB.toArray());
        win0.RUN_LIST.setListData(Run_PCB.toArray());
    }

    public static void Run(PCB pcb,CPU cpu){
        //使进程快进入运行态的方法
        pcb.Change_Status(RUN_STATUS);
        Run_PCB.add(pcb);
        pcb.GetCpu(cpu.GetCname());
        win0.RUN_LIST.setListData(Run_PCB.toArray());
        win0.READY_LIST.setListData(Ready_PCB.toArray());
        pcb.run();
    }

    public static void Pause(PCB pcb){
        //使进程块进入阻塞态的方法
        pcb.Change_Status(PAUSE_STATUS);
        Run_PCB.remove(pcb);
        Pause_PCB.add(pcb);
        win0.RUN_LIST.setListData(Run_PCB.toArray());
        win0.PAUSE_LIST.setListData(Pause_PCB.toArray());
        MemoryManager.DeletePcb(pcb);
        win0.memoryPanel.paint(win0.memoryPanel.getGraphics());
    }

    public static void Exit(PCB pcb){
        //使进程块进入完成态的方法
        pcb.Change_Status(EXIT_STATUS);
        pcb.PISALIVE = false;
        Run_PCB.remove(pcb);
        EXIT_PCB.add(pcb);
        win0.RUN_LIST.setListData(Run_PCB.toArray());
        win0.EXIT_LIST.setListData(EXIT_PCB.toArray());
        MemoryManager.DeletePcb(pcb);
        win0.memoryPanel.paint(win0.memoryPanel.getGraphics());
    }

    public static PCB SortPCB(CPU cpu,Boolean[] HaveReturn){
        //抢占式优先权算法，从就绪队列中选取优先级最高的交给空闲处理机运行
        int max = 0;
        int loc = 0;

        for(int i = 0;i < Ready_PCB.size();i++){
            if(!Ready_PCB.isEmpty()&&Ready_PCB.get(i).GetWeight() >= max ){
                max = Ready_PCB.get(i).GetWeight();
                loc = i;
            }
        }//for

        if(!Ready_PCB.isEmpty()&&Ready_PCB.get(loc) != null) {
            PCB exepcb = Ready_PCB.get(loc);
            if(exepcb.PreIsAlive()){
                //如果前趋活着的话
                if(exepcb.FindPreFR().Prestatus == PCBManager.READY_STATUS){
                    HaveReturn[0] = true;
                    PCB retpcb = exepcb.FindPreFR();
                    Ready_PCB.remove(exepcb.FindPreFR());
                    return retpcb;
                }
                else{
                    HaveReturn[0] = false;
                    return null;
                }
            }
            else if(exepcb.FindPreFR()!= null&&!exepcb.FindPreFR().PISALIVE){
                HaveReturn[0] = true;
                Ready_PCB.remove(exepcb);
                return exepcb;
            }
            else {
                HaveReturn[0] = false;
                return null;
            }
        }
        else {
            HaveReturn[0] = false;
            return null;
        }
    }
/*
    public static void ManagePCB(CPU cpu) {
        //管理PCB进程的方法，对所有创建的进程进行监听管理直至其进入完成队列
        while (!Ready_PCB.isEmpty() || !Run_PCB.isEmpty() || !Pause_PCB.isEmpty()) {
            SortPCB(cpu);
        }
    }
*/
}

