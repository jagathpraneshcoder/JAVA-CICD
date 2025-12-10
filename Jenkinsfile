pipeline {
    agent any

    tools {
        jdk 'Java-11'       // Make sure Jenkins has Java 11 installed and named 'Java-11'
        maven 'Maven-3'     // Make sure Jenkins has Maven installed and named 'Maven-3'
    }

    environment {
        // Optional: set environment variables if needed
        DISPLAY = ':99' // Needed if using headless Selenium in Linux
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/jagathpraneshcoder/Java-CICD.git',
                    credentialsId: 'github-jenkins-token' // replace with your Jenkins credentials ID
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Selenium Tests') {
            steps {
                // Run tests with Maven
                sh 'mvn test'
            }
        }

        stage('Publish Test Results') {
            steps {
                // JUnit test reports
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
        }
        failure {
          
        }
    }
}

