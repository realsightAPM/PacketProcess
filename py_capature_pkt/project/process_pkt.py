# encoding: utf-8
import multiprocessing
import time
import BloomFilter
import json
from kafka import KafkaProducer

class processPkt(multiprocessing.Process):

    def __init__(self,out_pipe,processors=None):
        multiprocessing.Process.__init__(self)
        self.out_pipe = out_pipe
        self.processors = processors
        self.pkt_id = 0
        self.bloomFilter = BloomFilter.MyBloomFilter()
        self.topic = "netpacket"
        self.queue = []
        print "init finish"

    def run(self):
        start = time.time()
        self.producer = KafkaProducer(bootstrap_servers='localhost:9092')
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
            self.queue.append(pkt_dst)
            if(len(self.queue) > 100):
                data = json.dumps(self.queue)
                future = self.producer.send("netpacket",data)
                print "send data"
                result = future.get(timeout=10)
                print "send success"
                print result
                self.queue=[]






            