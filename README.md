# ONOS-IoT-Cloud-Hub

The IoT-Cloud Hub is a component of the K-Cluster and serves as a bridgehead for IoT things. The IoT-Cloud Hub conducts overall control of the IoT, collecting data from the IoT device and validating the data first. It also controls the data path using SDN to securely collect the data and manages the IoT device and the SDN-enabled switch. IoT-Cloud Hub targets SmartX IoT-Cloud Services for small-scale IoT things. The SmartX IoT-Cloud Service is configured using the Kafka Messaging System, and IoT things transfers data using the wired / wifi / LoRaWAN communication interface and is transferred to the IoT-Cloud Hub via SDN-enabled switches.


## OpenSource Software for IoT-Cloud Hub

### ONOS : Open Network Operating System

### What is ONOS?
ONOS is a new SDN network operating system designed for high availability,
performance, scale-out.

### Top-Level Features

* High availability through clustering and distributed state management.
* Scalability through clustering and sharding of network device control.
* Performance that is good for a first release, and which has an architecture
  that will continue to support improvements.
* Northbound abstractions for a global network view, network graph, and
  application intents.
* Pluggable southbound for support of OpenFlow and new or legacy protocols.
* Graphical user interface to view multi-layer topologies and inspect elements
  of the topology.
* REST API for access to Northbound abstractions as well as CLI commands.
* CLI for debugging.
* Support for both proactive and reactive flow setup.
* SDN-IP application to support interworking with traditional IP networks
  controlled by distributed routing protocols such as BGP.
* IP-Optical use case demonstration.

Checkout our [website](http://www.onosproject.org) and our
[tools](http://www.onosproject.org/software/#tools)



### IO Visor : BCC, eBPF

### What is IO Visor?
The IO Visor Project is an open source project and a community of developers to accelerate the innovation, development, and sharing of virtualized in-kernel IO services for tracing, analytics, monitoring, security and networking functions. It builds on the Linux community to bring open, flexible, distributed, secure and easy to operate technologies that enable any stack to run efficiently on any physical infrastructure.


### BPF Compiler Collection (BCC)
BCC is a toolkit for creating efficient kernel tracing and manipulation programs, and includes several useful tools and examples.

### eBPF (extended Berkeley Packet Filter)
The IO Visor community has created many excellent sources of information and samples on eBPF from beginner to advanced levels.

