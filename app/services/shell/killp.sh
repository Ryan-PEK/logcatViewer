keyword1=$1
keyword2=$2

case "$keyword1" in
     *\ * )
           #echo "match"
           keyword1=\"$keyword1\"
          ;;
       *)
           #echo "no match"
           ;;
esac

case "$keyword2" in
     *\ * )
           #echo "match"
           keyword2=\"$keyword2\"
          ;;
       *)
           #echo "no match"
           ;;
esac


#echo "keyword1: "$keyword1
#echo "keyword2: "$keyword2

#echo "use ps to find target process(es)"
#ps -ef | grep $keyword1 | grep $keyword2

PID=$(ps -ef | grep -v killp | grep $keyword1 | grep $keyword2 | awk '{print $2}')
#echo "PID :"$PID

if [ -n "$PID" ]; then
    echo "found process: "$PID
    kill -9 $PID
fi

#PGID=$(ps opgid="$PID")
#kill -QUIT -"$PGID"