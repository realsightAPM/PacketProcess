class BaseProcess(object):

    def process(self,pkt_dst,pkt):
        try:
            pkt_dst = {}
            pkt_dst['pkt_id_i'] = self.pkt_id
            pkt_dst['src_addr_s'] = pkt.ip.src
            pkt_dst['src_port_s'] = pkt[pkt.transport_layer].srcport
            pkt_dst['dst_addr_s'] = pkt.ip.dst
            pkt_dst['dst_port_s'] = pkt[pkt.transport_layer].dstport
            pkt_dst['time'] = pkt.time
            self.pkt_id = self.pkt_id + 1
            return pkt_dst
        except AttributeError as e:
            print str(e)
        return None

    def process_summary(self,pkt_dst,pkt):
        pass

