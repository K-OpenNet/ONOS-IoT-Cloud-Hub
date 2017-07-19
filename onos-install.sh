#!/bin/bash

#
# ---------------------------------------------|
# You Must Start This Script Like This Command |
# . onos-installer.sh                          |
#   Written by Heebum Yoon                     |
# ---------------------------------------------|
#

echo "################################ONOS Installer Start!"

echo "################################Ubuntu JAVA 8 Install Start"

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
#export JAVA_HOME=/usr/lib/jvm/java-8-oracle


echo "################################GIT Install Start"
sudo apt-get install -y  git-core
 #sudo apt-get install -y unzip
 #sudo apt-get install -u python2.7
git --version

mkdir Downloads
mkdir Applications

cd Downloads

echo "################################Maven Install Start"
wget http://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz

tar -zxvf apache-maven-3.3.9-bin.tar.gz -C ../Applications/

echo "################################Karaf Install Start"
wget http://archive.apache.org/dist/karaf/3.0.5/apache-karaf-3.0.5.tar.gz

tar -zxvf apache-karaf-3.0.5.tar.gz -C ../Applications/

cd ~/

#git clone https://gerrit.onosproject.org/onos/

echo "################################ONOS Source Code Download"
git clone https://gerrit.onosproject.org/onos/

cd onos

git checkout 1.7.0
git branch

 #echo export ONOS_ROOT=~/onos >> /etc/bash.bashrc
 #echo source $ONOS_ROOT/tools/dev/bash_profile >> /etc/bash.bashrc
sudo bash -c 'echo "export ONOS_ROOT=~/onos" >> /etc/bash.bashrc'
sudo bash -c 'echo "source \$ONOS_ROOT/tools/dev/bash_profile" >> /etc/bash.bashrc'

#export ONOS_ROOT=~/onos
#source $ONOS_ROOT/tools/dev/bash_profile

source /etc/bash.bashrc

mvn clean install


