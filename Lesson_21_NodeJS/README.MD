Pre-Requeries
1. Build your own jenkins-docker-image with command
```docker build . -t experimental/pacman:v2 ```
2. To run
     docker-compose start command
```docker-compose -f docker-compose-deployment-pacman-and-mongo.yaml up -d```
3. To stop 
```docker-compose -f docker-compose-deployment-pacman-and-mongo.yaml stop``` 
4. To destroy
```docker-compose -f docker-compose-deployment-pacman-and-mongo.yaml destroy```