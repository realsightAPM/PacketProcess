# encoding: utf-8
import capture_pkt
import time
import process_pkt
from multiprocessing import Process,Pipe
import config_load

output_pipe,in_pipe = Pipe()

protocol_parser_dst = config_load.loadXmlFile("protocol.xml")

packet_fingerprint = config_load.loadXmlFile("packet_fingerprint.xml")

writer = capture_pkt.capturePkt(in_pipe,'lo0',protocol_parser_dst)

reader = process_pkt.processPkt(output_pipe)

#writer.daemon = True
#reader.daemon = True
writer.start()
reader.start()
reader.join()
writer.join()




