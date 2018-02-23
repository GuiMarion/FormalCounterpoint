#!/bin/bash


po = $(ps aux | grep -i "java -jar Exe.jar"
echo ${po#* }
