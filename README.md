#GadgetBadget Research and Innovation Support System Services
#SLIIT PAF Assignment | Y3S1

Project Description_
This is the GitHub repository for REST Web Services based PAF Assignment. There are 5 web services in total which makes up a 
complete Research and Innovation Supporting System Service known as "GadgetBadget". The whole system is divided into a total 
of 5 web services implementing the RESTful architecture using JAX-RS and Jersey Technologies based on Java and Maven. For the
deployment of the web services, Apache Tomcat server version 9 is used and a by using a test client like "Postman" can be 
used to test the API endpoints of all the web services.

Group Members_
1. Dissanayake D.M.I.M. (User Service)
2. Jayasinghe D.T. (Payment Service)
3. Bandara J.M.S.A. (ResearchHub Service)
4. Indrahenaka H.R.T.V. (Marketplace Service)
5. H.C.K. de Silva (Fund Service)

Web Services_
1. User Service
    User service manages all the user-related resources of the system as a whole. This service is used by other services for 
    Authentication and Authorization Services whilst it also provide User Account Handling, User-Role Management, and more. 
    User service communicates with almost every other service as a client to obtain statistics of tasks performed by users.
2. ResearchHub Service
    Research Hub service manages all the research projects and allows funders to take a look at all fundable projects. Other 
    than that, It also maintains a set of categories the projects can fall into. Research hub service inter-communicate with 
    funding service whenever the researcher wants to see the full list of funds received for a particular research project.
3. Marketplace Service
    Marketplace service lets the researchers to manage selling finished products. Consumers can get all available products 
    available to be purchased and if they are interested in buying a product, marketplace communicates with Payment service 
    in order to let the consumers make the payment.
4. Payment Service
    Payment service handles all the payments. Admin and Financial Manager of gadgetbadget are given the full access to get a complete 
    list of payments and other payment service related data, such as service charges and tax percentages. Consumers are indirectry 
    calling the user service when they are making a purchase for payment verfication before database recording of the payment details. 
    (Payment service communicates with User service to verify consumer/researcher payment methods such as 
    credit cards and things like billing addresses.)
5. Funding Service
    Handles all the fund management requests sent by funders. Also grants full access for Admin and Accountant roles, while consumers
    not having permission at all for any of the end points in this particular API. Accountants usually handles revenue calculations over 
    received funds and they also manage the service chages related to the funding service right here. Communicates with user service
    as a client to verify credit card details saving a just placed fund in the database.
    
Web Service Security in Serverside_
    In order to secure the API end points of all the web services, a request filter is used along with Custom defined role based 
    stateless authentication and authorization functionalities using JWT to authenticate and authorize all the request that came 
    through the filter from client/test client application(s). Web services' API endpoints are only accessible for users with the
    specified roles which are checked at the point of request filtering and as well as at every single API endpoint.
    
Database Scripsts_
    Databse Scripts written in MySQL can be found in each web service's project folder within a folder named "Database Script".
    Some of the projects have made the java documentaion available for all the classes and they can be found within a folder 
    named "doc" inside the main project directory.
    
Finalized on_
    Date: 2021/04/22
