# 读写kafka
#管理kafka客户端
import json
from kafka import kafkaProducer
class KafkaUtil:
    def __init__(self,serverAddr):
        self.producer = kafkaProducer(bootstrap_servers=serverAddr)

    def pushData(self,topic,data):
        json_data = json.dumps(pkt_dst)
        self.producer.send(topic,data)







