package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectStorageTest extends Thread
{
	String cloud, bucket, file;
	ConcurrentLinkedQueue<String> queue;
	ObjectStorageHandler storage;

	public ObjectStorageTest(String cloud, String bucket, String file, ConcurrentLinkedQueue<String> queue)
	{
		this.cloud = cloud;
		this.bucket = bucket;
		this.file  = file;
		this.queue = queue;

		// Cloud-specific configurations
		if (cloud.equals("AWS"))
		{
			storage = new AwsS3Handler();
		}
		else if (cloud.equals("Aliyun"))
		{
			storage = new AliyunOssHandler();
		}
		else if (cloud.equals("Huawei"))
		{
			storage = new HuaweiObsHandler();
		}
		else if (cloud.equals("OTC"))
		{
			storage = new OtcObsHandler();
		}
	}

	public void run()
	{
		try
		{
			// Repeat the upload / delete as long as there are jobs in the queue
			while (queue.poll() != null)
			{
				// Create a random object key, upload, then delete
				String key = UUID.randomUUID().toString();
				storage.upload(bucket, key, file);
				storage.delete(bucket, key);
			}
		} catch (Exception e)
		{
		}
	}

	public static void main(String[] args)
	{
		try
		{
			String cloud = args[0];
			String bucket = args[1];
			String file = args[2];

			// Create a job queue
			int total = 3200;
			if (args.length == 4)
			{
				try
				{
					total = Integer.parseInt(args[3]);
				} catch (Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
			System.out.println(total);
			ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
			for (int j=0; j<total; j++)
			{
				queue.add("job");
			}

			// Create nProc threads to work on the queue
			int nProc = Runtime.getRuntime().availableProcessors();
			ObjectStorageTest workers[] = new ObjectStorageTest[nProc];
			long time0 = System.currentTimeMillis();
			for (int j=0; j<nProc; j++)
			{
				workers[j] = new ObjectStorageTest(cloud, bucket, file, queue);
				workers[j].start();
			}
			for (int j=0; j<nProc; j++)
			{
				workers[j].join();
			}
			long time1 = System.currentTimeMillis();

			// Calculate total time
			long t = time1 - time0;
			System.out.println("Total time: " + t + " milliseconds");
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

