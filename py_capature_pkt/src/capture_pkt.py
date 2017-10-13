# encoding: utf-8
import pyshark
import multiprocessing
import json, logging, time
import Queue
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
        self.http_stack = {}
        self.number_queue = Queue.Queue()
        self.maxsize = configParser.getint('npm', 'queuesize')

    def __add_pkt(self,pkt):
        #print "add pkt"v
        try:
            pkt_dst = self.process(pkt)
            if (pkt_dst is not None):
                self.in_pipe.send(json.dumps(pkt_dst))
        except Exception ,e:
            print e
            logging.error(e);
    
    def process(self,pkt):
        pkt_dst = {}
        layer_list = []
        pkt_dst['frame_number'] = pkt.number
        pkt_dst['highest_layer'] = pkt.highest_layer
        if not self.dict.has_key(pkt_dst['highest_layer'].lower()):
            return None
        if pkt.highest_layer != 'HTTP': return None
        pkt_dst['length'] = pkt.length
        time_long = float(pkt.sniff_timestamp)
        pkt_dst['sniff_timestamp'] = long(time_long)
        pkt_dst['rs_timestamp'] = datetime.fromtimestamp(time_long).strftime("%Y-%m-%dT%H:%M:%SZ")
        flag = True
        for layer in pkt:
            layer_name = layer.layer_name
            if not self.dict.has_key(layer_name):
                continue
            layer_list.append(layer_name)
            if layer_name == 'tcp':
               self.process_tcp(layer, pkt_dst)
            if layer_name == 'http':
                flag = self.process_http(layer, pkt_dst)
            else:
                for name in layer.field_names:
                    if name != "" and name in self.dict[layer_name]:
                        pkt_dst[name] = layer.get_field(name)
        pkt_dst['layer_list'] = ','.join(layer_list)
        if flag:
            #print pkt_dst
            return pkt_dst
        else:
            return None

    def process_tcp(self, layer, pkt_dst):
        pkt_dst['expert_message'] = ''
        pkt_dst['flags'] = []
        for name in layer.field_names:
            if not name:
                continue
            val = layer.get_field(name)
            if name.startswith('flags_'):
                if val == '1':
                    pkt_dst['flags'].append(name.split('_')[1])
            elif name.startswith('analysis_'):
                pkt_dst[name] = layer.get_field(name)
            elif name.find('expert_message') != -1:
                pkt_dst['expert_message'] = val.split(':')[0]
            if name in self.dict['tcp']:
                if name == 'window_size_scalefactor' and int(val) < 0:
                    pkt_dst[name] = '0'
                else:
                    pkt_dst[name] = val
        pkt_dst['flags'] = ",".join(sorted(pkt_dst['flags']))
    def process_http(self, layer, pkt_dst):
        flag = False
        field_list = layer.field_names
        frame_no = pkt_dst['frame_number']
        for name in field_list:
            if name != "" and name in self.dict['http']:
                pkt_dst[name] = layer.get_field(name)
        if 'request' in field_list:
            if not self.http_stack.has_key(frame_no):
                self.http_stack[frame_no] = pkt_dst
                if len(self.http_stack) > self.maxsize:
                    print "Max Size: " + len(self.http_stack)
                    for i in self.http_stack:
                        pkt = self.http_stack.get(i)
                        if pkt['sniff_timestamp'] < (time.time() - 10):
                            self.http_stack.pop(i)
                    print "Size After Clean: " + len(self.http_stack)
            else:
                #print 'Dup request'
                pass
        elif 'response' in field_list:
            if pkt_dst.has_key('request_in'):
                request_in = pkt_dst.get('request_in')
                if self.http_stack.has_key(request_in):
                    pkt_req = self.http_stack.get(request_in)
                    for i in pkt_req:
                        pkt_dst[i] = pkt_req.get(i)
                    pkt_dst['response'] = 1
                    self.http_stack.pop(request_in)
                    flag = True
                else:
                    #print "No prev request"
                    pass
            else:
                #print "No request in"
                pass
        else:
            pass
        return flag

    def run(self):
        if self.localcheck == 'on':
            cap = pyshark.FileCapture(self.localfile, only_summaries=False, keep_packets=False)
        else:
            cap = pyshark.LiveCapture(interface=self.interface_name, only_summaries=False)
        cap.apply_on_packets(self.__add_pkt)

