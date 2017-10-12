# encoding: utf-8
import pyshark
import json
import gzip, zlib

# 打开存储的捕获文件
#cap = pyshark.FileCapture('/Users/jiajia/Downloads/http_pkt.pcap')

# 从网络接口上进行捕获

#cap = pyshark.LiveCapture(interface='en0')
#cap.sniff(packet_count=1000)
#layer_dict = {}

class Tools:
    def check_protocol(self, timeout):
        #cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/TNS_Oracle1.pcap')
        cap = pyshark.LiveCapture(interface='en0')
        cap.sniff(packet_count=timeout)
        protocol_list = []
        for pkt in cap:
            for layer in pkt:
                name = layer.layer_name
                if not name in protocol_list:
                    protocol_list.append(name)

        print "Protocol Detected:\n"
        print "\n".join(protocol_list)
        print

    def print_protocol(self, proto, timeout):
        #cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/TNS_Oracle1.pcap')
        cap = pyshark.LiveCapture(interface='en0')
        cap.sniff(packet_count=timeout)
        for pkt in cap:
            for layer in pkt:
                name = layer.layer_name
                if name != proto:
                    continue
                for item in layer.field_names:
                    print item + ": " + layer.get_field(item)
            print
        print

if __name__ == "__main__":
    tools = Tools()
    tools.check_protocol(10)
    tools.print_protocol('tns', 10)