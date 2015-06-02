#!/usr/bin/env python

import sys
import re
import getopt
import pymongo


def usage():
    print "Usage:%s [-d|-i|-h] [--device|--ip|--help] args...." % sys.argv[0]

if "__main__" == __name__:
    imei = ""
    ip = ""
    try:
        opts, args = getopt.getopt(
            sys.argv[1:], "d:i:h", ["device=", "ip=", "help"]);
        for opt, arg in opts:
            if opt in ("-h", "--help"):
                usage();
                sys.exit(1);
            elif opt in ("-d", "--device"):
                imei = arg
            elif opt in ("-i", "--ip"):
                ip = arg
    except getopt.GetoptError:
        print("invalid paramter!");
        usage();
        sys.exit(1);

    if imei == "":
        print("option -i is a must paramter for your device IMEI!");
        usage();
        sys.exit(1);
    
    mongo_client = pymongo.MongoClient("localhost", 27017)
    db = mongo_client["rlog"]
    collection = db["imei_" + imei]

    db.seqs.find_and_modify(
        {'colection': "imei_" + imei},
        {'$setOnInsert': {'collection':"imei_" + imei, 'id':0}},
        upsert=True)

    for line in sys.stdin:
        logcat_pattern = re.compile(
            "^(\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d\\.\\d+)\\s*(\\d+)\\s*(\\d+)\\s([VDIWEAF])\\s(.*?):\\s+(.*)$")
        match = re.search(logcat_pattern, line)
        if match:
            seq_cnt = db.seqs.find_and_modify(
                {'colection': "imei_" + imei},
                {'$inc': {'id': 1}},
            ).get('id')+1

            log_json = { '_id': seq_cnt,
                "time": match.group(1),
                "pid":  match.group(2),
                "ppid": match.group(3),
                "level": match.group(4),
                "activity": match.group(5),
                "message": match.group(6)}

            insert_id = collection.insert_one(log_json).inserted_id
            #print collection.count()
    sys.exit(0)


