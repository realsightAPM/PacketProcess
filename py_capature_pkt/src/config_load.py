from xml.dom.minidom import parse
import xml.dom.minidom

def loadXmlFile(fileName):
    dom = xml.dom.minidom.parse(fileName)
    root = dom.documentElement
    protocolList = root.getElementsByTagName('protocol')
    protocolDic = {}
    for index in range(len(protocolList)):
        proName = protocolList[index].getElementsByTagName('name')[0].childNodes[0].data
        proPars = protocolList[index].getElementsByTagName('parameter')
        parList = []
        for parameter in proPars:
            parList.append(parameter.childNodes[0].data)
        protocolDic[proName]=parList;
    return protocolDic

if __name__ == "__main__":
    dict = loadXmlFile('protocol.xml')
    print dict

