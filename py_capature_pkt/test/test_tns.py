import pyshark

cap = pyshark.FileCapture(input_file='/Users/jiajia/Develop/workspace/PacketProcess/py_capature_pkt/pcap/TNS_Oracle3.pcap')

sqlKeywords = ["declare", "begin",
               "comment on",
               "merge", "delete", "insert", "update", "select",
               "drop", "alter", "create",
               "truncate", "savepoint", "commit", "rollback"]

oracleName = ['\'', '(', ')', '_', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
              'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
              'V', 'W', 'X', 'Y', 'Z',
              'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
              'v', 'w', 'x', 'y', 'z']

#cap.load_packets()

for pkt in cap:

    for layer in pkt:
        if layer.layer_name != 'tns':
            continue
        startIndex = -1
        endIndex = -1
        # print layer.layer_name
        for name in sorted(layer.field_names):
            # if name != 'query': continue
            tnsData = layer.get_field(name)
            if name == 'data':
                #print tnsData
                tnsData = tnsData.decode("hex")
                #print tnsData
                for key in sqlKeywords:
                    if tnsData.find(key) != -1:
                        startIndex = tnsData.find(key)
                        # print startIndex
                        break
                if startIndex != -1:
                    for i in range(len(tnsData) - 1, startIndex, -1):
                        if tnsData[i] in oracleName:
                            endIndex = i + 1
                            # print endIndex
                            break
                    print pkt.number
                    print tnsData[startIndex:endIndex]
                startIndex = -1
