package com.renfei.utils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    public static Connection getConnection(){
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/jdbc.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String driverClass = properties.getProperty("driverClass");

        //加载驱动
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = null;
        //获取连接
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    public static boolean checkUser(String userId, String password){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean res = false;

        try{
            conn = JDBCUtils.getConnection();
            String sql = "select userId, password from user where userId=? and password=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,userId);
            ps.setString(2,password);


            rs = ps.executeQuery();
            if(rs.next()){
                System.out.println("yeahhhhh!");
                res = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return res;
    }
}
