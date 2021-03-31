Database Files_
  All exported Database Files are included in this folder. Databases were originally created and deployed using MySQL.
  Each web service configures Databases separately, thus, check each web service's db config before testing the service.
  It should made sure that the port numbers of the MySQL server is the same that is defined within the web services or
  the database connection might not get established properly when deployed.
  
Database Usage_
  1. Authentication service uses GadgetBadget_UsersDB
  2. ResearchHub service uses GadgetBadget_ResearchHubDB
  3. Marketplace service uses GadgetBadget_MarketplaceDB
  4. Payment service uses GadgetBadget_PaymentsDB
  5. Subscription service uses GadgetBadget_SubscriptionsDB

Each Database is designed and implemented by the member who designed the web-service associated with it.
Refer project report for Entity-Relationship diagrams or Relational Schemas of each Database.
