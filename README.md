# ucsma-application
Application to demo the UCSMA rate control protocol.

## Description
The idea of our application is based on the combination of UCSMA rate control protocol and ad-hoc network. In this way, we designed an application regarding campus tour, which is known to be really popular in North American universities. High school students visit their dream schools before application and travellers from all over the world visit prestigious universities. 

This application will provide visitors with a different experience of campus tour. Tour guide is a really important role in traditional campus tours; visitors must follow their tour guide who shows them the whole campus. However, our applicaiton makes every student in the university become potential tour guides and visitors can experience the real life of students on campus. For example, we can hire different students to take the device and show different aspects of their university life. Through our application, students can record attending lectures or tutorials, participating in club activities and going to cafeterias.

In addition, some high school students might be interested in a particular program or field of study. For example, a high school student who is interested in computer science can experience life of the student majoring in computer science on campus. They would be able to gain a better real-life student experience this way than the traditional campus tour.

The reason this application needs UCSMA rate control protocol is that we want to give visitors the opportunity to wander around different places on campus, which would cause the topology to be unstable. We do not want the mid people (represented by the middle node in our experiments) to be unable to receive videos or unable to send comments, so in order to enable the fairness of every visitor's experience, we're employing the rate control protocol. In addition, we do not use the Internet to implement this application because the Internet could be unsecure and might leak lecture materials which the university does not want to release to the public. Another reason for not using the Internet but a peer-to-peer network is that for those travellers from overseas, they may have trouble to access the Internet. These devices with our application can help them to enjoy their journeys.

## Development Tools

### Hypercast
Hypercast is a routing protocal and software that implements an overlay network for multiple overlay topologies. The website of Hypercast is as following: https://www.comm.utoronto.ca/hypercast/. Detailed documentation can be found on the website.

### Folders in the repo

#### config
This folder contains the mandatory Hypercast configuration file. And some parameters needed to be changed. 

Basic moderations:
1. Declare an overlayID, this configuration can be found in `Public/OverlayID`, which is the identifier of the overlay network.
2. To make the communication between two laptops, change `Public/Node/DTBuddyList/BuddyNum` to 2 on each laptop, and add another buddy in the `Public/Node/DTBuddyList/` with proper ip address and port number. 

#### lib
This folder contains all the required libraries of the current modules (Video and Audio streaming) of the application. `bcprov-jdk14-122.jar`, `crimson.jar`, `hypercast.jar` and `xalan.jar` are required for Hypercast. `bridj-0.7.0.jar`, `slf4j-api-1.7.2.jar` and `webcam-capture-0.3.12.jar` are required for the real-time video stream module.

#### src
This folder currently contains the source code of video and audio input and output stream which can be used in the application.

## Current Status

Currently, we finish the real-time video and audio modules which are essential for this application. Besides, we designed a testing method with our rate control protocol, which is mentioned in our last report, by changing the gateway of the devices we had (the AR9331 SoCs and our laptops). However, because the rate control protocol is currently implemented in the Packetspammer but not in the driver, the testing cannot be done using the same testing method at the moment. 

### Real time Video Stream
There are two java files for the real time video module, one is for sending video stream and the other is for receiving video stream and displaying the video on screen. Ways to implement is as following:

1. Capture images from camera of the laptop
2. Compressed it as jpg format. 
3. Use socket provided by hypercast to send it.
4. Decompression the images.
5. Show on images on the screen as smooth videos.

### Real time Audio Stream
The HAudioInputStream is for receiving the audio stream and playback on the device's speaker through implementing a SourceDataLine. The HAudioOutputStream is for capturing from the device's microphone and sending the audio stream to other nodes in the overlay network via Hypercast Stream Manager.

### Some issues when implementing
This section contains some issues with implementing these modules which might occur in future development. I hope this part can be helpful for the future development.

1. Hypercast provides developers with overlay socket input stream and output stream. However, its function is not as powerful as the input stream and output stream provided by Java socket. We can only write bytes to the stream. Therefore, we need to serialize the objects before writing to the output stream.

2. The packet we recieve from the input stream are encapsuled to a constant size buffer. The size of stream packet can be modified in configuration file(hypercast.xml) `Public/OutputStreamBuffersize` and `Public/InputStreamBuffersize`. However, we sometimes need to know the exactly length of packets. In this way, we can add a "header" to each packet which records the actual length of the packet. 

## Next Steps
1. Carry out a demo to show the difference of video and audio stream with and without our rate control protocol (and with/without unlocking). The current problem of carrying out a demo is mentioned as above: the rate control protocol is implemented in Packetspammer but not the driver of the AR9331 SoCs. Therefore, if we want to see the power of the rate control protocol, it will need to be implemented in the driver.

2. Find out a way to test with multiple devices. Currently we can install java running environment on the chip we have by OpenWrt SDK provided https://openwrt.org/docs/guide-developer/using_the_sdk and the issues and methods of mounting a usb flash drive on the devices is mentioned in our first report. But the AR9331 SoCs may not be powerful enough to run multiple threads.

