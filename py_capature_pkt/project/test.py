# encoding: utf-8
import capture_pkt
import time
import process_pkt
from multiprocessing import Process,Pipe

output_pipe,in_pipe = Pipe()

writer = capture_pkt.capturePkt(in_pipe,'lo0')
reader = process_pkt.processPkt(output_pipe)
writer.daemon = True
reader.daemon = True
writer.start()
reader.start()
reader.join()
writer.join()




