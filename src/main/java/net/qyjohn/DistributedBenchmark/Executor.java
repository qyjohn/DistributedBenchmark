package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Executor extends Thread
{
	String testId, testName, node, path, command, outMsg, errMsg;
	String jdbcUrl;
	Timestamp t0, t1;
	int exitVal, duration;

	public Executor(String testId, String testName, String node, String path, String command)
	{
		this.testId = testId;
		this.testName = testName;
		this.node = node;
		this.path = path;
		this.command = command;

		try
		{
			// Getting database properties from db.properties
			Properties prop = new Properties();
			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
			String dbHostname = prop.getProperty("dbHostname");
			String dbUsername = prop.getProperty("dbUsername");
			String dbPassword = prop.getProperty("dbPassword");
			String dbDatabase = prop.getProperty("dbDatabase");

			// Load the MySQL JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			jdbcUrl = "jdbc:mysql://" + dbHostname + "/" + dbDatabase + "?user=" + dbUsername + "&password=" + dbPassword;
		} catch (Exception e)
		{
		}
	}

	public void run()
	{
		try
		{
			System.out.println("    [-] Starting new executor");
			System.out.println("        " + command);
			execute();
			System.out.println("        Exit Value: " + exitVal);
			System.out.println("    [-] Shutting down executor");
		} catch (Exception e)
		{
		}
	}

	public void execute()
	{
		try
		{
			t0 = new Timestamp(new java.util.Date().getTime());
			long t00 = System.currentTimeMillis();

			String env_path = "PATH=" + path;
			String[] env = {env_path};
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command, env);
			StreamGobbler stderr = new StreamGobbler(proc.getErrorStream());
			StreamGobbler stdout = new StreamGobbler(proc.getInputStream());
			stderr.start();
			stdout.start();
			exitVal = proc.waitFor();
			stderr.join();
			stdout.join();

			t1 = new Timestamp(new java.util.Date().getTime());
			long t01 = System.currentTimeMillis();
			duration = (int) (t01 - t00);

			outMsg = stdout.getOutput();
			errMsg = stderr.getOutput();
			log();
		} catch (Exception e)
		{
			exitVal = -100;
			outMsg = "";
			errMsg = e.getMessage();
			log();
		}
	}

	/**
	 *
	 * Use JDBC connection to log the execution details.
	 *
	 */

	public void log()
	{
		try
		{
			// JDBC connection
			Connection conn = DriverManager.getConnection(jdbcUrl);
			String sql = "INSERT INTO logs (node, command, stdout, stderr, start_time, end_time, exit_value, duration, test_id, test_name) VALUES "
				+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, node);
			preparedStatement.setString(2, command);
			preparedStatement.setString(3, outMsg);
			preparedStatement.setString(4, errMsg);
			preparedStatement.setTimestamp(5, t0);
			preparedStatement.setTimestamp(6, t1);
			preparedStatement.setInt(7, exitVal);
			preparedStatement.setInt(8, duration);
			preparedStatement.setString(9, testId);
			preparedStatement.setString(10, testName);
			preparedStatement.executeUpdate();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}		
	}
}
