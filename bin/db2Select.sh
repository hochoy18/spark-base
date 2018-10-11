#!/usr/bin/env sh
DBNODE='razor'
DBUSER='db2inst1'
DBPASSWORD='111111'

#连接DB2
connDB2()
{
    if( /home/db2inst1/sqllib/bin/db2 connect to $1  user $2 using $3 > /dev/null )
    then
        echo 'OK'
    else
        echo "failed connect to ${DBNODE}"
        exit -1
    fi
}

#释放DB2连接
releaseDB2(){
    /home/db2inst1/sqllib/bin/db2 connect reset  > /dev/null
}
#查询操作
selectTable(){
 sql="SELECT ID,WORK_ID, JOB_ID, JOB_NAME, START_TIME, END_TIME ,DURATION,STATUS,DETAIL,DURATION FROM RAZOR_JOBS"
    /home/db2inst1/sqllib/bin/db2 -x ${sql}| while read ID WORK_ID JOB_ID JOB_NAME START_TIME END_TIME  DURATION STATUS DETAIL DURATION
    do
        echo "Result:
        ${ID}|${WORK_ID}|${JOB_ID}|${JOB_NAME}|${START_TIME}|${END_TIME}|${DURATION}|${STATUS}|${DETAIL}|${DURATION}"
    done
 sql2="SELECT COUNT(*) JOB_COUNT FROM RAZOR_JOBS"
    /home/db2inst1/sqllib/bin/db2 -x ${sql2}| while read JOB_COUNT
    do
        echo "job_COUNT  :   ${JOB_COUNT} "
    done
 sql1="SELECT  COUNT(*) WORK_COUNT FROM RAZOR_WORKS "
    /home/db2inst1/sqllib/bin/db2 -x ${sql1} | while read WORK_COUNT
    do
     echo "WORK_COUNT   :   ${WORK_COUNT}"
    done
}

#删除操作
deleteTable(){
    sql="DELETE RAZOR_JOBS"
    /home/db2inst1/sqllib/bin/db2 -x ${sql}
    sql1="DELETE RAZOR_WORKS"
    /home/db2inst1/sqllib/bin/db2 -x ${sql1}
}
#测试执行
run(){
 connDB2 ${DBNODE} ${DBUSER} ${DBPASSWORD}
 selectTable

 deleteTable
 echo "==========================delete============================="
 selectTable

 releaseDB2
}
echo "execute sql ................."
run





#!/bin/bash -l
hour_fun(){
    hdfs dfs -rmr   /user/gd/pub1/mobile-logs/cd/*/*/_F*

    for i in 6 7 8 9
    do
        echo "$i"
            for j in 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23
        do
            echo ${j}
            ./work.sh h 2018070${i} $j -f HDFS2HBase-clientdata
        done
    done
}

for ((m=1; m<=5; m ++))
do
    hour_fun
    echo ${m}
done