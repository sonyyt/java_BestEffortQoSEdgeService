package com;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class executionStatus{
	static String url = "jdbc:sqlite:/data/executionStatus/executionStatus.db";
	
	List<MsInfo> msInfo = new ArrayList<MsInfo>();
	
	public executionStatus() {
		initDatabase();
		String sql = "SELECT id, cost, latency, reliability, url FROM Microservice";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);

                while (rs.next()) {
                	msInfo.add(new MsInfo(rs.getString("id"),rs.getDouble("cost"),rs.getDouble("latency"), rs.getDouble("reliability"), rs.getString("url")));
               }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public void updateQoS() {
		updateMS();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		selectAll();
		msInfo = new ArrayList<MsInfo>();
		String sql = "SELECT id, cost, latency, reliability, url FROM Microservice";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);

                while (rs.next()) {
                	msInfo.add(new MsInfo(rs.getString("id"),rs.getDouble("cost"),rs.getDouble("latency"), rs.getDouble("reliability"), rs.getString("url")));
               }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		
	}
	
	
	
	public MsInfo getMSINFO(String msID) {
		for(MsInfo q:msInfo) {
			if(q.Microservice.equals(msID)) {
				return q;
			}
		}
		return null;
	}
	
	public void insertMSExecutionStatus(String msID, double latency, boolean status) {
        String sql = "INSERT INTO MicroserviceExecution(microservice, latency, status) VALUES(?,?,?)";
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, msID);
            pstmt.setDouble(2, latency);
            pstmt.setBoolean(3, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		
	}
    public static void createNewDatabase(String fileName) {
    	
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void createNewTable() {
        // SQLite connection string
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE Microservice (\n"
                + "	id text PRIMARY KEY,\n"
                + "	latency REAL NOT NULL,\n"
                + "	cost REAL NOT NULL,\n"
                + "	reliability REAL NOT NULL, \n"
                + "	url text NOT NULL\n"
                + ");";
        
        String sql_status = "CREATE TABLE MicroserviceExecution (\n"
                + "	id text PRIMARY KEY,\n"
                + "	microservice text NOT NULL,\n"
                + "	latency REAL NOT NULL,\n"
                + "	status BOOLEAN NOT NULL\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            stmt.execute(sql_status);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private Connection connect() {
        // SQLite connection string
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
 
    /**
     * Insert a new row into the warehouses table
     *
     * @param name
     * @param capacity
     */
    public void insertMS(String id, Double cost, Double latency, Double reliability, String url) {
        String sql = "INSERT INTO Microservice(id, cost, latency, reliability, url) VALUES(?,?,?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setDouble(2, cost);
            pstmt.setDouble(3, latency);
            pstmt.setDouble(4, reliability);
            pstmt.setString(5, url);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    public void updateMS() {
    	try (Connection conn = this.connect();
    			Statement stmt  = conn.createStatement();
    			ResultSet rs    = stmt.executeQuery("SELECT id FROM Microservice")){
        	 	while (rs.next()){
        	 		String msid = rs.getString("id");
        	 		Statement stmt_status  = conn.createStatement();
                    ResultSet re_status    = stmt_status.executeQuery("SELECT avg(latency),avg(status) from MicroserviceExecution where microservice = \""+msid+"\"");
                    re_status.next();
                    double l = re_status.getDouble(1);
                    double r = re_status.getDouble(2)*100;
                    PreparedStatement pstmt = conn.prepareStatement("UPDATE Microservice SET latency=?, reliability=? where id=?");
                    pstmt.setDouble(1, l);
                    pstmt.setDouble(2, r);
                    pstmt.setString(3, msid);
                    pstmt.executeUpdate();
        	 	}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * select all rows in the warehouses table
     */
    public void selectAll(){
        String sql = "SELECT id, cost, latency, reliability,url FROM Microservice";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("id") +  "\t" + 
                                   rs.getDouble("cost") + "\t" +
                                   rs.getDouble("latency") + "\t" +
                                   rs.getDouble("reliability") + "\t" + 
                				   rs.getString("url") + "\t");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
 
    public void initDatabase() {
    	createNewDatabase("");
    	createNewTable();
        this.insertMS("readTempSensor", 100.0, 100.0, 90.0, "http://192.168.0.112/readTempSensor.php");
        this.insertMS("estTemp", 100.0, 120.0, 95.0, "http://192.168.0.112/estTemp.php");
        this.insertMS("getGPS", 100.0, 100.0, 90.0, "http://192.168.0.112/getGPS.php");
        this.insertMS("readLocTemp", 100.0, 200.0, 100.0, "http://192.168.0.112/readLocTemp.php");
        //this.selectAll();
    }
    
    
    public static void main(String[] args) {
    	long startTime = System.nanoTime();
    	
    	executionStatus test = new executionStatus();
    	//test.initDatabase();
    	test.updateMS();
    	test.selectAll();
		long endTime = System.nanoTime();
		
		long timeElapsed = endTime - startTime;

		System.out.println("Execution time: milliseconds: " + timeElapsed / 1000000);
    }
}

class MsInfo{
	String Microservice;
	double cost;
	double latency;
	double reliability;
	String url;
	
	public MsInfo(String m, double c, double l, double r, String url) {
		this.Microservice = m;
		this.cost = c;
		this.latency = l;
		this.reliability = r;
		this.url = url;
	}
}

class PlanQoS{
	String executionPlan;
	double cost;
	double latency;
	double reliability;
	
	public PlanQoS(String plan, double c, double l, double r) {
		this.executionPlan = plan;
		this.cost = c;
		this.latency = l;
		this.reliability = r;
	}
}