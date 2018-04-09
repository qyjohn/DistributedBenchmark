package net.qyjohn.DistributedBenchmark;

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
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
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
		Submit submit = new Submit();
		submit.send("Test");
		submit.close();
	}
}
