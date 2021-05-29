# WebMarket
Generic platform that employs a fleet of riders and offers their service for any company that needs deliveries.

## Help for developers
1. Pivotal Tracker project
   - https://www.pivotaltracker.com/n/projects/2500281
   
2. Sonarcloud Dashboard
   - https://sonarcloud.io/dashboard?branch=178258876-dev-add-dependencies&id=Tqs-project_Backend
   
3. Deployed application link
   - https://webmarket-314811.oa.r.appspot.com
   

1. Run the application
    - ./mvnw spring-boot:run


2. Deploy application to google cloud manually
    - mvn clean package appengine:deploy (need to have installed on your computer google cloud SDK)
    - check the application running at [link](https://webmarket-314811.oa.r.appspot.com).


3. A google MySQL database is used, so no need to have local instances running

