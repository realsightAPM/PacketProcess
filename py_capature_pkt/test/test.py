# encoding: utf-8
import pyshark
import json
import gzip, zlib

# 打开存储的捕获文件

#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/mysql_complete.pcap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/pgsql.cap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/ms-sql-tds-rpc-requests.cap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/TNS_Oracle1.pcap')
cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/http_pkt.pcap')


# 从网络接口上进行捕获

#cap = pyshark.LiveCapture(interface='en0')
#cap.sniff(packet_count=1000)
#layer_dict = {}
data = {}

for pkt in cap:
    #layer_list = []
    #data['highest_layer'] = pkt.highest_layer
    #if not pkt.number in ['574']:
    #    continue
    #if not pkt.number in ['839', '841', '857', '859']:
    #    continue
    if pkt.highest_layer != 'HTTP': continue
    print "Number:" + pkt.number
    #print pkt.frame_info
    #print pkt.highest_layer
    #print pkt.interface_captured, pkt.length, pkt.number, pkt.captured_length
    #print pkt['tcp']
    #print pkt['http']
    #print


    for layer in pkt:
        if layer.layer_name != 'http' :
            continue
        for name in sorted(layer.field_names):
            #if not name in ['request', 'response', 'request_number', 'response_number', 'prev_request_in', 'prev_response_in', 'request_in', 'response_in']: continue
            val = layer.get_field(name)
            print name + ": " + val
        print
