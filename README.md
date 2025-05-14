# Expense-Watcher

## Project Description:
Expense-Watcher is a web application that allows users to track their expenses. It provides a user-friendly interface to add, edit, and delete expenses. For my purposes the application serves as a base for practicing deployments on Azure. Additionally, build scripts and docker containers were created to assist in this task.

## System requirements:
- Java 17 or higher
- Internet browser
- Maven

## Compiling and testing:
1. Compile the project using: 
        
        make build
3. Run the tests using:
        
        make tests

## Running instructions:
1. Run the server using:
        
        make webapi
2. Run the frontend by visiting the url `http://localhost:5050` on a browser

#### Building the Docker image:
    sudo docker build -t expense-tracker-webapi .

#### Running the Docker image:
    sudo docker run -t -p 5050:5050 --rm -it expense-tracker-webapi

## License

The underlying code for this application is used only as a placeholder for practicing deployment on Azure. It is not owned by me and is not licensed for distribution.
