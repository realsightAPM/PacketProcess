# encoding: utf-8
import pyshark
import json
import gzip, zlib

# 打开存储的捕获文件

#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/mysql_complete.pcap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/pgsql.cap')
cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/ms-sql-tds-rpc-requests.cap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/TNS_Oracle1.pcap')



# 从网络接口上进行捕获

#cap = pyshark.LiveCapture(interface='en0')
#cap.sniff(packet_count=1000)
#layer_dict = {}
data = {}

for pkt in cap:
    #layer_list = []
    #data['highest_layer'] = pkt.highest_layer
    #print pkt.frame_info
    #print pkt.highest_layer
    #print pkt.interface_captured, pkt.length, pkt.number, pkt.captured_length
    for layer in pkt:
        if layer.layer_name != 'tds':
            continue
        print layer.layer_name
        for name in sorted(layer.field_names):
            #if name != 'query': continue
            val = layer.get_field(name)
            if name == 'data':
                val = val.decode("hex")
            print name + ": " + val
        print