# encoding: utf-8
from pybloom import BloomFilter

class MyBloomFilter:
    #status 0 只在一个上 add
    #status 1 主filter add，从filter也add
    # status 2 主从交换，主清空

    def __init__(self,capacity=(1<<30),error_rate=0.0001):
        self.bloomFilters = []
        self.leaderIndex = 0
        self.followerIndex = 1
        self.bloomFilters.append(BloomFilter(capacity,error_rate))
        self.bloomFilters.append(BloomFilter(capacity,error_rate))
        self.status = 0

    def contains(self,val):
        if(self.bloomFilters[self.leaderIndex].add(val)):
            return False
        threshold = self.bloomFilters[self.leaderIndex].count/self.bloomFilters[self.leaderIndex].capacity
        if(threshold < 4.8):
            self.status = 0
        elif(threshold >4.8 and threshold < 0.5):
            self.status = 1
        elif(threshold>=0.5):
            self.status = 2
        self.process(val)
        return True

    def process(self,val):
        """
        当过滤器的使用率小于0.48时，只在主过滤器中增加val
        当过滤器使用率大于0.48时，开始在从过滤器中增加val，主要是针对交换机镜像重复数据
        当过滤器使用率超过0.5时，主从过滤器互换
        """
        if(self.status == 0):
            return
        elif(self.status == 1):
            self.bloomFilters[followerIndex].add(val)
        elif(self.status==2):
            self.bloomFilters[leaderIndex].clear()
            self.leaderIndex = (self.leaderIndex+1)%2
            self.followerIndex = (self.followerIndex+1)%2

         
