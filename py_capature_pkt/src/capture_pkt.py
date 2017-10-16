# encoding: utf-8
import pyshark
import multiprocessing
import json, logging, time
import Queue
from config_load import loadXmlFile
from datetime import datetime
import redis

class capturePkt(multiprocessing.Process):

    def __init__(self, in_pipe, configParser):
        multiprocessing.Process.__init__(self)
        self.in_pipe = in_pipe
        self.interface_name = configParser.get('npm', 'interface')
        self.dict_name = configParser.get('npm', 'protocolxml')
        self.localcheck = configParser.get('npm', 'localcheck')
        self.localfile = configParser.get('npm', 'localfile')
        self.dict = loadXmlFile(self.dict_name)
        self.rediscli = redis.Redis(host="localhost", port=6379)

    def init_redis(self):
        pass

    def __add_pkt(self,pkt):
        #try:
        pkt_dst = self.capture(pkt)
        if pkt_dst != None:
            self.send(pkt_dst)
        else:
            pass
        #except Exception ,e:
        #    print e
        #    logging.error(e);
    
    def capture(self, pkt):
        logging.debug(pkt.highest_layer)
        pkt_dst = {}
        #highest_layer = pkt.highest_layer.lower()
        #if not self.dict.has_key(highest_layer):
        #    return None
        pkt_dst['frame_number'] = pkt.number
        pkt_dst['highest_layer'] = pkt.highest_layer
        pkt_dst['length'] = pkt.length
        time_long = float(pkt.sniff_timestamp)
        pkt_dst['sniff_timestamp'] = long(time_long)
        pkt_dst['rs_timestamp'] = datetime.fromtimestamp(time_long).strftime("%Y-%m-%dT%H:%M:%SZ")
        pkt_dst['layer_list'] = []
        for layer in pkt:
            layer_name = layer.layer_name
            if not self.dict.has_key(layer_name): continue
            pkt_dst['layer_list'].append(layer_name)
            if layer_name == 'ip':
                self.capture_ip(layer, pkt_dst)
            if layer_name == 'tcp':
                self.capture_tcp(layer, pkt_dst)
            elif layer_name == 'http':
                self.capture_http(layer, pkt_dst)
            #else:
            #    self.capture_other(pkt, pkt_dst)

        return pkt_dst

    def capture_ip(self, layer, pkt_dst):
        for name in layer.field_names:
            if name in self.dict['ip']:
                pkt_dst[name] = layer.get_field(name)

    def capture_tcp(self, layer, pkt_dst):
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

    def capture_http(self, layer, pkt_dst):
        logging.debug("capture http")
        for name in layer.field_names:
            if name != "" and name in self.dict['http']:
                pkt_dst[name] = layer.get_field(name)
            #if name == 'request':
            #    print 'Rquest: ' + str(pkt_dst['frame_number']) + ' ' + layer.get_field('request_full_uri')
            #elif name == 'response':
            #    print "Response: " + layer.get_field('request_in') + ' ' + pkt_dst['highest_layer']

    def capture_other(self, layer, pkt_dst):
        for name in layer.field_names:
            if name != "" and name in self.dict[layer.layer_name]:
                pkt_dst[name] = layer.get_field(name)


    def send(self, pkt_dst):
        if 'http' in pkt_dst['layer_list']:
            if pkt_dst.has_key('request'):
                #print 'Rquest: ' + str(pkt_dst['frame_number']) + ' ' + pkt_dst['request_full_uri']
                req_key = 'http_request:'+pkt_dst['frame_number']
                self.rediscli.setex(req_key, json.dumps(pkt_dst), 60)
            elif pkt_dst.has_key('response'):
                #print "Response: " + str(pkt_dst['request_in']) + ' ' + pkt_dst['highest_layer']
                self.rediscli.lpush('http_response', json.dumps(pkt_dst))
        else:
            self.rediscli.lpush(pkt_dst['highest_layer'], json.dumps(pkt_dst))

    def run(self):
        if self.localcheck == 'on':
            cap = pyshark.FileCapture(self.localfile, only_summaries=False, keep_packets=False)
        else:
            cap = pyshark.LiveCapture(interface=self.interface_name, only_summaries=False)
        cap.apply_on_packets(self.__add_pkt)

