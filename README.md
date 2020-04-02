# ucsma-application
Application to demo the UCSMA rate control protocol.

## Description
The idea of our application is the combination of UCSMA rate control protocol and ad-hoc network. In this way, we design an application regarding to the campus tour. 

As everyone known, campus tour is really popular in North America universities. High school students will visit their dream schools before application and travellers from all of the world will visit prestigious universities. 

This application will provide visitors with a different experience of campus tour. Tour guides are really important roles in traditional campus tour. Visitors must follow the tour guides and the tour guides will show the whole campus to the visitors. However, our applicaiton makes every student in the university be tour guides and visitors can experience the true life of students on campus. For example, we can hire different students to take the device and show their real life on campus, student will go to the lecture or tutorials, will participate in club activities and will go to cafeterias. The device of those students will record their lives and visitors can see what is the true life of the students.

In addition, some high school students will also have their dream field. For example, a high school student who is interested in computer science can experience life of the student major in computer science on campus. They will have a more real experience than the traditional campus tour.

The reason this application need UCSMA rate control protocol is people can wander on campus and the topology will be unstable. We do not want the mid people cannot receive videos or cannot send comments. In addition, we do not use Internet to implement this application because Internet is kind of unsecure, we do not want everyone see what happened on campus, for example lectures might not be shown on Internet. Another reason is for those travellers from foreign countries, they may have trouble to access Internet. These device can help them to enjoy their journeys.

## Development Tools

### Hypercast
Hypercast is a software provided us with multiple overlay topologies and it is a routing protocol. The website of Hypercast is as following: https://www.comm.utoronto.ca/hypercast/. Detailed develop documents can be found on the website.

### Folders in the repo

#### config
This folder contains the mandatory configuration files of Hypercast. And some parameters needed to be changed. 

Basic moderations:
1. Declare an overlayID, this configuration can be found in `Public/OverlayID`, which is the identifier of the overlay network.
2. To make the communication between two laptops, change `Public/Node/DTBuddyList/BuddyNum` to 2 on each laptop, and add another buddy in the `Public/Node/DTBuddyList/` with proper ip address and port number. 

#### lib
This folder contains all the required libraries of this application currently. `bcprov-jdk14-122.jar`, `crimson.jar`, `hypercast.jar` and `xalan.jar` are required for Hypercast. `bridj-0.7.0.jar`, `slf4j-api-1.7.2.jar` and `webcam-capture-0.3.12.jar` is required for real time video stream module.

#### src
This folder contains source code of video and audio input and output stream currently which can be used in application.

## Current Status

Currently we finish the real time video module and real time audio modules which are essential to this application. Besides we design a testing method with our rate control protocol which is mentioned in our last report by changing the gateway of the devices we have and the laptop. However, because the rate control protocol is implemented in packetspammer but not in the driver, the testing cannot be done currently. 

### Real time Video Stream
There are two java files for the real time video module, one is for sending video stream and the other is for receiving video stream and display the video on screen. Ways to implement is as following:

1. Capture images from camera of the laptop
2. Compressed it as jpg format. 
3. Use socket provided by hypercast to send it.
4. Decompression the images.
5. Show on images on the screen as smooth videos.

### Real time Audio Stream


### Some issues when implementing
This section contains some issues with implementing these modules which might occur in future development. I hope this part can be helpful for the future development.

1. Hypercast provides developers with overlay socket input stream and output stream. However, its function is not as powerful as the input stream and output stream provided by Java socket. We can only write bytes to the stream. Therefore, we need to serialize the objects before writing to the output stream.

2. The packet we recieve from the input stream are encapsuled to a constant size buffer. The size of stream packet can be modified in configuration file(hypercast.xml) `Public/OutputStreamBuffersize` and `Public/InputStreamBuffersize`. However, we sometimes need to know the exactly length of packets. In this way, we can add a "header" to each packet which records the actual length of the packet. 

## Next Steps
1. Providing a demo with rate control protocol to see the difference of video and audio stream with and without our rate control protocol. The current problem of demo it is mentioned as above. Currently the rate control protocol is implemented in packetspammer but not the driver of the device we have. Therefore, if we want to see the power of the rate control protocol, this protocol needed to be implemented in the driver.

2. Find out a way to test with multiple devices. Currently we can install java running environment on the chip we have by openwrt sdk provided https://openwrt.org/docs/guide-developer/using_the_sdk and the issues and methods of mounting a u disk on the devices is mentioned in our first report. But the power of the devices is really weak. We are not sure whether multiple threads can run on our device.

