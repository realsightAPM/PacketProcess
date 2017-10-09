# encoding: utf-8
import pyshark
import multiprocessing
import json, logging
from config_load import loadXmlFile
from datetime import datetime

class capturePkt(multiprocessing.Process):

    def __init__(self, in_pipe, configParser):
        multiprocessing.Process.__init__(self)
        self.in_pipe = in_pipe
        self.interface_name = configParser.get('npm', 'interface')
        self.dict_name = configParser.get('npm', 'protocolxml')
        self.dict = loadXmlFile(self.dict_name)
        print self.dict_name

    def __add_pkt(self,pkt):
        #print "add pkt"v
        try:
            pkt_dst = self.process(pkt)
            if (pkt_dst is not None):
                self.in_pipe.send(json.dumps(pkt_dst))
        except Exception ,e:
            logging.error(e);
    
    def process(self,pkt):
        pkt_dst = {}
        layer_list = []
        pkt_dst['highest_layer'] = pkt.highest_layer
        time_long = float(pkt.sniff_timestamp)
        pkt_dst['sniff_timestamp'] = long(time_long)
        pkt_dst['solrtime'] = datetime.fromtimestamp(time_long).strftime("%Y-%m-%d\'T\'%H:%M:%S\'Z\'")
        for layer in pkt:
            layer_name = layer.layer_name
            if not self.dict.has_key(layer_name):
                continue
            layer_list.append(layer_name)
            for name in layer.field_names:
                if name != "" and name in self.dict[layer_name]:
                    pkt_dst[name] = layer.get_field(name)
        pkt_dst['layer_list'] = ','.join(layer_list)
        return pkt_dst

    def run(self):
        #cap = pyshark.LiveCapture(interface=self.interface_name,only_summaries=False)
        cap = pyshark.FileCapture("../pcap/http_pkt.pcap",only_summaries=False,keep_packets=False)
        cap.apply_on_packets(self.__add_pkt)

