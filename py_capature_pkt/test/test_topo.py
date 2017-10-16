# encoding: utf-8
import pyshark
import json
import gzip, zlib

# 打开存储的捕获文件

#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/mysql_complete.pcap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/pgsql.cap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/ms-sql-tds-rpc-requests.cap')
#cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/TNS_Oracle1.pcap')
cap = pyshark.FileCapture('/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/http.cap')

#cap.load_packets()

# 从网络接口上进行捕获

#cap = pyshark.LiveCapture(interface='en0')
#cap.sniff(packet_count=1000)
#layer_dict = {}
data = {}
viz_data = {}
nodes = []
conns = {}
for pkt in cap:
    for layer in pkt:
        if layer.layer_name != 'ip': continue
        src = pkt['ip'].get_field('src')
        dst = pkt['ip'].get_field('dst')
        if not src in nodes:
            nodes.append(src)
        if not dst in nodes:
            nodes.append(dst)
        conns[src] = dst

print nodes
print conns

viz_data['renderer'] = 'global'
viz_data['name'] = 'edge'
viz_data['nodes'] = []
viz_data['connections'] = []

for i in range(0, 1):
    temp = {}
    temp['source'] = 'INTERNET'
    temp['target'] = 'PSD'
    temp['metrics'] = { 'normal':100, 'danger': 10, 'warning': 20 }
    temp['notices'] = []
    temp['class'] = 'normal'
    viz_data['connections'].append(temp)

internet = {}
internet['renderer'] = 'region'
internet['name'] = 'INTERNET'
internet['displayName'] = 'INTERNET'
internet['nodes'] = []
internet['metadata'] = {}
internet['class'] = 'normal'
internet['connections'] = []

viz_data['nodes'].append(internet)

for j in range(0, 1):
    temp = {}
    temp['renderer'] = 'region'
    temp['name'] = 'PSD'
    temp['displayName'] = 'PSD'
    temp['metadata'] = {}
    temp['class'] = 'normal'
    temp['nodes'] = []
    temp['connections'] = []

    for k in range(0, len(nodes)):
        node = {}
        node['name'] = nodes[k]
        node['metadata'] = {}
        node['metadata']['streaming'] = 1
        node['renderer'] = 'focusedChild'
        temp['nodes'].append(node)

    for l in conns:
        conn = {}
        conn['source'] = l
        conn['target'] = conns[l]
        conn['metadata'] = {}
        conn['metadata']['streaming'] = 1
        conn['metrics'] = {
            "danger": 0.018,
            "normal": 39.846000000000004
        }
        temp['connections'].append(conn)

    #print json.dumps(temp)
    viz_data['nodes'].append(temp)


ori = {
              "renderer": "global",
              "name": "edge",
              "nodes": [
                {
                  "renderer": "region",
                  "name": "INTERNET",
                  "displayName": "总线",
                  "nodes": [],
                  "metadata": {},
                  "class": "normal",
                  "connections": []
                },
                {
                  "renderer": "region",
                  "name": "us-west-2",
                  "displayName": "Server1",
                  "nodes": [
                    {
                      "name": "立体库",
                      "metadata": {
                        "streaming": 1
                      },
                      "renderer": "focusedChild"
                    },
                    {
                      "name": "AMT",
                      "metadata": {
                        "streaming": 1
                      },
                      "renderer": "focusedChild"
                    },
                    {
                      "name": "OA",
                      "metadata": {
                        "streaming": 1
                      },
                      "renderer": "focusedChild"
                    },
                  ],
                  "connections": [
                    {
                      "source": "AMT",
                      "target": "立体库",
                      "metadata": {
                        "streaming": 1
                      },
                      "metrics": {
                        "danger": 0.018,
                        "normal": 39.846000000000004
                      }
                    },
                    {
                      "source": "AMT",
                      "target": "OA",
                      "metadata": {
                        "streaming": 1
                      },
                      "metrics": {
                        "normal": 32.126,
                        "danger": 0.010000000000000002
                      }
                    },
                    {
                      "source": "立体库",
                      "target": "OA",
                      "metadata": {
                        "streaming": 1
                      },
                      "metrics": {
                        "danger": 0.028000000000000004,
                        "normal": 32.202
                      }
                    },
                  ],
                  "metadata": {},
                  "class": "normal"
                }
              ],
              "connections": [
                {
                  "source": "INTERNET",
                  "target": "us-west-2",
                  "metrics": {
                    "normal": 10,
                    "danger": 1,
                    "warning": 0.96
                  },
                  "notices": [],
                  "class": "normal"
                },
              ],
            }

print json.dumps(viz_data)
#print json.dumps(ori)