package org.eclipse.utm;
import java.sql.*;
import javax.swing.*;

public class sqliteConnection {
	Connection conn = null;
	public static Connection dbConnector(){
		try{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:/Users/junqianfeng/eclipse/workspace/UTMTraceTable.sqlite");
			JOptionPane.showMessageDialog(null, "Connect Successful.");
			return conn;
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
	
	
}
