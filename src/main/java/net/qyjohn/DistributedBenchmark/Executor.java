package net.qyjohn.DistributedBenchmark;

import java.io.*;

public class Executor extends Thread
{
	String node, command, outMsg, errMsg;
	int exitVal;

	public Executor(String node, String command)
	{
		this.node = node;
		this.command = command;
	}

	public void run()
	{
		try
		{
			System.out.println("    [-] Starting new executor");
			System.out.println("        " + command);
			execute();
			System.out.println("    [-] Shutting down executor");
		} catch (Exception e)
		{
		}
	}

	public void execute()
	{
		try
		{
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command);
			StreamGobbler stderr = new StreamGobbler(proc.getErrorStream());
			StreamGobbler stdout = new StreamGobbler(proc.getInputStream());
			stderr.start();
			stdout.start();

			exitVal = proc.waitFor();
			outMsg = stdout.getOutput();
			errMsg = stderr.getOutput();
			System.out.println("        Exit Value: " + exitVal);

			log();
		} catch (Exception e)
		{
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
		} catch (Exception e)
		{
		}		
	}
}
