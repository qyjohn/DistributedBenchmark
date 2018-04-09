package net.qyjohn.DistributedBenchmark;

import java.io.*;
import java.net.*;
import java.util.*;
import com.rabbitmq.client.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Worker
{
	private Connection connection;
	private Channel channel;
	private String EXCHANGE_NAME = "DistributedBenchmark";

	public Worker()
	{
		try
		{
			// Getting the IP address of the worker node
			final String nodeName = InetAddress.getLocalHost().getHostAddress();

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

			channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, EXCHANGE_NAME, "");

			System.out.println("[*] Worker node: " + nodeName);
			System.out.println("[*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel)
			{
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException 
				{
					String message = new String(body, "UTF-8");

					try
					{
						JSONParser parser = new JSONParser();
						JSONObject jsonObj = (JSONObject) parser.parse(message);
						JSONArray records = (JSONArray) jsonObj.get("jobs");
						Iterator i = records.iterator();
						while (i.hasNext())
						{
							JSONObject record = (JSONObject) i.next();
							String node = (String) record.get("node");
							String command = (String) record.get("command");

							// Is this command for me?
							if (node.equals(nodeName) || node.equals("*"))
							{
								Executor executor = new Executor(nodeName, command);
								executor.start();
								executor.join();
							}
						}
					} catch (Exception e)
					{
					}
				}
			};
			channel.basicConsume(queueName, true, consumer);
		} catch (Exception e)
		{
		}
	}


	public static void main(String[] args)
	{
		Worker worker = new Worker();
	}
}
