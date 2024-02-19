pipeline {
    agent any
    tools {
     terraform 'tf1.6'
    }

    stages {
        stage('Clone Git repo') {
            steps {
                git(branch: 'main', url: 'https://github.com/LocalCoding/DevOps_Jan_24.git', credentialsId: 'test_credential_jenkins_git_access')
            }
        }
        stage('Plan') {
            steps {
                sh '''
                cd ./Lesson_10_Jenkins_pipelines/terraform_instance_config
                terraform init
                terraform plan -out=terraform.tfplan
                '''
            }
        }
        stage('Plan verification and user input') {
            steps {
                input message: 'proceed or abort?', ok: 'ok'
            }
        }
        stage('Apply') {
            steps {
                sh '''
                cd ./Lesson_10_Jenkins_pipelines/terraform_instance_config
                terraform apply terraform.tfplan
                '''
            }
        }
    }
}
