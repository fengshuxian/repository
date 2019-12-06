import com.sun.codemodel.internal.JOp;
import com.sun.java.swing.action.StateChangeAction;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.lang.String;

class AdminInterface
{
    static void adminInt()
    {
        JFrame f1 = new JFrame("管理员");

        f1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f1.setSize(300, 300);
        f1.setResizable(false);
        f1.setLocationRelativeTo(null);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 50));
        f1.setContentPane(panel1);
        
        JButton addNewBook = new JButton("添加新书");
        addNewBook.setFont(new Font(null, Font.PLAIN, 15));
        addNewBook.setPreferredSize(new Dimension(90, 35));
        panel1.add(addNewBook);
        addNewBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //自动获取书本编号
                String bkId = String.valueOf(ReaderInterface.getMax("tb_book", "bookid") + 1);


                //输入书本名称
                String bkNa = JOptionPane.showInputDialog(
                        f1,
                        "请输入书本名称"
                );

                //输出出版社
                String bkFr = JOptionPane.showInputDialog(
                        f1,
                        "请输入出版社"
                );


                //输入作者
                String bkWr = JOptionPane.showInputDialog(
                        f1,
                        "请输入作者"
                );

                //输入价格
                String bkPri = JOptionPane.showInputDialog(
                        f1,
                        "请输入价格"
                );

                //输入书本数量
                String bkCnt = JOptionPane.showInputDialog(
                        f1,
                        "请输入书本数量"
                );

                Map<Integer, String> m1 = new HashMap<Integer, String>();
                m1.put(0, bkId);
                m1.put(1, "'" + bkNa + "'");
                m1.put(2, "'" + bkFr + "'");
                m1.put(3, "'" + bkWr + "'");
                m1.put(4, bkPri);
                m1.put(5, bkCnt);
                m1.put(6, bkCnt);

                ReaderInterface.insertDoc("tb_book", m1);

            }
        });

        JButton addOldBook = new JButton("添加旧书");
        addOldBook.setFont(new Font(null, Font.PLAIN, 15));
        addOldBook.setPreferredSize(new Dimension(90, 35));
        panel1.add(addOldBook);
        addOldBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取书本编号
                String bkid = JOptionPane.showInputDialog(
                        f1,
                        "请输入书本编号"
                );


                //输入添加数量
                String bkCnt = JOptionPane.showInputDialog(
                        f1,
                        "请输入添加数量"
                );

                ReaderInterface.updateDoc("tb_book", "count", bkCnt, "bookid", bkid);
                ReaderInterface.updateDoc("tb_book", "total", bkCnt, "bookid", bkid);


            }
        });

        JButton delBook = new JButton("删除图书");
        delBook.setFont(new Font(null, Font.PLAIN, 15));
        delBook.setPreferredSize(new Dimension(90, 35));
        panel1.add(delBook);
        delBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取图书编号
                String bkId = JOptionPane.showInputDialog(
                        f1,
                        "请输入要删图书的编号"
                );

                List<String> l = new ArrayList<String>();
                l.add("count");
                l.add("total");
                List<Map<String, Object>> bkLi = ReaderInterface.getList("tb_book", "bookid", bkId, l);

                if (!bkLi.get(0).get("count").toString().equals(bkLi.get(0).get("total").toString()))
                {
                    JOptionPane.showMessageDialog(
                            f1,
                            "此书还有未归还的书本,无法删除!"
                    );
                }
                else if (bkLi.get(0).get("count").toString().equals(bkLi.get(0).get("total").toString()))
                {
                    ReaderInterface.delDoc("tb_book", "bookid", bkId);
                    JOptionPane.showMessageDialog(
                            f1,
                            "删除成功!"
                    );
                }
                else JOptionPane.showMessageDialog(
                            f1,
                            "请输入正确的书本编号"
                    );



            }
        });

        JButton delReader = new JButton("删除读者");
        delReader.setFont(new Font(null, Font.PLAIN, 15));
        delReader.setPreferredSize(new Dimension(90, 35));
        panel1.add(delReader);
        delReader.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reId = JOptionPane.showInputDialog(
                        f1,
                        "请输入读者编号"
                );

                List<String> l = new ArrayList<String>();
                l.add("arrears");
                l.add("lendcnt");

                List<Map<String, Object>> reLi = ReaderInterface.getList("tb_reader", "readerid", reId, l);

                if ( !reLi.get(0).get("arrears").toString().equals("0") || !reLi.get(0).get("lendcnt").toString().equals("0"))
                {
                    JOptionPane.showMessageDialog(
                            f1,
                            "有未还清的罚款或未归还的书本,无法删除!"
                    );
                }
                else
                {
                    ReaderInterface.delDoc("tb_reader", "readerid", reId);
                    JOptionPane.showMessageDialog(
                            f1,
                            "删除成功"
                    );
                }



            }
        });

        JButton logout = new JButton("退出登录");
        logout.setFont(new Font(null, Font.PLAIN, 15));
        logout.setPreferredSize(new Dimension(90, 35));
        panel1.add(logout);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f1.dispose();
            }
        });

        f1.setVisible(true);
    }
}

class ReaderInterface
{


    //计算x与当前日期的天数差
    static long calDays(String x)throws ParseException
    {


        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

        Date d2 = sdf.parse(x);

        long daysBetween=(new Date().getTime() - d2.getTime()) / (60 * 60 * 24 * 1000);

        return daysBetween;
    }

    static List<Map<String,Object>> ResultSetToList(ResultSet rs) throws SQLException
    {
        List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount=rsmd.getColumnCount();
        List<String> colNameList = new ArrayList<String>();
        for(int i = 0; i < colCount; i++)
        {
            colNameList.add(rsmd.getColumnName(i+1));
        }

        while(rs.next())
        {
            Map map=new HashMap<String, Object>();
            for(int i = 0; i < colCount; i++)
            {

                String key=colNameList.get(i);
                Object value=rs.getString(colNameList.get(i));
                map.put(key, value);

            }
            results.add(map);
        }
        return results;
    }

    static void delDoc(String tb, String cid, String id)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;

            sql = "delete from " + tb + " where " + cid + " = " + id;

            stmt.executeUpdate(sql);



            stmt.close();
            conn.close();




        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
    }


    static void updateDoc(String tb, String field, String value, String cid, String id)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;

            sql = "update " + tb + " set " + field + " = " + field + " + "  + value + " where " + cid + " = " + id;

            stmt.executeUpdate(sql);





            stmt.close();
            conn.close();





        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
    }


    static void insertDoc(String tb, Map<Integer, String> value)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;

            sql = "insert into " + tb + " value(";

            for (int i = 0; i < value.size() - 1; ++i) sql += value.get(i) + ",";

            sql += value.get(value.size() - 1);

            sql += ")";

            //System.out.println(sql);


            stmt.executeUpdate(sql);







            stmt.close();
            conn.close();





        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
    }

    static int getMax(String tb, String value)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;

            sql = "select max(" + value + ") from " + tb;


            ResultSet rs = stmt.executeQuery(sql);

            //System.out.println(sql);

            rs.next();
            int max = 0;
            max = Integer.parseInt(rs.getString("max(" + value + ")"));

            stmt.close();
            conn.close();
            return max;


        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
        return 0;
    }



    //带筛选
    static List<Map<String,Object>> getList(String tb, String cid, String id, List<String> l)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        List<Map<String,Object>> rSeToLi = new ArrayList<Map<String, Object>>();

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;


            sql = "select ";
            for (int i = 0; i < l.size() - 1; ++i) sql += l.get(i) + ",";
            sql += l.get(l.size() - 1);
            sql += " from " + tb;
            sql += " where " + cid + " = '" + id + "'";


            ResultSet rs = stmt.executeQuery(sql);

            rSeToLi = ResultSetToList(rs);




            rs.close();
            stmt.close();
            conn.close();





        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
        return rSeToLi;
    }


    //不带筛选
    static List<Map<String,Object>> getList(String tb, List<String> l)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        List<Map<String,Object>> rSeToLi = new ArrayList<Map<String, Object>>();

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;


            sql = "select ";
            for (int i = 0; i < l.size() - 1; ++i) sql += l.get(i) + ",";
            sql += l.get(l.size() - 1);
            sql += " from " + tb;



            ResultSet rs = stmt.executeQuery(sql);

            rSeToLi = ResultSetToList(rs);




            rs.close();
            stmt.close();
            conn.close();





        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
        return rSeToLi;
    }


    static List<Map<String,Object>> lendList = new ArrayList<Map<String,Object>>();
    static List<Map<String,Object>> readerList = new ArrayList<Map<String,Object>>();
    static List<Map<String,Object>> bookList = new ArrayList<Map<String,Object>>();
    //读者界面
    static void readerInt(String user)
    {
        JFrame f2 = new JFrame("读者");

        f2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f2.setSize(300, 250);
        f2.setResizable(false);
        f2.setLocationRelativeTo(null);
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 50));





        //sql = "select readerid, bookid, lendid, lastdate from tb_lend where readerid = '" + user + "'";

        //获取借条列表
        List<String> lenL = new ArrayList<String>();
        lenL.add("readerid");
        lenL.add("bookid");
        lenL.add("lendid");
        lenL.add("lastdate");


        lendList = getList("tb_lend", "readerid", user, lenL);

        //sql = "select readerid, lendcnt, arrears, mostlend from tb_reader where readerid = '" + user + "'";


        //获取读者列表
        List<String> rerL = new ArrayList<String>();
        rerL.add("readerid");
        rerL.add("lendcnt");
        rerL.add("arrears");
        rerL.add("mostlend");

        readerList = getList("tb_reader", "readerid", user, rerL);
        //sql = "select bookid, count, bookname from tb_book"


        //获取书本列表
        List<String> bokL = new ArrayList<String>();
        bokL.add("bookid");
        bokL.add("count");
        bokL.add("bookname");
        bookList = getList("tb_book",bokL);






        JButton lendbook = new JButton("借书");
        lendbook.setFont(new Font(null, Font.PLAIN, 15));
        lendbook.setPreferredSize(new Dimension(90, 35));
        panel2.add(lendbook);
        lendbook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputContent = JOptionPane.showInputDialog(
                        f2,
                        "请输入书本编号:"
                );


                //校验书本编号
                int index = -1;

                for (int i = 0; i < bookList.size(); ++i)
                    if (bookList.get(i).get("bookid").toString().equals(inputContent))
                    {
                        index = i;
                        break;
                    }

                //检查是否重复借阅同一本书
                boolean cfj = false;

                for (int i = 0; i < lendList.size(); ++i)
                    if (lendList.get(i).get("bookid").toString().equals(inputContent))
                    {
                        cfj = true;
                        break;
                    }


                if (cfj) JOptionPane.showMessageDialog(
                        f2,
                        "同一种书只能借阅一本!"
                );
                else if (index == -1) JOptionPane.showMessageDialog(
                        f2,
                        "请输入正确的书本编号!"
                );

                else if (bookList.get(index).get("count").toString().equals("0")) JOptionPane.showMessageDialog(
                        f2,
                        "库存书本已出借完!"
                );

                else if (Integer.parseInt(readerList.get(0).get("lendcnt").toString()) >= Integer.parseInt(readerList.get(0).get("mostlend").toString())) JOptionPane.showMessageDialog(
                        f2,
                        "你的借阅数已达上限!"
                );
                else
                {


                    //更新库存书本
                    updateDoc("tb_book", "count", "-1", "bookid", inputContent);

                    //更新读者借阅数
                    updateDoc("tb_reader", "lendcnt", "1", "readerid", readerList.get(0).get("readerid").toString());


                    //更新借条
                    Map m1 = new HashMap();

                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    String date = new String(sdf.format(new Date().getTime() + 432000000));

                    m1.put(0, readerList.get(0).get("readerid").toString());
                    m1.put(1, inputContent);
                    m1.put(2, "'" + bookList.get(index).get("bookname").toString() + "'");
                    m1.put(3, String.valueOf(getMax("tb_lend", "lendid") + 1));
                    m1.put(4, "'" + date + "'");
                    insertDoc("tb_lend", m1);


                    Map<String,Object> m2 = new HashMap<String, Object>();
                    m2.put("readerid", m1.get(0));
                    m2.put("bookid", m1.get(1));
                    m2.put("bookname", m1.get(2));
                    m2.put("lendid", m1.get(3));
                    m2.put("lastdate", m1.get(4));

                    //添加借条
                    lendList.add(m2);

                    //更新库存
                    bookList.get(index).put("count", Integer.parseInt(bookList.get(index).get("count").toString()) - 1);

                    //更新读者借阅数
                    readerList.get(0).put("lendcnt", Integer.parseInt(readerList.get(0).get("lendcnt").toString()) + 1);


                }




            }
        });

        JButton reBook = new JButton("还书");
        reBook.setFont(new Font(null, Font.PLAIN, 15));
        reBook.setPreferredSize(new Dimension(90, 35));
        panel2.add(reBook);
        reBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputContent = JOptionPane.showInputDialog(
                        f2,
                        "请输入书本编号:"
                );

                boolean b = false;
                int index = 0;
                for (int i = 0; i < lendList.size(); ++i)
                    if (lendList.get(i).get("bookid").toString().equals(inputContent))
                    {
                        b = true;
                        index = i;
                        break;
                    }


                if (b)
                {
                    try
                    {
                        long days = calDays(lendList.get(index).get("lastdate").toString());


                        if (days > 0)
                        {
                            JOptionPane.showMessageDialog(
                                    f2,
                                    String.format("还书成功！你需要缴纳%d元罚款!",days)
                            );

                            readerList.get(0).put("arrears", Integer.parseInt(readerList.get(0).get("arrears").toString()) + days);
                            //更新欠款
                            updateDoc("tb_reader", "arrears", String.format("%d", days), "readerid", lendList.get(index).get("readerid").toString());

                            //更细书库库存
                            updateDoc("tb_book", "count", "1", "bookid", lendList.get(index).get("bookid").toString());

                            //更新读者借书量
                            updateDoc("tb_reader", "lendcnt", "-1", "readerid", lendList.get(index).get("readerid").toString());

                            //删除借书记录
                            delDoc("tb_lend", "lendid", lendList.get(index).get("lendid").toString());


                        }
                        else
                        {
                            JOptionPane.showMessageDialog(
                                    f2,
                                    "还书成功！"
                            );

                            //更细书库库存
                            updateDoc("tb_book", "count", "1", "bookid", lendList.get(index).get("bookid").toString());

                            //更新读者借书量
                            updateDoc("tb_reader", "lendcnt", "-1", "readerid", lendList.get(index).get("readerid").toString());

                            //删除借书记录
                            delDoc("tb_lend", "lendid", lendList.get(index).get("lendid").toString());

                        }


                        lendList.get(index).put("bookid","hthrthrt&%^&%^^#$");

                    }catch (ParseException ee) {
                        ee.printStackTrace();
                    }


                }
                else
                {
                    JOptionPane.showMessageDialog(
                            f2,
                            "你没有借阅这本书！"
                    );
                }
            }
        });

        JButton rePay = new JButton("还款");
        rePay.setFont(new Font(null, Font.PLAIN, 15));
        rePay.setPreferredSize(new Dimension(90, 35));
        panel2.add(rePay);
        rePay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Integer.parseInt(readerList.get(0).get("arrears").toString()) == 0)
                {
                    JOptionPane.showMessageDialog(
                            f2,
                            "恭喜你！没有欠款！",
                            "还款",
                            JOptionPane.PLAIN_MESSAGE

                    );
                }
                else
                {
                    int res = JOptionPane.showConfirmDialog(
                            f2,
                            "确定还款" + readerList.get(0).get("arrears").toString() + "元吗？",
                            "还款",
                            JOptionPane.YES_NO_OPTION

                    );
                    if (res == JOptionPane.YES_OPTION)
                    {

                        updateDoc("tb_reader", "arrears", "-" + readerList.get(0).get("arrears").toString(), "readerid", readerList.get(0).get("readerid").toString());
                        readerList.get(0).put("arrears", "0");
                    }
                }

            }
        });

        JButton logout = new JButton("登出");
        logout.setFont(new Font(null, Font.PLAIN, 15));
        logout.setPreferredSize(new Dimension(90, 35));
        panel2.add(logout);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f2.dispose();
            }
        });



        f2.setContentPane(panel2);

        f2.setVisible(true);


    }
}

class LoginInterface
{
    static boolean check(String type, String ac, String pwd)
    {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/library";

        final String USER = "root";
        final String PASS = "wz123456";

        Connection conn = null;
        Statement stmt = null;

        try
        {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql;

            sql = "SELECT account FROM " + type + " where account = '" + ac  + "' and password = '" + pwd + "'";
            //sql = "select account from tb_admin";

            ResultSet rs = stmt.executeQuery(sql);

            boolean key = rs.next();

            rs.close();
            stmt.close();
            conn.close();


            return key;



        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException se2)
            {}
            try
            {
                if (conn != null) stmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
        return false;

    }


    static void loginInf()
    {
        JFrame lf = new JFrame("图书管理系统");

        lf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        lf.setSize(450, 300);
        lf.setResizable(false);
        lf.setLocationRelativeTo(null);

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);


        lf.setContentPane(panel);


        JLabel acnt = new JLabel("账号:");
        acnt.setFont(new Font(null, Font.PLAIN, 20));
        SpringLayout.Constraints acntCons = layout.getConstraints(acnt);
        acntCons.setX(Spring.constant(125));
        acntCons.setY(Spring.constant(75));
        panel.add(acnt);

        JLabel acntTip = new JLabel();
        acntTip.setFont(new Font(null, Font.PLAIN, 15));
        acntTip.setForeground(Color.RED);
        SpringLayout.Constraints acntTipCons = layout.getConstraints(acntTip);
        acntTipCons.setX(Spring.constant(135));
        acntTipCons.setY(Spring.constant(105));
        panel.add(acntTip);

        JTextField acntTe = new JTextField(10);
        acntTe.setFont(new Font(null, Font.PLAIN, 15));
        SpringLayout.Constraints acntTeCons = layout.getConstraints(acntTe);
        acntTeCons.setX(Spring.constant(175));
        acntTeCons.setY(Spring.constant(75));
        panel.add(acntTe);
        acntTe.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (acntTe.getText().length() >= 16) acntTip.setText("请输入小于16位字符的账号");
                else acntTip.setText("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (acntTe.getText().length() >= 16) acntTip.setText("请输入小于16位字符的账号");
                else acntTip.setText("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (acntTe.getText().length() >= 16) acntTip.setText("请输入小于16位字符的账号");
                else acntTip.setText("");
            }
        });

        JLabel pwd = new JLabel("密码:");
        pwd.setFont(new Font(null, Font.PLAIN, 20));
        SpringLayout.Constraints pwdCons = layout.getConstraints(pwd);
        pwdCons.setX(Spring.constant(125));
        pwdCons.setY(Spring.constant(125));
        panel.add(pwd);

        JLabel pwdTip = new JLabel();
        pwdTip.setFont(new Font(null, Font.PLAIN, 15));
        pwdTip.setForeground(Color.RED);
        SpringLayout.Constraints pwdTipCons = layout.getConstraints(pwdTip);
        pwdTipCons.setX(Spring.constant(135));
        pwdTipCons.setY(Spring.constant(155));
        panel.add(pwdTip);

        JPasswordField pwdPwd = new JPasswordField(10);
        pwdPwd.setFont(new Font(null, Font.PLAIN, 15));
        SpringLayout.Constraints pwdPwdCons = layout.getConstraints(pwdPwd);
        pwdPwdCons.setX(Spring.constant(175));
        pwdPwdCons.setY(Spring.constant(125));
        panel.add(pwdPwd);
        pwdPwd.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (pwdPwd.getPassword().length >= 16) pwdTip.setText("请输入小于16位字符的密码");
                else pwdTip.setText("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (pwdPwd.getPassword().length >= 16) pwdTip.setText("请输入小于16位字符的密码");
                else pwdTip.setText("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (pwdPwd.getPassword().length >= 16) pwdTip.setText("请输入小于16位字符的密码");
                else pwdTip.setText("");
            }
        });


        JButton logi = new JButton("登录");
        logi.setFont(new Font(null, Font.PLAIN, 15));
        logi.setPreferredSize(new Dimension(90, 25));
        SpringLayout.Constraints logiCons = layout.getConstraints(logi);
        logiCons.setX(Spring.constant(110));
        logiCons.setY(Spring.constant(200));
        panel.add(logi);
        logi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = acntTe.getText();
                String pass = new String(pwdPwd.getPassword());
                if (user.length() < 16 && pass.length() < 16 && user.length() > 0 && pass.length() > 0)
                {
                    if (check("tb_admin", user, pass))
                    {
                        lf.dispose();
                        AdminInterface.adminInt();
                    }
                    else if (check("tb_reader", user, pass))
                    {
                        lf.dispose();
                        ReaderInterface.readerInt(user);
                    }
                    else JOptionPane.showMessageDialog(
                            lf,
                            "请输入正确的账号和密码！"
                    );

                }
                else JOptionPane.showMessageDialog(
                        lf,
                        "请输入有效的账号和密码！"
                );
            }
        });


        JButton regi = new JButton("读者注册");
        regi.setFont(new Font(null, Font.PLAIN, 15));
        regi.setPreferredSize(new Dimension(90, 25));
        SpringLayout.Constraints regiCons = layout.getConstraints(regi);
        regiCons.setX(Spring.constant(260));
        regiCons.setY(Spring.constant(200));
        panel.add(regi);
        regi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        lf,
                        "请输入姓名"
                );

                String sex = JOptionPane.showInputDialog(
                        lf,
                        "请输入你的性别,男/女"
                );

                String age = JOptionPane.showInputDialog(
                        lf,
                       "请输入你的年龄"
                );

                String work = JOptionPane.showInputDialog(
                        lf,
                        "请输入你的工作"
                );

                String pwd = JOptionPane.showInputDialog(
                        lf,
                        "请输入你的密码"
                );

                Map<Integer, String> m1 = new HashMap<Integer, String>();

                int id = ReaderInterface.getMax("tb_reader", "readerid") + 1;
                m1.put(0, String.valueOf(id));
                m1.put(1, "'" + name + "'");
                m1.put(2, "'" + sex + "'");
                m1.put(3, age);
                m1.put(4, "'" + work + "'");
                m1.put(5, String.valueOf(0));
                m1.put(6, "'" + String.valueOf(id) + "'");
                m1.put(7, "'" + pwd  + "'");
                m1.put(8, String.valueOf(0));
                m1.put(9, String.valueOf(5));

                ReaderInterface.insertDoc("tb_reader", m1);

                JOptionPane.showMessageDialog(
                        lf,
                        "注册成功!你的账号是:" + id
                );
            }
        });


        lf.setVisible(true);

        //lf.dispose(); //销毁窗口


    }
}


public class Main
{



    public static void main(String[] args)
    {
        LoginInterface.loginInf();
        //AdminInterface.adminInt();
        //ReaderInterface.readerInt("1");







    }
}
