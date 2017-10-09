# encoding: utf-8
import pyshark
import json
import gzip, zlib

# 打开存储的捕获文件

cap = pyshark.FileCapture('/Users/jiajia/Downloads/eth0.cap')



# 从网络接口上进行捕获

#cap = pyshark.LiveCapture(interface='en0')
#cap.sniff(packet_count=1000)
#layer_dict = {}

for pkt in cap:
    data = {}
    layer_list = []
    data['highest_layer'] = pkt.highest_layer
    for layer in pkt:
        temp_data = {}
        if layer.layer_name != 'http2': continue
        #print layer.layer_name
        for name in layer.field_names:
        #    print name
        #print ', '.join(layer.field_names)
            if name != "":
                #data[name] = layer.get_field(name)
                temp_data[name] = layer.get_field(name)
                print name + ": " + layer.get_field(name)
                '''
                if name == 'data':
                    data = layer.get_field(name)
                    try:
                        print data.decode('hex')
                    except:
                        pass
                '''
        #print json.dumps(temp_data)
        print
    #print pkt.highest_layer + ": " + json.dumps(data)
'''
def format_protocol(protocol, items):
    print "<protocol title='protocol'>"
    print  "<name>" + protocol.upper() + "</name>"
    for i in items:
        print "\t<parameter>" + i + "</parameter>"
    print "</protocol>"

for pkt in cap:
    flag = False
    for i in range(0, len(pkt.layers)):
        layer_name = pkt[i].layer_name
        if not layer_dict.has_key(layer_name):
            layer_dict[layer_name] = []
        for item in dir(pkt[i]):
            if item.startswith('_') or item.startswith("get") or item == 'DATA_LAYER':
                continue
            if not item in layer_dict[layer_name]:
                flag = True
                layer_dict[layer_name].append(item);

for name in layer_dict:
    format_protocol(name, layer_dict[name])
'''