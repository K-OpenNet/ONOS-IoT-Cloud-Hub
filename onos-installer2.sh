#!/bin/bash

#
# ---------------------------------------------|
# You Must Start This Script Like This Command |
# ./onos-installer.sh                          |
#   Written by Heebum Yoon                     |
# ---------------------------------------------|
#
echo "################################ONOS Installer Start!"

echo "################################Ubuntu JAVA 8 Install Start"
if which java
then
        echo "java-8 has been installed."
else
        sudo apt-get purge openjdk*
        sudo apt-get install software-properties-common -y
        sudo add-apt-repository ppa:webupd8team/java -y
        echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
        sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
        sudo apt-get update -y

        export JAVA_HOME=/usr/lib/jvm/java-8-oracle

        sudo apt-get install -y oracle-java8-installer
        sudo apt-get install -y oracle-java8-set-default

        env | grep JAVA_HOME
fi
export JAVA_HOME=/usr/lib/jvm/java-8-oracle

mkdir Downloads
cd Downloads

if [ -f onos-1.7.1.tar.gz ]
then
        echo "onos-1.7.1.tar.gz exist"
else
        wget http://downloads.onosproject.org/release/onos-1.7.1.tar.gz
        tar -zxvf onos-1.7.1.tar.gz -C ../
fi

cd ..
cd onos-1.7.1
bin/onos-service server &

