# ONOS-IoT-Cloud-Hub

The IoT-Cloud Hub is a component of the K-Cluster and serves as a bridgehead for IoT things. The IoT-Cloud Hub conducts overall control of the IoT, collecting data from the IoT device and validating the data first. It also controls the data path using SDN to securely collect the data and manages the IoT device and the SDN-enabled switch. IoT-Cloud Hub targets SmartX IoT-Cloud Services for small-scale IoT things. The SmartX IoT-Cloud Service is configured using the Kafka Messaging System, and IoT things transfers data using the wired / wifi / LoRaWAN communication interface and is transferred to the IoT-Cloud Hub via SDN-enabled switches.


### OpenSource Software for IoT-Cloud Hub


ONOS : Open Network Operating System
====================================

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



IO Visor : BCC, eBPF
====================================

![BCC Logo](images/logo2.png)
# BPF Compiler Collection (BCC)

BCC is a toolkit for creating efficient kernel tracing and manipulation
programs, and includes several useful tools and examples. It makes use of 
extended BPF (Berkeley Packet Filters), formally known as eBPF, a new feature
that was first added to Linux 3.15. Much of what BCC uses requires Linux 4.1
and above.

eBPF was [described by](https://lkml.org/lkml/2015/4/14/232) Ingo Molnár as:

> One of the more interesting features in this cycle is the ability to attach eBPF programs (user-defined, sandboxed bytecode executed by the kernel) to kprobes. This allows user-defined instrumentation on a live kernel image that can never crash, hang or interfere with the kernel negatively.

