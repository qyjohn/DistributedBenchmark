package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.net.*;

public class SpeedTest extends Thread
{
	String url;
	
	public SpeedTest(String url)
	{
		this.url = url;
	}

	public void run()
	{
		try
		{
                        byte[] buffer = new byte[1024*1024];
                        InputStream in = new URL(url).openStream();
                        int size = 0, length = 0;
                        boolean go = true;
                        while (size != -1)
                        {
                                size = in.read(buffer);
                        }
                        in.close();
		} catch (Exception e)
		{
		}
	}

	public static void main(String[] args)
	{
		try
		{
			// args[0] is the IP address or hostname of the target
			int repeat = 20;
			String url = "http://" + args[0] + "/test.dat";
			int nProc = Runtime.getRuntime().availableProcessors();

			// Warming up test
			byte[] buffer = new byte[1024*1024];
			long time0 = System.currentTimeMillis();
			InputStream in = new URL(url).openStream();
			long size = 0, length = 0;
			boolean go = true;
			while (size != -1)
			{
				size = in.read(buffer);
				length = length + size;
			}
			in.close();
			long time1 = System.currentTimeMillis();
			long t = time1 - time0;
			float speed = length * 1000 / t;
			System.out.println("Data Size: " + length);
			System.out.println("Time: " + t);
			System.out.println("Speed: " + speed + "Bytes per second");

			SpeedTest workers[] = new SpeedTest[nProc];			
			for (int i=0; i<=repeat; i++)
			{
				// Do this N times
				time0 = System.currentTimeMillis();
				for (int j=0; j<nProc; j++)
				{
					workers[j] = new SpeedTest(url);
					workers[j].start();
				}
				for (int j=0; j<nProc; j++)
				{
					workers[j].join();
				}
				time1 = System.currentTimeMillis();
				t = time1 - time0;
				speed = nProc * (1000 * length / t) / 1024 / 1024;
				System.out.println("Speed: " + speed + "MBps per second");
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

