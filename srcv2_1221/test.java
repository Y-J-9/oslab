public class test {
    /*
    测试类，用来测试自己写的代码
     */
    public static void main(String[] args) {
        PCBManager.Create_Simple("123",101,10,5);
        PCBManager.Create_Simple("134",102,15,6);
        PCBManager.Create_Simple("145",103,20,10);
        try {
            PCBManager.ManagePCB();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
