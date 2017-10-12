# encoding: utf-8

import sys, os, argparse, ConfigParser, logging
from capture_pkt import capturePkt
from process_pkt import processPkt
from multiprocessing import Process,Pipe
from daemon import Daemon

levels = {
    'CRITICAL' : logging.CRITICAL,
    'ERROR' : logging.ERROR,
    'WARNING' : logging.WARNING,
    'INFO' : logging.INFO,
    'DEBUG' : logging.DEBUG
}

class npm(Daemon):

    def __init__(self, pid_path, configParser):
        Daemon.__init__(self, pid_path)
        self.configParser = configParser
        logging.basicConfig(
            filename=self.configParser.get('logging', 'filepath'),
            level=levels[self.configParser.get('logging', 'loglevel')],
            format=self.configParser.get('logging', 'logformat'),
        )


    def run(self):
        output_pipe,in_pipe = Pipe()

        #packet_fingerprint = config_load.loadXmlFile("packet_fingerprint.xml")

        # Capture from interface and write to pipe
        writer = capturePkt(in_pipe, self.configParser)

        # Read from pipe and push to Kafka
        reader = processPkt(output_pipe, self.configParser)

        writer.start()
        reader.start()
        reader.join()
        writer.join()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Example with non-optional arguments')
    parser.add_argument('action', choices=['start', 'stop', 'restart'])
    pid_path = '../pid/npm.pid'

    configFilePath = r'../conf/npm.conf'
    if not os.path.exists(configFilePath):
        logging.error("Config File is Missing. Please check the path: " + configFilePath)
        sys.exit()

    configParser = ConfigParser.RawConfigParser()
    configParser.read(configFilePath)

    npm = npm(pid_path, configParser)

    print "NPM Starting"
    npm.run();

    '''
    args = parser.parse_args()
    if args.action == 'start':
        npm.start()
    elif args.action == 'stop':
        npm.stop()
    elif args.action == 'restart':
        npm.restart()
    else:
        sys.exit("Wrong Argument.")
    '''
