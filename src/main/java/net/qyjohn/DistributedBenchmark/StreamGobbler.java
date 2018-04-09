package net.qyjohn.DistributedBenchmark;

import java.util.*;
import java.io.*;

public class StreamGobbler extends Thread
{
	InputStream is;
	String output = "";

	public StreamGobbler(InputStream is)
	{
		this.is = is;
	}

	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			while ( (line = br.readLine()) != null)
			{
				output = output + "\n" + line;
			}
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public String getOutput()
	{
		return output;
	}
}
