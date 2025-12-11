pipeline {
    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-8-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/jagathpraneshcoder/Java-CICD.git',
                    credentialsId: 'github-jenkins-token'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests=true'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Archive WAR File') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', onlyIfSuccessful: true
            }
        }

        stage('Deploy to Local Tomcat') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                sh '''
                    /opt/tomcat9/bin/shutdown.sh || true
                    cp target/*.war /opt/tomcat9/webapps/
                    /opt/tomcat9/bin/startup.sh
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Build, Test, and Deployment Successful!'
        }
        failure {
            echo '❌ Build, Test, or Deployment Failed!'
        }
    }
}
