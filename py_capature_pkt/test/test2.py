# encoding: utf-8
import src.capture_pkt
import time
import src.process_pkt
from multiprocessing import Process,Pipe
import src.config_load

output_pipe,in_pipe = Pipe()

protocol_parser_dst = src.config_load.loadXmlFile("protocol.xml")

packet_fingerprint = src.config_load.loadXmlFile("packet_fingerprint.xml")

writer = src.capture_pkt.capturePkt(in_pipe, 'en0', protocol_parser_dst)

writer.run();




