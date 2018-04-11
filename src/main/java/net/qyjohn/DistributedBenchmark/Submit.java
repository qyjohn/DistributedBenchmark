package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.rabbitmq.client.*;

public class Submit
{
	private Connection connection;
	private Channel channel;
	private String EXCHANGE_NAME = "DistributedBenchmark";

	public Submit()
	{
		try
		{
			// Getting database properties from db.properties
			Properties prop = new Properties();
			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
			String mqHostname = prop.getProperty("mqHostname");

			// Creating a connection to MQ
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(mqHostname);
			connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (Exception e)
		{
		}
	}

	public void send(String message)
	{
		try
		{
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
		} catch (Exception e)
		{
		}
	}

	public void close()
	{
		try
		{
			channel.close();
			connection.close();
		} catch (Exception e)
		{
		}
	}

	public static void main(String[] args)
	{
		try
		{
			Submit submit = new Submit();
			String uuid = UUID.randomUUID().toString();
			System.out.println("Test ID: " + uuid);
			uuid = "{\n    \"testId\" : \"" + uuid + "\",";
			String contents = new String(Files.readAllBytes(Paths.get(args[0]))).trim();
			contents = uuid + contents.substring(1);
			submit.send(contents);
			submit.close();
		} catch (Exception e)
		{
		}
	}
}
