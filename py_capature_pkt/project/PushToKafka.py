# encoding: utf-8
# 读写kafka
#管理kafka客户端
import json
from kafka import KafkaProducer
class KafkaUtil:
    producer = KafkaProducer(bootstrap_servers='localhost:9092')
    def __init__(self,serverAddr):
        print "serverAddr " + serverAddr
        
    def pushData(self,topic,data):
        print "topic "+topic
        set_p = self.producer.partitions_for("netpacket")
        print str(set_p)
        future = self.producer.send("netpacket",data)
        print "send data"
        result = future.get(timeout=10)
        print "send success"
        print result









