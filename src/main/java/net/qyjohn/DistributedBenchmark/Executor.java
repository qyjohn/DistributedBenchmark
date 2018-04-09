package net.qyjohn.DistributedBenchmark;

import java.io.*;

public class Executor extends Thread
{
	String job;

	public Executor(String job)
	{
		this.job = job;
	}

	public void run()
	{
		try
		{
			System.out.println("    [-] Starting new executor");
			System.out.println(job);
			System.out.println("    [-] Shutting down executor");
		} catch (Exception e)
		{
		}
	}
}
