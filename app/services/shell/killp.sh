keyword1=$1
keyword2=$2

echo "$keyword1"
echo "$keyword2"

PID=$(ps -ef | grep "$keyword1" | grep "$keyword2" | awk '{print $2}')
echo PID:$PID

kill $PID

#PGID=$(ps opgid="$PID")
#kill -QUIT -"$PGID"