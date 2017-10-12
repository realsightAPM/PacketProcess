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
        self.localcheck = configParser.get('npm', 'localcheck')
        self.localfile = configParser.get('npm', 'localfile')
        self.dict = loadXmlFile(self.dict_name)

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
        pkt_dst['frame_number'] = pkt.number
        pkt_dst['highest_layer'] = pkt.highest_layer
        pkt_dst['length'] = pkt.length
        time_long = float(pkt.sniff_timestamp)
        pkt_dst['sniff_timestamp'] = long(time_long)
        pkt_dst['rs_timestamp'] = datetime.fromtimestamp(time_long).strftime("%Y-%m-%dT%H:%M:%SZ")
        for layer in pkt:
            layer_name = layer.layer_name
            if not self.dict.has_key(layer_name):
                continue
            layer_list.append(layer_name)
            if layer_name == 'tcp':
                self.process_tcp(layer, pkt_dst)
            #elif layer_name == 'http':
            #    self.process_http(layer, pkt_dst)
            else:
                for name in layer.field_names:
                    if name != "" and name in self.dict[layer_name]:
                        pkt_dst[name] = layer.get_field(name)
        pkt_dst['layer_list'] = ','.join(layer_list)
        return pkt_dst

    def process_tcp(self, layer, pkt_dst):
        for name in layer.field_names:
            if not name:
                continue
            val = layer.get_field(name)
            if name.startswith('analysis_'):
                pkt_dst[name] = layer.get_field(name)
            elif name.find('expert_message') != -1:
                pkt_dst['expert_message'] = val.split(':')[0]
            if name in self.dict['tcp']:
                if name.startswith('flags_'):
                    if val == 1:
                        pkt_dst['flags'] = name.split('_')[1]
                elif name == 'window_size_scalefactor' and val == '-1':
                    pkt_dst[name] = '0'
                else:
                    pkt_dst[name] = val

    def process_http(self, layer, pkt_dst):
        pass

    def run(self):
        if self.localcheck == 'on':
            cap = pyshark.FileCapture(self.localfile, only_summaries=False, keep_packets=False)
        else:
            cap = pyshark.LiveCapture(interface=self.interface_name, only_summaries=False)
        cap.apply_on_packets(self.__add_pkt)

