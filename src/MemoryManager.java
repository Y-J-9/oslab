import java.util.ArrayList;

/*
@class:存储器管理者对象，针对于操作系统的存储器管理
@author:Sun
 */
public class MemoryManager{
    public static int[] MemoryList = new int[100];//分配100大小的存储空间
    public static ArrayList<Integer> startat;//存储空间开始的点

    static {
        //存储空间初始化
        for(int i = 0;i < MemoryList.length;i++){
            MemoryList[i] = 0;
        }
        //开始点初始化
        startat = new ArrayList<Integer>();
        startat.add(0);
    }
    public static boolean PushinMemory(PCB pcb){
        //将pcb放进内存的方法
        for(int i = 0;i < startat.size();i++){
            for(int at = startat.get(i);at < startat.get(i)+pcb.PCB_SIZE;at++){
                if(at == startat.get(i)+pcb.PCB_SIZE - 1&&at < 100){
                    //如果走到这里还没有break的话,可以把该进程放在这里
                    PushPCB(startat.get(i),pcb);
                    return true;
                }
                if(at >= 100||MemoryList[at] == 1){
                    break;
                }
            }
        }
        return false;//没有成功分入内存
    }

    public static void PushPCB(int start,PCB pcb){
        //把进程放入内存块的方法
        for(int i = start;i < start + pcb.PCB_SIZE;i++){
            MemoryList[i] = 1;
        }
        System.out.println(pcb.PID+"进程占"+pcb.PCB_SIZE+"大小，分配至"+start+"位置");
        pcb.Start_Memory = start;
        startat.add(start + pcb.PCB_SIZE);//添加开始项,下次可以从这里开始遍历
    }

    public static void DeletePcb(PCB pcb){
        //把进程从内存块中取出的方法
        for(int i = pcb.Start_Memory;i < pcb.Start_Memory+pcb.PCB_SIZE;i++){
            MemoryList[i] = 0;
        }
        for(int i = 0;i < startat.size();i++){
            if(startat.get(i) == pcb.Start_Memory+pcb.PCB_SIZE&&!startat.isEmpty()){
                startat.remove(i);
                break;
            }
        }
        for (int i = 0; i < PCBManager.Create_PCB.size(); i++) {
            if (MemoryManager.PushinMemory(PCBManager.Create_PCB.get(i))) {
                if(PCBManager.Create_PCB.get(i).FindPre() == null) {
                    PCBManager.Ready_FC(PCBManager.Create_PCB.get(i));
                    i--;
                }else {
                    PCBManager.Ready_FC(PCBManager.Create_PCB.get(i).FindPre());
                    i--;
                }
            }
        }
    }
}
