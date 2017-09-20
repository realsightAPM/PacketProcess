# encoding: utf-8
import pyshark
import multiprocessing
import json

class capturePkt(multiprocessing.Process):
    def __init__(self,in_pipe,interface_name):
        multiprocessing.Process.__init__(self)
        self.in_pipe = in_pipe
        self.interface_name = interface_name
        self.pkt_id = 0
    
    def __add_pkt(self,pkt):
        #print "add pkt"v
        pkt_dst = self.process_summaries(pkt)
        if (pkt_dst is not None):
            self.in_pipe.send(json.dumps(pkt_dst))
            

    def process(self,pkt):
        try:
            pkt_dst = {}
            pkt_dst['pkt_id_i'] = self.pkt_id
            pkt_dst['src_addr_s'] = pkt.ip.src
            pkt_dst['src_port_s'] = pkt[pkt.transport_layer].srcport
            pkt_dst['dst_addr_s'] = pkt.ip.dst
            pkt_dst['dst_port_s'] = pkt[pkt.transport_layer].dstport
            self.pkt_id = self.pkt_id + 1
            return pkt_dst
        except AttributeError as e:
            pass
          #  print str(e)
        return None

    def process_summaries(self,pkt):
        pkt_dst = {}
        pkt_dst['time'] = pkt.time
        pkt_dst['protocol'] = pkt.protocol
        pkt_dst['destination'] = pkt.destination
        pkt_dst['source'] = pkt.source
        pkt_dst['summary_line'] = pkt.summary_line
        pkt_dst['length'] = pkt.length
        pkt_dst['info'] = pkt.info
        pkt_dst['color'] = "tcp"
        return pkt_dst

    def run(self):
        #cap = pyshark.LiveCapture(interface=self.interface_name,only_summaries=True)
        cap = pyshark.FileCapture("/Users/zyd/apm/file2.cap",only_summaries=True,keep_packets=False)
        cap.apply_on_packets(self.__add_pkt)

