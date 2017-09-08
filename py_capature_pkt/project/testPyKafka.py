from kafka import KafkaProducer
import time

def test(producer):
    future = producer.send("netpacket","aaaaaaaa")
    result = future.get(timeout=10)
    print result


producer = KafkaProducer(bootstrap_servers='localhost:9092')

time.sleep(10)

set_p = producer.partitions_for("netpacket")
test(producer)
print str(set_p)



