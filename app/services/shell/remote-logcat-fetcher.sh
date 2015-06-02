#!/bin/bash

#check imei
if [[ -z "$1" ]]; then
	echo parameter imei is null
	exit 0
fi
#check ip
if [[ -z "$2" ]]; then
	echo parameter ip is null
	exit 0
fi
#check os
if [[ -z "$3" ]]; then
	echo parameter os is null
	exit 0
fi
#check devcie model
if [[ -z "$4" ]]; then
	echo parameter model is null
	exit 0
fi

imei=$1
ip=$2
os=$3
model=$4s

echo $imei
echo $ip
echo $os
echo $model

#while [[ 1 ]]; do
	#adb connect $ip:5555
	adb -s $ip:5555 logcat -v threadtime | python /Users/hongyanyin/projects/taobao/test/serverside/source/app/services/scripts/parser.py -d$imei -i$ip
#done
