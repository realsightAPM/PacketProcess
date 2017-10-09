from kafka import KafkaConsumer

consumer = KafkaConsumer('netpacket', bootstrap_servers='10.4.53.25:9092')
for msg in consumer:
    print msg