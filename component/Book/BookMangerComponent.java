package component.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookMangerComponent extends Box {
    final int WIDTH = 850;
    final int HEIGHT = 600;

    JFrame jf = null;
    private JTable table;
    String []titles = {"编号","书名","作者","出版社","价格","状态"};
    String[][] tableData;
    //二维结果集
    private DefaultTableModel tableModel;

    public BookMangerComponent(JFrame jf){
        //垂直布局
        super(BoxLayout.Y_AXIS);
        //组装视图
        this.jf = jf;

        JPanel btnPanel = new JPanel();
        Color color = new Color(203,220,217);
        btnPanel.setBackground(color);
        btnPanel.setMaximumSize(new Dimension(WIDTH,80));
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));//使得默认流式布局从右开始

        JButton addBtn = new JButton("添加图书");
        JButton updateBtn = new JButton("修改图书");
        JButton deleteBtn = new JButton("删除图书");
        JButton refreshBtn = new JButton("刷新");

        //刷新功能
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestData();
                tableModel = new DefaultTableModel(tableData,titles);//重新组装表
                table.setModel(tableModel);
            }
        });
        //添加功能
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出一个对话框,让用户输入信息
                new AddBookDialog(jf, "添加图书", true).setVisible(true);
            }
        });

        //修改功能
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取当前选中的bkid
                int selectedRow = table.getSelectedRow();//如果有选择的条目，则返回行号,没有选择返回-1
                if (selectedRow==-1){
                    JOptionPane.showMessageDialog(jf,"请选择要修改的行！");
                    return;
                }
                String bkid = tableModel.getValueAt(selectedRow, 0).toString();

                //弹出一个对话框，让用户修改
                new UpdateBookDialog(jf,"修改图书",true,bkid).setVisible(true);

            }
        });

        //删除功能
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取选中的条目
                int selectedRow = table.getSelectedRow();//如果有选择的条目，则返回行号,没有选择返回-1
                if (selectedRow==-1){
                    JOptionPane.showMessageDialog(jf,"请选择要删除的行！");
                    return;
                }

                //删除提示！
                int result= JOptionPane.showConfirmDialog(jf,"请您确认删除选中的条目吗？","确认删除",JOptionPane.YES_NO_OPTION);
                if (result!=JOptionPane.YES_OPTION){
                    return;
                }
                String bkid = tableModel.getValueAt(selectedRow, 0).toString();
                new DelectBookDialog(jf,"删除图书",true,bkid).setVisible(true);
            }
        });

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        this.add(btnPanel);

        //组装表格
        String []titles = {"编号","书名","作者","出版社","价格","状态"};

        //遍历表
        requestData();//只能获得结果集
        tableModel = new DefaultTableModel(tableData,titles);//重新组装表
        //设置table的性质
        table = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };//不允许在表中编辑
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //只能选中一行

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane);
        this.setVisible(true);
    }
    //请求数据
    public void requestData(){
        Connection con = null;
        Statement sql;
        ResultSet rs;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/booksdb", "name", "password");
            sql = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = sql.executeQuery("SELECT*FROM book");
            int i = 0, recordAmount = 0;
            rs.beforeFirst();
            while (rs.next())
                recordAmount++;
            tableData = new String[recordAmount][6];
            rs.beforeFirst();
            while (rs.next()) {
                String bkID = rs.getString(1);
                tableData[i][0] = bkID;
                String bkName = rs.getString(2);
                tableData[i][1] = bkName;
                String bkAuthor = rs.getString(3);
                tableData[i][2] = bkAuthor;
                String bkPress = rs.getString(4);
                tableData[i][3] = bkPress;
                String bkPrice = rs.getString(5);
                tableData[i][4] = bkPrice;
                String bkStatus = rs.getString(6);
                tableData[i][5] = bkStatus;
                i++;
            }
        } catch (SQLException e) {
            System.out.println("" + e);
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
