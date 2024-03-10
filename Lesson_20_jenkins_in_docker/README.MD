Pre-Requeries
1. Build your own jenkins-docker-image with command
```docker build . -t jenkins:custom_plugins```
2. To run
     docker-compose start command
```docker-compose -f docker-compose-jenkins-local.yaml up -d```
3. To stop 
```docker-compose -f docker-compose-jenkins-local.yaml stop``` 
4. To destroy
```docker-compose -f docker-compose-jenkins-local.yaml destroy```

to backup your jenkins from the loud

```rsync -avz -e "ssh -i "PathToYourKey" " --rsync-path="sudo rsync" ubuntu@34.235.32.214:/var/lib/jenkins jenkins_data```
```be mindfull of the variables and make sure you setup directory structure correct```