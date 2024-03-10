# reference to https://github.com/LocalCoding/DevOps_Practice_Jan24/blob/main/Jenkins_PASV/Jenkins/Dockerfile
# Используем официальный образ Jenkins_PASV LTS как базовый
FROM jenkins/jenkins:lts

# Переключаемся на пользователя root для установки дополнительных компонентов
USER root

# Установка Plugin Installation Manager Tool
RUN curl -fsSL "https://github.com/jenkinsci/plugin-installation-manager-tool/releases/download/2.12.3/jenkins-plugin-manager-2.12.3.jar" -o /opt/jenkins-plugin-manager.jar

# Переключаемся обратно на пользователя jenkins перед установкой плагинов
USER jenkins

# Копируем файл с перечнем плагинов и устанавливаем их
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN java -jar /opt/jenkins-plugin-manager.jar --plugin-file /usr/share/jenkins/ref/plugins.txt

# Задаем рабочую директорию
WORKDIR /var/jenkins_home
