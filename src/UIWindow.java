import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/*
@class:交互界面类，用于显示进程的实时运行状态,并为用户提供输入输出环境
@author:Sun
 */
public class UIWindow extends JFrame {
    /*
    @attribute:属性集
     */
    private JPanel NorthPanel;//边界布局中北面的布局面板，用于输入
    private JScrollPane WestPanel;//用于可视化进程的西面板
    private JPanel RestPanel;//用于可视化进程的剩余面板
    private JPanel SouthPanel;//用于展示内存空间以及操作处理暂停的面板
    public MemoryPanel memoryPanel;//用于可视化存储空间的面板
    private PCBListRander pcbListRander;//JList的渲染器
    public JList CREATE_LIST;//创建完进程后的可视化列表
    public JList READY_LIST;//就绪列表
    public JList RUN_LIST;//运行列表
    public JList PAUSE_LIST;//暂停列表
    public JList EXIT_LIST;//退出列表
    private JTextField namefield;//输入进程名的文本框
    private JTextField timefield;//输入运行时间的文本框
    private JTextField weightfield;//输入运行权重的文本框
    private JTextField numfield;//输入创建进程个数的文本框
    private JTextField beffiled;//输入前趋进程的文本框
    private JLabel CPU_NUML;//显示CPU个数的标签
    Object obj = new Object();
    Thread thread0;
    private int CPU_NUM = 1;

    class MemoryPanel extends JPanel{
        //实现存储空间可视化的面板
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int i = 0;i < MemoryManager.startat.size();i++){
                g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
                //对于存储空间的每一个小段都进行绘制,一个小段的横轴宽为5
                for(int i0 = MemoryManager.startat.get(i);i0 < 100&&MemoryManager.MemoryList[i0] == 1&&i+1 < MemoryManager.startat.size()&&i0 < MemoryManager.startat.get(i+1);i0++) {
                    g.fillRect(0 + i0* 5, 0, 5, this.getHeight());
                }
            }
        }
    }
    /*
    @method:构造方法，用于初始化界面
     */
    public UIWindow(){
        this.thread0 = new Thread(new CPU("处理机0号",obj));
        this.thread0.start();
        init();
        this.setSize(1000,900);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);

        this.setVisible(true);
    }

    public void init(){
        //初始化界面的方法
        this.setLayout(new BorderLayout());
        this.initNorthPanel();//初始化北面板
        this.InitWestPanel();//初始化西面板
        this.InitRestPanel();//初始化剩余面板
        this.InitSouthPanel();//初始化南面板
    }

    public void initNorthPanel(){
        //初始化北面板的方法，先进行北面板的布局设计
        this.NorthPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(30);
        this.NorthPanel.setLayout(flowLayout);
        this.add(this.NorthPanel,BorderLayout.NORTH);
        //再添加标签
          //统计总进程的标签
        JLabel sumlable = new JLabel("进程总数："+String.valueOf(123),JLabel.CENTER);
        class sumthread implements Runnable{
            @Override
            public void run() {
                while(true){
                    sumlable.setText("进程总数：" + String.valueOf(PCBManager.Ready_PCB.size() + PCBManager.Run_PCB.size() + PCBManager.Pause_PCB.size()));
                }
            }
            //统计总进程数的进程类
        }
        sumthread sum = new sumthread();
        new Thread(sum).start();
        sumlable.setFont(new Font("黑体",1,15));
        sumlable.setForeground(new Color(101,140,140));
        this.NorthPanel.add(sumlable);
        //添加输入文本框的方法
            //名称输入文本框
        JLabel namelabel = new JLabel("进程名：",JLabel.CENTER);
        namelabel.setFont(new Font("宋体",0,13));

        this.namefield = new JTextField();
        this.namefield.setPreferredSize(new Dimension(50,40));

        JPanel newPanel = new JPanel();
        newPanel.add(namelabel);
        newPanel.add(namefield);
        this.NorthPanel.add(newPanel);
            //运行时间输入文本框
        JLabel timelabel = new JLabel("运行时间：",JLabel.CENTER);
        timelabel.setFont(new Font("宋体",0,13));

        this.timefield= new JTextField();
        this.timefield.setPreferredSize(new Dimension(50,40));

        JPanel timePanel = new JPanel();
        timePanel.add(timelabel);
        timePanel.add(timefield);
        this.NorthPanel.add(timePanel);
             //运行权重输入文本框
        JLabel weightlabel = new JLabel("运行权重：",JLabel.CENTER);
        weightlabel.setFont(new Font("宋体",0,13));

        this.weightfield = new JTextField();
        this.weightfield.setPreferredSize(new Dimension(50,40));

        JPanel weightPanel = new JPanel();
        weightPanel.add(weightlabel);
        weightPanel.add(weightfield);
        this.NorthPanel.add(weightPanel);
            //创建进程个数输入文本框
        JLabel numlabel = new JLabel("创建个数：",JLabel.CENTER);
        numlabel.setFont(new Font("宋体",0,13));

        this.numfield = new JTextField();
        this.numfield.setPreferredSize(new Dimension(50,40));

        JPanel numPanel = new JPanel();
        weightPanel.add(numlabel);
        weightPanel.add(numfield);
        this.NorthPanel.add(numPanel);
            //确认输入按钮
        JButton jButton = new JButton("添加");
        class buttonsure implements ActionListener{
            /*
            @class:确认输入按钮的按钮监听器，当点击时创建PCB对象并且清空数据段的值
             */
            /*
            @attribute:属性段
             */
            private JTextField namefield;//输入进程名的文本框
            private JTextField timefield;//输入运行时间的文本框
            private JTextField weightfield;//输入运行权重的文本框
            private JTextField numfield;//输入进程个数的文本框
            /*
            @method:方法段，构造方法以及监听方法
             */
            public buttonsure(JTextField namefield,JTextField timefield,JTextField weightfield,JTextField numfield){
                this.namefield = namefield;
                this.timefield = timefield;
                this.weightfield = weightfield;
                this.numfield = numfield;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    /*
                    如果没有捕获到异常的话创建新PCB进程块并且清空文本框中的数据以便下次输入，否则弹出错误提示
                     */
                    for(int i = 0;i < Integer.valueOf(numfield.getText());i++) {
                        PCB pcb = PCBManager.Create_Simple(this.namefield.getText(), Integer.valueOf(this.timefield.getText()), Integer.valueOf(this.weightfield.getText()));
                        if(beffiled.getText() != "0"){
                            pcb.Pre_ID = Integer.valueOf(beffiled.getText());
                        }
                    }
                    this.namefield.setText("");
                    this.timefield.setText("");
                    this.weightfield.setText("");
                    this.numfield.setText("");

                }catch (Exception a){
                    JOptionPane.showMessageDialog(null,"请输入正确的信息");
                }
            }
        }
        buttonsure b0 = new buttonsure(this.namefield,this.timefield,this.weightfield,this.numfield);
        jButton.addActionListener(b0);

        jButton.setFont(new Font("黑体",1,15));
        this.NorthPanel.add(jButton);
    }
    /*
    @method:初始化西面板的方法，用来初始化进程列表
    @author:Sun
  */
    public void InitWestPanel(){
        this.CREATE_LIST =new JList(PCBManager.Create_PCB.toArray());
        this.WestPanel =new JScrollPane(this.CREATE_LIST);
        JLabel headLabel = new JLabel("进程名     进程ID     权重     剩余时间     状态",SwingConstants.CENTER);
        headLabel.setPreferredSize(new Dimension(200,30));
        this.WestPanel.setColumnHeaderView(headLabel);//设置列表头
        this.pcbListRander = new PCBListRander();
        this.CREATE_LIST.setCellRenderer(this.pcbListRander);
        this.add(this.WestPanel, BorderLayout.WEST);
    }
    /*
    @class:列表渲染器
     */
    class PCBListRander implements ListCellRenderer{
        JButton jLabel = new JButton();
        PCBListRander(){
            jLabel.setFont(new Font("黑体",10,15));
            jLabel.setPreferredSize(new Dimension(200,30));
        }
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
              PCB pcb = (PCB)value;
              if(index%2 == 0){
                  jLabel.setBackground(Color.WHITE);
              }
              else{
                  jLabel.setBackground(Color.LIGHT_GRAY);
              }
              if(isSelected){
                  jLabel.setBackground(Color.GRAY);
              }
              jLabel.setText(pcb.Pname+"   "+String.valueOf(pcb.PID)+"   "+String.valueOf(pcb.Weight)+"   "+String.valueOf(pcb.Now_time)+"    "+pcb.Get_Status());
              return jLabel;
        }
    }
    class PauseListRander implements ListCellRenderer{

        JButton jLabel = new JButton();
        PauseListRander(){
            jLabel.setFont(new Font("黑体",10,15));
            jLabel.setPreferredSize(new Dimension(200,30));
        }
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            PCB pcb = (PCB)value;
            if(index%2 == 0){
                jLabel.setBackground(Color.WHITE);
            }
            else{
                jLabel.setBackground(Color.LIGHT_GRAY);
            }
            if(isSelected){
                jLabel.setBackground(Color.GRAY);
                PCBManager.Ready_FP(pcb);
            }
            jLabel.setText(pcb.Pname+"   "+String.valueOf(pcb.PID)+"   "+String.valueOf(pcb.Weight)+"   "+String.valueOf(pcb.Now_time)+"    "+pcb.Get_Status());
            return jLabel;
        }
    }
    /*
    @method:初始化剩余面板的方法，用来初始化进程列表
    @author:Sun
     */
    public void InitRestPanel(){
        //剩余面板采用框框式布局，每一个布局设置一个滚动页面，滚动页面内部是一个JList
        this.RestPanel = new JPanel();
        this.RestPanel.setLayout(new GridLayout(2,2,50,50));
        this.READY_LIST = new JList();
        this.READY_LIST.setCellRenderer(this.pcbListRander);
        JScrollPane Readyscr = new JScrollPane(this.READY_LIST);
        this.RestPanel.add(Readyscr);
        this.RUN_LIST = new JList();
        this.RUN_LIST.setCellRenderer(this.pcbListRander);
        JScrollPane Runscr = new JScrollPane(this.RUN_LIST);
        this.RestPanel.add(Runscr);
        this.PAUSE_LIST = new JList();
        this.PAUSE_LIST.setCellRenderer(new PauseListRander());
        JScrollPane Pausescr = new JScrollPane(this.PAUSE_LIST);
        this.RestPanel.add(Pausescr);
        this.EXIT_LIST = new JList();
        this.EXIT_LIST.setCellRenderer(this.pcbListRander);
        JScrollPane Exitscr = new JScrollPane(this.EXIT_LIST);
        this.RestPanel.add(Exitscr);
        this.add(RestPanel,BorderLayout.CENTER);
    }
    /*
    @method:初始化南面板的方法
     */
    public void InitSouthPanel(){
        this.SouthPanel = new JPanel();
        this.SouthPanel.setLayout(new FlowLayout(0,30,5));//设置流式布局
        this.SouthPanel.setPreferredSize(new Dimension(0,150));//设置纵向大小
        //设置开始按钮
        JButton jButton = new JButton(new ImageIcon(new ImageIcon("Start.jpg").getImage().getScaledInstance(50,30,Image.SCALE_DEFAULT)));
        jButton.setPreferredSize(new Dimension(50,30));
        class pushinThread implements Runnable {
            @Override
            public void run() {
                    for(int cout = 0;cout < 10;cout++) {
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
        }
        class startlistener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                   new Thread(new pushinThread()).start();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                memoryPanel.paint(memoryPanel.getGraphics());
                }
            }

            //实现按钮点击的类，实现点击开始的功能
        jButton.addActionListener(new startlistener());
        this.SouthPanel.add(jButton);
        //设置暂停按钮
        JButton pButton = new JButton(new ImageIcon(new ImageIcon("Pause.jpg").getImage().getScaledInstance(50,30,Image.SCALE_DEFAULT)));//设置缩放图像图标
        pButton.setPreferredSize(new Dimension(50,30));
        class pauselistener implements ActionListener{
            //暂停按钮的监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0;i < PCBManager.Run_PCB.size();i++){
                    PCBManager.Pause(PCBManager.Run_PCB.get(i));
                    i--;
                }
            }
        }
        pauselistener p0 = new pauselistener();
        pButton.addActionListener(p0);
        this.SouthPanel.add(pButton);

        this.memoryPanel = new MemoryPanel();
        this.memoryPanel.setBackground(Color.GRAY);
        memoryPanel.setPreferredSize(new Dimension(500,100));
        this.SouthPanel.add(memoryPanel);

        class cpuaddListener implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new CPU("处理机"+CPU_NUM+"号",obj)).start();
                CPU_NUM++;
                CPU_NUML.setText("当前处理机数："+CPU_NUM);
            }
        }
        JButton cpuaddButton = new JButton("增加处理机");
        cpuaddButton.addActionListener(new cpuaddListener());
        JPanel hanPanel = new JPanel();
        hanPanel.setLayout(new FlowLayout(0));
        hanPanel.setPreferredSize(new Dimension(120,150));
        this.CPU_NUML = new JLabel("当前处理机数："+this.CPU_NUM);
        hanPanel.add(this.CPU_NUML);
        hanPanel.add(cpuaddButton);
        this.SouthPanel.add(hanPanel);
        this.beffiled = new JTextField("101");
        JPanel bfePanel = new JPanel();
        bfePanel.setLayout(new FlowLayout(0));
        bfePanel.setPreferredSize(new Dimension(100,150));
        bfePanel.add(new JLabel("设置前趋进程："));
        this.beffiled.setPreferredSize(new Dimension(50,30));
        bfePanel.add(this.beffiled);
        this.SouthPanel.add(bfePanel);
        this.add(this.SouthPanel,BorderLayout.SOUTH);
    }
}


