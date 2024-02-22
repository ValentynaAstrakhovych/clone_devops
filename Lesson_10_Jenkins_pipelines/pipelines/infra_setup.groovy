pipeline {
    agent any
    tools {
     terraform 'tf1.6'
    }

    stages {
        stage('Clone Git repo') {
            steps {
    // Bind the SSH private key to an environment variable
    withCredentials([sshUserPrivateKey(credentialsId: 'NameOfYourKey', keyFileVariable: 'SSH_KEY')]) {
        sh '''
        # Start the SSH agent and add the key
        eval $(ssh-agent -s)
        ssh-add $SSH_KEY

        # Ensure GitHub is a known host
        ssh-keyscan -H github.com >> ~/.ssh/known_hosts
        ### Make sure repository does not exists
        rm -rf ./DevOps_Jan_24
        
        # Use verbose output for the git command
        GIT_SSH_COMMAND="ssh -vvv" git clone -b main --single-branch git@github.com:YourForkRepository/DevOps_Jan_24.git

        # Clean up the SSH agent
        eval $(ssh-agent -k)
        '''
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
