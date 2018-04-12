package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.util.*;
import com.obs.services.*;
import com.obs.services.model.*;

public class HuaweiObsHandler implements ObjectStorageHandler
{
	public ObsClient client;

	public HuaweiObsHandler()
	{
		try
		{
			// Getting runtime configuration from config.properties
			Properties prop = new Properties();
			InputStream input = new FileInputStream("object_storage.credentials");
			prop.load(input);
			String obsEndpoint = prop.getProperty("huaweiObsEndpoint");
			String obsAccessKeyId = prop.getProperty("huaweiObsAccessKeyId");
			String obsSecretKeyId = prop.getProperty("huaweiObsSecretKeyId");

			// OSSClient
			client = new ObsClient(obsAccessKeyId, obsSecretKeyId, obsEndpoint);
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
			System.out.println("Uploading " + bucket + "/" + key);
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
			S3Object object = client.getObject(bucket, key);
			InputStream in = object.getObjectContent();
			OutputStream out = new FileOutputStream(new File(fileFullPath));
			
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1) 
			{
				out.write(bytes, 0, read);
			}
			out.close();
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
			ListObjectsRequest request = new ListObjectsRequest(bucket);
			request.setPrefix(prefix);
			for (S3Object object : client.listObjects(request).getObjectSummaries())
			{
				client.deleteObject(bucket, object.getObjectKey());
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}				
	}

}
