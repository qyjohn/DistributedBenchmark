package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.util.*;
import com.aliyun.oss.*;
import com.aliyun.oss.model.*;

public class AliyunOssHandler implements ObjectStorageHandler
{
	public OSSClient client;

	public AliyunOssHandler()
	{
		try
		{
			// Getting runtime configuration from config.properties
			Properties prop = new Properties();
			InputStream input = new FileInputStream("object_storage.credentials");
			prop.load(input);
			String ossEndpoint = prop.getProperty("aliyunOssEndpoint");
			String ossAccessKeyId = prop.getProperty("aliyunOssAccessKeyId");
			String ossSecretKeyId = prop.getProperty("aliyunOssSecretKeyId");

			// OSSClient
			client = new OSSClient(ossEndpoint, ossAccessKeyId, ossSecretKeyId);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void upload(String bucket, String key, String fileFullPath)
	{
		try
		{
			File file = new File(fileFullPath);
			client.putObject(bucket, key, file);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void download(String bucket, String key, String fileFullPath)
	{
		try
		{
			File file = new File(fileFullPath);
			GetObjectRequest request = new GetObjectRequest(bucket, key);
			client.getObject(request, file);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void delete(String bucket, String key)
	{
		try
		{
			client.deleteObject(bucket, key);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
	}

	public void deletePrefix(String bucket, String prefix)
	{
		try
		{
			for (OSSObjectSummary file : client.listObjects(bucket, prefix).getObjectSummaries())
			{
				client.deleteObject(bucket, file.getKey());
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}				
	}

}
