pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'phuquocchamp' 
    }
    stages {
        stage('Checkout') {
            steps {
                git url: "https://github.com/phuquocchamp/LinkedHub.git", branch: "dev"
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh "chmod +x ./gradlew"
                    sh "./gradlew clean build -x test --no-daemon"
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                dir('backend') {
                    sh "docker build -t ${DOCKER_REGISTRY}/linkedhub-backend:latest ."
                }
            }            
        }

        stage('Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                    sh "docker push ${DOCKER_REGISTRY}/linkedhub-backend:latest"
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose -f docker-compose.yml down'
                sh 'docker-compose -f docker-compose.yml up -d --build'
            }
        }
    }
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}