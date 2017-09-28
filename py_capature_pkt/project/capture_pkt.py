# encoding: utf-8
import pyshark
import multiprocessing
import json
import packet_parser
import traceback

class capturePkt(multiprocessing.Process):
    def __init__(self,in_pipe,interface_name,parser_rule_dst):
        multiprocessing.Process.__init__(self)
        self.in_pipe = in_pipe
        self.interface_name = interface_name
        self.pkt_id = 0
        self.parser_rule_dst = parser_rule_dst
    
    def __add_pkt(self,pkt):
        #print "add pkt"v
        try:
            pkt_dst = self.process(pkt)
            if (pkt_dst is not None):
                self.in_pipe.send(json.dumps(pkt_dst))
        except Exception ,e:
            print "ERROR ############"
            traceback.print_exc(e)

    
    def process(self,pkt):
        protocol = pkt.highest_layer 
        if protocol not in self.parser_rule_dst:
            return None
        pkt_dst = {}
        print 'protocol #### '+protocol
        field_list = self.parser_rule_dst[protocol]
        for field in field_list:
            value = getattr(packet_parser,"get_"+field)(pkt)
            pkt_dst[field] = value
        return pkt_dst

    def run(self):
        #cap = pyshark.LiveCapture(interface=self.interface_name,only_summaries=False)
        cap = pyshark.FileCapture("/Users/zyd/socket.cap",only_summaries=False,keep_packets=False)
        cap.apply_on_packets(self.__add_pkt)

