# encoding: utf-8
import multiprocessing
import time
import BloomFilter
import json
from PushToKafka import KafkaUtil

class processPkt(multiprocessing.Process):

    def __init__(self,out_pipe,processors=None):
        multiprocessing.Process.__init__(self)
        self.out_pipe = out_pipe
        self.processors = processors
        self.pkt_id = 0
        self.bloomFilter = BloomFilter.MyBloomFilter()
        self.kafkaUtil = KafkaUtil(serverAddr='localhost:9092')
        self.topic = 'net_packet'
        self.queue = []

    def run(self):
        start = time.time()
        while True:
                pkts = self.out_pipe.recv()
                pkt_dst = json.loads(pkts)
                self.process(pkt_dst)
                self.pkt_id = self.pkt_id + 1
                if(self.pkt_id > 200):
                    print str(time.time() - start)
                    start = time.time()
                    self.pkt_id = 0

    def process(self,pkt_dst):
        """
        把数据解析处理，然后返回可以直接推送入kafka的数据
        """
        value = []
        value.append(''.join(pkt_dst['time']))
        value.append(''.join(pkt_dst['destination']))
        value.append(''.join(pkt_dst['source']))
        value.append(''.join(pkt_dst['protocol']))
        if(self.bloomFilter.contains(''.join(value))):
            return
        else:
            queue.append(pkt_dst)
            if(len(queue) > 100):
                self.kafkaUtil.pushData(self.topic,json.dumps(queue))
                queue=[]






            