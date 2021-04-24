*NOTE: Database Scripts can also be found in each web service project's Database Script Directory which is within the Directory structure
of each Project. This directory is created with the same files added to one location just to make them easily accessible.
  
WARNING_
  *. Database scripts have been pre-populated with set of sample data.
  *. Do NOT change the configurations saved in jwt_config tables in any of the script files as they have been pre-configured with
    a pre-generated public-private key pair that uniquely identifies user/service tokens and these keys the private keys should NOT
    be shared with anyone. Public key can be displayed and can be used to verify tokens by necessary parties if interested or concerned 
    about the safety of the JWT tokens.
  *. Do NOT alter tables with prefix given as "_seq" in any script. These are used for primary key generation using triggers/Stored procedures.
  *. some of the tasks of web services are a set of procedure calls and each database script either contain one or more pre-coded stored procedures.

Database Configuration_
  1.  All Database Scripts have been written in MySQL.
  2.  All five scripts have to be executed in a local MySQL server or should be hosted in the cloud before the services could access them.
      Configure each web service to utilize the hosted databases by providing the URL of the database server where the database is hosted
      in the DBHandler classes of the web services.
  3.  Note that each DB Script can be hosted seperately and there are no attachments between the scripts. (Not tightly coupled - hence 
      easy to maintain)

Web Services and Their relevant Database Scripts_
  1. User Service - gadgetgadget_users.sql
  2. Research Hub Service - gadgetbadget_researchhub.sql
  3. Payment Service - gadgetbadget_payments.sql
  4. Marketplace Service - gadgetbadget_marketplace.sql
  5. Funding Service - gadgetbadget_funding.sql

Hosting in Different Database Servers_
  *. If hosted in different Database servers, first, the host address has to be configured in the relevant web service.
  *. These configurations can be done in the two files, DBHandler.java class of each web service

Primary Key Generation_
  *. Primary keys are generated with a unique prefix for each table that has a unique primary key followed by the last two digits of the
    on-going year and and 6 more digits which gives 999999 possible keys per year till approximately year 3020. (A long term key solution)
  *. Key wastage can be made minimum by cleaning up the _seq tables properly. (mainly after testing)
