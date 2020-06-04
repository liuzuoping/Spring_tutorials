package cn.itxiaoliu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class jdbcTest {
    public static void main(String [] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection("jdbc:mysql:///address", "xiaoliu", "960614abcd");
        PreparedStatement pstm = conn.prepareStatement("select * from account");
        ResultSet rs = pstm.executeQuery();
        while (rs.next()){
            System.out.println(rs.getString("name")+"---"+rs.getString("money"));
        }
        rs.close();
        pstm.close();
        conn.close();
    }
}
