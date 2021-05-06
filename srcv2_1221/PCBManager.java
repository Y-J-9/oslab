import java.util.ArrayList;

/*
@class:PCB调度器，用于生成，管理进程
@author:Sun
 */
public class PCBManager{
    /*
    @attribute:属性模块
     */
    private static final int CREATE_STATUS = 0;//进程的起始态，准备入就绪队列
    private static final int READY_STATUS = 1;//进程的就绪态，已经在就绪队列中了等待调度机运行
    private static final int RUN_STATUS = 2;//进程的运行态
    private static final int PAUSE_STATUS = 3;//进程的阻塞态
    private static final int EXIT_STATUS = 4;//进程的完成态
    private static ArrayList<PCB> Create_PCB;//初始状态的进程列表
    private static ArrayList<PCB> Ready_PCB;//就绪状态的进程列表
    private static ArrayList<PCB> Run_PCB;//运行状态的进程列表
    private static ArrayList<PCB> Pause_PCB;//阻塞状态的进程列表
    private static ArrayList<PCB> EXIT_PCB;//完成状态的进程列表
    private static ArrayList<CPU> CPU_List;//处理机列表
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
        CPU_List = new ArrayList<CPU>();
        setCPU_List();
    }

    private static void setCPU_List(){
        //初始化处理机列表的方法
        for(int i = 0;i < 2;i++){
            CPU cpu = new CPU();
            cpu.setCname("处理机"+i+"号");
            CPU_List.add(cpu);//处理机入队
            cpu.start();
        }
    }

    public static void Create_Simple(String Pname,int PID,int All_time,int weight){
        //创建独立进程的方法
        PCB pcb = new PCB(Pname,PID,All_time,weight,CREATE_STATUS);
        Create_PCB.add(pcb);
        Ready_FC(pcb);
    }

    public static void Ready_FC(PCB pcb){
        //使进程块从初始态进入就绪态的方法
        pcb.Change_Status(READY_STATUS);
        Create_PCB.remove(pcb);
        Ready_PCB.add(pcb);
    }

    public static void Ready_FP(PCB pcb){
        //使进程块从阻塞态进入就绪态的方法
        pcb.Change_Status(READY_STATUS);
        Pause_PCB.remove(pcb);
        Ready_PCB.add(pcb);
    }

    public static void Ready_FR(PCB pcb){
        //使进程块从运行态时间片轮转到就绪态的方法
        pcb.Change_Status(READY_STATUS);
        Run_PCB.remove(pcb);
        Ready_PCB.add(pcb);
    }

    public static void Run(PCB pcb,CPU cpu){
        //使进程快进入运行态的方法
        pcb.Change_Status(RUN_STATUS);
        Ready_PCB.remove(pcb);
        Run_PCB.add(pcb);
        cpu.GetPcb(pcb);
    }

    public static void Pause(PCB pcb){
        //使进程块进入阻塞态的方法
        pcb.Change_Status(PAUSE_STATUS);
        Run_PCB.remove(pcb);
        Pause_PCB.add(pcb);
    }

    public static void Exit(PCB pcb){
        //使进程块进入完成态的方法
        pcb.Change_Status(EXIT_STATUS);
        Run_PCB.remove(pcb);
        EXIT_PCB.add(pcb);
    }

    public static void SortPCB(){
        //抢占式优先权算法，从就绪队列中选取优先级最高的交给空闲处理机运行
        int max = 0;
        int loc = 0;

        for(int i = 0;i < Ready_PCB.size();i++){
            if(Ready_PCB.get(i).GetWeight() >= max){
                max = Ready_PCB.get(i).GetWeight();
                loc = i;
            }

        }//for
        for(int i = 0;i < CPU_List.size();i++) {
            if(!CPU_List.get(i).isBusy()) {
                Run(Ready_PCB.get(loc),CPU_List.get(i));//调入不忙的处理机运行该进程
              break;
            }
        }

    }

    public static boolean JudgeWait(){
        //判断主线程是否该等待CPU处理完两个进程
        for(int i = 0;i < CPU_List.size();i++){
            if(CPU_List.get(i).Signal){
                return true;
            }
        }
        return false;
    }

    public static void ManagePCB() {
        //管理PCB进程的方法，对所有创建的进程进行监听管理直至其进入完成队列
        while(!Ready_PCB.isEmpty()||!Run_PCB.isEmpty()||!Pause_PCB.isEmpty()){
            SortPCB();
        }
    }

}
