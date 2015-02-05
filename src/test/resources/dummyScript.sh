#!/bin/bash
while [ "true"  ]
do
    sleep 2
    echo `date`|tee /tmp/mylog.log
done