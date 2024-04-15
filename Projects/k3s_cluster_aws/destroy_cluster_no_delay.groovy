pipeline {
    options {
        ansiColor('xterm')
    }
    agent any
    tools {
        terraform 'tf1.6'
    }

    stages {
        stage('Sparse Checkout') {
            steps {
                script {
                    checkout([$class: 'GitSCM', 
                              branches: [[name: 'main']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions: [[
                                  $class: 'SparseCheckoutPaths',
                                  sparseCheckoutPaths: [[path: 'Projects/k3s_cluster_aws/']]
                              ]],
                              userRemoteConfigs: [[
                                    url: 'git@github.com:LocalCoding/DevOps_Jan_24.git',
                                    credentialsId: 'jenkins_access_to_git'
                              ]]
                    ])
                }
            }
        }   
        stage('Terraform Plan Destroy Worker Nodes') {
            steps {
                sh '''
                cd ./Projects/k3s_cluster_aws/cluster_init/terraform/worker_node_config
                terraform init -input=false
                terraform plan -destroy -out=terraform_destroy.tfplan
                '''
            }
        }
        stage('Terraform Apply Destroy Worker Nodes') {
            steps {
                sh '''
                cd ./Projects/k3s_cluster_aws/cluster_init/terraform/worker_node_config
                terraform apply -input=false terraform_destroy.tfplan
                '''
            }
        }
        stage('Terraform Plan Destroy Master Node(s)') {
            steps {
                sh '''
                cd ./Projects/k3s_cluster_aws/cluster_init/terraform/master_node_config
                terraform init -input=false
                terraform plan -destroy -out=terraform_destroy.tfplan
                '''
            }
        }
        stage('Terraform Apply Destroy Master Node(s)') {
            steps {
                sh '''
                cd ./Projects/k3s_cluster_aws/cluster_init/terraform/master_node_config
                terraform apply -input=false terraform_destroy.tfplan
                '''
            }
        }
        stage('Terraform Plan Destroy VPC') {
            steps {
                sh '''
                cd ./Projects/k3s_cluster_aws/cluster_init/terraform/main_vpc_config
                terraform init -input=false
                terraform plan -destroy -out=terraform_destroy.tfplan
                '''
            }
        }
        stage('Terraform Apply Destroy VPC') {
            steps {
                sh '''
                cd ./Projects/k3s_cluster_aws/cluster_init/terraform/main_vpc_config
                terraform apply -input=false terraform_destroy.tfplan
                '''
            }
        }
    }
}
