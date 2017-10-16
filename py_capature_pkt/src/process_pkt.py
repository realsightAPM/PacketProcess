# encoding: utf-8
import multiprocessing
import time, os, sys
import BloomFilter
import json, logging
from kafka import KafkaProducer
import redis
import schedule
import ConfigParser
from datetime import datetime

class processPkt(multiprocessing.Process):

    def __init__(self, configParser, pkt_fingerprint_dst=None, has_repetition=False):
        multiprocessing.Process.__init__(self)
        self.pkt_id = 0
        if has_repetition:
            self.bloomFilter = BloomFilter.MyBloomFilter()
            self.pkt_fingerprint_dst = pkt_fingerprint_dst
        self.queue = []
        self.has_repetition = has_repetition
        self.kafka_addr = configParser.get('kafka', 'addr')
        self.kafka_topic = configParser.get('kafka', 'topic')
        self.kafka_push_batch = configParser.getint('kafka', 'kafka_push_batch')
        self.producer = KafkaProducer(bootstrap_servers=self.kafka_addr)
        self.configParser = configParser
        self.rediscli = redis.Redis(host="localhost", port=6379)

    def run(self):

        #schedule.every(10).seconds.do(self.process_tcp)
        schedule.every(10).seconds.do(self.process_http)

        while True:
            schedule.run_pending()
            time.sleep(1)

    def process_tcp(self):
        logging.debug("process tcp")

        packages = self.rediscli.lrange('TCP', 0, -1)
        merge_time = long(time.time())
        rs_timestamp = datetime.fromtimestamp(merge_time).strftime("%Y-%m-%dT%H:%M:%SZ")
        self.rediscli.delete('TCP')
        statistic = {}
        for item in packages:
            item = json.loads(item)
            key = "{0}:{1}:{2}:{3}:{4}:{5}".format(item['src'], item['srcport'], item['dst'], item['dstport'], item['expert_message'], item['flags'])
            if not statistic.has_key(key):
                statistic[key] = {}
                statistic[key]['count'] = 0
                statistic[key]['length'] = 0
                statistic[key]['sum_window_size'] = 0
                statistic[key]['sum_window_size_value'] = 0
                statistic[key]['sum_analysis_ack_rtt'] = 0
                statistic[key]['count_analysis_ack_rtt'] = 0
                statistic[key]['sum_analysis_bytes_in_flight'] = 0

            statistic[key]['count'] += 1
            statistic[key]['length'] += long(item['length'])

            if item.has_key('window_size'):
                statistic[key]['sum_window_size'] += int(item['window_size'])

            if item.has_key('window_size_value'):
                statistic[key]['sum_window_size_value'] += int(item['window_size_value'])

            if item.has_key('analysis_bytes_in_flight'):
                statistic[key]['sum_analysis_bytes_in_flight'] += int(item['analysis_bytes_in_flight'])

            if item.has_key('analysis_ack_rtt'):
                statistic[key]['sum_analysis_ack_rtt'] += float(item['analysis_ack_rtt'])
                statistic[key]['count_analysis_ack_rtt'] += 1

        output_queue = []
        for sta in statistic:
            output = {}
            (src, srcport, dst, dstport, expert_message, flags) = sta.split(":")
            output['merge_time'] = merge_time
            output['rs_timestamp'] = rs_timestamp
            output['src'] = src
            output['srcport'] = srcport
            output['dst'] = dst
            output['dstport'] = dstport
            output['expert_message'] = expert_message
            output['flags'] = flags
            output['highest_layer'] = 'TCP'
            for i in statistic[sta]:
                output[i] = statistic[sta][i]
            #print output
            output_queue.append(output)
            # if len(output_queue) > self.kafka_push_batch:
            #    self.output_kafka(output_queue)
        # self.output_kafka(output_queue)

    def process_http(self):
        print "process http"
        responses = self.rediscli.lrange('http_response', 0, -1)
        self.rediscli.delete("http_response")
        merge_time = long(time.time())
        rs_timestamp = datetime.fromtimestamp(merge_time).strftime("%Y-%m-%dT%H:%M:%SZ")

        statistic = {}
        for res in responses:
            res = json.loads(res)
            if res.has_key('request_in'):
                req_no = res['request_in']
                req = self.rediscli.get('http_request:' + req_no)
                if req != None:
                    req = json.loads(req)
                    key = "{0}\1{1}\1{2}\1{3}\1{4}\1{5}\1{6}\1{7}\1{8}".format(
                        req['src'], req['srcport'], req['dst'], req['dstport'],
                        req['request_method'], req['request_full_uri'], res['response_code'],
                        req['expert_message'], res['expert_message']
                    )
                    if not statistic.has_key(key):
                        statistic[key] = {}
                        statistic[key]['count'] = 0
                        statistic[key]['req_length'] = 0
                        statistic[key]['res_length'] = 0
                        statistic[key]['time_sum'] = 0
                        statistic[key]['time_count'] = 0

                    statistic[key]['count'] += 1
                    statistic[key]['req_length'] += long(req['length'])
                    statistic[key]['res_length'] += long(res['length'])
                    if res.has_key('time'):
                        statistic[key]['time_count'] += 1
                        statistic[key]['time_sum'] += float(res['time'])
                else:
                    logging.debug("no req")
            else:
                logging.debug("no request in")

        output_queue = []
        for sta in statistic:
            output = {}
            (src, srcport, dst, dstport, method, uri, code, req_em, res_em) = sta.split("\1")
            output['merge_time'] = merge_time
            output['rs_timestamp'] = rs_timestamp
            output['src'] = src
            output['srcport'] = srcport
            output['dst'] = dst
            output['dstport'] = dstport
            output['req_expert_message'] = req_em
            output['res_expert_message'] = res_em
            output['http_uri'] = uri
            output['http_method'] = method
            output['http_response_code'] = code
            output['highest_layer'] = 'HTTP'
            for i in statistic[sta]:
                output[i] = statistic[sta][i]
            #print output
            output_queue.append(output)
            if len(output_queue) > self.kafka_push_batch:
                self.output_kafka(output_queue)
        self.output_kafka(output_queue)

    def __get_pkt_fingerprint(self,pkt_dst):
        field_list = self.pkt_fingerprint_dst
        value = ""
        if field_list is not None:
            value = [value.append(pkt_dst[field] for field in field_list)]
        return value

    def output_kafka(self, output_queue):
        if len(output_queue) == 0:
            return
        data = json.dumps(output_queue)
        self.producer.send(self.kafka_topic, data)
        output_queue = []



if __name__ == "__main__":
    configParser = ConfigParser.RawConfigParser()

    configFilePath = r'../conf/npm.conf'
    if not os.path.exists(configFilePath):
        logging.error("Config File is Missing. Please check the path: " + configFilePath)
        sys.exit()
    configParser.read(configFilePath)

    p = processPkt(configParser)
    p.run()



            