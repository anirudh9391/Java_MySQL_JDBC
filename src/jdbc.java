

import java.sql.*;
import java.util.StringTokenizer;
import java.io.*;

public class jdbc

{
    public static void main(String args[])
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/homework4","root","");
            Statement stmt = con.createStatement();

            //employee(eid:integer, name:string, salary:integer)
            //worksfor(eid:integer, mid:integer)

            String sql_create1 = "CREATE TABLE employee " +
                    "(eid INTEGER not NULL, " +
                    " name VARCHAR(255), " +
                    " salary INTEGER, " +
                    " PRIMARY KEY ( eid ))";

            String sql_create2 = "CREATE TABLE worksfor " +
                    "(eid INTEGER not NULL, " +
                    " mid INTEGER not NULL, " +
                    " FOREIGN KEY ( eid ) "+
                    " REFERENCES employee (eid) "+
                    " ON DELETE CASCADE) ";

            //stmt.executeUpdate(sql_create1);
            //stmt.executeUpdate(sql_create2);
            String filename = "input.txt";
            FileReader fileReader = new FileReader(filename);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            String arr[] = new String[10];
            String choice="";

            ops obj = new ops();
            while((line  = bufferedReader.readLine()) != null)
            {
                int k=0;
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreTokens())
                {
                    arr[k++] = st.nextToken();
                }

                choice = arr[0];

                switch(choice)
                {
                    case "1": obj.delete(arr[1] , stmt);break;
                    case "2": obj.insert(arr , stmt , k-1);break;
                    case "3": obj.avg(stmt);break;
                    case "4": obj.all(stmt , Integer.parseInt(arr[1]));break;
                    case "5": obj.avg_mgr(Integer.parseInt(arr[1]) , stmt);break;
                    case "6": obj.mgr_count(stmt);break;
                }

                //System.out.println(line);
            }

            ResultSet sel = stmt.executeQuery("SELECT * from employee");
            while (sel.next())
                System.out.println(sel.getInt("eid")+"\t\t"+ sel.getString("name")+"\t\t"+sel.getInt("salary"));
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class ops
{
    ResultSet rs1 = null;
    void delete(String eid , Statement stmt)
    {
        int eid1 = Integer.parseInt(eid);
        String del = "DELETE FROM employee WHERE eid="+eid1;
        System.out.println(del);
        try
        {
            stmt.executeUpdate(del);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    void insert(String arr[] , Statement stmt , int k)
    {
        System.out.println("choice 2 is detected, comes to function");

        for(int i=0;i<=k;i++)
        {
            System.out.println(i+"."+arr[i]);
        }


        int eid1 = Integer.parseInt(arr[1]);
        String name1 = arr[2];
        int salary1 = Integer.parseInt(arr[3]);
        int mid[] = new int[10];

        String ins = "INSERT INTO employee(eid,name,salary) VALUES("+eid1+",'"+name1+"',"+salary1+")";


        System.out.println(ins);
        try
        {
            stmt.executeUpdate(ins);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(k>3	&&	Integer.parseInt(arr[4])!=0)
        {
            for(int i=4;i<=k;i++)
            {
                mid[i-4] = Integer.parseInt(arr[i]);
                System.out.println(i+"."+mid[i-4]);
            }


            for(int i=0;i<k-3;i++)
            {
                String ins2 = "INSERT INTO worksfor(eid,mid) VALUES("+eid1+","+mid[i]+")";

                try
                {
                    stmt.executeUpdate(ins2);
                }
                catch (SQLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    void avg(Statement stmt)
    {
        String aver = "SELECT avg(salary) from employee";

        try
        {
            ResultSet rs = stmt.executeQuery(aver);
            if(rs.next())
                System.out.println("Average is : "+rs.getFloat(1));
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void avg_mgr(int mid, Statement stmt)
    {
        String aver = "SELECT avg(salary) from employee natural join worksfor WHERE mid="+mid+" group by mid";

        try
        {
            ResultSet rs = stmt.executeQuery(aver);
            if(rs.next())
            {
                System.out.println("Average is : "+rs.getFloat(1));
                System.out.println("Average is aaa: "+Math.round(rs.getInt(1)));
            }
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void mgr_count(Statement stmt)
    {
        String sel = "SELECT name from employee natural join worksfor group by eid having count(*)>1";

        try
        {
            ResultSet rs = stmt.executeQuery(sel);
            System.out.println("Employee name with atleast 2 managers is : ");
            int c=0;
            while(rs.next())
            {
                c++;
                System.out.println(c+"."+rs.getString(1));

            }
            if(c == 0)
            {
                System.out.println("No Employee with more than one manager ");

            }


        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    void all(Statement stmt , int mid) throws SQLException
    {
        int arr[] = new int[1000];
        String arr1[] = new String[1000];

        int temp = 0;
        String temp1 = null;


        String sel = "SELECT eid,name from employee natural join worksfor WHERE mid="+mid;
        //System.out.println("Rec call : "+sel);
        ResultSet rs = stmt.executeQuery(sel);
        int k=0;
        while(rs.next())
        {
            temp = rs.getInt("eid");
            arr[k] = temp;
            arr1[k] = (rs.getString("name"));
            //System.out.println(k+"."+arr1[k]);
            k++;
            //System.out.println("Int is : "+temp+" Name is "+this.rs1.getString("name"));
            //all(stmt,temp);
        }
        if(k>0)
        {
            for(int i=0;i<=k;i++)
            {
                //System.out.println("Int is : "+rs.getInt("eid")+" Name is "+rs.getString("name"));
                if(arr[i]!=0 &&	arr1[i]!=null)
                    System.out.println(arr[i] + "\t"+ arr1[i]);
                all(stmt , arr[i]);
            }
        }
        else
        {
            //System.out.println("returning");
            return;
        }
//			return null;
    }
}
