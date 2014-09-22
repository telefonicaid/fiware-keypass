#!/bin/bash
# keypass : This starts and stops keypass daemon
#
# chkconfig: 12345 12 88
# description: keypass
# processname: keypass
# pidfile: /var/run/keypass.pid

# Source function library.

. /etc/rc.d/init.d/functions

CURR="$( cd "$( dirname "$( readlink -f ${BASH_SOURCE[0]} )" )" && pwd )"

pname="keypass"
user="keypass"

exe="java -jar $CURR/keypass.jar"
cfg="$CURR/config.yml"
server="$exe server $cfg"
migrate="$exe db migrate $cfg"

pidfile="/var/run/keypass.pid"

RETVAL=0

start() {
    echo -n "Starting $pname : "
    #daemon ${exe} # Not working ...
    if [ -s ${pidfile} ]; then
       RETVAL=1
       echo -n "Already running !" && warning
    else
        touch $pidfile
        chown $user $pidfile
        su -s /bin/sh $user -c "
                cd $CURR
                exec setsid ${server}   \
                </dev/null >/dev/null 2>&1 &
                echo \$! >${pidfile}
                disown \$!
                "
        PID=`cat $pidfile`
        [ $PID ] && success || failure
    fi
    echo
}

stop() {
    echo -n "Shutting down $pname : "
    if [ -f $pidfile ]; then
        PID=`cat $pidfile`
        kill -9 $PID
        RETVAL=$?
        [ $RETVAL -eq 0 ] && success || failure
        rm -f $pidfile
    else
        echo -n "Not running" && failure
    fi
    echo
}

restart() {
    echo -n "Restarting $pname : "
    stop
    sleep 5
    start
}

upgradedb() {
    su -s /bin/sh $user -c "$migrate"
}

case "$1" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    status)
        status ${pname}
    ;;
    restart)
        restart
    ;;
    upgradedb)
        upgradedb
    ;;
    *)
        echo "Usage: $0 {start|stop|status|restart|upgradedb}"
    ;; esac

exit 0