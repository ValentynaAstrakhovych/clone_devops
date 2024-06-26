pipeline {
    agent any

    parameters {
        string(name: 'ACTION', defaultValue: 'abort', description: 'Action to take')
        choice(name: 'SLEEP', choices: ['yes', 'no'], description: 'Sleep before Ansible Playbook?')
    }

    tools {
        terraform 'tf1.6'
    }

    environment {
        GIT_REPO = 'git@github.com:LocalCoding/DevOps_Jan_24.git'
        GIT_CREDENTIALS = 'jenkins_access_to_git'
        ANSIBLE_DIRECTORY = 'Lesson_14_Jenkins_pipelines_NodeJS_Apps/project_setup_files/ansible'
        TF_DIRECTORY = 'Lesson_14_Jenkins_pipelines_NodeJS_Apps/project_setup_files/terraform'
    }

    stages {

        stage('Clone Git repo') {
            steps {
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: 'main']],
                    userRemoteConfigs: [[
                        url: "${GIT_REPO}",
                        credentialsId: "${GIT_CREDENTIALS}"
                    ]]
                ]
            }
        }

        stage('Terraform Init & Plan') {
            steps {
                dir("${TF_DIRECTORY}") {
                    sh '''
                    terraform init -input=false
                    terraform plan -out=terraform.tfplan
                    '''
                }
            }
        }

        stage('User Approval') {
            steps {
                script {
                    timeout(time: 5, unit: 'MINUTES') {
                        def userInput = input(
                            id: 'userInput', 
                            message: 'Choose to proceed with the build:', 
                            parameters: [choice(name: 'Proceed?', choices: ['yes', 'abort'], description: 'Proceed or Abort')]
                        )
                        if (userInput == 'abort') {
                            error('Build was aborted by the user.')
                        }
                    }
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                dir("${TF_DIRECTORY}") {
                    sh 'terraform apply -auto-approve terraform.tfplan'
                }
            }
        }

        stage('Get Terraform Outputs') {
            steps {
                dir("${TF_DIRECTORY}") {
                    sh 'terraform output web-address-nodejs > ../ansible/instance_ip.txt'
                }
            }
        }

        stage('Install Ansible') {
            steps {
                sh '''
                sudo apt-add-repository --yes --update ppa:ansible/ansible
                sudo apt-get install ansible -y
                '''
            }
        }

        stage('Run Ansible for the apps and load balancer') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'access_for_new_node_js_app', keyFileVariable: 'SSH_KEY')]) {
                    dir("${ANSIBLE_DIRECTORY}") {
                        script {
                            timeout(time: 2, unit: 'MINUTES') {
                                def userConfirmation = input(
                                    id: 'ConfirmAnsibleRun', 
                                    message: 'Confirm running Ansible playbook immidiately or wait for timeout to pass:',
                                    parameters: [choice(name: 'Confirm', choices: ['yes', 'no'], description: 'Confirm to run Ansible w/o timeout')],
                                    defaultValue: 'yes'
                                )
                                if (userConfirmation == 'no') {
                                    error('Ansible playbook run was aborted by the user.')
                                }
                            }
                                                
                        sh '''
                        ansible-playbook -i instance_ip.txt playbook_nodejs_dadjokes.yaml -u ubuntu --private-key='$SSH_KEY' -e 'ansible_ssh_common_args="-o StrictHostKeyChecking=no"'
                        '''
                        }
                    }
                }
            }
        }
    }
}

