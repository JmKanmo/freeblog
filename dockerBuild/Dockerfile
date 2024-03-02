FROM centos:7

RUN yum -y upgrade
RUN yum -y install rdate
RUN yum install -y net-tools
RUN yum install lrzsz -y
RUN yum -y install wget
RUN yum -y install unzip
RUN yum -y install tar
RUN yum install -y \
    java-11-openjdk \


ENV JAVA_HOME /etc/alternatives/jre

EXPOSE 8400 22

ADD jboot.sh /freeblog/jboot.sh
ADD freeblog-2.0.war /freeblog/freeblog-2.0.war

RUN chmod +x /freeblog/jboot.sh

CMD /freeblog/jboot.sh


