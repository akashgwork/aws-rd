pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'akashgwork/aws-rds' // Update with your Docker image name
        DOCKER_REGISTRY = 'docker.io'
        EC2_HOST = 'ec2-3-108-59-138.ap-south-1.compute.amazonaws.com'
        EC2_USER = 'ubuntu'
        PEM_PATH = '/tmp/my-key.pem'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/akashgwork/aws-rd.git' //Update with your GitHub repo URL
            }
        }

        stage('Retrieve PEM Key from AWS Secrets Manager') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'aws-access-id', variable: 'AWS_ACCESS_KEY_ID'),
                        string(credentialsId: 'aws-secret-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                        string(credentialsId: 'pem-key', variable: 'PEM_KEY_SECRET_NAME')
                    ]) {
                        // Use AWS CLI to retrieve the PEM key from Secrets Manager
                        def pemKey = sh(
                            script: """
                                AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \\
                                aws secretsmanager get-secret-value --secret-id $PEM_KEY_SECRET_NAME --query SecretString --output text
                            """,
                            returnStdout: true
                        ).trim()

                        // Write the PEM key to a file
                        writeFile(file: PEM_PATH, text: pemKey)
                        sh "chmod 400 ${PEM_PATH}"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t $DOCKER_REGISTRY/$DOCKER_IMAGE .'
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD"
                    }
                    sh 'docker push $DOCKER_REGISTRY/$DOCKER_IMAGE'
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'ec2-host', variable: 'EC2_HOST'),
                        string(credentialsId: 'ec2-user', variable: 'EC2_USER')
                    ]) {
                        sh """
                        ssh -o StrictHostKeyChecking=no -i ${PEM_PATH} ${EC2_USER}@${EC2_HOST} \\
                        'docker pull $DOCKER_REGISTRY/$DOCKER_IMAGE && docker run -d -p 8080:8080 $DOCKER_REGISTRY/$DOCKER_IMAGE'
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            sh "rm -f ${PEM_PATH}" // Clean up PEM file
            echo 'Pipeline completed'
        }
    }
}
