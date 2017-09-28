# encoding: utf-8
import multiprocessing
import time
import BloomFilter
import json
from kafka import KafkaProducer
import traceback

class processPkt(multiprocessing.Process):

    def __init__(self,out_pipe,pkt_fingerprint_dst=None,has_repetition=False):
        multiprocessing.Process.__init__(self)
        self.out_pipe = out_pipe
        self.pkt_id = 0
        if has_repetition:
            self.bloomFilter = BloomFilter.MyBloomFilter()
            self.pkt_fingerprint_dst = pkt_fingerprint_dst
        self.topic = "netpacket"
        self.queue = []
        self.has_repetition = has_repetition
        

    def run(self):
        start = time.time()
        try:
            self.producer = KafkaProducer(bootstrap_servers='10.4.53.25:9092')
        except Exception,e:
            traceback.print_exc(e)
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
        if(self.has_repetition):
            value = self.__get_pkt_fingerprint(pkt_dst)
            if self.bloomFilter.contains(value):
                return
        self.queue.append(pkt_dst)
        if(len(self.queue) > 100):
            data = json.dumps(self.queue)
            future = self.producer.send("netpacket",data)
            #print "send data"
            result = future.get(timeout=10)
            print "send success"
            print result
            self.queue=[]

    def __get_pkt_fingerprint(self,pkt_dst):
        field_list = self.pkt_fingerprint_dst
        value = ""
        if field_list is not None:
            value = [value.append(pkt_dst[field] for field in field_list)]
        return value






            