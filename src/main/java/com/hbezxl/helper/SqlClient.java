package com.hbezxl.helper;


import java.sql.*;


public class SqlClient
{

    private   String driverName  ;
    private   String connectionString  ;

    private String userName;
    private String password;

    public SqlClient() {
    }

    public SqlClient(String driverName, String connectionString) {
        this.driverName = driverName;
        this.connectionString = connectionString;
        this.userName = "";
        this.password = "";
    }

    public SqlClient(String driverName, String connectionString, String userName, String password) {
        this.driverName = driverName;
        this.connectionString = connectionString;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getConnectionString() {
        return connectionString;
    }


    public  Connection GetConnection()
    {
        Connection connection = null;
        try
        {
            Class.forName(driverName);
            if(this.userName =="" && this.password=="")
            {
                connection =  DriverManager.getConnection(this.connectionString);
            }
            else
            {
                connection =  DriverManager.getConnection(this.connectionString,this.userName,this.password);
            }
        }
        catch (Exception Exc)
        {
            TraceHelper.TraceInfo("GetConnection " + CommonHelper.GetExceptionStackTrace(Exc));
        }

        return connection;
    }


    public  int ExecSql(Connection I_Connection, String I_CmdText)
    {
        int i;
        try
        {
            Statement stmt = I_Connection.createStatement();
            i = stmt.executeUpdate(I_CmdText);

        } catch (SQLException ex)
        {

            i = -1;
        }

        return i;
    }

    public  Object ExecScalar(Connection I_Connection, String I_CmdText)
    {
        ResultSet rs = ExecResultSet(I_Connection, I_CmdText);
        Object obj = buildScalar(rs);

        return obj;
    }

    public   ResultSet ExecResultSet(Connection I_Connection, String I_CmdText)
    {
        ResultSet R_Result = null;

        try {
            Statement stmt  = I_Connection.createStatement();
            R_Result =  stmt.executeQuery(I_CmdText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return R_Result;

    }

    public   Object buildScalar(ResultSet I_ResultSet)
    {

        Object obj = null;
        try
        {
            if (I_ResultSet.next())
            {
                obj = I_ResultSet.getObject(1);
            }
        } catch (SQLException ex)
        {

        }
        return obj;
    }


    public   void close(Object I_Obj)
    {
        if (I_Obj == null)
        {
            return;
        }
        try
        {
            if (I_Obj instanceof Statement)
            {
                ((Statement) I_Obj).close();
            } else if (I_Obj instanceof PreparedStatement)
            {
                ((PreparedStatement) I_Obj).close();
            } else if (I_Obj instanceof ResultSet)
            {
                ((ResultSet) I_Obj).close();
            } else if (I_Obj instanceof Connection)
            {
                ((Connection) I_Obj).close();
            }
        } catch (SQLException ex)
        {

        }
    }

}
