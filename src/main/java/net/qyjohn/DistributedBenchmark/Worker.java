package net.qyjohn.DistributedBenchmark;

import java.io.*;
import com.rabbitmq.client.*;

public class Worker
{
	private Connection connection;
	private Channel channel;
	private String EXCHANGE_NAME = "DistributedBenchmark";

	public Worker()
	{
		try
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, EXCHANGE_NAME, "");

			System.out.println("[*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel)
			{
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException 
				{
					String message = new String(body, "UTF-8");
					Executor executor = new Executor(message);
					executor.start();
//					String message = new String(body, "UTF-8");
//					System.out.println(" [x] Received '" + message + "'");
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
