public class test {
    /*
    测试类，用来测试自己写的代码
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            PCBManager p1 = new PCBManager();
        }catch (Exception e){
            System.out.println("Swing组件线程不安全，请重启");
        }
    }
}
