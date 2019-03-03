package com.xxin.mails.utils;


import com.xxin.mails.conf.Constants;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    private final static String drive =Constants.JDBC_DRIVE;
    private final static String url=Constants.DB_URL;
    private final static String username =Constants.DB_USER;
    private final static String password=Constants.DB_PASSWORD;
    private static Connection connection =null;
    static{
        try {
            Class.forName(drive);
        }catch (Exception e){
            System.out.println("error at DBUtils.static 26:"+e.getMessage());
        }
    }
    public static Connection getConnection() throws SQLException {
        if (connection==null ||connection.isClosed()){
            connection = DriverManager.getConnection(url,username,password);
        }
        return connection;
    }
    public static void closeConnection(){
        try {
            if (connection!=null||!connection.isClosed())
            connection.close();
        } catch (SQLException e) {
            System.out.println("error at DBUtils.closeConnection 40:"+e.getMessage());
        }
    }
    public static List<Object> queryAll(Class obj){
        List<Object> list = new ArrayList<>();
        try {
            String table = obj.getName().substring(obj.getName().lastIndexOf(".")+1,obj.getName().length());
            String sql = "select * from "+table;
            connection  = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            Method[]methods = obj.getDeclaredMethods();
            Field[] fields = obj.getDeclaredFields();
            String[] fieldName = new String[fields.length];
            for (int i=0;i<fields.length;i++){
                fieldName[i] = fields[i].getName();
            }
            while (resultSet.next()){
                Object o = obj.newInstance();
                list.add(setValue(fieldName, methods,o ,resultSet));
            }
            return list;
        } catch (Exception e) {
            closeConnection();
            e.printStackTrace();
        }
        return null;
    }
    public static <T> int  save(T t){
        StringBuilder sql = new StringBuilder();
        try {
            Class cls = t.getClass();
            String table = t.getClass().getSimpleName();
            sql.append("replace ").append(table).append(" values(");
            for (int i=0;i<cls.getDeclaredFields().length;i++){
                if (i==0){
                    sql.append("?");
                }else{
                    sql.append(",?");
                }
            }
            sql.append(")");
            connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from "+table+" limit 1");
            ResultSetMetaData metaData = resultSet.getMetaData();
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            Method[] methods = cls.getDeclaredMethods();
            for (int i=0;i<metaData.getColumnCount();i++){
                for (Method method:methods){
                    if (method.getName().toUpperCase().contains("GET"+(metaData.getColumnName(i+1).toUpperCase()))){
                       Object v =  method.invoke(t)==null? "":method.invoke(t);
                       if (metaData.getColumnTypeName(i+1).contains("BLOB")){
                           System.out.println("存储blob数据");
                           SerialBlob b = (SerialBlob) v;
//                           String blobString = new String(b.getBytes(1, (int) b.length()));
//                           ByteArrayInputStream stream = new ByteArrayInputStream(blobString.getBytes());
                           preparedStatement.setBlob(i+1,b.getBinaryStream());
                       }else {
                           preparedStatement.setString(i+1,v.toString());
                       }
                        break;
                    }
                }
            }
            return  preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static Object query(String[] cols, Object key, Object val, Class obj){
        Object o = null;
        try {
            o = obj.newInstance();
            String table = obj.getName().substring(obj.getName().lastIndexOf(".")+1,obj.getName().length());
            String sql="";
            Object[]vals=null;
            if (key.getClass().isArray()){
                int length = Array.getLength(key);
                Object[] os = new Object[length];
                vals=new Object[length];
                for (int i = 0; i < os.length; i++) {
                    os[i] = Array.get(key, i);
                    vals[i]= Array.get(val,i);
                }
                sql = makeSql(sql, cols)+table+" where ";
                for (int i=0;i<os.length;i++){
                    if (i+1==os.length){
                        sql +=  os[i] + " = ?";
                    }else{
                        sql +=  os[i] + " = ? and ";
                    }
                }
            }else{
                sql = makeSql(sql, cols)+table+" where "+ key+ " = ?";
            }
            connection  = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            if (key.getClass().isArray()){
                for (int i=0;i<vals.length;i++){
                    System.out.println(vals[i].toString());
                    statement.setString(i+1,vals[i].toString());
                }
            }else{
                statement.setString(1,val.toString());
            }
            System.out.println(sql);
           statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            Method[]methods = obj.getDeclaredMethods();
            Field[] fields = obj.getDeclaredFields();
            String[] fieldName = new String[fields.length];
            if (cols!=null){
                fieldName = cols;
            }else {
                for (int i=0;i<fields.length;i++){
                    fieldName[i] = fields[i].getName();
                }
            }
            resultSet.last();
            List<Object> list = new ArrayList<>();
            if (resultSet.getRow()>1){
                resultSet.beforeFirst();
                while (resultSet.next()){
                    o = obj.newInstance();
                    o = setValue(fieldName, methods,o ,resultSet);
                    list.add(o);
                }
            }else if (resultSet.getRow()==1){
                resultSet.first();
                o = setValue(fieldName, methods,o ,resultSet);
                list.add(o);
            }else {
                return null;
            }
            return list;
        } catch (Exception e) {
            System.out.println("error at DBUtils.query 180:"+e.getMessage());
            closeConnection();
        }
        return null;
    }
    private static String makeSql(String sql, String[] cols) {
        sql = "select ";
        if (cols!=null){
            for (int i=0;i<cols.length;i++){
                if (i+1==cols.length){
                    sql += cols[i];
                }else {
                    sql += cols[i]+",";
                }
            }
            sql += " from ";
        }else {
            sql = "select * from ";
        }
        return sql;
    }
    public static Object doSql(String sql, Class obj, Object...val){
        Object o = null;
        List<Object> objectList = new ArrayList<>();
        try {
            o = obj.newInstance();
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < val.length; i++) {
                preparedStatement.setString(i + 1, val[i].toString());
            }
            if (sql.toUpperCase().contains("UPDATE")) {
                return preparedStatement.executeUpdate();
            } else {
                preparedStatement.executeQuery();
                ResultSet resultSet = preparedStatement.getResultSet();
                Method[] methods = obj.getDeclaredMethods();
                Field[] fields = obj.getDeclaredFields();
                String[] fieldName = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldName[i] = fields[i].getName();
                 }
                 while (resultSet != null && resultSet.next()) {
                     o = setValue(fieldName, methods, o, resultSet);
                     objectList.add(o);
                }
             }
        }catch (Exception e){
            e.printStackTrace();
        }
        return objectList;
    }

    public static int deleteBy(String[]key, String[] val, Class obj){
        Object o = null;
        try {
            o = obj.newInstance();
            String table = obj.getName().substring(obj.getName().lastIndexOf(".")+1,obj.getName().length());
            String sql = "delete from "+table+" where ";
            for (int i=0;i<key.length;i++){
                if (i+1==key.length){
                    sql +=  key[i] + " = ?";
                }else{
                    sql +=  key[i] + " = ? and ";
                }
            }
            connection  = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i=0;i<val.length;i++){
                statement.setString(i+1,val[i] );
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            closeConnection();
            e.printStackTrace();
        }
        return 0;
    }
    private static Object setValue(String[] fields, Method[] methods, Object o, ResultSet resultSet) throws SQLException, InvocationTargetException, IllegalAccessException, IOException {
        for (String field:fields){
            for (Method method:methods){
                if (method.getName().toUpperCase().contains("SET"+field.toUpperCase())){
                    switch (method.getParameterTypes()[0].getName()){
                        case "int":
                            method.invoke(o,resultSet.getInt(field));break;
                        case "java.lang.String":
                            method.invoke(o,resultSet.getString(field));break;
                        case "java.sql.Date":
                            method.invoke(o,resultSet.getDate(field));break;
                        case "double":
                            method.invoke(o,resultSet.getDouble(field));break;
                        case "long":
                            method.invoke(o,resultSet.getLong(field));break;
                        case "javax.sql.rowset.serial.SerialBlob":
                            System.out.println("获取blob");
                            SerialBlob b = new SerialBlob(resultSet.getBlob(field));
                            method.invoke(o, b);
                            break;
                        case "java.sql.Timestamp":
                            method.invoke(o,resultSet.getTimestamp(field));break;
                    }
                    break;
                }
            }
        }
        return o;
    }
    public static int count(Object key, Object val, Class obj){
        Object o = null;
        try {
            o = obj.newInstance();
            String table = obj.getName().substring(obj.getName().lastIndexOf(".") + 1, obj.getName().length());
            String sql = "select count(*) cnt from "+table+" where ";
            if (key.getClass().isArray()){
                Object[] keys = Object2Array(key);
                for (int i=0;i<keys.length;i++){
                    if (i+1==keys.length){
                        sql += keys[i] + " = ?";
                    }else {
                        sql += keys[i] + " = ? and";
                    }
                }
            }else if (key!=null){
                sql += key +" = ?";
            }else {
                sql = "select count(*) cnt from "+table;
            }
            System.out.println(sql);
            connection  = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            if (key.getClass().isArray()){
                Object[] vals = Object2Array(val);
                for (int i=0;i<vals.length;i++){
                    statement.setString(i+1, vals[i].toString());
                }
            }else if (key!=null){
                statement.setString(1, val.toString());
            }
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            resultSet.first();
            return resultSet.getInt("cnt");
        }catch (Exception e){
           closeConnection();
            return -1;
        }
    }
    private static Object[] Object2Array(Object obj){
        Object[]vals=null;
        int length = Array.getLength(obj);
        vals=new Object[length];
        for (int i = 0; i < vals.length; i++) {
            vals[i]= Array.get(obj,i);
        }
        return vals;
    }
}
