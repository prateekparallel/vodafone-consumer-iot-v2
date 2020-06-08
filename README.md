# vodafone-consumer-iot-v2

This is just a partitial submission of my project. This file will be modified as per direction in the BRS document.

How to compile and test this project -


cd  vodafone-consumer-iot-v2
mvnw clean package   or mvnw test

or
mvn  test  or mvn clean package

end-to-end test from postman or soapui tool

post request:- (I changed the uri path as per RESTful service standard)

http://localhost:8001/iot/v2/event

body -

{
	filepath: "C:/path/to/data.csv"
}

make sure file is availabe before test

get request:-

http://localhost:8001/iot/v2/event?ProductId=WG11155638?tstmp=1582605137000

